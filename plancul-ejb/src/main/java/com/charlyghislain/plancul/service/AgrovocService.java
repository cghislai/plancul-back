package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocPlantProductTupleSearchClient;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductResultTuple;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantResult;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocProductResult;
import com.charlyghislain.plancul.domain.agrovoc.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.filter.PlantProductTupleFilter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AgrovocService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private AgrovocPlantProductTupleSearchClient plantProductTupleSearchClient;

    public List<PlantProductTupleResult> searchPlantProducts(PlantProductTupleFilter filter) {
        String language = filter.getLanguage();
        String queryString = filter.getQueryString();

        List<AgrovocPlantProductResultTuple> plantProductTuples = plantProductTupleSearchClient.findPlantProductTuples(queryString, language);

        return plantProductTuples.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private PlantProductTupleResult mapToDomain(AgrovocPlantProductResultTuple agrovocPlantProductResultTuple) {
        AgrovocPlantResult plant = agrovocPlantProductResultTuple.getPlant();
        AgrovocProductResult product = agrovocPlantProductResultTuple.getProduct();
        String language = plant.getLanguage();
        String plantLabel = plant.getLabel();
        String plantNodeUri = plant.getNodeUri();
        String productLabel = product.getLabel();

        PlantProductTupleResult domainTuple = new PlantProductTupleResult();
        domainTuple.setLanguage(language);
        domainTuple.setPlantAgrovocUri(plantNodeUri);
        domainTuple.setPlantLabel(plantLabel);
        domainTuple.setProductLabel(productLabel);
        return domainTuple;
    }


}
