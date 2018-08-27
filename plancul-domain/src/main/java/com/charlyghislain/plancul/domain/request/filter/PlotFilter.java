package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.Tenant;

import java.util.Optional;

public class PlotFilter {
    private Tenant tenant;
    private String nameContains;

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Optional<String> getNameContains() {
        return Optional.ofNullable(nameContains);
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }
}
