package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.security.CallerGroups;
import com.charlyghislain.plancul.domain.security.CallerGroups_;
import com.charlyghislain.plancul.domain.security.Caller_;
import com.charlyghislain.plancul.domain.util.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.security.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.validation.ValidPassword;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Stateless
public class SecurityService {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final int PASSWORD_RESET_TOKEN_AVAILABILITY_DAYS = 7;

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;
    @Inject
    private PasswordHash passwordHash;
    @Inject
    private SecurityContext securityContext;


    public Caller findLoggedCaller() {
        String callerName = securityContext.getCallerPrincipal().getName();
        return this.findCallerByName(callerName)
                .orElseThrow(IllegalStateException::new);
    }

    public boolean isAdminLogged() {
        return securityContext.isCallerInRole(ApplicationGroup.ADMIN.name());
    }

    public Optional<Caller> findCallerByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Caller> query = criteriaBuilder.createQuery(Caller.class);
        Root<Caller> callerRoot = query.from(Caller.class);

        Path<String> namePath = callerRoot.get(Caller_.name);
        Predicate namePredicate = criteriaBuilder.equal(namePath, name);

        Path<Boolean> activePath = callerRoot.get(Caller_.active);
        Predicate activePredicate = criteriaBuilder.equal(activePath, true);

        query.where(namePredicate, activePredicate);

        TypedQuery<Caller> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().findFirst();
    }


    public boolean doesMyPasswordNeedUpdate() {
        Caller loggedCaller = this.findLoggedCaller();
        return loggedCaller.isPasswordNeedsChange();
    }

    public Caller updateMyPassword(@ValidPassword String clearTextPassword) {
        Caller loggedCaller = this.findLoggedCaller();

        Caller mergedCaller = updateCallerPassword(clearTextPassword, loggedCaller);
        return mergedCaller;
    }

    public Caller updatePassword(@NotNull String callerName, @ValidPassword String clearTextPassword, @NotNull String updateToken) {
        Caller caller = this.findCallerByNameAndPasswordResetToken(callerName, updateToken)
                .orElseThrow(OperationNotAllowedException::new);

        LocalDateTime passwordResetTokenExpiration = caller.getPasswordResetTokenExpiration();

        if (passwordResetTokenExpiration != null && passwordResetTokenExpiration.isBefore(LocalDateTime.now())) {
            throw new PlanCulRuntimeException("This password reset token has expired", HttpStatus.SC_PRECONDITION_FAILED);
        }

        Caller mergedCaller = updateCallerPassword(clearTextPassword, caller);
        return mergedCaller;
    }

    public void checkUserMailDidNotChange(@Valid User user) {
        Caller caller = user.getCaller();
        String email = user.getEmail();

        String callerName = caller.getName();
        if (!callerName.equals(email)) {
            throw new PlanCulRuntimeException("User mail cannot be changed");
        }
    }

    public Set<ApplicationGroup> findCallerGroups(Caller caller) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationGroup> query = criteriaBuilder.createQuery(ApplicationGroup.class);
        Root<CallerGroups> callerGroupsRoot = query.from(CallerGroups.class);

        Path<Caller> callerPath = callerGroupsRoot.get(CallerGroups_.caller);
        Predicate callerPredicate = criteriaBuilder.equal(callerPath, caller);
        Path<ApplicationGroup> groupPath = callerGroupsRoot.get(CallerGroups_.group);

        query.select(groupPath);
        query.where(callerPredicate);

        TypedQuery<ApplicationGroup> typedQuery = entityManager.createQuery(query);
        List<ApplicationGroup> resultList = typedQuery.getResultList();
        return new HashSet<>(resultList);
    }

    public boolean isValidCallerPasswordHash(Caller caller, char[] password) {
        String storedCallerPassword = caller.getPassword();
        if (storedCallerPassword == null || storedCallerPassword.isEmpty()) {
            return false;
        }
        return passwordHash.verify(password, storedCallerPassword);
    }

    @RolesAllowed(ApplicationGroupNames.ADMIN)
    public void createAdminAccount(String username, String password) {
        String passwordHash = this.passwordHash.generate(password.toCharArray());
        this.createCallerWithPassword(username, passwordHash, ApplicationGroup.ADMIN);

        this.deactivateDefaultAdminAccount();
    }

    public void createDefaultAccounts() {
        Optional<Caller> activeAdminCaller = this.findActiveAdminCaller();
        if (!activeAdminCaller.isPresent()) {
            String hashedPassword = this.createNewAdminHashedPassword();
            Caller newAdminCaller = this.createCallerWithPassword(DEFAULT_ADMIN_USERNAME, hashedPassword, ApplicationGroup.ADMIN);
            newAdminCaller.setPasswordNeedsChange(true);
        }
    }

    public Caller createNewCaller(String name) {
        return this.createCaller(name, ApplicationGroup.USER);
    }

    private String createNewRandomToken() {
        byte[] pwBytes = new byte[32];
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(pwBytes);
        } catch (NoSuchAlgorithmException e) {
            new Random(System.currentTimeMillis())
                    .nextBytes(pwBytes);
        }
        byte[] encodedBytes = Base64.getEncoder().encode(pwBytes);
        try {
            String token = new String(encodedBytes, "UTF-8");
            return token;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode a base64 token", e);
        }
    }

    private String createNewAdminHashedPassword() {
        String plainPassword = this.createNewRandomToken();
        this.logDefaultAdminPassword(plainPassword);
        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());
        return hashedPassword;
    }

    private void logDefaultAdminPassword(String plainPassword) {
        LOG.info("\n" +
                "=========== Your admin password ===============\n" +
                "\n" +
                plainPassword +
                "\n" +
                "===============================================\n" +
                "Use it to login with the default '" + DEFAULT_ADMIN_USERNAME + "' user.");
    }


    private Caller createCallerWithPassword(String name, String hashedPassword, ApplicationGroup... groups) {
        Caller caller = new Caller();
        caller.setName(name);
        caller.setPassword(hashedPassword);
        caller.setActive(true);
        Caller managedCaller = entityManager.merge(caller);

        Arrays.stream(groups)
                .forEach(group -> this.createCallerGroup(managedCaller, group));
        return managedCaller;
    }

    private Caller createCaller(String name, ApplicationGroup... groups) {
        String passwordResetToken = this.createNewRandomToken();
        LocalDateTime passwordResetTokenExpiration = this.getPasswordResetTokenExpiration();

        Caller caller = new Caller();
        caller.setName(name);
        caller.setPasswordResetToken(passwordResetToken);
        caller.setPasswordResetTokenExpiration(passwordResetTokenExpiration);
        caller.setPasswordNeedsChange(true);
        caller.setActive(false);
        Caller managedCaller = entityManager.merge(caller);

        Arrays.stream(groups)
                .forEach(group -> this.createCallerGroup(managedCaller, group));
        entityManager.flush();
        return managedCaller;
    }


    private void createCallerGroup(Caller caller, ApplicationGroup group) {
        CallerGroups callerGroups = new CallerGroups();
        callerGroups.setCaller(caller);
        callerGroups.setGroup(group);
        entityManager.merge(callerGroups);
    }


    private Caller updateCallerPassword(String clearTextPassword, Caller caller) {
        String hashedPassword = passwordHash.generate(clearTextPassword.toCharArray());
        caller.setPassword(hashedPassword);
        caller.setPasswordNeedsChange(false);
        caller.setPasswordResetToken(null);
        caller.setActive(true);

        return entityManager.merge(caller);
    }


    private LocalDateTime getPasswordResetTokenExpiration() {
        return LocalDateTime.now()
                .plusDays(PASSWORD_RESET_TOKEN_AVAILABILITY_DAYS);
    }

    private Optional<Caller> findCallerByNameAndPasswordResetToken(String name, String resetToken) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Caller> query = criteriaBuilder.createQuery(Caller.class);
        Root<Caller> callerRoot = query.from(Caller.class);

        Path<String> namePath = callerRoot.get(Caller_.name);
        Predicate namePredicate = criteriaBuilder.equal(namePath, name);

        Path<Boolean> activePath = callerRoot.get(Caller_.active);
        Predicate activePredicate = criteriaBuilder.equal(activePath, false);

        Path<String> tokenPath = callerRoot.get(Caller_.passwordResetToken);
        Predicate tokenpredicate = criteriaBuilder.equal(tokenPath, resetToken);

        query.where(namePredicate, activePredicate, tokenpredicate);

        TypedQuery<Caller> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().findFirst();
    }

    private Optional<Caller> findActiveAdminCaller() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Caller> query = criteriaBuilder.createQuery(Caller.class);
        Root<CallerGroups> groupsRoot = query.from(CallerGroups.class);

        Path<ApplicationGroup> groupPath = groupsRoot.get(CallerGroups_.group);
        Predicate adminPredicate = criteriaBuilder.equal(groupPath, ApplicationGroup.ADMIN);

        Path<Caller> callerPath = groupsRoot.get(CallerGroups_.caller);

        Path<Boolean> activePath = callerPath.get(Caller_.active);
        Predicate activePredicate = criteriaBuilder.equal(activePath, true);

        query.select(callerPath);
        query.where(adminPredicate, activePredicate);

        TypedQuery<Caller> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().findFirst();
    }


    private void deactivateDefaultAdminAccount() {
        this.findCallerByName(DEFAULT_ADMIN_USERNAME)
                .ifPresent(this::deactivateAccount);
    }

    private void deactivateAccount(Caller caller) {
        caller.setActive(false);
        entityManager.merge(caller);
    }
}
