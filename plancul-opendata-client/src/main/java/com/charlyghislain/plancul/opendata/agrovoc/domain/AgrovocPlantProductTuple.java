package com.charlyghislain.plancul.opendata.agrovoc.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.StringJoiner;

public class AgrovocPlantProductTuple {

    @NonNull
    private String matchedTerm;
    @NonNull
    private String plantPreferredLabel;
    @NonNull
    private String productPreferredLabel;
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

    public String getProductPreferredLabel() {
        return productPreferredLabel;
    }

    public void setProductPreferredLabel(String productPreferredLabel) {
        this.productPreferredLabel = productPreferredLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgrovocPlantProductTuple tuple = (AgrovocPlantProductTuple) o;

        if (!matchedTerm.equals(tuple.matchedTerm)) return false;
        if (!plantPreferredLabel.equals(tuple.plantPreferredLabel)) return false;
        if (!productPreferredLabel.equals(tuple.productPreferredLabel)) return false;
        if (!plantURI.equals(tuple.plantURI)) return false;
        if (!productURI.equals(tuple.productURI)) return false;
        return language.equals(tuple.language);
    }

    @Override
    public int hashCode() {
        int result = matchedTerm.hashCode();
        result = 31 * result + plantPreferredLabel.hashCode();
        result = 31 * result + productPreferredLabel.hashCode();
        result = 31 * result + plantURI.hashCode();
        result = 31 * result + productURI.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgrovocPlantProductTuple.class.getSimpleName() + "[", "]")
                .add("matchedTerm='" + matchedTerm + "'")
                .add("plantPreferredLabel='" + plantPreferredLabel + "'")
                .add("productPreferredLabel='" + productPreferredLabel + "'")
                .add("plantURI='" + plantURI + "'")
                .add("productURI='" + productURI + "'")
                .add("language='" + language + "'")
                .toString();
    }
}
