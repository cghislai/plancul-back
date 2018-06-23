package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.api.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.api.response.WsPlantProductResult;
import com.charlyghislain.plancul.util.ContentLanguage;
import com.charlyghislain.plancul.util.LanguageContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlantProductTupleConverter {

    @Inject
    @ContentLanguage
    private LanguageContainer contentLanguage;

    public WsPlantProductResult toWsPlantProductResult(PlantProductTupleResult tupleResult) {
        String language = tupleResult.getLanguage();
        String plantLabel = tupleResult.getPlantLabel();
        String productLabel = tupleResult.getProductLabel();
        String plantAgrovocUri = tupleResult.getPlantAgrovocUri();
        String productAgrovocUri = tupleResult.getProductAgrovocUri();

        WsPlantProductResult wsPlantProductResult = new WsPlantProductResult();
        wsPlantProductResult.setLanguage(language);
        wsPlantProductResult.setPlantAgrovocUri(plantAgrovocUri);
        wsPlantProductResult.setPlantLabel(plantLabel);
        wsPlantProductResult.setProductLabel(productLabel);
        wsPlantProductResult.setProductAgrovocUri(productAgrovocUri);
        return wsPlantProductResult;
    }

    public PlantProductTupleFilter fromWsPlantProductTupleQueryFilter(WsPlantProductTupleFilter wsFilter) {
        String queryString = wsFilter.getQueryString();

        Language language = contentLanguage.getLanguage();

        PlantProductTupleFilter tupleFilter = new PlantProductTupleFilter();
        tupleFilter.setLanguage(language);
        tupleFilter.setQueryString(queryString);
        return tupleFilter;
    }
}
