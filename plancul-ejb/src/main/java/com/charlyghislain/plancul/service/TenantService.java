package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.Tenant_;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TenantService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;
    @Inject
    private UserService userService;
    @Inject
    private SecurityService securityService;

    @RolesAllowed(ApplicationGroupNames.ADMIN)
    public Tenant createTenant(Tenant tenant) {
        validationService.validateNoId(tenant);

        Tenant managedTenant = entityManager.merge(tenant);
        return managedTenant;
    }

    public Tenant saveTenant(Tenant tenant) {
        if (tenant.getId() == null) {
            return this.createTenant(tenant);
        }
        validationService.validateLoggedUserHasTenantRole(tenant, TenantRole.ADMIN);

        Tenant managedTenant = entityManager.merge(tenant);
        return managedTenant;
    }

    public Optional<Tenant> findTenantById(long id) {
        Tenant foundTenant = entityManager.find(Tenant.class, id);
        Optional<Tenant> foundTenantOptional = Optional.ofNullable(foundTenant);
        foundTenantOptional.ifPresent(validationService::validateLoggedUserHasTenantRole);
        return foundTenantOptional;
    }

    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> query = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> rootTenant = query.from(Tenant.class);

        List<Predicate> predicates = this.createTenantPredicates(tenantFilter, rootTenant);

        return searchService.search(pagination, query, rootTenant, predicates);
    }

    private List<Predicate> createTenantPredicates(TenantFilter tenantFilter, Root<Tenant> rootTenant) {
        List<Predicate> predicateList = new ArrayList<>();

        searchService.createLoggedUserTenantsPredicate(rootTenant)
                .ifPresent(predicateList::add);

        Path<String> namePath = rootTenant.get(Tenant_.name);
        tenantFilter.getNameContains()
                .map(query -> this.createContainsPredicate(query, namePath))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createContainsPredicate(String query, Path<String> namePath) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        String likeMatchString = '%' + query + '%';
        Predicate likePredicate = criteriaBuilder.like(namePath, likeMatchString);
        return likePredicate;
    }

}
