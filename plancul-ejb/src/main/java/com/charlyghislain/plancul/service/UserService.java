package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.security.Caller;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class UserService {


    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

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
        Tenant mamangedTenant = tenantService.saveTenant(tenant);

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
        tenantUserRole.setTenant(mamangedTenant);
        tenantUserRole.setUser(managedUser);
        tenantUserRole.setTenantRole(tenantRole);
        TenantUserRole managedRole = entityManager.merge(tenantUserRole);

        return managedUser;
    }

    public User saveUser(User existingUser) {
        validationService.validateNonNullId(existingUser);

        User managedUser = entityManager.merge(existingUser);
        return managedUser;
    }
}
