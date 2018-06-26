package com.charlyghislain.plancul.domain.api;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsCrop implements WsDomainEntity {

    private Long id;
    @NotNull
    @Size(max = 255)
    private String displayName;
    @NotNull
    @Size(max = 255)
    private String family;
    @NotNull
    @Size(max = 255)
    private String species;

    @Nullable
    @Size(max = 255)
    private String subSpecies;
    @Nullable
    @Size(max = 255)
    private String cultivar;

    @Nullable
    private WsRef<WsAgrovocPlant> agrovocPlantWsRef;
    @Nullable
    private WsRef<WsAgrovocProduct> agrovocProductWsRef;
    @Nullable
    private WsRef<WsTenant> tenantRestriction;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String getFamily() {
        return family;
    }

    public void setFamily(@NotNull String family) {
        this.family = family;
    }

    @NotNull
    public String getSpecies() {
        return species;
    }

    public void setSpecies(@NotNull String species) {
        this.species = species;
    }

    public String getSubSpecies() {
        return subSpecies;
    }

    public void setSubSpecies(String subSpecies) {
        this.subSpecies = subSpecies;
    }

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public WsRef<WsAgrovocPlant> getAgrovocPlantWsRef() {
        return agrovocPlantWsRef;
    }

    public void setAgrovocPlantWsRef(WsRef<WsAgrovocPlant> agrovocPlantWsRef) {
        this.agrovocPlantWsRef = agrovocPlantWsRef;
    }

    public WsRef<WsAgrovocProduct> getAgrovocProductWsRef() {
        return agrovocProductWsRef;
    }

    public void setAgrovocProductWsRef(WsRef<WsAgrovocProduct> agrovocProductWsRef) {
        this.agrovocProductWsRef = agrovocProductWsRef;
    }

    public WsRef<WsTenant> getTenantRestriction() {
        return tenantRestriction;
    }

    public void setTenantRestriction(WsRef<WsTenant> tenantRestriction) {
        this.tenantRestriction = tenantRestriction;
    }
}
