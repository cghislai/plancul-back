package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.authenticator.client.AuthenticatorUserClient;
import com.charlyghislain.plancul.authenticator.client.exception.AuthenticatorClientError;
import com.charlyghislain.plancul.authenticator.client.exception.UserNotFoundException;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.TenantUserRole;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.User_;
import com.charlyghislain.plancul.domain.exception.PlanCulRuntimeException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.UserFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.security.AuthenticatorUser;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class UserQueryService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private SearchService searchService;
    @Inject
    private TenantUserRolesQueryService tenantUserRolesQueryService;
    @Inject
    private AuthenticatorUserClient authenticatorUserClient;

    @Inject
    @Claim("uid")
    private Instance<ClaimValue<Long>> jwtUidClaim;
    @Inject
    private Instance<JsonWebToken> jsonWebTokens;


    public Optional<User> findUser(UserFilter userFilter) {
        CriteriaQuery<User> query = searchService.createSearchQuery(User.class, userFilter, this::createPredicates);
        return searchService.getSingleResult(query);
//                .request(this::isUserAccessibleToLoggedUser);
    }


    public SearchResult<User> findUsers(UserFilter userFilter, Pagination pagination, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        List<Predicate> predicates = createPredicates(userRoot, userFilter);
        return searchService.search(pagination, getDefaultSorts(), language, query, userRoot, predicates);
    }


    public Optional<User> findUserById(long id) {
        UserFilter userFilter = new UserFilter();
        userFilter.setUserId(id);
        return this.findUser(userFilter);
    }

    public Optional<User> findUserByAuthenticatorUid(long authenticatorUid) {
        UserFilter userFilter = new UserFilter();
        userFilter.setAuthenticatorUid(authenticatorUid);
        return this.findUser(userFilter);
    }

    public List<String> findUserApplicationGroups(long authenticatorUid) {
        Optional<User> userOptional = this.findUserByAuthenticatorUid(authenticatorUid);
        boolean isUser = userOptional.isPresent();
        if (!isUser) {
            return Collections.singletonList(ApplicationGroupNames.UNREGISTERED_USER);
        }
        User user = userOptional.get();
        Set<String> applicationGroups = new HashSet<>();
        applicationGroups.add(ApplicationGroupNames.REGISTERED_USER);

        List<TenantUserRole> tenantsWithUserRole = tenantUserRolesQueryService.findAllTenantUserRoles(user, TenantRole.USER);
        if (!tenantsWithUserRole.isEmpty()) {
            applicationGroups.add(ApplicationGroupNames.TENANT_USER);
        }

        List<TenantUserRole> tenantsWithAdminRole = tenantUserRolesQueryService.findAllTenantUserRoles(user, TenantRole.ADMIN);
        if (!tenantsWithAdminRole.isEmpty()) {
            applicationGroups.add(ApplicationGroupNames.TENANT_MANAGER);
            applicationGroups.add(ApplicationGroupNames.TENANT_USER);
        }

        if (user.isAdmin()) {
            applicationGroups.add(ApplicationGroupNames.ADMIN);
        }
        return Arrays.asList(applicationGroups.toArray(new String[0]));
    }


    public Optional<User> getLoggedUser() {
        return this.findCallerUser();
    }


    public AuthenticatorUser getLoggedAuthenticatorUser() {
        Long jwtUid = jwtUidClaim.get().getValue();
        AuthenticatorUser user = findAuthenticatorUser(jwtUid)
                .orElseThrow(IllegalStateException::new);
        return user;
    }

    public Optional<AuthenticatorUser> findAuthenticatorUser(Long authenticatorUserId) {
        try {
            AuthenticatorUser user = this.authenticatorUserClient.getUser(authenticatorUserId);
            return Optional.of(user);
        } catch (UserNotFoundException e) {
            return Optional.empty();
        } catch (AuthenticatorClientError e) {
            throw new PlanCulRuntimeException(e);
        }
    }

    public Optional<AuthenticatorUser> findAuthenticatorUserByEmail(String email) {
        return this.authenticatorUserClient.findUserUserWithMail(email);
    }

    public List<TenantUserRole> getLoggedUserTenantsRoles() {
        return this.findCallerUser()
                .map(this.tenantUserRolesQueryService::findAllTenantUserRoles)
                .orElseGet(ArrayList::new);
    }

    public boolean isUserActive(User user) {
        Long authenticatorUid = user.getAuthenticatorUid();
        return findAuthenticatorUser(authenticatorUid)
                .map(AuthenticatorUser::isActive)
                .orElse(false);
    }

    private Optional<User> findCallerUser() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Long jwtUid = jwtUidClaim.get().getValue();
        Path<Long> authenticatorUidPath = userRoot.get(User_.authenticatorUid);
        Predicate callerPredicate = criteriaBuilder.equal(authenticatorUidPath, jwtUid);
        query.where(callerPredicate);

        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        return typedQuery.getResultList().stream()
                .findFirst();
    }

    private boolean isUserAccessibleToLoggedUser(User user) {
        List<Tenant> userTenants = this.tenantUserRolesQueryService.findAllTenantUserRoles(user)
                .stream()
                .map(TenantUserRole::getTenant)
                .collect(Collectors.toList());
        List<Tenant> tenantsForWhichCallerIsAdmin = this.findCallerUser()
                .map(caller -> this.tenantUserRolesQueryService.findAllTenantUserRoles(caller, TenantRole.ADMIN))
                .map(list -> list.stream().map(TenantUserRole::getTenant).collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
        return userTenants.stream()
                .anyMatch(tenantsForWhichCallerIsAdmin::contains);
    }


    private List<Predicate> createPredicates(From<?, User> userFrom, UserFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        Path<Long> idPath = userFrom.get(User_.id);
        Optional.ofNullable(filter.getUserId())
                .map(id -> criteriaBuilder.equal(idPath, id))
                .ifPresent(predicates::add);

        Path<Long> authenticatorUidPath = userFrom.get(User_.authenticatorUid);
        Optional.ofNullable(filter.getAuthenticatorUid())
                .map(uid -> criteriaBuilder.equal(authenticatorUidPath, uid))
                .ifPresent(predicates::add);

        Path<Boolean> adminPath = userFrom.get(User_.admin);
        Optional.ofNullable(filter.getAdmin())
                .map(admin -> criteriaBuilder.equal(adminPath, admin))
                .ifPresent(predicates::add);

        return predicates;

    }

    private List<Sort<User>> getDefaultSorts() {
        return new ArrayList<>();
    }


}
