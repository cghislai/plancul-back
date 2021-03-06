package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.util.Optional;

public class BedFilter {

    @Nullable
    private Tenant tenant;
    @Nullable
    private String nameQuery;
    @Nullable
    private Plot plot;
    @Nullable
    private Bed exactBed;
    @Nullable
    private String patchQuery;
    @Nullable
    private String patch;
    @Nullable
    private BigDecimal minSurface;

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Optional<String> getNameQuery() {
        return Optional.ofNullable(nameQuery);
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    public Optional<Plot> getPlot() {
        return Optional.ofNullable(plot);
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    public Optional<Bed> getExactBed() {
        return Optional.ofNullable(exactBed);
    }

    public void setExactBed(Bed exactBed) {
        this.exactBed = exactBed;
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
