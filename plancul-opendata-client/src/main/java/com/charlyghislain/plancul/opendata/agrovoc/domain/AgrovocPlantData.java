package com.charlyghislain.plancul.opendata.agrovoc.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class AgrovocPlantData {

    private String language;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Optional<String> getSpeciesName() {
        return Optional.ofNullable(speciesName);
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public Optional<String> getSpeciesNodeURI() {
        return Optional.ofNullable(speciesNodeURI);
    }

    public void setSpeciesNodeURI(String speciesNodeURI) {
        this.speciesNodeURI = speciesNodeURI;
    }

    public Optional<String> getFamilyName() {
        return Optional.ofNullable(familyName);
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Optional<String> getFamilyNodeURI() {
        return Optional.ofNullable(familyNodeURI);
    }

    public void setFamilyNodeURI(String familyNodeURI) {
        this.familyNodeURI = familyNodeURI;
    }

    public Optional<String> getSubSpeciesName() {
        return Optional.ofNullable(subSpeciesName);
    }

    public void setSubSpeciesName(String subSpeciesName) {
        this.subSpeciesName = subSpeciesName;
    }

    public Optional<String> getSubSpeciesNodeURI() {
        return Optional.ofNullable(subSpeciesNodeURI);
    }

    public void setSubSpeciesNodeURI(String subSpeciesNodeURI) {
        this.subSpeciesNodeURI = subSpeciesNodeURI;
    }
}
