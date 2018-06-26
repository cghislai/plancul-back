package com.charlyghislain.plancul.domain.api.response;

import com.charlyghislain.plancul.domain.api.util.WsLanguage;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsAgrovocPlantProduct implements Serializable {

    @NotNull
    private WsLanguage language;
    @NotNull
    private String matchedTerm;
    @NotNull
    private String plantPreferredLabel;
    @NotNull
    private String productPreferredLabel;
    @NotNull
    private String plantURI;
    @NotNull
    private String productURI;

    @NotNull
    public WsLanguage getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull WsLanguage language) {
        this.language = language;
    }

    @NotNull
    public String getMatchedTerm() {
        return matchedTerm;
    }

    public void setMatchedTerm(@NotNull String matchedTerm) {
        this.matchedTerm = matchedTerm;
    }

    @NotNull
    public String getPlantPreferredLabel() {
        return plantPreferredLabel;
    }

    public void setPlantPreferredLabel(@NotNull String plantPreferredLabel) {
        this.plantPreferredLabel = plantPreferredLabel;
    }

    @NotNull
    public String getPlantURI() {
        return plantURI;
    }

    public void setPlantURI(@NotNull String plantURI) {
        this.plantURI = plantURI;
    }

    @NotNull
    public String getProductURI() {
        return productURI;
    }

    public void setProductURI(@NotNull String productURI) {
        this.productURI = productURI;
    }

    @NotNull
    public String getProductPreferredLabel() {
        return productPreferredLabel;
    }

    public void setProductPreferredLabel(@NotNull String productPreferredLabel) {
        this.productPreferredLabel = productPreferredLabel;
    }
}
