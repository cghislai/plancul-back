package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.TenantUserRoleConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.WsTenantUserRole;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.security.JwtService;
import com.charlyghislain.plancul.service.SecurityService;
import com.charlyghislain.plancul.service.UserService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/user/me")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoggedUserResource {

    @EJB
    private UserService userService;
    @EJB
    private SecurityService securityService;

    @Inject
    private SecurityContext securityContext;
    @Inject
    private JwtService jwtService;
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

    @PUT
    public WsRef<WsUser> updateUser(@NotNull @Valid WsUser wsUser) {
        User loggedUser = this.userService.getLoggedUser()
                .orElseThrow(IllegalStateException::new);

        userConverter.updateEntity(loggedUser, wsUser);
        User savedUser = userService.saveUser(loggedUser);

        WsRef<WsUser> reference = userConverter.reference(savedUser);
        return reference;
    }

    @GET
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createJwtToken() {
        Caller caller = securityService.findLoggedCaller();
        String jwtToken = jwtService.createJwtForCallerName(caller);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        return Response.ok(jwtToken)
                .cacheControl(cacheControl)
                .build();
    }

    @PUT
    @Path("/password")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updatePassword(@NotNull String password) {
        securityService.updateMyPassword(password);
    }

    @GET
    @Path("/password/expired")
    public boolean isMyPasswordExpired() {
        return securityService.doesMyPasswordNeedUpdate();
    }

    @GET
    @Path("/tenants")
    public List<WsTenantUserRole> getMyTenants() {
        return this.userService.getLoggedUserTenantsRoles()
                .stream()
                .map(tenantUserRoleConverter::toWsEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/role/admin")
    public boolean amIAdmin() {
        return securityContext.isCallerInRole(ApplicationGroup.ADMIN.name());
    }

    @GET
    @Path("/role/user")
    public boolean amIUser() {
        return securityContext.isCallerInRole(ApplicationGroup.USER.name());
    }

    @GET
    @Path("/role/anonymous")
    public boolean amIAnonymous() {
        return securityContext.isCallerInRole(ApplicationGroup.ANONYMOUS.name());
    }
}
