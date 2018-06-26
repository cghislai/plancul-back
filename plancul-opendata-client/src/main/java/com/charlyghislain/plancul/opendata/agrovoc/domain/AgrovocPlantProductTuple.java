package com.charlyghislain.plancul.opendata.agrovoc.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.StringJoiner;

public class AgrovocPlantProductTuple {

    @NonNull
    private String matchedTerm;
    @NonNull
    private String plantPreferredLabel;
    @NonNull
    private String plantURI;
    @NonNull
    private String productURI;
    @NonNull
    private String language;

    public String getMatchedTerm() {
        return matchedTerm;
    }

    public void setMatchedTerm(String matchedTerm) {
        this.matchedTerm = matchedTerm;
    }

    public String getPlantPreferredLabel() {
        return plantPreferredLabel;
    }

    public void setPlantPreferredLabel(String plantPreferredLabel) {
        this.plantPreferredLabel = plantPreferredLabel;
    }

    public String getPlantURI() {
        return plantURI;
    }

    public void setPlantURI(String plantURI) {
        this.plantURI = plantURI;
    }

    public String getProductURI() {
        return productURI;
    }

    public void setProductURI(String productURI) {
        this.productURI = productURI;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgrovocPlantProductTuple.class.getSimpleName() + "[", "]")
                .add("matchedTerm='" + matchedTerm + "'")
                .add("plantPreferredLabel='" + plantPreferredLabel + "'")
                .add("plantURI='" + plantURI + "'")
                .add("productURI='" + productURI + "'")
                .add("language='" + language + "'")
                .toString();
    }
}
