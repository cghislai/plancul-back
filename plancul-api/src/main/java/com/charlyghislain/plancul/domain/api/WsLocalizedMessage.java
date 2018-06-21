package com.charlyghislain.plancul.domain.api;


import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;

import javax.validation.constraints.NotNull;

public class WsLocalizedMessage implements WsDomainEntity {
    @NotNull
    private Long id;
    @NotNull
    private String languageCode;
    @NotNull
    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
