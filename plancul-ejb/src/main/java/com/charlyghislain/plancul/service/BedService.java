package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.*;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.domain.request.sort.BedSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Inject
    private CultureService cultureService;


    public Bed saveBed(Bed bed) throws OperationNotAllowedException {
        if (bed.getId() == null) {
            return this.createBed(bed);
        }
        Tenant tenant = getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(tenant);

        bed.setUpdated(LocalDateTime.now());
        Bed managedBed = entityManager.merge(bed);
        return managedBed;
    }


    public void removePlotBeds(Plot plot) {
        BedFilter bedFilter = new BedFilter();
        bedFilter.setPlot(plot);
        removeAllBeds(bedFilter);
    }

    public void deleteBed(Bed bed) throws OperationNotAllowedException {
        validationService.validateNonNullId(bed);
        Tenant tenant = getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(tenant);

        removeBed(bed);
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

    public List<String> findBedPatches(BedFilter bedFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
        Root<Bed> rootBed = query.from(Bed.class);

        Path<String> patchPath = rootBed.get(Bed_.patch);
        Predicate hasPatchPredicate = criteriaBuilder.isNotNull(patchPath);

        List<Predicate> predicates = this.createBedPredicates(bedFilter, rootBed);
        predicates.add(hasPatchPredicate);

        Order patchOrder = criteriaBuilder.asc(patchPath);


        query.select(patchPath);
        query.distinct(true);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(patchOrder);

        TypedQuery<String> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
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
                .filter(query -> !query.isEmpty())
                .map(query -> this.createNamesContainsPredicate(query, rootBed))
                .ifPresent(predicateList::add);

        bedFilter.getExactBed()
                .map(bed -> criteriaBuilder.equal(rootBed, bed))
                .ifPresent(predicateList::add);

        bedFilter.getPlot()
                .map(plot -> criteriaBuilder.equal(plotPath, plot))
                .ifPresent(predicateList::add);

        bedFilter.getPatch()
                .map(patch -> this.createPatchPredicate(rootBed, patch))
                .ifPresent(predicateList::add);

        bedFilter.getPatchQuery()
                .map(query -> this.createPatchQueryPredicate(rootBed, query))
                .ifPresent(predicateList::add);

        bedFilter.getMinSurface()
                .map(surface -> this.createMinSurfacePredicate(rootBed, surface))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createMinSurfacePredicate(From<?, Bed> rootBed, BigDecimal minSurface) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<BigDecimal> surfacePath = rootBed.get(Bed_.surface);
        Predicate minSurfacePredicate = criteriaBuilder.greaterThanOrEqualTo(surfacePath, minSurface);
        return minSurfacePredicate;
    }

    private Predicate createPatchPredicate(From<?, Bed> rootBed, String patch) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<String> patchPath = rootBed.get(Bed_.patch);
        return criteriaBuilder.equal(patchPath, patch);
    }

    private Predicate createPatchQueryPredicate(From<?, Bed> rootBed, String query) {
        Path<String> patchPath = rootBed.get(Bed_.patch);
        return searchService.createTextMatchPredicate(patchPath, query);
    }

    private List<Sort<Bed>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, BedSortField.NAME));
    }

    private Bed createBed(Bed bed) throws OperationNotAllowedException {
        Tenant bedTenant = this.getBedTenant(bed);
        validationService.validateLoggedUserHasTenantRole(bedTenant);

        bed.setCreated(LocalDateTime.now());
        bed.setUpdated(LocalDateTime.now());
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


    private void removeAllBeds(BedFilter bedFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bed> query = criteriaBuilder.createQuery(Bed.class);
        Root<Bed> rootBed = query.from(Bed.class);

        List<Predicate> predicates = this.createBedPredicates(bedFilter, rootBed);
        query.where(predicates.toArray(new Predicate[0]));

        List<Bed> beds = searchService.getAllResults(query);
        beds.forEach(this::removeBed);
    }

    public void removeBed(Bed bed) {
        Bed managedBed = entityManager.merge(bed);

        cultureService.removeBedCultures(managedBed);

        entityManager.remove(managedBed);
    }

}
