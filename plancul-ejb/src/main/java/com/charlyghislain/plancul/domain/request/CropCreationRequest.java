package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.Tenant;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Optional;

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

    public Optional<String> getCultivar() {
        return Optional.ofNullable(cultivar);
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public Optional<Tenant> getTenantRestriction() {
        return Optional.ofNullable(tenantRestriction);
    }

    public void setTenantRestriction(Tenant tenantRestriction) {
        this.tenantRestriction = tenantRestriction;
    }
}
