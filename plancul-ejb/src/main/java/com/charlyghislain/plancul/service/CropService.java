package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Crop_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.AgrovocPlantFilter;
import com.charlyghislain.plancul.domain.request.filter.AgrovocProductFilter;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class CropService {


    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;

    public Optional<Crop> findCropById(long id) {
        Crop found = entityManager.find(Crop.class, id);
        return Optional.ofNullable(found)
                .filter(this::isCropAccessibleToLoggedUser);
    }

    public Crop createPlot(CropCreationRequest cropCreationRequest) {
        // TODO: ask FAO sparql if plant produces product - or skip the product part altogether.
        // The product should probably not be linked to a crop, but rather a crop-yield of some sort.
        // It is kept here for the moment as it is useful to search crops by their product names (Solanum tuberosum vs potato)
        String agrovocPlantUri = cropCreationRequest.getAgrovocPlantUri();
        String agrovocProductUri = cropCreationRequest.getAgrovocProductUri();
        Optional<String> cultivar = cropCreationRequest.getCultivar();
        Optional<Tenant> tenantRestriction = cropCreationRequest.getTenantRestriction();

        tenantRestriction
                .ifPresent(validationService::validateLoggedUserHasTenantRole);

        AgrovocPlant agrovocPlant = this.getOrCreateAgrovocPlant(agrovocPlantUri);
        AgrovocProduct agrovocProduct = this.getOrCreateAgrovocProduct(agrovocProductUri);

        Crop crop = new Crop();
        crop.setAgrovocPlant(agrovocPlant);
        crop.setAgrovocProduct(agrovocProduct);
        crop.setCultivar(cultivar.orElse(null));
        crop.setTenant(tenantRestriction.orElse(null));

        Crop managedCrop = entityManager.merge(crop);
        return managedCrop;
    }


    public SearchResult<Crop> findCrops(CropFilter cropFilter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Crop> query = criteriaBuilder.createQuery(Crop.class);
        Root<Crop> rootCrop = query.from(Crop.class);

        List<Predicate> predicates = this.createCropPredicates(cropFilter, rootCrop);

        return searchService.search(pagination, query, rootCrop, predicates);
    }


    public List<Predicate> createCropPredicates(CropFilter cropFilter, From<?, Crop> cropSource) {
        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Tenant> tenantPath = cropSource.get(Crop_.tenant);
        searchService.createLoggedUserTenantsPredicate(tenantPath)
                .ifPresent(predicateList::add);

        cropFilter.getTenant()
                .map(t -> criteriaBuilder.equal(tenantPath, t))
                .ifPresent(predicateList::add);

        cropFilter.getExactCrop()
                .map(c -> criteriaBuilder.equal(cropSource, c))
                .ifPresent(predicateList::add);

        cropFilter.getPlant()
                .map(p -> this.createPlantPredicate(cropSource, p))
                .ifPresent(predicateList::add);

        Optional<Language> queryLanguage = cropFilter.getQueryLanguage();
        cropFilter.getNamesQuery()
                .map(q -> this.createNamesQueryPredicate(cropSource, q, queryLanguage))
                .ifPresent(predicateList::add);

        cropFilter.getPlantQuery()
                .map(q -> this.createPlantNamesQueryPredicate(cropSource, q, queryLanguage))
                .ifPresent(predicateList::add);

        cropFilter.getCultivarQuery()
                .map(q -> this.createCultivarQueryPredicate(cropSource, q))
                .ifPresent(predicateList::add);

        cropFilter.getShared()
                .map(shared -> this.createSharedPredicate(cropSource, shared))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private AgrovocPlant getOrCreateAgrovocPlant(String agrovocPlantUri) {
        AgrovocPlantFilter plantFilter = new AgrovocPlantFilter();
        plantFilter.setUri(agrovocPlantUri);
        Pagination pagination = new Pagination(1);
        SearchResult<AgrovocPlant> plantResults = agrovocService.findAgrovocPlants(plantFilter, pagination);

        if (plantResults.getTotalCount() == 0) {
            return agrovocService.createAgrovocPlant(agrovocPlantUri);
        } else if (plantResults.getTotalCount() == 1) {
            return plantResults.getList().get(0);
        } else {
            throw new IllegalStateException("Multiple entries for a single uri");
        }
    }


    private AgrovocProduct getOrCreateAgrovocProduct(String agrovocProductUri) {
        AgrovocProductFilter productFilter = new AgrovocProductFilter();
        productFilter.setUri(agrovocProductUri);
        Pagination pagination = new Pagination(1);
        SearchResult<AgrovocProduct> productResults = agrovocService.findAgrovocProducts(productFilter, pagination);

        if (productResults.getTotalCount() == 0) {
            return agrovocService.createAgrovocProduct(agrovocProductUri);
        } else if (productResults.getTotalCount() == 1) {
            return productResults.getList().get(0);
        } else {
            throw new IllegalStateException("Multiple entries for a single uri");
        }
    }

    private boolean isCropAccessibleToLoggedUser(Crop crop) {
        return crop.getTenant()
                .map(validationService::hasLoggedUserTenantRole)
                .orElse(true);
    }

    private Predicate createSharedPredicate(From<?, Crop> cropSource, Boolean shared) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<Tenant> tenantPath = cropSource.get(Crop_.tenant);
        Predicate isShared = criteriaBuilder.isNull(tenantPath);
        Predicate sharedPredicate = criteriaBuilder.equal(isShared, shared);
        return sharedPredicate;
    }

    private Predicate createNamesQueryPredicate(From<?, Crop> cropSource, String query, Optional<Language> queryLanguage) {
        Predicate plantNameQueryPredicate = this.createPlantNamesQueryPredicate(cropSource, query, queryLanguage);

        Join<Crop, AgrovocProduct> productJoin = cropSource.join(Crop_.agrovocProduct);
        Predicate productNamesPredicate = agrovocService.createProductNameQueryPredicate(query, queryLanguage, productJoin);

        Predicate cultivarPredicate = this.createCultivarQueryPredicate(cropSource, query);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate namesPredicate = criteriaBuilder.or(plantNameQueryPredicate, productNamesPredicate, cultivarPredicate);
        return namesPredicate;
    }

    private Predicate createPlantNamesQueryPredicate(From<?, Crop> cropSource, String query, Optional<Language> queryLanguage) {
        Join<Crop, AgrovocPlant> plantPath = cropSource.join(Crop_.agrovocPlant);
        Predicate plantNameQueryPredicate = agrovocService.createPlantNameQueryPredicate(query, queryLanguage, plantPath);
        return plantNameQueryPredicate;
    }

    private Predicate createCultivarQueryPredicate(From<?, Crop> cropSource, String query) {
        Path<String> cultivarPath = cropSource.get(Crop_.cultivar);
        Predicate cultivarPredicate = searchService.createTextMatchPredicate(cultivarPath, query);
        return cultivarPredicate;
    }

    private Predicate createPlantPredicate(From<?, Crop> cropSource, AgrovocPlant plant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<AgrovocPlant> plantPath = cropSource.get(Crop_.agrovocPlant);
        Predicate predicate = criteriaBuilder.equal(plantPath, plant);
        return predicate;
    }
}
