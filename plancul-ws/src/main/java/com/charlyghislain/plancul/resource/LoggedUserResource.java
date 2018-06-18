package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.TenantUserRoleConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.request.WsTenantUserRole;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/user/me")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoggedUserResource {

    @EJB
    private UserService userService;
    @Inject
    private UserConverter userConverter;
    @Inject
    private TenantUserRoleConverter tenantUserRoleConverter;

    @GET
    public WsUser getMyUser() {
        return this.userService.getLoggedUser()
                .map(userConverter::toWsEntity)
                .orElseThrow(ReferenceNotFoundException::new);
    }


    @GET
    @Path("/tenants")
    public List<WsTenantUserRole> getMyTenants() {
        return this.userService.getLoggedUserTenantsRoles()
                .stream()
                .map(tenantUserRoleConverter::toWsEntity)
                .collect(Collectors.toList());
    }
}
