package com.charlyghislain.plancul.security;

import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpBearerCredentialProvider implements HttpCredentialProvider {
    private static final String AUtHORIZATION_HEADER_NAME = "Authorization";

    @Override
    public Optional<Credential> extractCredential(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader(AUtHORIZATION_HEADER_NAME))
                .flatMap(this::parseAuthorizationHeader)
                .map(JwtTokenCredential::new);
    }

    private Optional<String> parseAuthorizationHeader(String headerValue) {
        boolean isBearer = headerValue.startsWith("Bearer");
        if (!isBearer) {
            return Optional.empty();
        }
        String tokenValue = headerValue.replaceFirst("^Bearer ", "");
        return Optional.of(tokenValue);
    }
}
