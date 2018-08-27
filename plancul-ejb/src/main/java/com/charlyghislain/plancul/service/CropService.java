package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Crop_;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.User;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.AgrovocPlantFilter;
import com.charlyghislain.plancul.domain.request.filter.AgrovocProductFilter;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.request.sort.CropSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class CropService {


    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private AgrovocService agrovocService;
    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;
    @Inject
    private UserQueryService userQueryService;

    public Optional<Crop> findCropById(long id) {
        Crop found = entityManager.find(Crop.class, id);
        return Optional.ofNullable(found)
                .filter(this::isCropAccessibleToLoggedUser);
    }

    public Crop createCrop(CropCreationRequest cropCreationRequest) throws OperationNotAllowedException {
        // TODO: ask FAO sparql if plant produces product - or skip the product part altogether.
        // The product should probably not be linked to a crop, but rather a crop-yield of some sort.
        // It is kept here for the moment as it is useful to search crops by their product names (Solanum tuberosum vs potato)
        String displayName = cropCreationRequest.getDisplayName();
        String family = cropCreationRequest.getFamily();
        String species = cropCreationRequest.getSpecies();
        Tenant tenant = cropCreationRequest.getTenant();
        boolean shared = cropCreationRequest.isShared();
        Language language = cropCreationRequest.getLanguage();
        Optional<String> subSpecies = cropCreationRequest.getSubSpecies();
        Optional<String> cultivar = cropCreationRequest.getCultivar();
        Optional<String> agrovocPlantURI = cropCreationRequest.getAgrovocPlantURI();
        Optional<String> agrovocProductURI = cropCreationRequest.getAgrovocProductURI();

        validationService.validateLoggedUserHasTenantRole(tenant);
        User user = userQueryService.getLoggedUser().orElseThrow(IllegalStateException::new);
        LocalDateTime creationTime = LocalDateTime.now();

        Crop crop = new Crop();
        crop.setFamily(family);
        crop.setSpecies(species);
        subSpecies.ifPresent(crop::setSubSpecies);
        cultivar.ifPresent(crop::setCultivar);
        crop.setCreationUser(user);
        crop.setCreationDateTime(creationTime);

        agrovocPlantURI.map(this::getOrCreateAgrovocPlant)
                .ifPresent(plant -> this.setCropAgrovocPlant(crop, plant));

        agrovocProductURI.map(this::getOrCreateAgrovocProduct)
                .ifPresent(crop::setAgrovocProduct);

        if (!shared) {
            crop.setTenant(tenant);
        }
        Crop managedCrop = entityManager.merge(crop);

        LocalizedMessage displayNameMessage = new LocalizedMessage();
        displayNameMessage.setLanguage(language);
        displayNameMessage.setLabel(displayName);
        LocalizedMessage managedDisplayNameMessage = entityManager.merge(displayNameMessage);

        managedCrop.getDisplayName().add(managedDisplayNameMessage);

        return managedCrop;
    }


    public SearchResult<Crop> findCrops(CropFilter cropFilter, Pagination pagination, Language language) {
        List<Sort<Crop>> defaultSorts = this.getDefaultSorts();
        return this.findCrops(cropFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Crop> findCrops(CropFilter cropFilter, Pagination pagination, List<Sort<Crop>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Crop> query = criteriaBuilder.createQuery(Crop.class);
        Root<Crop> rootCrop = query.from(Crop.class);

        List<Predicate> predicates = this.createCropPredicates(cropFilter, rootCrop);

        return searchService.search(pagination, sorts, language, query, rootCrop, predicates);
    }

    private List<Sort<Crop>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, CropSortField.PLANT_NAME));
    }

    public List<Predicate> createCropPredicates(CropFilter cropFilter, From<?, Crop> cropSource) {
        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Tenant> tenantPath = cropSource.join(Crop_.tenant, JoinType.LEFT);
        searchService.createLoggedUserTenantsPredicate(tenantPath, true)
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
                .filter(q -> !q.isEmpty())
                .map(q -> this.createNamesQueryPredicate(cropSource, q, queryLanguage))
                .ifPresent(predicateList::add);

        cropFilter.getPlantQuery()
                .filter(q -> !q.isEmpty())
                .map(q -> this.createPlantNamesQueryPredicate(cropSource, q, queryLanguage))
                .ifPresent(predicateList::add);

        cropFilter.getDisplayNameQuery()
                .filter(q -> !q.isEmpty())
                .map(q -> this.createDisplayNameQueryPredicate(cropSource, q, queryLanguage))
                .ifPresent(predicateList::add);

        cropFilter.getCultivarQuery()
                .filter(q -> !q.isEmpty())
                .map(q -> this.createCultivarQueryPredicate(cropSource, q))
                .ifPresent(predicateList::add);

        cropFilter.getShared()
                .map(shared -> this.createSharedPredicate(cropSource, shared))
                .ifPresent(predicateList::add);

        cropFilter.getCreatedByLoggedUser()
                .map(createdByLoggedUser -> this.createCreatedByLoggedUserPredicate(cropSource, createdByLoggedUser))
                .ifPresent(predicateList::add);

        return predicateList;
    }


    private AgrovocPlant getOrCreateAgrovocPlant(String agrovocPlantUri) {
        AgrovocPlantFilter plantFilter = new AgrovocPlantFilter();
        plantFilter.setUri(agrovocPlantUri);
        Pagination pagination = new Pagination(1);
        SearchResult<AgrovocPlant> plantResults = agrovocService.findAgrovocPlants(plantFilter, pagination, Language.DEFAULT_LANGUAGE);

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
        SearchResult<AgrovocProduct> productResults = agrovocService.findAgrovocProducts(productFilter, pagination, Language.DEFAULT_LANGUAGE);

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


    private Predicate createCreatedByLoggedUserPredicate(From<?, Crop> cropSource, Boolean createdByLoggedUser) {
        User loggedUser = userQueryService.getLoggedUser().orElse(null);
        Predicate createdByUserPredicate = this.createCreatedByUserPredicate(cropSource, loggedUser);
        if (createdByLoggedUser) {
            return createdByUserPredicate;
        } else {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            return criteriaBuilder.not(createdByUserPredicate);
        }
    }

    private Predicate createCreatedByUserPredicate(From<?, Crop> cropSource, User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<User> userPath = cropSource.get(Crop_.creationUser);
        return criteriaBuilder.equal(userPath, user);
    }

    private Predicate createNamesQueryPredicate(From<?, Crop> cropSource, String query, Optional<Language> queryLanguage) {
        Predicate plantNameQueryPredicate = this.createPlantNamesQueryPredicate(cropSource, query, queryLanguage);

        Join<Crop, AgrovocProduct> productJoin = cropSource.join(Crop_.agrovocProduct, JoinType.LEFT);
        Predicate productNamesPredicate = agrovocService.createProductNameQueryPredicate(query, queryLanguage, productJoin);

        Predicate displayNameQueryPredicate = this.createDisplayNameQueryPredicate(cropSource, query, queryLanguage);
        Predicate cultivarPredicate = this.createCultivarQueryPredicate(cropSource, query);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate namesPredicate = criteriaBuilder.or(plantNameQueryPredicate, productNamesPredicate, displayNameQueryPredicate, cultivarPredicate);
        return namesPredicate;
    }

    private Predicate createPlantNamesQueryPredicate(From<?, Crop> cropSource, String query, Optional<Language> queryLanguage) {
        Join<Crop, AgrovocPlant> plantPath = cropSource.join(Crop_.agrovocPlant, JoinType.LEFT);
        Predicate plantNameQueryPredicate = agrovocService.createPlantNameQueryPredicate(query, queryLanguage, plantPath);
        return plantNameQueryPredicate;
    }

    private Predicate createCultivarQueryPredicate(From<?, Crop> cropSource, String query) {
        Path<String> cultivarPath = cropSource.get(Crop_.cultivar);
        Predicate cultivarPredicate = searchService.createTextMatchPredicate(cultivarPath, query);
        return cultivarPredicate;
    }

    private Predicate createDisplayNameQueryPredicate(From<?, Crop> cropSource, String query, Optional<Language> queryLanguage) {
        ListJoin<Crop, LocalizedMessage> displayNameJoin = cropSource.join(Crop_.displayName);
        return searchService.createLocalizedTextMatchPredicate(displayNameJoin, query, queryLanguage);
    }

    private Predicate createPlantPredicate(From<?, Crop> cropSource, AgrovocPlant plant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<AgrovocPlant> plantPath = cropSource.join(Crop_.agrovocPlant, JoinType.LEFT);
        Predicate predicate = criteriaBuilder.equal(plantPath, plant);
        return predicate;
    }

    private void setCropAgrovocPlant(Crop crop, AgrovocPlant plant) {
        crop.setAgrovocPlant(plant);

        // Taxonomy english labels should be the latin ones either way
        AgrovocPlantData plantData = this.agrovocService.searchAgrovocPlantData(plant.getAgrovocNodeId(), Language.DEFAULT_LANGUAGE);
        Optional<String> speciesName = plantData.getSpeciesName();
        Optional<String> familyName = plantData.getFamilyName();
        Optional<String> subSpeciesName = plantData.getSubSpeciesName();

        familyName.ifPresent(crop::setFamily);
        speciesName.ifPresent(crop::setSpecies);
        subSpeciesName.ifPresent(crop::setSubSpecies);
    }
}
