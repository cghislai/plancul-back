package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation_;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;
import com.charlyghislain.plancul.domain.request.filter.TenantUserRoleInvitationFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TenantUserRoleInvitationQueryService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SearchService searchService;

    public TenantUserRoleInvitation validateTenantUserRoleInvitationToken(@NotNull String tenantToken) throws InvalidTokenException {
        return findTenantUserRoleInvitation(tenantToken)
                .orElseThrow(InvalidTokenException::new);
    }

    public Optional<TenantUserRoleInvitation> findTenantUserRoleInvitation(TenantUserRoleInvitationFilter tenantUserRoleInvitationFilter) {
        CriteriaQuery<TenantUserRoleInvitation> query = searchService.createSearchQuery(TenantUserRoleInvitation.class, tenantUserRoleInvitationFilter, this::createPredicates);
        return searchService.getSingleResult(query);
    }

    public Optional<TenantUserRoleInvitation> findTenantUserRoleInvitation(String token) {
        TenantUserRoleInvitationFilter tenantUserRoleInvitationFilter = new TenantUserRoleInvitationFilter();
        tenantUserRoleInvitationFilter.setToken(token);
        return this.findTenantUserRoleInvitation(tenantUserRoleInvitationFilter);
    }

    public List<TenantUserRoleInvitation> findAllTenantUserRoleInvitations(TenantUserRoleInvitationFilter tenantUserRoleInvitationFilter) {
        CriteriaQuery<TenantUserRoleInvitation> query = searchService.createSearchQuery(TenantUserRoleInvitation.class, tenantUserRoleInvitationFilter, this::createPredicates);
        return searchService.getAllResults(query);
    }

    private List<Predicate> createPredicates(From<?, TenantUserRoleInvitation> tenantUserRoleInvitationFrom, TenantUserRoleInvitationFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        Path<Tenant> tenantPath = tenantUserRoleInvitationFrom.get(TenantUserRoleInvitation_.tenant);
        Optional.ofNullable(filter.getTenant())
                .map(tenant -> criteriaBuilder.equal(tenantPath, tenant))
                .ifPresent(predicates::add);

        Path<TenantRole> tenantRolePath = tenantUserRoleInvitationFrom.get(TenantUserRoleInvitation_.tenantRole);
        Optional.ofNullable(filter.getTenantRole())
                .map(tenantRole -> criteriaBuilder.equal(tenantRolePath, tenantRole))
                .ifPresent(predicates::add);

        Path<String> tokenPath = tenantUserRoleInvitationFrom.get(TenantUserRoleInvitation_.token);
        Optional.ofNullable(filter.getToken())
                .map(token -> criteriaBuilder.equal(tokenPath, token))
                .ifPresent(predicates::add);

        return predicates;
    }

}
