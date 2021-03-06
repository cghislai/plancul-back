package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientValidationErrorException;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRoleInvitation;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.InvalidTokenException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.exception.PlanCulException;
import com.charlyghislain.plancul.domain.exception.ValidationErrorException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.TenantUserRoleFilter;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import com.charlyghislain.plancul.domain.validation.ValidEmail;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class UserUpdateService {

    private final static Logger LOG = LoggerFactory.getLogger(UserUpdateService.class);

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
    private CommunicationService communicationService;
    @Inject
    private UserCreationQueue userCreationQueue;
    @Inject
    private TenantService tenantService;


    @Inject
    @Claim("uid")
    private ClaimValue<Long> jwtUidClaim;


    public User createUser(@NotNull User newUser, @NotNull AuthenticatorUser newAuthenticatorUser,
                           @NotNull String password) throws OperationNotAllowedException, InvalidTokenException, ValidationErrorException {
        return this.createUser(newUser, newAuthenticatorUser, password, null, null);
    }

    public User createUser(@NotNull User newUser, @NotNull AuthenticatorUser newAuthenticatorUser,
                           @NotNull String password, @Nullable String adminToken, @Nullable String tenantInvitationToken)
            throws InvalidTokenException, OperationNotAllowedException, ValidationErrorException {
        newAuthenticatorUser.setActive(false);

        boolean adminAccount = this.checkAdminAccountCreation(adminToken);
        Optional<TenantUserRoleInvitation> tenantUserRoleInvitation = this.checkTenantInvitationToken(tenantInvitationToken);
        String email = newAuthenticatorUser.getEmail();

        // We might be called back by authenticator while waiting for its response to the POST request.
        // As such, we need to persist a reference to the user being added to be able to correlate the added authenticator
        // user in the callback.
        newUser.setAdmin(adminAccount);
        newUser.setCreated(LocalDateTime.now());
        userCreationQueue.putUser(email, newUser);

//        newAuthenticatorUser.setActive(true);
        try {
            AuthenticatorUser createdAuthenticatorUser = authenticatorUserClient.createUser(newAuthenticatorUser, password);
            Long authenticatorUserId = createdAuthenticatorUser.getId();

            newUser.setAuthenticatorUid(authenticatorUserId);
            newUser.setAdmin(adminAccount);
            User savedUser = saveUser(newUser);

            tenantUserRoleInvitation.ifPresent(invitation -> this.createTenantRole(savedUser, invitation));
            this.initializeNewUserAccount(savedUser);
            if (adminAccount) {
                this.authenticatorUserClient.setUserActive(authenticatorUserId);
                this.applicationInitializationService.checkInitializationStatus();
            }
            return savedUser;
        } catch (AuthenticatorClientValidationErrorException validationError) {
            throw new ValidationErrorException(validationError.getViolationList());
        } catch (AuthenticatorClientError authenticatorClientError) {
            throw new OperationNotAllowedException(authenticatorClientError.getMessage());
        } finally {
            userCreationQueue.removeUser(email);
        }
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
        newUser.setCreated(LocalDateTime.now());
        User savedUser = saveUser(newUser);

        tenantUserRoleInvitation.ifPresent(invitation -> this.createTenantRole(savedUser, invitation));
        this.initializeNewUserAccount(savedUser);

        if (adminAccount) {
            this.authenticatorUserClient.setUserActive(loggedAuthenticatorUserId);
            this.applicationInitializationService.checkInitializationStatus();
        }

        return savedUser;
    }

    public void validateUserEmail(User user, String verificationToken) {
        Long authenticatorUid = user.getAuthenticatorUid();
        authenticatorUserClient.validateUserEmail(authenticatorUid, verificationToken);
    }

    public void sendNewPasswordResetToken(User user) throws PlanCulException {
        String passwordResetToken = authenticatorUserClient.createNewPasswordResetToken(user.getAuthenticatorUid());
        communicationService.sendPasswordResetToken(user, passwordResetToken);
    }


    public void resetUserPassword(User user, String resetToken, String password) throws OperationNotAllowedException, ValidationErrorException {
        Long authenticatorUid = user.getAuthenticatorUid();
        try {
            authenticatorUserClient.resetUserPassword(authenticatorUid, resetToken, password);
        } catch (AuthenticatorClientValidationErrorException validationError) {
            throw new ValidationErrorException(validationError.getViolationList());
        } catch (AuthenticatorClientError e) {
            throw new OperationNotAllowedException(e.getMessage());
        }
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


    public void removeUser(User user) {
        User managedUser = entityManager.merge(user);

        Long authenticatorUid = managedUser.getAuthenticatorUid();
        this.forgetAuthenticatorUser(authenticatorUid);

        List<TenantUserRole> tenantUserRoles = tenantUserRolesQueryService.findAllTenantUserRoles(managedUser);
        List<Tenant> distinctTenants = tenantUserRoles.stream()
                .map(TenantUserRole::getTenant)
                .distinct()
                .collect(Collectors.toList());
        tenantUserRoles.forEach(tenantUserRolesUpdateService::removeTenantUserRole);
        List<Tenant> tenantsWithoutUsers = distinctTenants.stream()
                .filter(this::isOrphanTenant)
                .collect(Collectors.toList());

        tenantsWithoutUsers.forEach(this.tenantService::removeTenant);
        entityManager.remove(managedUser);
    }

    public boolean isOrphanTenant(Tenant t) {
        TenantUserRoleFilter roleFilter = new TenantUserRoleFilter();
        roleFilter.setTenant(t);
        List<TenantUserRole> allTenantUserRoles = tenantUserRolesQueryService.findAllTenantUserRoles(roleFilter);
        return allTenantUserRoles.isEmpty();
    }

    private void forgetAuthenticatorUser(Long authenticatorUid) {
        if (authenticatorUid == null) {
            return;
        }
        try {
            AuthenticatorUser authenticatorUser = authenticatorUserClient.getUser(authenticatorUid);
            authenticatorUserClient.forgetUser(authenticatorUid);
        } catch (AuthenticatorClientError authenticatorClientError) {
            LOG.warn("Could not forget authenticator user " + authenticatorUid, authenticatorClientError);
        }
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
                .orElseThrow(() -> new InvalidTokenException("Invalid admin token"));
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


    private void initializeNewUserAccount(User savedUser) throws OperationNotAllowedException {
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

    private void setUserPassword(@NotNull String password, Long authenticatorUserId) throws OperationNotAllowedException {
        try {
            this.authenticatorUserClient.setUserPassword(authenticatorUserId, password);
        } catch (AuthenticatorClientError authenticatorClientError) {
            throw new OperationNotAllowedException("Could not set password");
        }
    }

    private User saveUser(@Valid User user) {
        user.setUpdated(LocalDateTime.now());
        User managedUser = entityManager.merge(user);
        return managedUser;
    }

}
