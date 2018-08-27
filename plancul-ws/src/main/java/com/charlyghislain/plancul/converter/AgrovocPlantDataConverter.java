package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.response.WsAgrovocPlantData;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;
import com.charlyghislain.plancul.util.AcceptedLanguage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AgrovocPlantDataConverter {

    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public WsAgrovocPlantData toWsAgrovocPlantData(AgrovocPlantData agrovocPlantData) {
        String language = agrovocPlantData.getLanguage();
        Optional<String> speciesName = agrovocPlantData.getSpeciesName();
        Optional<String> speciesNodeURI = agrovocPlantData.getSpeciesNodeURI();
        Optional<String> familyName = agrovocPlantData.getFamilyName();
        Optional<String> familyNodeURI = agrovocPlantData.getFamilyNodeURI();
        Optional<String> subSpeciesName = agrovocPlantData.getSubSpeciesName();
        Optional<String> subSpeciesNodeURI = agrovocPlantData.getSubSpeciesNodeURI();

        WsLanguage wsLanguage = WsLanguage.fromCode(language)
                .orElseThrow(IllegalStateException::new);

        WsAgrovocPlantData wsAgrovocPlantData = new WsAgrovocPlantData();
        wsAgrovocPlantData.setLanguage(wsLanguage);
        speciesName.ifPresent(wsAgrovocPlantData::setSpeciesName);
        speciesNodeURI.ifPresent(wsAgrovocPlantData::setSpeciesNodeURI);
        familyName.ifPresent(wsAgrovocPlantData::setFamilyName);
        familyNodeURI.ifPresent(wsAgrovocPlantData::setFamilyNodeURI);
        subSpeciesName.ifPresent(wsAgrovocPlantData::setSubSpeciesName);
        subSpeciesNodeURI.ifPresent(wsAgrovocPlantData::setSubSpeciesNodeURI);

        return wsAgrovocPlantData;
    }

}
