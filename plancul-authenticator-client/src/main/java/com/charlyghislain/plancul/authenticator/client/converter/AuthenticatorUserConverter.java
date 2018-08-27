package com.charlyghislain.plancul.authenticator.client.converter;

import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticatorUserConverter {

    public AuthenticatorUser toAuthenticatorUser(WsApplicationUser wsApplicationUser) {
        Long id = wsApplicationUser.getId();
        String name = wsApplicationUser.getName();
        String email = wsApplicationUser.getEmail();
        boolean active = wsApplicationUser.isActive();
        boolean emailVerified = wsApplicationUser.isEmailVerified();
        boolean passwordExpired = wsApplicationUser.isPasswordExpired();

        AuthenticatorUser authenticatorUser = new AuthenticatorUser();
        authenticatorUser.setActive(active);
        authenticatorUser.setEmail(email);
        authenticatorUser.setEmailVerified(emailVerified);
        authenticatorUser.setId(id);
        authenticatorUser.setName(name);
        authenticatorUser.setPasswordExpired(passwordExpired);
        return authenticatorUser;
    }
}
