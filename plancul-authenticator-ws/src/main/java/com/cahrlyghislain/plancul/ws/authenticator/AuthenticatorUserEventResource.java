package com.cahrlyghislain.plancul.ws.authenticator;

import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.CommunicationService;
import com.charlyghislain.plancul.service.UserQueryService;

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
    @Inject
    private CommunicationService communicationService;
    @Inject
    private UserQueryService userQueryService;

    @Override
    public CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser) {
        userQueryService.findUserByAuthenticatorUid(wsApplicationUser.getId())
                .ifPresent(user -> this.checkSendVerificationMail(user, wsApplicationUser));

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


    private void checkSendVerificationMail(User user, WsApplicationUser wsApplicationUser) {
        boolean emailVerified = wsApplicationUser.isEmailVerified();
        boolean active = wsApplicationUser.isActive();
        boolean admin = user.isAdmin();

        if (!active && !emailVerified) {
            String emailVerificationToken = authenticatorUserClient.createNewEmailVerificationToken(user);
            if (admin) {
                authenticatorUserClient.validateUserEmail(user.getAuthenticatorUid(), emailVerificationToken);
            } else {
                communicationService.sendAccountEmailVerification(user, emailVerificationToken);
            }
        }
    }

}
