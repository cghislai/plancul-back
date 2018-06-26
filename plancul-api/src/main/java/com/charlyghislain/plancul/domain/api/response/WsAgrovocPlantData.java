package com.charlyghislain.plancul.domain.api.response;

import com.charlyghislain.plancul.domain.api.util.WsLanguage;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

public class WsAgrovocPlantData implements Serializable {

    private WsLanguage language;

    @Nullable
    private String speciesName;
    @Nullable
    private String speciesNodeURI;

    @Nullable
    private String familyName;
    @Nullable
    private String familyNodeURI;

    @Nullable
    private String subSpeciesName;
    @Nullable
    private String subSpeciesNodeURI;

    public WsLanguage getLanguage() {
        return language;
    }

    public void setLanguage(WsLanguage language) {
        this.language = language;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getSpeciesNodeURI() {
        return speciesNodeURI;
    }

    public void setSpeciesNodeURI(String speciesNodeURI) {
        this.speciesNodeURI = speciesNodeURI;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyNodeURI() {
        return familyNodeURI;
    }

    public void setFamilyNodeURI(String familyNodeURI) {
        this.familyNodeURI = familyNodeURI;
    }

    public String getSubSpeciesName() {
        return subSpeciesName;
    }

    public void setSubSpeciesName(String subSpeciesName) {
        this.subSpeciesName = subSpeciesName;
    }

    public String getSubSpeciesNodeURI() {
        return subSpeciesNodeURI;
    }

    public void setSubSpeciesNodeURI(String subSpeciesNodeURI) {
        this.subSpeciesNodeURI = subSpeciesNodeURI;
    }
}
