package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRole_;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.User_;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.security.exception.OperationNotAllowedException;

import javax.ejb.EJB;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserService {


    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SecurityContext securityContext;
    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;
    @EJB
    private TenantService tenantService;
    @EJB
    private SecurityService securityService;

    public Optional<User> findUserById(long id) {
        User found = entityManager.find(User.class, id);
        return Optional.ofNullable(found);
    }

    public User createUser(UserCreationRequest userCreationRequest) {
        Tenant tenant = userCreationRequest.getTenant();
        Tenant managedTenant = tenantService.saveTenant(tenant);

        String email = userCreationRequest.getEmail();
        String password = userCreationRequest.getPassword();
        Caller caller = securityService.createNewCaller(email, password);

        String firstName = userCreationRequest.getFirstName();
        String lastName = userCreationRequest.getLastName();
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCaller(caller);
        User managedUser = entityManager.merge(user);

        TenantRole tenantRole = userCreationRequest.getTenantRole();
        TenantUserRole tenantUserRole = new TenantUserRole();
        tenantUserRole.setTenant(managedTenant);
        tenantUserRole.setUser(managedUser);
        tenantUserRole.setTenantRole(tenantRole);
        TenantUserRole managedRole = entityManager.merge(tenantUserRole);

        return managedUser;
    }

    public User saveUser(User existingUser) {
        validationService.validateNonNullId(existingUser);
        boolean adminLogged = securityService.isAdminLogged();
        if (!adminLogged) {
            this.getLoggedUser()
                    .map(existingUser::equals)
                    .orElseThrow(OperationNotAllowedException::new);
        }

        User managedUser = entityManager.merge(existingUser);
        return managedUser;
    }

    public List<TenantUserRole> findUserTenantRoles(User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantUserRole> query = criteriaBuilder.createQuery(TenantUserRole.class);
        Root<TenantUserRole> userRoot = query.from(TenantUserRole.class);

        Path<User> userPath = userRoot.get(TenantUserRole_.user);
        Predicate userPredicate = criteriaBuilder.equal(userPath, user);
        query.where(userPredicate);

        TypedQuery<TenantUserRole> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public Optional<TenantRole> findUserRoleForTenant(User user, Tenant tenant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantRole> query = criteriaBuilder.createQuery(TenantRole.class);
        Root<TenantUserRole> tenantUserRoleRoot = query.from(TenantUserRole.class);

        Path<User> userPath = tenantUserRoleRoot.get(TenantUserRole_.user);
        Path<Tenant> tenantPath = tenantUserRoleRoot.get(TenantUserRole_.tenant);
        Path<TenantRole> tenantRolePath = tenantUserRoleRoot.get(TenantUserRole_.tenantRole);

        Predicate userPredicate = criteriaBuilder.equal(userPath, user);
        Predicate tenantPredicate = criteriaBuilder.equal(tenantPath, tenant);

        query.select(tenantRolePath);
        query.where(userPredicate, tenantPredicate);

        TypedQuery<TenantRole> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList().stream()
                .findFirst();
    }


    public Optional<User> getLoggedUser() {
        String callerName = securityContext.getCallerPrincipal().getName();
        return securityService.findCallerByName(callerName)
                .flatMap(this::findCallerUser);
    }


    public List<TenantUserRole> getLoggedUserTenantsRoles() {
        String callerName = securityContext.getCallerPrincipal().getName();
        return securityService.findCallerByName(callerName)
                .flatMap(this::findCallerUser)
                .map(this::findUserTenantRoles)
                .orElseGet(ArrayList::new);
    }

    private Optional<User> findCallerUser(Caller caller) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Path<Caller> callerPath = userRoot.get(User_.caller);
        Predicate callerPredicate = criteriaBuilder.equal(callerPath, caller);
        query.where(callerPredicate);

        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        return typedQuery.getResultList().stream()
                .findFirst();
    }
}
