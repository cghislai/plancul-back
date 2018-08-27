package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import com.charlyghislain.plancul.domain.validation.ValidEmail;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Stateless
public class UserUpdateService {


    private static final String ACCOUNT_INIT_FRONTEND_PATH = "/account/init";
    private static final String ACCOUNT_INIT_UID_PARAM = "uid";
    private static final String ACCOUNT_INIT_TOKEN_PARAM = "token";

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private ApplicationInitializationService applicationInitializationService;
    @Inject
    private AuthenticatorUserClient authenticatorUserClient;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private TenantUserRolesUpdateService tenantUserRolesUpdateService;
    @Inject
    private TenantUserRolesQueryService tenantUserRolesQueryService;
    @Inject
    private TenantUserRoleInvitationUpdateService tenantUserRoleInvitationUpdateService;
    @Inject
    private TenantUserRoleInvitationQueryService tenantUserRoleInvitationQueryService;
    @Inject
    private TenantUpdateService tenantUpdateService;

    @Inject
    @Claim("uid")
    private ClaimValue<Long> jwtUidClaim;


    public User createUser(@NotNull User newUser, @NotNull AuthenticatorUser newAuthenticatorUser) throws OperationNotAllowedException, InvalidTokenException {
        return this.createUser(newUser, newAuthenticatorUser, null, null, null);
    }

    public User createUser(@NotNull User newUser, @NotNull AuthenticatorUser newAuthenticatorUser,
                           @NotNull String password) throws OperationNotAllowedException, InvalidTokenException {
        return this.createUser(newUser, newAuthenticatorUser, password, null, null);
    }

    public User createUser(@NotNull User newUser, @NotNull AuthenticatorUser newAuthenticatorUser,
                           @Nullable String password, @Nullable String adminToken, @Nullable String tenantInvitationToken) throws InvalidTokenException, OperationNotAllowedException {
        newAuthenticatorUser.setActive(false);

        boolean adminAccount = this.checkAdminAccountCreation(adminToken);
        Optional<TenantUserRoleInvitation> tenantUserRoleInvitation = this.checkTenantInvitationToken(tenantInvitationToken);


//        newAuthenticatorUser.setActive(true);
        AuthenticatorUser createdAuthenticatorUser = authenticatorUserClient.createUser(newAuthenticatorUser);
        Long authenticatorUserId = createdAuthenticatorUser.getId();

        newUser.setAuthenticatorUid(authenticatorUserId);
        newUser.setAdmin(adminAccount);
        User savedUser = saveUser(newUser);

        tenantUserRoleInvitation.ifPresent(invitation -> this.createTenantRole(savedUser, invitation));
        this.initializeNewUserAccount(savedUser);

        if (password != null) {
            this.authenticatorUserClient.setUserPassword(authenticatorUserId, password);
        }
        if (adminAccount) {
            this.authenticatorUserClient.setUserActive(authenticatorUserId);
            this.applicationInitializationService.checkInitializationStatus();
        }
        return savedUser;
    }

    public User registerNewUser(@NotNull User newUser, @Nullable String adminToken) throws InvalidTokenException, OperationNotAllowedException {
        return this.registerNewUser(newUser, adminToken, null);
    }

    public User registerNewUser(@NotNull User newUser, @Nullable String adminToken, @Nullable String tenantInvitationToken) throws InvalidTokenException, OperationNotAllowedException {
        AuthenticatorUser loggedAuthenticatorUser = userQueryService.getLoggedAuthenticatorUser();
        Long loggedAuthenticatorUserId = loggedAuthenticatorUser.getId();

        boolean adminAccount = this.checkAdminAccountCreation(adminToken);
        Optional<TenantUserRoleInvitation> tenantUserRoleInvitation = this.checkTenantInvitationToken(tenantInvitationToken);

        newUser.setAdmin(adminAccount);
        newUser.setAuthenticatorUid(loggedAuthenticatorUserId);
        User savedUser = saveUser(newUser);

        tenantUserRoleInvitation.ifPresent(invitation -> this.createTenantRole(savedUser, invitation));
        this.initializeNewUserAccount(savedUser);

        if (adminAccount) {
            this.authenticatorUserClient.setUserActive(loggedAuthenticatorUserId);
            this.applicationInitializationService.checkInitializationStatus();
        }

        return savedUser;
    }


    public User updateLoggedUser(@NotNull User existingUser, @NotNull User userUpdate) throws OperationNotAllowedException {
        userQueryService.getLoggedUser()
                .filter(existingUser::equals)
                .orElseThrow(OperationNotAllowedException::new);
        Language language = userUpdate.getLanguage();
        String firstName = userUpdate.getFirstName();
        String lastName = userUpdate.getLastName();

        existingUser.setLanguage(language);
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        return saveUser(existingUser);
    }

    private boolean checkAdminAccountCreation(@Nullable String token) throws InvalidTokenException, OperationNotAllowedException {
        if (token != null) {
            checkAdminTokenIsValid(token);
            return true;
        } else {
            this.checkAdminAccountInitialized();
            return false;
        }
    }


    private Optional<TenantUserRoleInvitation> checkTenantInvitationToken(@Nullable String tenantInvitationToken) throws InvalidTokenException {
        if (tenantInvitationToken != null) {
            TenantUserRoleInvitation invitation = this.tenantUserRoleInvitationQueryService.validateTenantUserRoleInvitationToken(tenantInvitationToken);
            return Optional.of(invitation);
        } else {
            return Optional.empty();
        }
    }


    private void checkAdminTokenIsValid(@NotNull String adminToken) throws InvalidTokenException {
        applicationInitializationService.getAdminToken()
                .filter(adminToken::equals)
                .orElseThrow(InvalidTokenException::new);
    }

    private void checkAdminAccountInitialized() throws OperationNotAllowedException {
        boolean adminAccountInitialized = applicationInitializationService.isAdminAccountInitialized();
        if (!adminAccountInitialized) {
            throw new OperationNotAllowedException();
        }
    }

    private void createTenantRole(User user, TenantUserRoleInvitation tenantUserRoleInvitation) {
        try {
            tenantUserRolesUpdateService.createTenantUserTole(user, tenantUserRoleInvitation);
        } catch (InvalidTokenException e) {
            throw new IllegalStateException(e);
        }
    }


    private void initializeNewUserAccount(User savedUser) {
        boolean hasNoTenant = tenantUserRolesQueryService.findAllTenantUserRoles(savedUser).isEmpty();
        if (hasNoTenant) {
            String newTenantName = savedUser.getFirstName() + " " + savedUser.getLastName();

            Tenant tenant = new Tenant();
            tenant.setName(newTenantName);
            Tenant managedTenant = tenantUpdateService.createTenant(tenant);

            tenantUserRolesUpdateService.createTenantUserRole(savedUser, managedTenant, TenantRole.ADMIN);
        }
    }


    public void inviteUser(String email) {
        Optional<User> registeredUser = authenticatorUserClient.findUserUserWithMail(email)
                .map(AuthenticatorUser::getId)
                .flatMap(userQueryService::findUserByAuthenticatorUid);

        if (registeredUser.isPresent()) {
            // sendInvitationToRegisteredUser()
        } else {
            // sendInvitationToNewUser
        }
    }

    public void inviteUser(@ValidEmail String email, Tenant tenant, TenantRole tenantRole) {
        Optional<User> registeredUser = authenticatorUserClient.findUserUserWithMail(email)
                .map(AuthenticatorUser::getId)
                .flatMap(userQueryService::findUserByAuthenticatorUid);

        if (registeredUser.isPresent()) {
            User user = registeredUser.get();
            TenantUserRole tenantUserRole = tenantUserRolesUpdateService.createTenantUserRole(user, tenant, tenantRole);
            // sendNotificationNewTenantAdded
        } else {
            TenantUserRoleInvitation invitation = tenantUserRoleInvitationUpdateService.createInvitation(tenant, tenantRole);
            // sendInvitationToNewUser
        }
    }

    private User saveUser(@Valid User user) {
        User managedUser = entityManager.merge(user);
        return managedUser;
    }

}