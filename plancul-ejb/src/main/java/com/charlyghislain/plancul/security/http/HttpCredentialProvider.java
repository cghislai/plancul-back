package com.charlyghislain.plancul.security.http;

import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface HttpCredentialProvider {

    Optional<Credential> extractCredential(HttpServletRequest servletRequest);
}
