package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

@Stateless
public class TenantUserRolesUpdateService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private TenantUserRoleInvitationQueryService tenantUserRoleInvitationQueryService;


    public TenantUserRole createTenantUserTole(User user, TenantUserRoleInvitation tenantUserRoleInvitation) throws InvalidTokenException {
        tenantUserRoleInvitationQueryService.validateTenantUserRoleInvitationToken(tenantUserRoleInvitation.getToken());

        Tenant tenant = tenantUserRoleInvitation.getTenant();
        TenantRole tenantRole = tenantUserRoleInvitation.getTenantRole();

        TenantUserRole tenantUserRole = new TenantUserRole();
        tenantUserRole.setTenant(tenant);
        tenantUserRole.setTenantRole(tenantRole);
        tenantUserRole.setUser(user);
        TenantUserRole managedTenantUserRole = this.saveTenantUserRole(tenantUserRole);
        this.removeInvitation(tenantUserRoleInvitation);
        return managedTenantUserRole;
    }

    public TenantUserRole createTenantUserRole(User user, Tenant tenant, TenantRole tenantRole) {
        TenantUserRole tenantUserRole = new TenantUserRole();
        tenantUserRole.setTenant(tenant);
        tenantUserRole.setTenantRole(tenantRole);
        tenantUserRole.setUser(user);
        TenantUserRole managedTenantUserRole = this.saveTenantUserRole(tenantUserRole);
        return managedTenantUserRole;
    }

    public void removeTenantUserRole(TenantUserRole tenantUserRole) {
        TenantUserRole managedUserRole = entityManager.merge(tenantUserRole);
        entityManager.remove(managedUserRole);
    }


    private void removeInvitation(TenantUserRoleInvitation tenantUserRoleInvitation) {
        TenantUserRoleInvitation managedInvitation = entityManager.merge(tenantUserRoleInvitation);
        entityManager.remove(managedInvitation);
    }

    private TenantUserRole saveTenantUserRole(@Valid TenantUserRole tenantUserRole) {
        return entityManager.merge(tenantUserRole);
    }
}
