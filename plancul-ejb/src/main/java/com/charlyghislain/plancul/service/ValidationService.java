package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.util.WithId;
import com.charlyghislain.plancul.domain.util.exception.NoIdException;
import com.charlyghislain.plancul.security.exception.OperationNotAllowedException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ValidationService {

    @EJB
    private UserService userService;
    @EJB
    private SecurityService securityService;

    void validateNonNullId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (!idOptional.isPresent()) {
            throw new NoIdException();
        }
    }

    boolean hasLoggedUserTenantRole(Tenant tenant) {
        boolean adminLogged = securityService.isAdminLogged();
        boolean hasTenantRole = userService.getLoggedUser()
                .flatMap(user -> userService.findUserRoleForTenant(user, tenant))
                .map(role -> role != null)
                .orElse(false);
        return adminLogged || hasTenantRole;
    }

    boolean hasLoggedUserTenantRole(Tenant tenant, TenantRole role) {
        boolean adminLogged = securityService.isAdminLogged();
        boolean hasTenantRole = userService.getLoggedUser()
                .flatMap(user -> userService.findUserRoleForTenant(user, tenant))
                .map(tenantRole -> tenantRole == role)
                .orElse(false);
        return adminLogged || hasTenantRole;
    }

    void validateLoggedUserHasTenantRole(Tenant tenant) {
        if (!this.hasLoggedUserTenantRole(tenant)) {
            throw new OperationNotAllowedException();
        }
    }


    void validateLoggedUserHasTenantRole(Tenant tenant, TenantRole role) {
        if (!this.hasLoggedUserTenantRole(tenant, role)) {
            throw new OperationNotAllowedException();
        }
    }
}
