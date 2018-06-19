package com.charlyghislain.plancul;


import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("")
//@DeclareRoles({ApplicationGroupNames.ADMIN, ApplicationGroupNames.USER})
//@ServletSecurity(
//        value = @HttpConstraint(
//                rolesAllowed = ApplicationGroupNames.USER,
//                transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL,
//                value = ServletSecurity.EmptyRoleSemantic.DENY
//        ),
//        httpMethodConstraints = @HttpMethodConstraint(
//                value = HttpMethod.OPTIONS,
//                transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL,
//                emptyRoleSemantic = ServletSecurity.EmptyRoleSemantic.PERMIT
//        )
//)
public class PlanCulWsApplication extends ResourceConfig {

    public PlanCulWsApplication() {
        packages(getClass().getPackage().getName());
    }

}
