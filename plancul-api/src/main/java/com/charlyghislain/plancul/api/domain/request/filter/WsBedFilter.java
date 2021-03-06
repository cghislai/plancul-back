package com.charlyghislain.plancul.api.domain.request.filter;

import com.charlyghislain.plancul.api.domain.WsBed;
import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.WsPlot;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

public class WsBedFilter implements Serializable {

    @Nullable
    private WsRef<WsTenant> tenantWsRef;
    @Nullable
    private String nameQuery;
    @Nullable
    private String patch;
    @Nullable
    private String patchQuery;
    @Nullable
    private WsRef<WsPlot> plotWsRef;
    @Nullable
    private WsRef<WsBed> exactBedWsRef;
    @Nullable
    private BigDecimal minSurface;

    public Optional<WsRef<WsTenant>> getTenantWsRef() {
        return Optional.ofNullable(tenantWsRef);
    }

    public void setTenantWsRef(@Nullable WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }

    public Optional<String> getNameQuery() {
        return Optional.ofNullable(nameQuery);
    }

    public void setNameQuery(@Nullable String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public Optional<WsRef<WsPlot>> getPlotWsRef() {
        return Optional.ofNullable(plotWsRef);
    }

    public void setPlotWsRef(@Nullable WsRef<WsPlot> plotWsRef) {
        this.plotWsRef = plotWsRef;
    }

    public Optional<WsRef<WsBed>> getExactBedWsRef() {
        return Optional.ofNullable(exactBedWsRef);
    }

    public void setExactBedWsRef(@Nullable WsRef<WsBed> exactBedWsRef) {
        this.exactBedWsRef = exactBedWsRef;
    }

    public Optional<String> getPatch() {
        return Optional.ofNullable(patch);
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    public Optional<String> getPatchQuery() {
        return Optional.ofNullable(patchQuery);
    }

    public void setPatchQuery(String patchQuery) {
        this.patchQuery = patchQuery;
    }

    public Optional<BigDecimal> getMinSurface() {
        return Optional.ofNullable(minSurface);
    }

    public void setMinSurface(BigDecimal minSurface) {
        this.minSurface = minSurface;
    }
}
