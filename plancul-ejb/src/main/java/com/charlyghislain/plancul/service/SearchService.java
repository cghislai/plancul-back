package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.LocalizedMessage_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.DateFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.request.sort.SortMappingContext;
import com.charlyghislain.plancul.domain.request.sort.SortMappingResult;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.security.enterprise.SecurityContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private SecurityContext securityContext;


    <T extends DomainEntity> Optional<T> getSingleResult(CriteriaQuery<T> query) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        List<T> listResult = typedQuery.getResultList();
        return listResult.stream().findAny();
    }


    <T extends DomainEntity> List<T> getAllResults(CriteriaQuery<T> query) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        List<T> listResult = typedQuery.getResultList();
        return listResult;
    }

    <T extends DomainEntity, F> CriteriaQuery<T> createSearchQuery(Class<T> resultType, F filter,
                                                                   BiFunction<From<?, T>, F, List<Predicate>> filterMapper
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(resultType);
        Root<T> queryRoot = query.from(resultType);
        List<Predicate> predicates = filterMapper.apply(queryRoot, filter);

        query.select(queryRoot);
        query.where(predicates.toArray(new Predicate[0]));
        return query;
    }


    <T extends DomainEntity> SearchResult<T> search(Pagination pagination, List<Sort<T>> sorts, Language language, CriteriaQuery<T> query, Root<T> root, List<Predicate> predicates) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Expression<Long> count = criteriaBuilder.countDistinct(root);
        countQuery.select(count);
        countQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();

        List<Order> orders = sorts.stream()
                .map(sort -> this.createOrder(sort, language, root, predicates))
                .collect(Collectors.toList());

        query.select(root);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(orders);
        query.distinct(true);

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pagination.getOffset());
        typedQuery.setMaxResults(pagination.getSize());
        List<T> resultList = typedQuery.getResultList();

        SearchResult<T> result = new SearchResult<>(resultList, countResult);
        return result;
    }

    private <T extends DomainEntity> Order createOrder(Sort<T> sort, Language language, Root<T> root, List<Predicate> predicateList) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        SortMappingContext mappingContext = new SortMappingContext(language, criteriaBuilder);

        SortMappingResult<?> sortMappingResult = sort.getMappingFunction().apply(root, mappingContext);
        Expression<?> sortExpression = sortMappingResult.getSortExpression();
        List<Predicate> predicates = sortMappingResult.getPredicates();

        predicateList.addAll(predicates);

        if (sort.isAscending()) {
            return criteriaBuilder.asc(sortExpression);
        } else {
            return criteriaBuilder.desc(sortExpression);
        }
    }

    public Predicate createTextMatchPredicate(Path<String> messagePath, String query) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        String queryStringToMatch = this.makePartialLikeQuery(query);
        Expression<String> lowercaseLabelPath = criteriaBuilder.lower(messagePath);
        Predicate labelPredicate = criteriaBuilder.like(lowercaseLabelPath, queryStringToMatch);
        return labelPredicate;
    }

    public Predicate createLocalizedTextMatchPredicate(Path<LocalizedMessage> messagePath, String query, Optional<Language> language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        String queryStringToMatch = this.makePartialLikeQuery(query);

        Path<String> labelPath = messagePath.get(LocalizedMessage_.label);
        Expression<String> lowercaseLabelPath = criteriaBuilder.lower(labelPath);
        Predicate labelPredicate = criteriaBuilder.like(lowercaseLabelPath, queryStringToMatch);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(labelPredicate);


        language.map(lang -> this.createLanguagePredicate(messagePath, lang))
                .ifPresent(predicateList::add);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }


    public Optional<Predicate> createLoggedUserTenantsPredicate(Path<Tenant> rootTenant) {
        return this.createLoggedUserTenantsPredicate(rootTenant, false);
    }

    public Optional<Predicate> createLoggedUserTenantsPredicate(Path<Tenant> rootTenant, boolean allowNull) {
        boolean adminLogged = securityContext.isCallerInRole(ApplicationGroupNames.ADMIN);
        if (adminLogged) {
            return Optional.empty();
        }

        List<Tenant> allowedTenantList = userQueryService.getLoggedUserTenantsRoles().stream()
                .map(TenantUserRole::getTenant)
                .collect(Collectors.toList());
        Predicate tenantMatchpredicate = rootTenant.in(allowedTenantList);
        if (allowNull) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            Predicate nullPredicate = criteriaBuilder.isNull(rootTenant);

            Predicate nullOrMatchpredicate = criteriaBuilder.or(tenantMatchpredicate, nullPredicate);
            return Optional.of(nullOrMatchpredicate);
        } else {
            return Optional.of(tenantMatchpredicate);
        }
    }

    public Predicate createDateFilterPredicate(Path<LocalDate> datePath, DateFilter dateFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        dateFilter.getNotBefore()
                .map(minDate -> criteriaBuilder.greaterThanOrEqualTo(datePath, minDate))
                .ifPresent(predicateList::add);

        dateFilter.getNotAfter()
                .map(minDate -> criteriaBuilder.lessThanOrEqualTo(datePath, minDate))
                .ifPresent(predicateList::add);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }


    private Predicate createLanguagePredicate(Path<LocalizedMessage> messagePath, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<Language> languagePath = messagePath.get(LocalizedMessage_.language);
        return criteriaBuilder.equal(languagePath, language);
    }

    private String makePartialLikeQuery(String query) {
        return "%" + query.toLowerCase() + "%";
    }


}
