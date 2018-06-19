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
import javax.persistence.criteria.From;
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
        return Optional.ofNullable(foundTenant)
                .filter(validationService::hasLoggedUserTenantRole);
    }

    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> query = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> rootTenant = query.from(Tenant.class);

        List<Predicate> predicates = this.createTenantPredicates(tenantFilter, rootTenant);

        return searchService.search(pagination, query, rootTenant, predicates);
    }

    @RolesAllowed({ApplicationGroupNames.ADMIN})
    private Tenant createTenant(Tenant tenant) {
        Tenant managedTenant = entityManager.merge(tenant);
        return managedTenant;
    }

    private List<Predicate> createTenantPredicates(TenantFilter tenantFilter, Root<Tenant> rootTenant) {
        List<Predicate> predicateList = new ArrayList<>();

        searchService.createLoggedUserTenantsPredicate(rootTenant)
                .ifPresent(predicateList::add);

        tenantFilter.getNameContains()
                .map(query -> this.createContainsPredicate(query, rootTenant))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createContainsPredicate(String query, From<?, Tenant> tenantSource) {
        Path<String> namePath = tenantSource.get(Tenant_.name);
        Predicate predicate = searchService.createTextMatchPredicate(namePath, query);
        return predicate;
    }

}
