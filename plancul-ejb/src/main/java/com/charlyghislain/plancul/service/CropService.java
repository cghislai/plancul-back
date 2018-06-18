package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.AgrovocPlantFilter;
import com.charlyghislain.plancul.domain.request.filter.AgrovocProductFilter;
import com.charlyghislain.plancul.domain.result.SearchResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        return Optional.ofNullable(found);
    }

    public Crop createPlot(CropCreationRequest cropCreationRequest) {
        // TODO: ask FAO sparql if plant produces product - or skip the product part altogether.
        // The product should probably not be linked to a crop, but rather a crop-yield of some sort.
        // It is kept here for the moment as it is useful to search crops by their product names (Solanum tuberosum vs potato)
        String agrovocPlantUri = cropCreationRequest.getAgrovocPlantUri();
        String agrovocProductUri = cropCreationRequest.getAgrovocProductUri();
        String cultivar = cropCreationRequest.getCultivar();
        Tenant tenantRestriction = cropCreationRequest.getTenantRestriction();

        AgrovocPlant agrovocPlant = this.getOrCreateAgrovocPlant(agrovocPlantUri);
        AgrovocProduct agrovocProduct = this.getOrCreateAgrovocProduct(agrovocProductUri);

        Crop crop = new Crop();
        crop.setAgrovocPlant(agrovocPlant);
        crop.setAgrovocProduct(agrovocProduct);
        crop.setCultivar(cultivar);
        crop.setTenant(tenantRestriction);

        Crop managedCrop = entityManager.merge(crop);
        return managedCrop;
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

}
