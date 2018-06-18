package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.i18n.WsLocalizedMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AgrovocPlantConverter implements ToWsDomainObjectConverter<AgrovocPlant, WsAgrovocPlant> {

    @Inject
    private LocalizedMessageConverter localizedMessageConverter;

    @Override
    public WsAgrovocPlant toWsEntity(AgrovocPlant entity) {
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

        WsAgrovocPlant wsAgrovocPlant = new WsAgrovocPlant();
        wsAgrovocPlant.setAgrovocNodeId(agrovocNodeId);
        wsAgrovocPlant.setAlternativeLabels(alternativeWsLabels);
        wsAgrovocPlant.setPreferedLabel(preferedWsLabels);
        wsAgrovocPlant.setId(id);

        return wsAgrovocPlant;
    }
}
