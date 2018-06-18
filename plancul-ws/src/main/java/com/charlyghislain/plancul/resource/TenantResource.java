package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.TenantConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.converter.request.UserCreationRequestConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.request.WsUserCreationRequest;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.TenantService;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tenant")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TenantResource {

    @EJB
    private TenantService tenantService;
    @EJB
    private UserService userService;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private UserCreationRequestConverter userCreationRequestConverter;
    @Inject
    private UserConverter userConverter;

    @GET
    @Path("/{id}")
    public WsTenant getTenant(@PathParam("id") long id) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsTenant wsTenant = tenantConverter.toWsEntity(tenant);
        return wsTenant;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsTenant> updateTenant(@PathParam("id") long id, @NotNull @Valid WsTenant wsTenant) {
        Tenant tenant = tenantConverter.fromWsEntity(wsTenant);
        Tenant savedTenant = tenantService.saveTenant(tenant);
        WsRef<WsTenant> reference = tenantConverter.reference(savedTenant);
        return reference;
    }

    @POST
    @Path("/{id}/user")
    public WsRef<WsUser> addTenantUser(@PathParam("id") long id, @NotNull @Valid WsUserCreationRequest wsUserCreationRequest) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        UserCreationRequest userCreationRequest = userCreationRequestConverter.fromWsUserCreationRequest(wsUserCreationRequest, tenant);
        User user = userService.createUser(userCreationRequest);

        WsRef<WsUser> userWsRef = userConverter.reference(user);
        return userWsRef;
    }


}
