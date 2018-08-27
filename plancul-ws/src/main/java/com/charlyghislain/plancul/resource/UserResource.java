package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.request.WsUserRegistration;
import com.charlyghislain.plancul.converter.AuthenticatorUserConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.converter.WsUserConverter;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import com.charlyghislain.plancul.service.UserQueryService;
import com.charlyghislain.plancul.service.UserUpdateService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/user")
@RolesAllowed({ApplicationGroupNames.REGISTERED_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;

    @Inject
    private UserConverter userConverter;
    @Inject
    private WsUserConverter wsUserConverter;
    @Inject
    private AuthenticatorUserConverter authenticatorUserConverter;

    @Inject
    @Claim("uid")
    private ClaimValue<Long> jwtUidClaim;

    @POST
    @Path("/new")
    @PermitAll
    public WsUser createNewUser(@Valid WsUserRegistration wsUserRegistration) {
        AuthenticatorUser authenticatorUser = authenticatorUserConverter.toAuthenticatorUser(wsUserRegistration)
                .orElseGet(this::getLoggedUser);
        Optional<String> adminToken = Optional.ofNullable(wsUserRegistration.getAdminToken());
        Optional<String> password = Optional.ofNullable(wsUserRegistration.getPassword());
        Optional<String> tenantInvitationToken = Optional.ofNullable(wsUserRegistration.getTenantInvitationToken());

        User user = userConverter.fromWsEntity(wsUserRegistration.getUser());

        try {
            User createdUser = userUpdateService.createUser(user, authenticatorUser,
                    password.orElse(null), adminToken.orElse(null), tenantInvitationToken.orElse(null));

            return wsUserConverter.toWsEntity(createdUser);
        } catch (InvalidTokenException | OperationNotAllowedException e) {
            throw new WsException(Response.Status.NOT_ACCEPTABLE);
        }
    }

    @POST
    @Path("/register")
    @RolesAllowed(ApplicationGroupNames.UNREGISTERED_USER)
    public WsUser registerNewUser(@Valid WsUserRegistration wsUserRegistration) {
        Optional<String> adminToken = Optional.ofNullable(wsUserRegistration.getAdminToken());
        Optional<String> tenantInvitationToken = Optional.ofNullable(wsUserRegistration.getTenantInvitationToken());

        User user = userConverter.fromWsEntity(wsUserRegistration.getUser());

        try {
            User createdUser = userUpdateService.registerNewUser(user,
                    adminToken.orElse(null), tenantInvitationToken.orElse(null));

            return wsUserConverter.toWsEntity(createdUser);
        } catch (InvalidTokenException | OperationNotAllowedException e) {
            throw new WsException(Response.Status.NOT_ACCEPTABLE);
        }
    }

    @GET
    @Path("/{id}")
    public WsUser getUser(@PathParam("id") long id) {
        User user = userQueryService.findUserById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsUser wsUser = wsUserConverter.toWsEntity(user);
        return wsUser;
    }

    private AuthenticatorUser getLoggedUser() {
        return userQueryService.getLoggedAuthenticatorUser();
    }

}
