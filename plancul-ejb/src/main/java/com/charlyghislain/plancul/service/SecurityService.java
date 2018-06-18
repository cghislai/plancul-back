package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.security.CallerGroups;
import com.charlyghislain.plancul.domain.security.CallerGroups_;
import com.charlyghislain.plancul.domain.security.Caller_;
import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.security.enterprise.identitystore.PasswordHash;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Stateless
public class SecurityService {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    private static final String DEFAULT_ADMIN_USERNAME = "admin";

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;
    @Inject
    private PasswordHash passwordHash;


    public Optional<Caller> findCallerByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Caller> query = criteriaBuilder.createQuery(Caller.class);
        Root<Caller> callerRoot = query.from(Caller.class);

        Path<String> namePath = callerRoot.get(Caller_.name);
        Predicate namePredicate = criteriaBuilder.equal(namePath, name);

        query.where(namePredicate);

        TypedQuery<Caller> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream().findFirst();
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
        return new HashSet<>(typedQuery.getResultList());
    }


    public boolean isValidCallerPassword(Caller caller, char[] password) {
        String storedCallerPassword = caller.getPassword();
        if (storedCallerPassword == null || storedCallerPassword.isEmpty()) {
            return false;
        }
        return passwordHash.verify(password, storedCallerPassword);
    }

    public void createDefaultAccounts() {
        Optional<Caller> adminCaller = this.findCallerByName(DEFAULT_ADMIN_USERNAME);
        if (!adminCaller.isPresent()) {
            String hashedPassword = this.createNewAdminHashedPassword();
            Caller newAdminCaller = this.createCaller(DEFAULT_ADMIN_USERNAME, hashedPassword, ApplicationGroup.ADMIN, ApplicationGroup.USER);
            newAdminCaller.setPasswordNeedsChange(true);
        }
    }


    public Caller createNewCaller(String name, String clearTextPassword) {
        String hashedPassword = passwordHash.generate(clearTextPassword.toCharArray());
        return this.createCaller(name, hashedPassword, ApplicationGroup.USER);
    }

    private String createNewAdminHashedPassword() {
        byte[] pwBytes = new byte[32];
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(pwBytes);
        } catch (NoSuchAlgorithmException e) {
            new Random(System.currentTimeMillis())
                    .nextBytes(pwBytes);
        }
        byte[] encodedBytes = Base64.getEncoder().encode(pwBytes);
        try {
            String plainPassword = new String(encodedBytes, "UTF-8");
            this.logDefaultAdminPassword(plainPassword);
            String hashedPassword = passwordHash.generate(plainPassword.toCharArray());
            return hashedPassword;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode a default admin password", e);
        }
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


    private Caller createCaller(String name, String hashedPassword, ApplicationGroup... groups) {
        Caller caller = new Caller();
        caller.setName(name);
        caller.setPassword(hashedPassword);
        Caller managedCaller = entityManager.merge(caller);

        Arrays.stream(groups)
                .forEach(group -> this.createCallerGroup(managedCaller, group));
        return managedCaller;
    }

    private void createCallerGroup(Caller caller, ApplicationGroup group) {
        CallerGroups callerGroups = new CallerGroups();
        callerGroups.setCaller(caller);
        callerGroups.setGroup(group);
        entityManager.merge(callerGroups);
    }

}
