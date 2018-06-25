package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.api.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.api.util.WsLanguage;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.sort.AgrovocPlantSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AgrovocPlantConverter implements ToWsDomainObjectConverter<AgrovocPlant, WsAgrovocPlant> {

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private LocalizedMessageConverter localizedMessageConverter;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public AgrovocPlant load(WsRef<WsAgrovocPlant> ref) {
        return agrovocService.findAgrovocPlantById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsAgrovocPlant toWsEntity(AgrovocPlant entity) {
        List<LocalizedMessage> preferedLabel = entity.getPreferedLabel();
        String agrovocNodeId = entity.getAgrovocNodeId();
        List<LocalizedMessage> alternativeLabels = entity.getAlternativeLabels();
        Long id = entity.getId();

        WsLanguage wsLanguage = WsLanguage.fromCode(acceptedLanguage.getCode()).orElseThrow(IllegalStateException::new);
        String preferedLabelValue = localizedMessageConverter.toLocalizedStrings(preferedLabel, acceptedLanguage)
                .stream()
                .findFirst()
                .orElse(null);
        List<String> alternativeLabelValues = localizedMessageConverter.toLocalizedStrings(alternativeLabels, acceptedLanguage);

        WsAgrovocPlant wsAgrovocPlant = new WsAgrovocPlant();
        wsAgrovocPlant.setAgrovocNodeId(agrovocNodeId);
        wsAgrovocPlant.setLanguage(wsLanguage);
        wsAgrovocPlant.setPreferedLabel(preferedLabelValue);
        wsAgrovocPlant.setAlternativeLabels(alternativeLabelValues);
        wsAgrovocPlant.setId(id);

        return wsAgrovocPlant;
    }

    @Override
    public Optional<Sort<AgrovocPlant>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, AgrovocPlantSortField::valueOf);
    }
}
