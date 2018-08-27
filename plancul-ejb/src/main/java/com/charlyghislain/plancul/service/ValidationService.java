package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.util.WithId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.util.Optional;

@ApplicationScoped
public class ValidationService {

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private TenantUserRolesQueryService tenantUserRolesQueryService;
    @Inject
    private SecurityContext securityContext;

    void validateNonNullId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (!idOptional.isPresent()) {
            throw new IllegalStateException();
        }
    }

    boolean hasLoggedUserTenantRole(Tenant tenant) {
        boolean adminLogged = securityContext.isCallerInRole(ApplicationGroupNames.ADMIN);
        boolean hasTenantRole = userQueryService.getLoggedUser()
                .flatMap(user -> tenantUserRolesQueryService.findTenantUserRole(user, tenant))
                .isPresent();
        return adminLogged || hasTenantRole;
    }

    boolean hasLoggedUserTenantRole(Tenant tenant, TenantRole role) {
        boolean adminLogged = securityContext.isCallerInRole(ApplicationGroupNames.ADMIN);
        boolean hasTenantRole = userQueryService.getLoggedUser()
                .flatMap(user -> tenantUserRolesQueryService.findTenantUserRole(user, tenant, role))
                .isPresent();
        return adminLogged || hasTenantRole;
    }

    void validateLoggedUserHasTenantRole(Tenant tenant) throws OperationNotAllowedException {
        if (!this.hasLoggedUserTenantRole(tenant)) {
            throw new OperationNotAllowedException();
        }
    }

    void validateLoggedUserHasTenantRole(Tenant tenant, TenantRole role) throws OperationNotAllowedException {
        if (!this.hasLoggedUserTenantRole(tenant, role)) {
            throw new OperationNotAllowedException();
        }
    }
}
