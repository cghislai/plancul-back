package com.charlyghislain.plancul.authenticator.client.converter;

import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUserWithPassword;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationUserConverter {

    public WsApplicationUser toWsApplicationUser(AuthenticatorUser authenticatorUser) {
        WsApplicationUser wsApplicationUser = new WsApplicationUser();
        updateWsApplicationUser(authenticatorUser, wsApplicationUser);
        return wsApplicationUser;
    }

    public WsApplicationUserWithPassword toWsApplicationUser(AuthenticatorUser authenticatorUser, String password) {
        WsApplicationUserWithPassword applicationUserWithPassword = new WsApplicationUserWithPassword();
        updateWsApplicationUser(authenticatorUser, applicationUserWithPassword);
        applicationUserWithPassword.setPassword(password);
        return applicationUserWithPassword;
    }

    private void updateWsApplicationUser(AuthenticatorUser authenticatorUser, WsApplicationUser wsApplicationUser) {

        Long id = authenticatorUser.getId();
        String name = authenticatorUser.getName();
        String email = authenticatorUser.getEmail();
        boolean emailVerified = authenticatorUser.isEmailVerified();
        boolean passwordExpired = authenticatorUser.isPasswordExpired();
        boolean active = authenticatorUser.isActive();

        wsApplicationUser.setActive(active);
        wsApplicationUser.setEmail(email);
        wsApplicationUser.setEmailVerified(emailVerified);
        wsApplicationUser.setId(id);
        wsApplicationUser.setName(name);
        wsApplicationUser.setPasswordExpired(passwordExpired);
    }
}