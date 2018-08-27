package com.cahrlyghislain.plancul.ws.authenticator;

import com.cahrlyghislain.plancul.ws.authenticator.converter.WsApplicationRoleConverter;
import com.charlyghislain.authenticator.application.api.AuthorizationResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.UserQueryService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed(ApplicationGroupNames.AUTHENTICATOR)
@RequestScoped
public class AuthenticatorAuthorizationResource implements AuthorizationResource {

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private WsApplicationRoleConverter wsApplicationRoleConverter;

    @Override
    public List<WsApplicationRole> listUserRoles(long userId) {
        return userQueryService.findUserApplicationGroups(userId)
                .stream()
                .map(wsApplicationRoleConverter::toWsApplicationRole)
                .collect(Collectors.toList());
    }

    @Override
    public WsHealthCheckStatus checkAuthenticatorAuthorization() {
        WsHealthCheckStatus healthCheckStatus = new WsHealthCheckStatus();
        healthCheckStatus.setName("test-app");
        healthCheckStatus.setState(WsHealthStatus.UP);
        return healthCheckStatus;
    }
}
