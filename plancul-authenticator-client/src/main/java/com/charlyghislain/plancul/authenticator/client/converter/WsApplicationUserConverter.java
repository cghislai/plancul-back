package com.charlyghislain.plancul.authenticator.client.converter;

import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationUserConverter {

    public WsApplicationUser toWsApplicationUser(AuthenticatorUser authenticatorUser) {
        Long id = authenticatorUser.getId();
        String name = authenticatorUser.getName();
        String email = authenticatorUser.getEmail();
        boolean emailVerified = authenticatorUser.isEmailVerified();
        boolean passwordExpired = authenticatorUser.isPasswordExpired();
        boolean active = authenticatorUser.isActive();

        WsApplicationUser wsApplicationUser = new WsApplicationUser();
        wsApplicationUser.setActive(active);
        wsApplicationUser.setEmail(email);
        wsApplicationUser.setEmailVerified(emailVerified);
        wsApplicationUser.setId(id);
        wsApplicationUser.setName(name);
        wsApplicationUser.setPasswordExpired(passwordExpired);
        return wsApplicationUser;
    }
}
