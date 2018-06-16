package com.charlyghislain.plancul.security;

import javax.security.enterprise.credential.Credential;

public class JwtTokenCredential implements Credential {

    private final String token;

    public JwtTokenCredential(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
