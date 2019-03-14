package com.charlyghislain.plancul.ws.authenticator;


import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import org.eclipse.microprofile.auth.LoginConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
@DeclareRoles({ApplicationGroupNames.AUTHENTICATOR})
@LoginConfig(authMethod = "MP-JWT")
public class PlanCulAuthenticatorApplication extends ResourceConfig {

    public PlanCulAuthenticatorApplication() {
        registerClasses(
                AuthenticatorAuthorizationResource.class,
                AuthenticatorHealthResource.class,
                AuthenticatorUserEventResource.class
        );
    }

}
