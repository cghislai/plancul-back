package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRole_;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.request.filter.TenantUserRoleFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TenantUserRolesQueryService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SearchService searchService;
    @Inject
    private AuthenticatorUserClient authenticatorUserClient;


    public Optional<TenantUserRole> findTenantUserRole(TenantUserRoleFilter tenantUserRoleFilter) {
        CriteriaQuery<TenantUserRole> query = searchService.createSearchQuery(TenantUserRole.class, tenantUserRoleFilter, this::createPredicates);
        return searchService.getSingleResult(query);
    }

    public Optional<TenantUserRole> findTenantUserRole(User user, Tenant tenant) {
        TenantUserRoleFilter tenantUserRoleFilter = new TenantUserRoleFilter();
        tenantUserRoleFilter.setUser(user);
        tenantUserRoleFilter.setTenant(tenant);
        return this.findTenantUserRole(tenantUserRoleFilter);
    }

    public Optional<TenantUserRole> findTenantUserRole(User user, Tenant tenant, TenantRole tenantRole) {
        TenantUserRoleFilter tenantUserRoleFilter = new TenantUserRoleFilter();
        tenantUserRoleFilter.setUser(user);
        tenantUserRoleFilter.setTenant(tenant);
        tenantUserRoleFilter.setTenantRole(tenantRole);
        return this.findTenantUserRole(tenantUserRoleFilter);
    }

    public List<TenantUserRole> findAllTenantUserRoles(TenantUserRoleFilter tenantUserRoleFilter) {
        CriteriaQuery<TenantUserRole> query = searchService.createSearchQuery(TenantUserRole.class, tenantUserRoleFilter, this::createPredicates);
        return searchService.getAllResults(query);
    }

    public List<TenantUserRole> findAllTenantUserRoles(User user) {
        TenantUserRoleFilter tenantUserRoleFilter = new TenantUserRoleFilter();
        tenantUserRoleFilter.setUser(user);
        return this.findAllTenantUserRoles(tenantUserRoleFilter);
    }

    public List<TenantUserRole> findAllTenantUserRoles(User user, TenantRole tenantRole) {
        TenantUserRoleFilter tenantUserRoleFilter = new TenantUserRoleFilter();
        tenantUserRoleFilter.setUser(user);
        tenantUserRoleFilter.setTenantRole(tenantRole);
        return this.findAllTenantUserRoles(tenantUserRoleFilter);
    }

    private List<Predicate> createPredicates(From<?, TenantUserRole> tenantUserRoleFrom, TenantUserRoleFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        Path<User> userPath = tenantUserRoleFrom.get(TenantUserRole_.user);
        Optional.ofNullable(filter.getUser())
                .map(user -> criteriaBuilder.equal(userPath, user))
                .ifPresent(predicates::add);

        Path<Tenant> tenantPath = tenantUserRoleFrom.get(TenantUserRole_.tenant);
        Optional.ofNullable(filter.getTenant())
                .map(tenant -> criteriaBuilder.equal(tenantPath, tenant))
                .ifPresent(predicates::add);

        Path<TenantRole> tenantRolePath = tenantUserRoleFrom.get(TenantUserRole_.tenantRole);
        Optional.ofNullable(filter.getTenantRole())
                .map(tenantRole -> criteriaBuilder.equal(tenantRolePath, tenantRole))
                .ifPresent(predicates::add);

        return predicates;
    }

}
