package com.charlyghislain.plancul.astronomy.ws;


import org.eclipse.microprofile.auth.LoginConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({AstronomyApplication.ROLE_USER})
public class AstronomyApplication extends ResourceConfig {

    public static final String ROLE_USER = "REGISTERED_USER";

    public AstronomyApplication() {
        packages(getClass().getPackage().getName());
    }

}