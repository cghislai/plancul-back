package com.charlyghislain.plancul.ws.authenticator;

import com.charlyghislain.authenticator.application.api.HealthResource;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckResponse;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;

@RolesAllowed(ApplicationGroupNames.AUTHENTICATOR)
@RequestScoped
public class AuthenticatorHealthResource implements HealthResource {

    @Override
    public @NonNull WsHealthCheckResponse getHealth() {
        WsHealthCheckResponse healthCheckResponse = new WsHealthCheckResponse();
        healthCheckResponse.setOutcome(WsHealthStatus.UP);
        healthCheckResponse.setChecks(new ArrayList<>());
        return healthCheckResponse;
    }
}
