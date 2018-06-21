package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.Bed_;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Plot_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.request.sort.BedSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;

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
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class BedService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private ValidationService validationService;
    @Inject
    private SearchService searchService;


    public Bed saveBed(Bed bed) {
        if (bed.getId() == null) {
            return this.createBed(bed);
        }
        Tenant tenant = getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(tenant);

        Bed managedBed = entityManager.merge(bed);
        return managedBed;
    }


    public void deleteBed(Bed bed) {
        validationService.validateNonNullId(bed);
        Tenant tenant = getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(tenant);

        Bed managedBed = entityManager.merge(bed);
        entityManager.remove(managedBed);
    }

    public Optional<Bed> findBedById(long id) {
        Bed foundBed = entityManager.find(Bed.class, id);
        return Optional.ofNullable(foundBed)
                .filter(bed -> validationService.hasLoggedUserTenantRole(getBedTenant(bed)));
    }

    public SearchResult<Bed> findBeds(BedFilter bedFilter, Pagination pagination, Language language) {
        List<Sort<Bed>> defaultSorts = this.getDefaultSorts();
        return this.findBeds(bedFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Bed> findBeds(BedFilter bedFilter, Pagination pagination, List<Sort<Bed>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bed> query = criteriaBuilder.createQuery(Bed.class);
        Root<Bed> rootBed = query.from(Bed.class);

        List<Predicate> predicates = this.createBedPredicates(bedFilter, rootBed);

        return searchService.search(pagination, sorts, language, query, rootBed, predicates);
    }


    public List<Predicate> createBedPredicates(BedFilter bedFilter, From<?, Bed> rootBed) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        Path<Plot> plotPath = rootBed.get(Bed_.plot);
        Path<Tenant> tenantPath = plotPath.get(Plot_.tenant);
        searchService.createLoggedUserTenantsPredicate(tenantPath)
                .ifPresent(predicateList::add);

        bedFilter.getTenant()
                .map(tenant -> criteriaBuilder.equal(tenantPath, tenant))
                .ifPresent(predicateList::add);

        bedFilter.getNameQuery()
                .map(query -> this.createNamesContainsPredicate(query, rootBed))
                .ifPresent(predicateList::add);

        bedFilter.getExactBed()
                .map(bed -> criteriaBuilder.equal(rootBed, bed))
                .ifPresent(predicateList::add);

        bedFilter.getPlot()
                .map(plot -> criteriaBuilder.equal(plotPath, plot))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private List<Sort<Bed>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, BedSortField.NAME));
    }

    private Bed createBed(Bed bed) {
        Tenant bedTenant = this.getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(bedTenant);

        Bed managedBed = entityManager.merge(bed);
        return managedBed;
    }


    private Predicate createNamesContainsPredicate(String query, From<?, Bed> bedSource) {
        Path<String> namePath = bedSource.get(Bed_.name);
        Predicate predicate = searchService.createTextMatchPredicate(namePath, query);
        return predicate;
    }


    @NotNull
    private Tenant getBedTenant(Bed bed) {
        Plot plot = bed.getPlot();
        return plot.getTenant();
    }

}
