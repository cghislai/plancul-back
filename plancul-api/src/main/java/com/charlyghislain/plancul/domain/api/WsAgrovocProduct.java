package com.charlyghislain.plancul.domain.api;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WsAgrovocProduct implements WsDomainEntity {

    @NotNull
    private Long id;
    @NotNull
    private String agrovocNodeId;
    @NotNull
    private String language;
    @NotNull
    private String preferedLabel;
    @NotNull
    private List<String> alternativeLabels = new ArrayList<>();

    @Override
    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getAgrovocNodeId() {
        return agrovocNodeId;
    }

    public void setAgrovocNodeId(@NotNull String agrovocNodeId) {
        this.agrovocNodeId = agrovocNodeId;
    }

    @NotNull
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull String language) {
        this.language = language;
    }

    @NotNull
    public String getPreferedLabel() {
        return preferedLabel;
    }

    public void setPreferedLabel(@NotNull String preferedLabel) {
        this.preferedLabel = preferedLabel;
    }

    @NotNull
    public List<String> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(@NotNull List<String> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }
}
