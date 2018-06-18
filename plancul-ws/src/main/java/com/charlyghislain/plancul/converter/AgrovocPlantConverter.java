package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.LanguageContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AgrovocPlantConverter implements ToWsDomainObjectConverter<AgrovocPlant, WsAgrovocPlant> {

    @Inject
    private LocalizedMessageConverter localizedMessageConverter;
    @Inject
    @AcceptedLanguage
    private LanguageContainer acceptedLanguage;

    @Override
    public WsAgrovocPlant toWsEntity(AgrovocPlant entity) {
        List<LocalizedMessage> preferedLabel = entity.getPreferedLabel();
        String agrovocNodeId = entity.getAgrovocNodeId();
        List<LocalizedMessage> alternativeLabels = entity.getAlternativeLabels();
        Long id = entity.getId();

        Language language = acceptedLanguage.getLanguage();
        String preferedLabelValue = localizedMessageConverter.toLocalizedStrings(preferedLabel, language)
                .stream()
                .findFirst()
                .orElse(null);
        List<String> alternativeLabelValues = localizedMessageConverter.toLocalizedStrings(alternativeLabels, language);

        WsAgrovocPlant wsAgrovocPlant = new WsAgrovocPlant();
        wsAgrovocPlant.setAgrovocNodeId(agrovocNodeId);
        wsAgrovocPlant.setLanguage(language.getCode());
        wsAgrovocPlant.setPreferedLabel(preferedLabelValue);
        wsAgrovocPlant.setAlternativeLabels(alternativeLabelValues);
        wsAgrovocPlant.setId(id);

        return wsAgrovocPlant;
    }
}
