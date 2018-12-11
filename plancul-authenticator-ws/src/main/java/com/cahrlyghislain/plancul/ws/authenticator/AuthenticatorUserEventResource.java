package com.cahrlyghislain.plancul.ws.authenticator;

import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.PlanCulException;
import com.charlyghislain.plancul.domain.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.CommunicationService;
import com.charlyghislain.plancul.service.UserCreationQueue;
import com.charlyghislain.plancul.service.UserQueryService;
import com.charlyghislain.plancul.service.UserUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RolesAllowed(ApplicationGroupNames.AUTHENTICATOR)
@RequestScoped
public class AuthenticatorUserEventResource implements UserEventResource {

    private final static Logger LOG = LoggerFactory.getLogger(AuthenticatorUserEventResource.class);
    @Inject
    private AuthenticatorUserClient authenticatorUserClient;
    @Inject
    private CommunicationService communicationService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private UserCreationQueue userCreationQueue;

    @Override
    public CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser) {
        Long id = wsApplicationUser.getId();
        String email = wsApplicationUser.getEmail();
        LOG.debug("userAdded callback for authenticator userId {} with mail {}", id, email);

        // User being added might not be persisted in database yet.
        User userNullable = userQueryService.findUserByAuthenticatorUid(id)
                .orElseGet(() -> userCreationQueue.getUser(email).orElse(null));

        if (userNullable != null) {
            this.checkSendVerificationMail(userNullable, wsApplicationUser);
        } else {
            LOG.warn("userAdded callback: Could not find plancul user with mail {}", email);
        }

        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletionStage<Void> userEmailVerified(WsApplicationUser wsApplicationUser) {
        Long id = wsApplicationUser.getId();
        String email = wsApplicationUser.getEmail();
        LOG.debug("userEmailVerified callback for authenticator userId {} with mail {}", id, email);

        authenticatorUserClient.setUserActive(wsApplicationUser.getId());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> userRemoved(Long id) {
        LOG.debug("userRemoved callback for authenticator userId {} ", id);

        userQueryService.findUserByAuthenticatorUid(id)
                .ifPresent(userUpdateService::removeUser);

        return CompletableFuture.completedFuture(null);
    }


    private void checkSendVerificationMail(User user, WsApplicationUser wsApplicationUser) {
        boolean emailVerified = wsApplicationUser.isEmailVerified();
        boolean active = wsApplicationUser.isActive();
        boolean admin = user.isAdmin();

        if (!active && !emailVerified) {
            Long wsApplicationUserId = wsApplicationUser.getId();
            String emailVerificationToken = authenticatorUserClient.createNewEmailVerificationToken(wsApplicationUserId);
            if (admin) {
                authenticatorUserClient.validateUserEmail(user.getAuthenticatorUid(), emailVerificationToken);
            } else {
                sendAccountEmailVerificationMessage(user, emailVerificationToken);
            }
        }
    }

    private void sendAccountEmailVerificationMessage(User user, String emailVerificationToken) {
        try {
            communicationService.sendAccountEmailVerification(user, emailVerificationToken);
        } catch (PlanCulException e) {
            throw new PlanCulRuntimeException("Could not send account email verification message", e);
        }
    }

}
