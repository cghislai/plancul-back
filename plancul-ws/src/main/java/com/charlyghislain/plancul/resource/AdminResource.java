package com.charlyghislain.plancul.resource;


import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.converter.request.UserCreationRequestConverter;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.request.WsAdminAccountUpdateRequest;
import com.charlyghislain.plancul.domain.request.WsUserTenantCreationRequest;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.SecurityService;
import com.charlyghislain.plancul.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/admin")
@RolesAllowed({ApplicationGroupNames.ADMIN})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @EJB
    private SecurityService securityService;
    @EJB
    private UserService userService;
    @Inject
    private UserCreationRequestConverter userCreationRequestConverter;
    @Inject
    private UserConverter userConverter;

    @PUT
    @Path("/loginInfo")
    public void updateMyInfo(WsAdminAccountUpdateRequest updateRequest) {
        String userName = updateRequest.getUserName();
        String password = updateRequest.getPassword();
        securityService.updateMyLogin(userName);
        securityService.updateMyPassword(password);
    }

    @POST
    @Path("/tenant")
    public WsRef<WsUser> createTenant(@NotNull @Valid WsUserTenantCreationRequest wsUserTenantCreationRequest) {
        UserCreationRequest userCreationRequest = userCreationRequestConverter.fromWsUserTenantCreationRequest(wsUserTenantCreationRequest);
        userCreationRequest.setTenantRole(TenantRole.ADMIN);

        User createdUser = userService.createUser(userCreationRequest);

        WsRef<WsUser> reference = userConverter.reference(createdUser);
        return reference;
    }

}
