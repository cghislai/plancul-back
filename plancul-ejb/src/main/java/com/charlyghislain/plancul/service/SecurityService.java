package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.security.CallerGroups;
import com.charlyghislain.plancul.domain.security.CallerGroups_;
import com.charlyghislain.plancul.domain.security.Caller_;
import com.charlyghislain.plancul.domain.security.Group;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Stateless
public class SecurityService {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

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

    public Set<Group> findCallerGroups(Caller caller) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Group> query = criteriaBuilder.createQuery(Group.class);
        Root<CallerGroups> callerGroupsRoot = query.from(CallerGroups.class);

        Path<Caller> callerPath = callerGroupsRoot.get(CallerGroups_.caller);
        Predicate callerPredicate = criteriaBuilder.equal(callerPath, caller);
        Path<Group> groupPath = callerGroupsRoot.get(CallerGroups_.group);

        query.select(groupPath);
        query.where(callerPredicate);

        TypedQuery<Group> typedQuery = entityManager.createQuery(query);
        return new HashSet<>(typedQuery.getResultList());
    }


    public boolean isValidCallerPassword(Caller caller, char[] password) {
        return passwordHash.verify(password, caller.getPassword());
    }

    public void createDefaultAccounts() {
        Optional<Caller> adminCaller = this.findCallerByName(DEFAULT_ADMIN_USERNAME);
        if (!adminCaller.isPresent()) {
            String hashedPassword = passwordHash.generate(DEFAULT_ADMIN_PASSWORD.toCharArray());
            this.createCaller(DEFAULT_ADMIN_USERNAME, hashedPassword, Group.ADMIN, Group.USER);
        }
    }


    private Caller createCaller(String name, String hashedPassword, Group... groups) {
        Caller caller = new Caller();
        caller.setName(name);
        caller.setPassword(hashedPassword);
        Caller managedCaller = entityManager.merge(caller);

        Arrays.stream(groups)
                .forEach(group -> this.createCallerGroup(managedCaller, group));
        return managedCaller;
    }

    private void createCallerGroup(Caller caller, Group group) {
        CallerGroups callerGroups = new CallerGroups();
        callerGroups.setCaller(caller);
        callerGroups.setGroup(group);
        entityManager.merge(callerGroups);
    }

}
