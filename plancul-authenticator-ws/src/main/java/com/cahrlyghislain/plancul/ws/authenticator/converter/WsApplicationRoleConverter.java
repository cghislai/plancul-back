package com.cahrlyghislain.plancul.ws.authenticator.converter;

import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class WsApplicationRoleConverter {

    public WsApplicationRole toWsApplicationRole(String roleName) {
        WsApplicationRole wsApplicationRole = new WsApplicationRole();
        wsApplicationRole.setName(roleName);
        return wsApplicationRole;
    }
}
