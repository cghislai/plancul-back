package com.charlyghislain.plancul.domain.filter;

import com.charlyghislain.plancul.domain.Tenant;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class PlotFilter {
    @NotNull
    private Tenant tenant;
    private String nameContains;

    @NotNull
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
    }


    public Optional<String> getNameContains() {
        return Optional.ofNullable(nameContains);
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }
}
