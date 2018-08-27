package com.charlyghislain.plancul.domain.security;

import com.charlyghislain.authenticator.application.api.domain.AuthenticatorConstants;

public class ApplicationGroupNames {
    public static final String ADMIN = "ADMIN";
    public static final String UNREGISTERED_USER = "UNREGISTERED_USER";
    public static final String REGISTERED_USER = "REGISTERED_USER";
    public static final String TENANT_USER = "TENANT_USER";
    public static final String TENANT_MANAGER = "TENANT_MANAGER";
    public static final String AUTHENTICATOR = AuthenticatorConstants.AUTHENTICATOR_ROLE_APPLICATION;
}
