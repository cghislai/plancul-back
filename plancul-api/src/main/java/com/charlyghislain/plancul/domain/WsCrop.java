package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsCrop implements WsDomainEntity {

    private Long id;
    @NotNull
    private WsRef<WsAgrovocPlant> agrovocPlantWsRef;
    @NotNull
    private WsRef<WsAgrovocProduct> agrovocProductWsRef;
    @Size(max = 255)
    @Nullable
    private String cultivar;
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
    public WsRef<WsAgrovocPlant> getAgrovocPlantWsRef() {
        return agrovocPlantWsRef;
    }

    public void setAgrovocPlantWsRef(@NotNull WsRef<WsAgrovocPlant> agrovocPlantWsRef) {
        this.agrovocPlantWsRef = agrovocPlantWsRef;
    }

    @NotNull
    public WsRef<WsAgrovocProduct> getAgrovocProductWsRef() {
        return agrovocProductWsRef;
    }

    public void setAgrovocProductWsRef(@NotNull WsRef<WsAgrovocProduct> agrovocProductWsRef) {
        this.agrovocProductWsRef = agrovocProductWsRef;
    }

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public WsRef<WsTenant> getTenantRestriction() {
        return tenantRestriction;
    }

    public void setTenantRestriction(WsRef<WsTenant> tenantRestriction) {
        this.tenantRestriction = tenantRestriction;
    }
}
