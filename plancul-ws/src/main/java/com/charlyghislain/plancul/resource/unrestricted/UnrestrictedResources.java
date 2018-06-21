package com.charlyghislain.plancul.resource.unrestricted;

import com.charlyghislain.plancul.domain.api.request.WsUserAccountInitRequest;
import com.charlyghislain.plancul.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/unrestricted")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UnrestrictedResources {


    @EJB
    private UserService userService;

    @POST
    @Path("/user/init")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String initUserAccount(WsUserAccountInitRequest initRequest) {
        String email = initRequest.getEmail();
        String password = initRequest.getPassword();
        String passwordToken = initRequest.getPasswordToken();

        String token = userService.activateUserAccount(email, password, passwordToken);
        return token;
    }

}
