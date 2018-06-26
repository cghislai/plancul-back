package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.api.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.domain.api.response.WsPlantProductResult;
import com.charlyghislain.plancul.domain.api.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.util.AcceptedLanguage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlantProductTupleConverter {

    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public WsPlantProductResult toWsPlantProductResult(PlantProductTupleResult plantProductTupleResult) {
        String matchedTerm = plantProductTupleResult.getMatchedTerm();
        String plantPreferredLabel = plantProductTupleResult.getPlantPreferredLabel();
        String plantURI = plantProductTupleResult.getPlantURI();
        String productURI = plantProductTupleResult.getProductURI();
        Language language = plantProductTupleResult.getLanguage();

        WsLanguage wsLanguage = WsLanguage.fromCode(language.getCode())
                .orElseThrow(IllegalStateException::new);

        WsPlantProductResult wsPlantProductResult = new WsPlantProductResult();
        wsPlantProductResult.setLanguage(wsLanguage);
        wsPlantProductResult.setMatchedTerm(matchedTerm);
        wsPlantProductResult.setPlantPreferredLabel(plantPreferredLabel);
        wsPlantProductResult.setPlantURI(plantURI);
        wsPlantProductResult.setProductURI(productURI);

        return wsPlantProductResult;
    }

    public PlantProductTupleFilter fromWsPlantProductTupleQueryFilter(WsPlantProductTupleFilter wsFilter) {
        String queryString = wsFilter.getQueryString();

        PlantProductTupleFilter tupleFilter = new PlantProductTupleFilter();
        tupleFilter.setLanguage(acceptedLanguage);
        tupleFilter.setQueryString(queryString);
        return tupleFilter;
    }
}
