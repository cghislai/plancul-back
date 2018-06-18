package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.LocalizedMessage_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;
    @EJB
    private I18NService i18NService;
    @EJB
    private UserService userService;
    @EJB
    private SecurityService securityService;

    <T extends DomainEntity> SearchResult<T> search(Pagination pagination, CriteriaQuery<T> query, Root<T> root, List<Predicate> predicates) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Expression<Long> count = criteriaBuilder.count(root);
        countQuery.select(count);
        countQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();


        query.select(root);
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pagination.getOffset());
        typedQuery.setMaxResults(pagination.getSize());
        List<T> resultList = typedQuery.getResultList();

        SearchResult<T> result = new SearchResult<>(resultList, countResult);
        return result;
    }

    public Predicate createLocalizedTextMatchPredicate(Path<LocalizedMessage> messagePath, String query, Optional<String> language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        String queryStringToMatch = this.makePartialLikeQuery(query);

        Path<String> labelPath = messagePath.get(LocalizedMessage_.label);
        Expression<String> lowercaseLabelPath = criteriaBuilder.lower(labelPath);
        Predicate labelPredicate = criteriaBuilder.like(lowercaseLabelPath, queryStringToMatch);

        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(labelPredicate);

        language.filter(i18NService::isSupportedLanguage)
                .map(lang -> this.createLanguagePredicate(messagePath, lang))
                .ifPresent(predicateList::add);

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }


    public Optional<Predicate> createLoggedUserTenantsPredicate(Path<Tenant> rootTenant) {
        boolean adminLogged = securityService.isAdminLogged();
        if (!adminLogged) {
            return Optional.empty();
        }

        List<Tenant> allowedTenantList = userService.getLoggedUserTenantsRoles().stream()
                .map(TenantUserRole::getTenant)
                .collect(Collectors.toList());
        return Optional.of(rootTenant.in(allowedTenantList));
    }


    private Predicate createLanguagePredicate(Path<LocalizedMessage> messagePath, String language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<Language> languagePath = messagePath.get(LocalizedMessage_.language);
        return criteriaBuilder.equal(languagePath, language);
    }

    private String makePartialLikeQuery(String query) {
        return "%" + query.toLowerCase() + "%";
    }


}
