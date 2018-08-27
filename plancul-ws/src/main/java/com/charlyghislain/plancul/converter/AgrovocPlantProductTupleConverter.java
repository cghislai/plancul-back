package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.api.domain.response.WsAgrovocPlantProduct;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.util.AcceptedLanguage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AgrovocPlantProductTupleConverter {

    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public WsAgrovocPlantProduct toWsPlantProductResult(PlantProductTupleResult plantProductTupleResult) {
        String matchedTerm = plantProductTupleResult.getMatchedTerm();
        String plantPreferredLabel = plantProductTupleResult.getPlantPreferredLabel();
        String plantURI = plantProductTupleResult.getPlantURI();
        String productURI = plantProductTupleResult.getProductURI();
        Language language = plantProductTupleResult.getLanguage();
        String productPreferredLabel = plantProductTupleResult.getProductPreferredLabel();

        WsLanguage wsLanguage = WsLanguage.fromCode(language.getCode())
                .orElseThrow(IllegalStateException::new);

        WsAgrovocPlantProduct wsAgrovocPlantProduct = new WsAgrovocPlantProduct();
        wsAgrovocPlantProduct.setLanguage(wsLanguage);
        wsAgrovocPlantProduct.setMatchedTerm(matchedTerm);
        wsAgrovocPlantProduct.setPlantPreferredLabel(plantPreferredLabel);
        wsAgrovocPlantProduct.setPlantURI(plantURI);
        wsAgrovocPlantProduct.setProductURI(productURI);
        wsAgrovocPlantProduct.setProductPreferredLabel(productPreferredLabel);

        return wsAgrovocPlantProduct;
    }

    public PlantProductTupleFilter fromWsPlantProductTupleQueryFilter(WsPlantProductTupleFilter wsFilter) {
        String queryString = wsFilter.getQueryString();

        PlantProductTupleFilter tupleFilter = new PlantProductTupleFilter();
        tupleFilter.setLanguage(acceptedLanguage);
        tupleFilter.setQueryString(queryString);
        return tupleFilter;
    }
}
