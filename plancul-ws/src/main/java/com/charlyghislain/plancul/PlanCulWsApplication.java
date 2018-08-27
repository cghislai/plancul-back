package com.charlyghislain.plancul;


import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import org.eclipse.microprofile.auth.LoginConfig;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
@DeclareRoles({ApplicationGroupNames.ADMIN, ApplicationGroupNames.TENANT_USER, ApplicationGroupNames.TENANT_MANAGER,
        ApplicationGroupNames.UNREGISTERED_USER, ApplicationGroupNames.AUTHENTICATOR})
@LoginConfig(authMethod = "MP-JWT")
public class PlanCulWsApplication extends ResourceConfig {

    public PlanCulWsApplication() {
        packages(getClass().getPackage().getName());
    }

}
