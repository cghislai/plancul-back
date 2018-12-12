package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.util.WithId;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.util.Optional;
import java.util.Set;

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
        boolean adminLogged = isAdminLogged();
        boolean hasTenantRole = userQueryService.getLoggedUser()
                .flatMap(user -> tenantUserRolesQueryService.findTenantUserRole(user, tenant))
                .isPresent();
        return adminLogged || hasTenantRole;
    }

    boolean hasLoggedUserTenantRole(Tenant tenant, TenantRole role) {
        boolean adminLogged = isAdminLogged();
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

    private boolean isAdminLogged() {
        // FIXME: should be able to use SecurityContext@isUserInRole
        JsonWebToken jsonWebToken = (JsonWebToken) securityContext.getCallerPrincipal();
        Set<String> groups = jsonWebToken.getGroups();
        return groups.contains(ApplicationGroupNames.ADMIN);
    }
}
