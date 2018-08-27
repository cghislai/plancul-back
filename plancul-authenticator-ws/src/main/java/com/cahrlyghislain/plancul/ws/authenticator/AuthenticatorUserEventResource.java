package com.cahrlyghislain.plancul.ws.authenticator;

import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RolesAllowed(ApplicationGroupNames.AUTHENTICATOR)
@RequestScoped
public class AuthenticatorUserEventResource implements UserEventResource {

    @Inject
    private AuthenticatorUserClient authenticatorUserClient;

    @Override
    public CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser) {
        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletionStage<Void> userEmailVerified(WsApplicationUser wsApplicationUser) {
        authenticatorUserClient.setUserActive(wsApplicationUser.getId());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> userRemoved(Long id) {
        return CompletableFuture.completedFuture(null);
    }

}
