package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.Tenant_;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.request.sort.TenantSortField;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class TenantService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @EJB
    private PlotService plotService;

    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;


    public Tenant saveTenant(Tenant tenant) {
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


    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination, Language language) {
        List<Sort<Tenant>> defaultSorts = this.getDefaultSorts();
        return this.findTenants(tenantFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Tenant> findTenants(TenantFilter tenantFilter, Pagination pagination, List<Sort<Tenant>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tenant> query = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> rootTenant = query.from(Tenant.class);

        List<Predicate> predicates = this.createTenantPredicates(tenantFilter, rootTenant);

        return searchService.search(pagination, sorts, language, query, rootTenant, predicates);
    }

    @RolesAllowed({ApplicationGroupNames.ADMIN})
    private Tenant createTenant(Tenant tenant) {
        Tenant managedTenant = entityManager.merge(tenant);

        this.createDefaultPlot(managedTenant);
        return managedTenant;
    }
    
    private List<Sort<Tenant>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, TenantSortField.NAME));
    }


    private List<Predicate> createTenantPredicates(TenantFilter tenantFilter, Root<Tenant> rootTenant) {
        List<Predicate> predicateList = new ArrayList<>();

        searchService.createLoggedUserTenantsPredicate(rootTenant)
                .ifPresent(predicateList::add);

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


    private void createDefaultPlot(Tenant managedTenant) {
        Plot plot = new Plot();
        plot.setTenant(managedTenant);
        plot.setName("Default");
        plotService.savePlot(plot);
    }
}
