package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.request.WsPasswordReset;
import com.charlyghislain.plancul.api.domain.request.WsUserEmailVerification;
import com.charlyghislain.plancul.api.domain.request.WsUserRegistration;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.converter.AuthenticatorUserConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.converter.UserConverter;
import com.charlyghislain.plancul.converter.WsUserConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.exception.PlanCulException;
import com.charlyghislain.plancul.domain.exception.ValidationErrorException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.domain.request.filter.UserFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import com.charlyghislain.plancul.service.UserQueryService;
import com.charlyghislain.plancul.service.UserUpdateService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.exception.InvalidPasswordException;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;
import com.charlyghislain.plancul.util.exception.WsValidationException;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
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
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @Inject
    @Claim("uid")
    private ClaimValue<Long> jwtUidClaim;

    @POST
    @Path("/new")
    @PermitAll
    public WsUser createNewUser(@Valid WsUserRegistration wsUserRegistration) {
        AuthenticatorUser authenticatorUser = authenticatorUserConverter.toAuthenticatorUser(wsUserRegistration)
                .orElseGet(this::getLoggedUser);
        String password = Optional.ofNullable(wsUserRegistration.getPassword())
                .orElseThrow(InvalidPasswordException::new);
        Optional<String> adminToken = Optional.ofNullable(wsUserRegistration.getAdminToken());
        Optional<String> tenantInvitationToken = Optional.ofNullable(wsUserRegistration.getTenantInvitationToken());

        User user = userConverter.fromWsEntity(wsUserRegistration.getUser());

        try {
            User createdUser = userUpdateService.createUser(user, authenticatorUser, password,
                    adminToken.orElse(null), tenantInvitationToken.orElse(null));

            return wsUserConverter.toWsEntity(createdUser);
        } catch (InvalidTokenException | OperationNotAllowedException e) {
            throw new WsException(Response.Status.NOT_ACCEPTABLE, e.getMessage());
        } catch (ValidationErrorException e) {
            throw new WsValidationException(e.getViolationList());
        }
    }

    @POST
    @Path("/email/verification")
    @PermitAll
    public void verifyUserEmail(@Valid WsUserEmailVerification wsUserEmailVerification) {
        User user = Optional.ofNullable(wsUserEmailVerification.getEmail())
                .flatMap(userQueryService::findAuthenticatorUserByEmail)
                .map(AuthenticatorUser::getId)
                .flatMap(userQueryService::findUserByAuthenticatorUid)
                .orElseThrow(ReferenceNotFoundException::new);
        String verificationToken = wsUserEmailVerification.getVerificationToken();
        userUpdateService.validateUserEmail(user, verificationToken);
    }

    @POST
    @Path("/password/resetToken")
    @PermitAll
    @Consumes(MediaType.TEXT_PLAIN)
    public void createNewPasswordResetToken(@NotNull String email) {
        User user = userQueryService.findAuthenticatorUserByEmail(email)
                .map(AuthenticatorUser::getId)
                .flatMap(userQueryService::findUserByAuthenticatorUid)
                .orElseThrow(ReferenceNotFoundException::new);
        try {
            userUpdateService.sendNewPasswordResetToken(user);
        } catch (PlanCulException e) {
            throw new WsException(Response.Status.INTERNAL_SERVER_ERROR, "Password reset message could not be sent");
        }
    }

    @POST
    @Path("/password/reset")
    @PermitAll
    public void resetPassword(@Valid WsPasswordReset wsPasswordReset) {
        String email = wsPasswordReset.getEmail();
        String resetToken = wsPasswordReset.getResetToken();
        String password = wsPasswordReset.getPassword();

        User user = userQueryService.findAuthenticatorUserByEmail(email)
                .map(AuthenticatorUser::getId)
                .flatMap(userQueryService::findUserByAuthenticatorUid)
                .orElseThrow(ReferenceNotFoundException::new);

        userUpdateService.resetUserPassword(user, resetToken, password);
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


    @POST
    @RolesAllowed(ApplicationGroupNames.ADMIN)
    @Path("/search")
    public WsSearchResult<WsUser> searchUsers() {
        UserFilter userFilter = new UserFilter();
        SearchResult<User> userResults = userQueryService.findUsers(userFilter, pagination, acceptedLanguage);
        WsSearchResult<WsUser> wsSearchResult = searchResultConverter.convertSearchResults(userResults, wsUserConverter);
        return wsSearchResult;
    }

    @DELETE
    @RolesAllowed(ApplicationGroupNames.ADMIN)
    @Path("/{id}")
    public void deleteUser(@PathParam("id") long id) {
        User user = userQueryService.findUserById(id)
                .orElseThrow(ReferenceNotFoundException::new);
        userUpdateService.removeUser(user);
    }

    private AuthenticatorUser getLoggedUser() {
        return userQueryService.getLoggedAuthenticatorUser();
    }

}
