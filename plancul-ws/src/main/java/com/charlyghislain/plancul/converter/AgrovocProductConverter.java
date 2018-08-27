package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.api.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.sort.AgrovocProductSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AgrovocProductConverter implements ToWsDomainObjectConverter<AgrovocProduct, WsAgrovocProduct> {

    @Inject
    private LocalizedMessageConverter localizedMessageConverter;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @Override
    public WsAgrovocProduct toWsEntity(AgrovocProduct entity) {
        List<LocalizedMessage> preferedLabel = entity.getPreferedLabel();
        String agrovocNodeId = entity.getAgrovocNodeId();
        List<LocalizedMessage> alternativeLabels = entity.getAlternativeLabels();
        Long id = entity.getId();


        String preferedLabelValue = localizedMessageConverter.toLocalizedStrings(preferedLabel, acceptedLanguage)
                .stream()
                .findFirst()
                .orElse(null);
        List<String> alternativeLabelValues = localizedMessageConverter.toLocalizedStrings(alternativeLabels, acceptedLanguage);
        WsLanguage wsLanguage = WsLanguage.fromCode(acceptedLanguage.getCode()).orElseThrow(IllegalStateException::new);

        WsAgrovocProduct wsAgrovocProduct = new WsAgrovocProduct();
        wsAgrovocProduct.setAgrovocNodeId(agrovocNodeId);
        wsAgrovocProduct.setLanguage(wsLanguage);
        wsAgrovocProduct.setPreferedLabel(preferedLabelValue);
        wsAgrovocProduct.setAlternativeLabels(alternativeLabelValues);
        wsAgrovocProduct.setId(id);

        return wsAgrovocProduct;
    }

    @Override
    public Optional<Sort<AgrovocProduct>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, AgrovocProductSortField::valueOf);
    }
}
