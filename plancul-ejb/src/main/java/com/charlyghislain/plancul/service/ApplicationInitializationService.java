package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.request.filter.UserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ApplicationInitializationService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInitializationService.class);

    @Inject
    private UserQueryService userQueryService;

    private String adminToken;
    private boolean adminAccountInitialized;

    public void checkInitializationStatus() {
        checkAdminAccount();
    }

    private void checkAdminAccount() {
        UserFilter userFilter = new UserFilter();
        userFilter.setAdmin(true);
        adminAccountInitialized = checkAdminAccountInitialized(userFilter);
        if (!adminAccountInitialized && adminToken == null) {
            adminToken = UUID.randomUUID().toString();
            LOG.info("Your admin account initialization token:\n\n" + adminToken + "\n\n");
        }
    }

    public boolean isAdminAccountInitialized() {
        return this.adminAccountInitialized;
    }

    Optional<String> getAdminToken() {
        return Optional.ofNullable(adminToken);
    }


    private boolean checkAdminAccountInitialized(UserFilter userFilter) {
        return userQueryService.findUser(userFilter)
                .map(this::checkAdminUserExists)
                .orElse(false);
    }

    private boolean checkAdminUserExists(User adminUser) {
        Long authenticatorUid = adminUser.getAuthenticatorUid();
        boolean exists = userQueryService.findAuthenticatorUser(authenticatorUid).isPresent();
        if (!exists) {
            throw new RuntimeException("Could not find the admin user account at the authenticator endpoint");
        }
        return true;
    }

}
