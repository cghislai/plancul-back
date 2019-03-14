package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
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
        invitation.setCreated(LocalDateTime.now());
        return this.saveInvitation(invitation);
    }

    private TenantUserRoleInvitation saveInvitation(@Valid TenantUserRoleInvitation invitation) {
        invitation.setUpdated(LocalDateTime.now());
        return entityManager.merge(invitation);
    }


}
