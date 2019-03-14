package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Plot_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.request.filter.PlotFilter;
import com.charlyghislain.plancul.domain.request.sort.PlotSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class PlotService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private ValidationService validationService;
    @Inject
    private SearchService searchService;
    @Inject
    private BedService bedService;


    public Plot savePlot(Plot plot) throws OperationNotAllowedException {
        if (plot.getId() == null) {
            return this.createPlot(plot);
        }

        plot.setUpdated(LocalDateTime.now());
        Plot managedPlot = entityManager.merge(plot);
        return managedPlot;
    }


    public void removeTenantPlots(Tenant tenant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Plot> query = criteriaBuilder.createQuery(Plot.class);
        Root<Plot> rootPlot = query.from(Plot.class);

        PlotFilter plotFilter = new PlotFilter();
        plotFilter.setTenant(tenant);
        List<Predicate> predicates = this.createPlotPredicates(plotFilter, rootPlot);
        query.where(predicates.toArray(new Predicate[0]));

        List<Plot> plots = searchService.getAllResults(query);
        plots.forEach(this::removePlot);
    }

    public void deletePlot(Plot plot) throws OperationNotAllowedException {
        validationService.validateNonNullId(plot);
        validationService.validateLoggedUserHasTenantRole(plot.getTenant());

        removePlot(plot);
    }


    public Optional<Plot> findPlotById(long id) {
        Plot foundPlot = entityManager.find(Plot.class, id);
        return Optional.ofNullable(foundPlot)
                .filter(plot -> validationService.hasLoggedUserTenantRole(plot.getTenant()));
    }


    public SearchResult<Plot> findPlots(PlotFilter plotFilter, Pagination pagination, Language language) {
        List<Sort<Plot>> defaultSorts = this.getDefaultSorts();
        return this.findPlots(plotFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Plot> findPlots(PlotFilter plotFilter, Pagination pagination, List<Sort<Plot>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Plot> query = criteriaBuilder.createQuery(Plot.class);
        Root<Plot> rootPlot = query.from(Plot.class);

        List<Predicate> predicates = this.createPlotPredicates(plotFilter, rootPlot);

        return searchService.search(pagination, sorts, language, query, rootPlot, predicates);
    }

    private List<Sort<Plot>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, PlotSortField.NAME));
    }


    private Plot createPlot(Plot plot) throws OperationNotAllowedException {
        plot.setCreated(LocalDateTime.now());
        plot.setUpdated(LocalDateTime.now());

        Plot managedPlot = entityManager.merge(plot);
        return managedPlot;
    }


    private List<Predicate> createPlotPredicates(PlotFilter plotFilter, Root<Plot> rootPlot) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        Path<Tenant> tenantPath = rootPlot.get(Plot_.tenant);
        searchService.createLoggedUserTenantsPredicate(tenantPath)
                .ifPresent(predicateList::add);

        plotFilter.getTenant()
                .map(tenant -> criteriaBuilder.equal(tenantPath, tenant))
                .ifPresent(predicateList::add);

        plotFilter.getNameContains()
                .map(query -> this.createNamesContainsPredicate(query, rootPlot))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createNamesContainsPredicate(String query, Root<Plot> plotSource) {
        Path<String> namePath = plotSource.get(Plot_.name);
        Predicate predicate = searchService.createTextMatchPredicate(namePath, query);
        return predicate;
    }

    private void removePlot(Plot plot) {
        Plot managedPlot = entityManager.merge(plot);

        bedService.removePlotBeds(managedPlot);

        entityManager.remove(managedPlot);
    }
}
