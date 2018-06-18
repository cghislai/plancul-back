package com.charlyghislain.plancul.security;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Default
public class CompositeHttpAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private Instance<HttpCredentialProvider> httpCredentialProviders;
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        AuthenticationStatus authenticationStatus = httpCredentialProviders.stream()
                .map(provider -> provider.extractCredential(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(identityStoreHandler::validate)
                .map(httpMessageContext::notifyContainerAboutLogin)
                .orElse(AuthenticationStatus.NOT_DONE);
        sendResponse(response, authenticationStatus);
        return authenticationStatus;
    }

    private void sendResponse(HttpServletResponse response, AuthenticationStatus authenticationStatus) {
        try {
            switch (authenticationStatus) {
                case SEND_FAILURE:
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    break;
                case SEND_CONTINUE:
                    response.sendError(HttpServletResponse.SC_CONTINUE);
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send authentication response", e);
        }
    }

}
