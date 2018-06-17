package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.WsPlantProductResult;
import com.charlyghislain.plancul.domain.agrovoc.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.filter.WsPlantProductTupleFilter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlantProductTupleConverter {

    public WsPlantProductResult toWsPlantProductResult(PlantProductTupleResult tupleResult) {
        String language = tupleResult.getLanguage();
        String plantLabel = tupleResult.getPlantLabel();
        String productLabel = tupleResult.getProductLabel();
        String plantAgrovocUri = tupleResult.getPlantAgrovocUri();

        WsPlantProductResult wsPlantProductResult = new WsPlantProductResult();
        wsPlantProductResult.setLanguage(language);
        wsPlantProductResult.setPlantAgrovocUri(plantAgrovocUri);
        wsPlantProductResult.setPlantLabel(plantLabel);
        wsPlantProductResult.setProductLabel(productLabel);
        return wsPlantProductResult;
    }

    public PlantProductTupleFilter fromWsPlantProductTupleQueryFilter(WsPlantProductTupleFilter wsFilter) {
        String language = wsFilter.getLanguage();
        String queryString = wsFilter.getQueryString();

        PlantProductTupleFilter tupleFilter = new PlantProductTupleFilter();
        tupleFilter.setLanguage(language);
        tupleFilter.setQueryString(queryString);
        return tupleFilter;
    }
}
