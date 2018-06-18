package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.i18n.WsLocalizedMessage;
import com.charlyghislain.plancul.domain.util.WsDomainEntity;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WsAgrovocPlant implements WsDomainEntity {

    @NotNull
    private Long id;
    @NotNull
    private String agrovocNodeId;
    @NotNull
    private List<WsLocalizedMessage> preferedLabel = new ArrayList<>();
    @NotNull
    private List<WsLocalizedMessage> alternativeLabels = new ArrayList<>();

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
    public List<WsLocalizedMessage> getPreferedLabel() {
        return preferedLabel;
    }

    public void setPreferedLabel(@NotNull List<WsLocalizedMessage> preferedLabel) {
        this.preferedLabel = preferedLabel;
    }

    @NotNull
    public List<WsLocalizedMessage> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(@NotNull List<WsLocalizedMessage> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }
}
