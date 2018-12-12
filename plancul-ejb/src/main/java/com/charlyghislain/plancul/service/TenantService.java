package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.*;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.domain.request.filter.TenantUserRoleFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.request.sort.TenantSortField;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.stat.TenantStat;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class TenantService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private PlotService plotService;
    @Inject
    private CropService cropService;
    @Inject
    private CultureService cultureService;
    @Inject
    private TenantUserRolesQueryService tenantUserRolesQueryService;
    @Inject
    private TenantUserRolesUpdateService tenantUserRolesUpdateService;

    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;


    public Tenant saveTenant(Tenant tenant) throws OperationNotAllowedException {
        if (tenant.getId() == null) {
            return this.createTenant(tenant);
        }
        validationService.validateLoggedUserHasTenantRole(tenant, TenantRole.ADMIN);

        Tenant managedTenant = entityManager.merge(tenant);
        return managedTenant;
    }

    public Optional<Tenant> findTenantById(long id) {
        Tenant foundTenant = entityManager.find(Tenant.class, id);
        return Optional.ofNullable(foundTenant)
                .filter(validationService::hasLoggedUserTenantRole);
    }


    public TenantStat calcTenantStat(Tenant tenant) {
        CultureFilter cultureFilter = new CultureFilter();
        cultureFilter.setTenant(tenant);
        Long cultureCount = cultureService.countCultures(cultureFilter);

        TenantStat tenantStat = new TenantStat();
        tenantStat.setTenant(tenant);
        tenantStat.setCultureCount(cultureCount);
        return tenantStat;
    }


    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination, Language language) {
        List<Sort<Tenant>> defaultSorts = this.getDefaultSorts();
        return this.findTenants(tenantFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination, List<Sort<Tenant>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> query = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> rootTenant = query.from(Tenant.class);

        List<Predicate> predicates = this.createTenantPredicates(tenantFilter, rootTenant, true);

        return searchService.search(pagination, sorts, language, query, rootTenant, predicates);
    }

    public Long countTenants(TenantFilter tenantFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> query = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> rootTenant = query.from(Tenant.class);

        List<Predicate> predicates = this.createTenantPredicates(tenantFilter, rootTenant, false);

        return searchService.count(rootTenant, predicates);
    }

    public void removeTenant(Tenant tenant) {
        Tenant managedTenant = entityManager.merge(tenant);

        cultureService.removeTenantCultures(managedTenant);
        cropService.removeTenantCrops(managedTenant);
        plotService.removeTenantPlots(managedTenant);

        TenantUserRoleFilter roleFilter = new TenantUserRoleFilter();
        roleFilter.setTenant(managedTenant);
        List<TenantUserRole> allTenantUserRoles = tenantUserRolesQueryService.findAllTenantUserRoles(roleFilter);
        allTenantUserRoles.forEach(tenantUserRolesUpdateService::removeTenantUserRole);

        entityManager.remove(managedTenant);
    }

    @RolesAllowed({ApplicationGroupNames.ADMIN})
    private Tenant createTenant(Tenant tenant) throws OperationNotAllowedException {
        Tenant managedTenant = entityManager.merge(tenant);

        this.createDefaultPlot(managedTenant);
        return managedTenant;
    }

    private List<Sort<Tenant>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, TenantSortField.NAME));
    }


    private List<Predicate> createTenantPredicates(TenantFilter tenantFilter, Root<Tenant> rootTenant, boolean restrictUserAccess) {
        List<Predicate> predicateList = new ArrayList<>();

        if (restrictUserAccess) {
            searchService.createLoggedUserTenantsPredicate(rootTenant)
                    .ifPresent(predicateList::add);
        }

        tenantFilter.getNameContains()
                .map(query -> this.createContainsPredicate(query, rootTenant))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createContainsPredicate(String query, From<?, Tenant> tenantSource) {
        Path<String> namePath = tenantSource.get(Tenant_.name);
        Predicate predicate = searchService.createTextMatchPredicate(namePath, query);
        return predicate;
    }


    private void createDefaultPlot(Tenant managedTenant) throws OperationNotAllowedException {
        Plot plot = new Plot();
        plot.setTenant(managedTenant);
        plot.setName("Default");
        plotService.savePlot(plot);
    }
}
