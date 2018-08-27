package com.charlyghislain.plancul.api.domain;


import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;

import javax.validation.constraints.NotNull;

public class WsLocalizedMessage implements WsDomainEntity {
    @NotNull
    private Long id;
    @NotNull
    private WsLanguage language;
    @NotNull
    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public WsLanguage getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull WsLanguage language) {
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
