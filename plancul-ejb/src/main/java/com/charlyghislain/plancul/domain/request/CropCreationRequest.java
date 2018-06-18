package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.Tenant;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class CropCreationRequest {

    @NotNull
    private String agrovocPlantUri;
    @NotNull
    private String agrovocProductUri;
    @Nullable
    private String cultivar;
    @Nullable
    private Tenant tenantRestriction;

    @NotNull
    public String getAgrovocPlantUri() {
        return agrovocPlantUri;
    }

    public void setAgrovocPlantUri(@NotNull String agrovocPlantUri) {
        this.agrovocPlantUri = agrovocPlantUri;
    }

    @NotNull
    public String getAgrovocProductUri() {
        return agrovocProductUri;
    }

    public void setAgrovocProductUri(@NotNull String agrovocProductUri) {
        this.agrovocProductUri = agrovocProductUri;
    }

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public Tenant getTenantRestriction() {
        return tenantRestriction;
    }

    public void setTenantRestriction(Tenant tenantRestriction) {
        this.tenantRestriction = tenantRestriction;
    }
}
