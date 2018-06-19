package com.charlyghislain.plancul.domain.request.filter;

import java.util.Optional;

public class TenantFilter {

    private String nameContains;

    public Optional<String> getNameContains() {
        return Optional.ofNullable(nameContains);
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

}
