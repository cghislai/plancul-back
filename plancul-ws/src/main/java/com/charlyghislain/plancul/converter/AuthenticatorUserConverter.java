package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.request.WsUserRegistration;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AuthenticatorUserConverter {

    public Optional<AuthenticatorUser> toAuthenticatorUser(WsUserRegistration wsUserRegistration) {
        String email = wsUserRegistration.getEmail();
        String name = wsUserRegistration.getName();

        if (email == null || name == null) {
            return Optional.empty();
        }

        AuthenticatorUser authenticatorUser = new AuthenticatorUser();
        authenticatorUser.setName(name);
        authenticatorUser.setEmail(email);
        return Optional.of(authenticatorUser);
    }
}
