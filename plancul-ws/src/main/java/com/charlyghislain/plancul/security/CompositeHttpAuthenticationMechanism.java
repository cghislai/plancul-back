package com.charlyghislain.plancul.security;

import com.charlyghislain.plancul.util.CrossOriginResourceSharingResponseFilter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class CompositeHttpAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private Instance<HttpCredentialProvider> httpCredentialProviders;
    @Inject
    private IdentityStoreHandler identityStoreHandler;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        CredentialValidationResult validationResult = httpCredentialProviders.stream()
                .map(provider -> provider.extractCredential(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(identityStoreHandler::validate)
                .orElse(CredentialValidationResult.NOT_VALIDATED_RESULT);
        return this.handleValidationResult(validationResult, httpMessageContext);
    }

    private AuthenticationStatus handleValidationResult(CredentialValidationResult validationResult, HttpMessageContext httpMessageContext) {
        if (validationResult.getStatus() == CredentialValidationResult.Status.VALID) {
            return httpMessageContext.notifyContainerAboutLogin(validationResult);
        } else if (httpMessageContext.isProtected()) {
            HttpServletResponse response = httpMessageContext.getResponse();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            crossOriginResourceSharingResponseFilter.filter(response);
            return AuthenticationStatus.SEND_FAILURE;
        } else {
            return httpMessageContext.doNothing();
        }
    }

}
