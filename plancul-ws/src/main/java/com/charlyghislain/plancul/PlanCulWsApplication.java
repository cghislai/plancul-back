package com.charlyghislain.plancul;


import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
public class PlanCulWsApplication extends ResourceConfig {

    public PlanCulWsApplication() {
        packages(getClass().getPackage().getName());
    }
}
