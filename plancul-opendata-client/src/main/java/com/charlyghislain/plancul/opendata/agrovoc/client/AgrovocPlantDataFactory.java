package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AgrovocPlantDataFactory {

    public static AgrovocPlantData createAgrovocPlantData(
            Resource familyResource, Literal familyLabel,
            Resource speciesResource, Literal speciesLabel,
            @Nullable Resource subspeciesResource, @Nullable Literal subspeciesLabel) {

        AgrovocPlantData plantData = new AgrovocPlantData();
        plantData.setLanguage(familyLabel.getLanguage());

        plantData.setFamilyNodeURI(familyResource.getURI());
        plantData.setFamilyName(familyLabel.getString());

        plantData.setSpeciesNodeURI(speciesResource.getURI());
        plantData.setSpeciesName(speciesLabel.getString());

        if (subspeciesResource != null && subspeciesLabel != null) {
            plantData.setSubSpeciesNodeURI(subspeciesResource.getURI());
            plantData.setSubSpeciesName(subspeciesLabel.getString());
        }
        return plantData;
    }
}
