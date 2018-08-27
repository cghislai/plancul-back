package com.charlyghislain.plancul.authenticator.client.provider;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class JwtTokenProvider implements ClientRequestFilter {
    private String token;

    public JwtTokenProvider(String token) {
        this.token = token;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add("Authorization", "Bearer " + token);
    }
}
