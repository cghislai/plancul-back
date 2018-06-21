package com.charlyghislain.plancul.domain.api.request;

import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.util.WsRef;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

public class WsCropCreationRequest implements Serializable {
    @NotNull
    private String agrovocPlantUri;
    @NotNull
    private String agrovocProductUri;
    private String cultivar;
    private WsRef<WsTenant> tenantRestrictionRef;

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

    public Optional<WsRef<WsTenant>> getTenantRestrictionRef() {
        return Optional.ofNullable(tenantRestrictionRef);
    }

    public void setTenantRestrictionRef(WsRef<WsTenant> tenantRestrictionRef) {
        this.tenantRestrictionRef = tenantRestrictionRef;
    }
}
