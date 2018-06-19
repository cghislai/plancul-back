package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.WsCrop;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Optional;

public class WsCropFilter implements Serializable {

    @Nullable
    private WsRef<WsTenant> tenantWsRef;
    @Nullable
    private WsRef<WsCrop> exactCropWsRef;
    @Nullable
    private WsRef<WsAgrovocPlant> plantWsRef;
    @Nullable
    private String namesQuery;
    @Nullable
    private String plantQuery;
    @Nullable
    private String cultivarQuery;
    @Nullable
    private Boolean shared;

    public Optional<WsRef<WsTenant>> getTenantWsRef() {
        return Optional.ofNullable(tenantWsRef);
    }

    public void setTenantWsRef(@Nullable WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }

    public Optional<WsRef<WsCrop>> getExactCropWsRef() {
        return Optional.ofNullable(exactCropWsRef);
    }

    public void setExactCropWsRef(@Nullable WsRef<WsCrop> exactCropWsRef) {
        this.exactCropWsRef = exactCropWsRef;
    }

    public Optional<WsRef<WsAgrovocPlant>> getPlantWsRef() {
        return Optional.ofNullable(plantWsRef);
    }

    public void setPlantWsRef(@Nullable WsRef<WsAgrovocPlant> plantWsRef) {
        this.plantWsRef = plantWsRef;
    }

    public Optional<String> getNamesQuery() {
        return Optional.ofNullable(namesQuery);
    }

    public void setNamesQuery(@Nullable String namesQuery) {
        this.namesQuery = namesQuery;
    }

    public Optional<String> getPlantQuery() {
        return Optional.ofNullable(plantQuery);
    }

    public void setPlantQuery(@Nullable String plantQuery) {
        this.plantQuery = plantQuery;
    }

    public Optional<String> getCultivarQuery() {
        return Optional.ofNullable(cultivarQuery);
    }

    public void setCultivarQuery(@Nullable String cultivarQuery) {
        this.cultivarQuery = cultivarQuery;
    }

    public Optional<Boolean> getShared() {
        return Optional.ofNullable(shared);
    }

    public void setShared(@Nullable Boolean shared) {
        this.shared = shared;
    }
}
