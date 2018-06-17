package com.charlyghislain.plancul.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsPlantProductResult implements Serializable {
    @NotNull
    private String language;
    @NotNull
    private String plantLabel;
    @NotNull
    private String productLabel;
    @NotNull
    private String plantAgrovocUri;

    @NotNull
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull String language) {
        this.language = language;
    }

    @NotNull
    public String getPlantLabel() {
        return plantLabel;
    }

    public void setPlantLabel(@NotNull String plantLabel) {
        this.plantLabel = plantLabel;
    }

    @NotNull
    public String getProductLabel() {
        return productLabel;
    }

    public void setProductLabel(@NotNull String productLabel) {
        this.productLabel = productLabel;
    }

    @NotNull
    public String getPlantAgrovocUri() {
        return plantAgrovocUri;
    }

    public void setPlantAgrovocUri(@NotNull String plantAgrovocUri) {
        this.plantAgrovocUri = plantAgrovocUri;
    }

}
