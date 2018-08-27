package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsTenantUserRole;
import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.TenantUserRoleConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.converter.WsUserConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.UserQueryService;
import com.charlyghislain.plancul.service.UserUpdateService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/user/me")
@RolesAllowed({ApplicationGroupNames.REGISTERED_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class LoggedUserResource {

    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private UserQueryService userQueryService;

    @Inject
    private SecurityContext securityContext;
    @Inject
    private UserConverter userConverter;
    @Inject
    private WsUserConverter wsUserConverter;
    @Inject
    private TenantUserRoleConverter tenantUserRoleConverter;

    @GET
    public WsUser getMyUser() {
        return this.userQueryService.getLoggedUser()
                .map(wsUserConverter::toWsEntity)
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @PUT
    public WsRef<WsUser> updateUser(@NotNull @Valid WsUser wsUser) {
        User loggedUser = this.userQueryService.getLoggedUser()
                .orElseThrow(IllegalStateException::new);

        User useUpdate = userConverter.fromWsEntity(wsUser);
        try {
            User updatedUser = userUpdateService.updateLoggedUser(loggedUser, useUpdate);
            return wsUserConverter.reference(updatedUser);
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }

    @GET
    @Path("/tenants")
    public List<WsTenantUserRole> getMyTenants() {
        return this.userQueryService.getLoggedUserTenantsRoles()
                .stream()
                .map(tenantUserRoleConverter::toWsEntity)
                .collect(Collectors.toList());
    }

}
