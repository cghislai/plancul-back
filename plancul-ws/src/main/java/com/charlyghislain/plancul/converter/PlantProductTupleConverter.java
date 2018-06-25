package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.api.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.domain.api.response.WsPlantProductResult;
import com.charlyghislain.plancul.domain.api.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.util.ContentLanguage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlantProductTupleConverter {

    @Inject
    @ContentLanguage
    private Language contentLanguage;

    public WsPlantProductResult toWsPlantProductResult(PlantProductTupleResult tupleResult) {
        Language language = tupleResult.getLanguage();
        String plantLabel = tupleResult.getPlantLabel();
        String productLabel = tupleResult.getProductLabel();
        String plantAgrovocUri = tupleResult.getPlantAgrovocUri();
        String productAgrovocUri = tupleResult.getProductAgrovocUri();

        WsLanguage wsLanguage = WsLanguage.fromCode(language.getCode())
                .orElseThrow(IllegalStateException::new);

        WsPlantProductResult wsPlantProductResult = new WsPlantProductResult();
        wsPlantProductResult.setLanguage(wsLanguage);
        wsPlantProductResult.setPlantAgrovocUri(plantAgrovocUri);
        wsPlantProductResult.setPlantLabel(plantLabel);
        wsPlantProductResult.setProductLabel(productLabel);
        wsPlantProductResult.setProductAgrovocUri(productAgrovocUri);
        return wsPlantProductResult;
    }

    public PlantProductTupleFilter fromWsPlantProductTupleQueryFilter(WsPlantProductTupleFilter wsFilter) {
        String queryString = wsFilter.getQueryString();

        PlantProductTupleFilter tupleFilter = new PlantProductTupleFilter();
        tupleFilter.setLanguage(contentLanguage);
        tupleFilter.setQueryString(queryString);
        return tupleFilter;
    }
}
