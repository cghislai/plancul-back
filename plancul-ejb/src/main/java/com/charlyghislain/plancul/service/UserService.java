package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.TenantUserRole_;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.User_;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.mail.RenderedMail;
import com.charlyghislain.plancul.domain.mail.template.AccountCreationInvitationTemplate;
import com.charlyghislain.plancul.domain.request.UserCreationRequest;
import com.charlyghislain.plancul.domain.security.ApplicationGroup;
import com.charlyghislain.plancul.domain.security.Caller;
import com.charlyghislain.plancul.domain.util.PlanCulProperties;
import com.charlyghislain.plancul.domain.util.PlanCulPropertiesProvider;
import com.charlyghislain.plancul.security.JwtService;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class UserService {


    private static final String ACCOUNT_INIT_FRONTEND_PATH = "/account/init";
    private static final String ACCOUNT_INIT_CALLER_PARAM = "caller";
    private static final String ACCOUNT_INIT_TOKEN_PARAM = "token";

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SecurityContext securityContext;
    @Inject
    private ValidationService validationService;
    @Inject
    private PlanCulPropertiesProvider planCulPropertiesProvider;
    @Inject
    private MailTemplateService mailTemplateService;
    @Inject
    private JwtService jwtService;

    @EJB
    private TenantService tenantService;
    @EJB
    private SecurityService securityService;
    @EJB
    private MailService mailService;

    public Optional<User> findUserById(long id) {
        User found = entityManager.find(User.class, id);
        return Optional.ofNullable(found)
                .filter(this::isUserAccessibleToLoggedUser);
    }

    public User createUser(@NotNull @Valid UserCreationRequest userCreationRequest) {
        Tenant tenant = userCreationRequest.getTenant();
        Tenant managedTenant = tenantService.saveTenant(tenant);

        String email = userCreationRequest.getEmail();
        Caller caller = securityService.createNewCaller(email);

        String firstName = userCreationRequest.getFirstName();
        String lastName = userCreationRequest.getLastName();
        Language language = userCreationRequest.getLanguage();

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLanguage(language);
        user.setCaller(caller);
        User managedUser = entityManager.merge(user);

        TenantRole tenantRole = userCreationRequest.getTenantRole();
        TenantUserRole tenantUserRole = new TenantUserRole();
        tenantUserRole.setTenant(managedTenant);
        tenantUserRole.setUser(managedUser);
        tenantUserRole.setTenantRole(tenantRole);
        TenantUserRole managedRole = entityManager.merge(tenantUserRole);

        entityManager.flush();
        this.sendAccountInitializationInvitation(user);
        return managedUser;
    }

    public User saveUser(User user) {
        validationService.validateNonNullId(user);
        boolean adminLogged = securityService.isAdminLogged();
        if (!adminLogged) {
            this.getLoggedUser()
                    .map(user::equals)
                    .orElseThrow(OperationNotAllowedException::new);
        }

        securityService.checkUserMailDidNotChange(user);
        User managedUser = entityManager.merge(user);

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

    public String getAccountInitializationUrl(User user) {
        String frontUrl = planCulPropertiesProvider.getValue(PlanCulProperties.APP_FRONT_URL);
        Caller caller = user.getCaller();
        String callerName = caller.getName();
        String passwordResetToken = caller.getPasswordResetToken();
        Language language = user.getLanguage();


        try {
            String encodedCallerName = URLEncoder.encode(callerName, "UTF-8");
            String encodedResetToken = URLEncoder.encode(passwordResetToken, "UTF-8");
            String url = frontUrl
                    + "/" + language.getCode()
                    + ACCOUNT_INIT_FRONTEND_PATH
                    + "?" + ACCOUNT_INIT_CALLER_PARAM + "=" + encodedCallerName
                    + "&" + ACCOUNT_INIT_TOKEN_PARAM + "=" + encodedResetToken;
            return url;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String activateUserAccount(String email, String password, String passwordToken) {
        Caller caller = securityService.updatePassword(email, password, passwordToken);
        Set<ApplicationGroup> callerGroups = securityService.findCallerGroups(caller);
        String jwt = jwtService.createJwt(caller, callerGroups);
        return jwt;
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

    private boolean isUserAccessibleToLoggedUser(User user) {
        Optional<Tenant> userTenantForWhichLoggedUserIsAdmin = findUserTenantRoles(user)
                .stream()
                .map(TenantUserRole::getTenant)
                .filter(tenant -> validationService.hasLoggedUserTenantRole(tenant, TenantRole.ADMIN))
                .findAny();
        return userTenantForWhichLoggedUserIsAdmin.isPresent();
    }


    private void sendAccountInitializationInvitation(User user) {
        AccountCreationInvitationTemplate invitationTemplate = this.mailTemplateService.createAccountCreationInvitationTemplate(user);
        RenderedMail renderedMail = this.mailTemplateService.renderMail(invitationTemplate, user.getLanguage());
        mailService.sendMail(renderedMail);
    }

}
