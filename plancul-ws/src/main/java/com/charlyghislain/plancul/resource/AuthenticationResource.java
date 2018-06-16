package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.security.JwtService;
import com.charlyghislain.plancul.service.SecurityService;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @Inject
    private SecurityContext securityContext;
    @Inject
    private JwtService jwtService;

    @EJB
    private SecurityService securityService;

    @POST
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    public String createJwtToken() {
        Principal callerPrincipal = securityContext.getCallerPrincipal();
        if (callerPrincipal == null) {
            throw new NotAuthorizedException("Not authenticated");
        }
        String callerName = callerPrincipal.getName();
        Caller caller = securityService.findCallerByName(callerName)
                .orElseThrow(IllegalStateException::new);
        String jwtToken = jwtService.createJwtForCallerName(caller);
        return jwtToken;
    }
}
