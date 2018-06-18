package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.i18n.WsLocalizedMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AgrovocProductConverter implements ToWsDomainObjectConverter<AgrovocProduct, WsAgrovocProduct> {

    @Inject
    private LocalizedMessageConverter localizedMessageConverter;

    @Override
    public WsAgrovocProduct toWsEntity(AgrovocProduct entity) {
        List<LocalizedMessage> preferedLabel = entity.getPreferedLabel();
        String agrovocNodeId = entity.getAgrovocNodeId();
        List<LocalizedMessage> alternativeLabels = entity.getAlternativeLabels();
        Long id = entity.getId();

        List<WsLocalizedMessage> preferedWsLabels = preferedLabel.stream()
                .map(localizedMessageConverter::toWsEntity)
                .collect(Collectors.toList());
        List<WsLocalizedMessage> alternativeWsLabels = alternativeLabels.stream()
                .map(localizedMessageConverter::toWsEntity)
                .collect(Collectors.toList());

        WsAgrovocProduct wsAgrovocProduct = new WsAgrovocProduct();
        wsAgrovocProduct.setAgrovocNodeId(agrovocNodeId);
        wsAgrovocProduct.setAlternativeLabels(alternativeWsLabels);
        wsAgrovocProduct.setPreferedLabel(preferedWsLabels);
        wsAgrovocProduct.setId(id);

        return wsAgrovocProduct;
    }
}
