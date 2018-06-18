package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.util.exception.NoIdException;
import com.charlyghislain.plancul.domain.util.exception.UnexpectedIdException;
import com.charlyghislain.plancul.domain.util.WithId;
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

    void validateNoId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (idOptional.isPresent()) {
            throw new UnexpectedIdException();
        }
    }

    void validateNonNullId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (!idOptional.isPresent()) {
            throw new NoIdException();
        }
    }

    void validateLoggedUserHasTenantRole(Tenant tenant) {
        boolean adminLogged = securityService.isAdminLogged();
        if (adminLogged) {
            return;
        }
        userService.getLoggedUser()
                .flatMap(user -> userService.findUserRoleForTenant(user, tenant))
                .orElseThrow(OperationNotAllowedException::new);
    }


    void validateLoggedUserHasTenantRole(Tenant tenant, TenantRole role) {
        boolean adminLogged = securityService.isAdminLogged();
        if (adminLogged) {
            return;
        }
        userService.getLoggedUser()
                .flatMap(user -> userService.findUserRoleForTenant(user, tenant))
                .filter(userRole -> userRole == role)
                .orElseThrow(OperationNotAllowedException::new);
    }
}
