package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Plot_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.PlotFilter;
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
import java.util.ArrayList;
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


    public Plot createPlot(Plot plot) {
        validationService.validateNoId(plot);
        validationService.validateLoggedUserHasTenantRole(plot.getTenant());

        Plot managedPlot = entityManager.merge(plot);
        return managedPlot;
    }

    public Plot savePlot(Plot plot) {
        validationService.validateNonNullId(plot);
        validationService.validateLoggedUserHasTenantRole(plot.getTenant());

        Plot managedPlot = entityManager.merge(plot);
        return managedPlot;
    }

    public void deletePlot(Plot plot) {
        validationService.validateNonNullId(plot);
        validationService.validateLoggedUserHasTenantRole(plot.getTenant());

        entityManager.remove(plot);
    }

    public Optional<Plot> findPlotById(long id) {
        Plot foundPlot = entityManager.find(Plot.class, id);
        Optional<Plot> foundPlotOptional = Optional.ofNullable(foundPlot);
        foundPlotOptional.map(Plot::getTenant)
                .ifPresent(validationService::validateLoggedUserHasTenantRole);
        return foundPlotOptional;
    }

    public SearchResult<Plot> findPlots(PlotFilter plotFilter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Plot> query = criteriaBuilder.createQuery(Plot.class);
        Root<Plot> rootPlot = query.from(Plot.class);

        List<Predicate> predicates = this.createPlotPredicates(plotFilter, rootPlot);

        return searchService.search(pagination, query, rootPlot, predicates);
    }

    private List<Predicate> createPlotPredicates(PlotFilter plotFilter, Root<Plot> rootPlot) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        Tenant tenant = plotFilter.getTenant();
        validationService.validateLoggedUserHasTenantRole(tenant);

        Path<Tenant> tenantPath = rootPlot.get(Plot_.tenant);
        Predicate tenantPredicate = criteriaBuilder.equal(tenantPath, tenant);
        predicateList.add(tenantPredicate);

        Path<String> namePath = rootPlot.get(Plot_.name);
        plotFilter.getNameContains()
                .map(query -> this.createContainsPredicate(query, namePath))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createContainsPredicate(String query, Path<String> namePath) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        String likeMatchString = '%' + query + '%';
        Predicate likePredicate = criteriaBuilder.like(namePath, likeMatchString);
        return likePredicate;
    }

}
