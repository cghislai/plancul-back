package com.charlyghislain.plancul.security.http;

import javax.security.enterprise.credential.BasicAuthenticationCredential;
import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpBasicCredentialProvider implements HttpCredentialProvider {
    private static final String AUtHORIZATION_HEADER_NAME = "Authorization";

    @Override
    public Optional<Credential> extractCredential(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader(AUtHORIZATION_HEADER_NAME))
                .flatMap(this::parseAuthorizationHeader)
                .map(BasicAuthenticationCredential::new);
    }

    private Optional<String> parseAuthorizationHeader(String headerValue) {
        boolean isBasic = headerValue.startsWith("Basic");
        if (!isBasic) {
            return Optional.empty();
        }
        String encodedCredential = headerValue.replaceFirst("^Basic ", "");
        return Optional.of(encodedCredential);
    }

}
