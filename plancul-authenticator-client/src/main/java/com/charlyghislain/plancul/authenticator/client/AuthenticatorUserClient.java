package com.charlyghislain.plancul.authenticator.client;

import com.charlyghislain.authenticator.management.api.UserResource;
import com.charlyghislain.authenticator.management.api.domain.*;
import com.charlyghislain.plancul.authenticator.client.converter.AuthenticatorUserConverter;
import com.charlyghislain.plancul.authenticator.client.converter.WsApplicationUserConverter;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.provider.ClientErrorsHidden;
import com.charlyghislain.plancul.authenticator.client.provider.JwtTokenProvider;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.config.ConfigConstants;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@ApplicationScoped
@ClientErrorsHidden
public class AuthenticatorUserClient {

    private UserResource userResource;

    @Inject
    private AuthenticatorUserConverter authenticatorUserConverter;
    @Inject
    private WsApplicationUserConverter wsApplicationUserConverter;
    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_MANAGEMENT_API_SECRET)
    private String secretToken;
    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_MANAGEMENT_API_URL)
    private String apiUrl;

    @PostConstruct
    public void init() {
        try {
            this.userResource = RestClientBuilder.newBuilder()
                    .register(new JwtTokenProvider(secretToken))
                    .baseUrl(new URL(apiUrl))
                    .build(UserResource.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticatorUser createUser(AuthenticatorUser authenticatorUser, String password) throws AuthenticatorClientError {
        WsApplicationUserWithPassword wsApplicationUser = wsApplicationUserConverter.toWsApplicationUser(authenticatorUser, password);
        WsApplicationUser createdUser = userResource.createUser(wsApplicationUser);

        return authenticatorUserConverter.toAuthenticatorUser(createdUser);
    }


    public AuthenticatorUser setUserActive(Long userId) {
        WsApplicationUser wsApplicationUser = userResource.getUser(userId);
        wsApplicationUser.setActive(true);
        WsApplicationUser updatedUser = userResource.updateUser(userId, wsApplicationUser);
        return this.authenticatorUserConverter.toAuthenticatorUser(updatedUser);
    }


    public String createNewPasswordResetToken(Long userId) {
        WsPasswordResetToken newPasswordResetToken = userResource.createNewPasswordResetToken(userId);
        String token = newPasswordResetToken.getToken();
        return token;
    }


    public void resetUserPassword(Long userId, String resetToken, String password) {
        WsPasswordReset wsPasswordReset = new WsPasswordReset();
        wsPasswordReset.setPassword(password);
        wsPasswordReset.setResetToken(resetToken);
        userResource.resetUserPassword(userId, wsPasswordReset);
    }

    public String createNewEmailVerificationToken(User user) {
        Long authenticatorUid = user.getAuthenticatorUid();
        WsEmailVerificationToken emailVerificationToken = userResource.getEmailVerificationToken(authenticatorUid);
        String token = emailVerificationToken.getToken();
        return token;
    }

    public void validateUserEmail(Long userId, String verificationToken) {
        userResource.checkEmailVerification(userId, verificationToken);
    }

    public AuthenticatorUser setUserPassword(Long userId, String password) throws AuthenticatorClientError {
        try {
            WsApplicationUser wsApplicationUser = userResource.updateUserPassword(userId, password);
            return authenticatorUserConverter.toAuthenticatorUser(wsApplicationUser);
        } catch (WebApplicationException e) {
            throw new AuthenticatorClientError(e);
        }
    }

    public AuthenticatorUser getUser(long id) throws AuthenticatorClientError {
        WsApplicationUser wsApplicationUser = userResource.getUser(id);
        return authenticatorUserConverter.toAuthenticatorUser(wsApplicationUser);
    }

    public Optional<AuthenticatorUser> findUserUserWithMail(String email) {
        WsUserApplicationFilter wsUserApplicationFilter = new WsUserApplicationFilter();
        wsUserApplicationFilter.setUserEmail(email);
        WsPagination wsPagination = new WsPagination(1);
        WsResultList<WsApplicationUser> resultList = userResource.listUsers(wsUserApplicationFilter, wsPagination);
        return resultList.getResults().stream()
                .findFirst()
                .map(authenticatorUserConverter::toAuthenticatorUser);
    }

}
