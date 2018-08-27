package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.UUID;

@Stateless
public class TenantUserRoleInvitationUpdateService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;


    public TenantUserRoleInvitation createInvitation(Tenant tenant, TenantRole tenantRole) {
        String token = UUID.randomUUID().toString();

        TenantUserRoleInvitation invitation = new TenantUserRoleInvitation();
        invitation.setTenant(tenant);
        invitation.setTenantRole(tenantRole);
        invitation.setToken(token);
        return this.saveInvitation(invitation);
    }

    private TenantUserRoleInvitation saveInvitation(@Valid TenantUserRoleInvitation invitation) {
        return entityManager.merge(invitation);
    }


}
