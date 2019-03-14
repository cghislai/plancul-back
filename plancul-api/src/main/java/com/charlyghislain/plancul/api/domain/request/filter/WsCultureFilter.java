package com.charlyghislain.plancul.api.domain.request.filter;

import com.charlyghislain.plancul.api.domain.WsBedPreparationType;
import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Optional;

public class WsCultureFilter implements Serializable {
    @Nullable
    private WsRef<WsTenant> tenantWsRef;
    @Nullable
    private WsCropFilter cropFilter;
    @Nullable
    private WsBedFilter bedFilter;
    @Nullable
    private WsDateFilter sowingDate;
    @Nullable
    private WsDateFilter germinationDate;
    @Nullable
    private WsDateFilter firstHarvestDate;
    @Nullable
    private WsDateFilter lastHarvestDate;
    @Nullable
    private WsDateFilter transplantationDate;
    @Nullable
    private WsDateFilter bedOccupancyStartDate;
    @Nullable
    private WsDateFilter bedOccupancyEndDate;
    @Nullable
    private WsDateFilter startDate;
    @Nullable
    private WsDateFilter endDate;
    @Nullable
    private Boolean nursing;
    @Nullable
    private Boolean bedPreparation;
    @Nullable
    private WsBedPreparationType bedPreparationType;
    @Nullable
    private String notesQuery;

    public Optional<WsRef<WsTenant>> getTenantWsRef() {
        return Optional.ofNullable(tenantWsRef);
    }

    public void setTenantWsRef(WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }

    public Optional<WsCropFilter> getCropFilter() {
        return Optional.ofNullable(cropFilter);
    }

    public void setCropFilter(WsCropFilter cropFilter) {
        this.cropFilter = cropFilter;
    }

    public Optional<WsBedFilter> getBedFilter() {
        return Optional.ofNullable(bedFilter);
    }

    public void setBedFilter(WsBedFilter bedFilter) {
        this.bedFilter = bedFilter;
    }

    public Optional<WsDateFilter> getSowingDate() {
        return Optional.ofNullable(sowingDate);
    }

    public void setSowingDate(WsDateFilter sowingDate) {
        this.sowingDate = sowingDate;
    }

    public Optional<WsDateFilter> getGerminationDate() {
        return Optional.ofNullable(germinationDate);
    }

    public void setGerminationDate(WsDateFilter germinationDate) {
        this.germinationDate = germinationDate;
    }

    public Optional<WsDateFilter> getFirstHarvestDate() {
        return Optional.ofNullable(firstHarvestDate);
    }

    public void setFirstHarvestDate(WsDateFilter firstHarvestDate) {
        this.firstHarvestDate = firstHarvestDate;
    }

    public Optional<WsDateFilter> getLastHarvestDate() {
        return Optional.ofNullable(lastHarvestDate);
    }

    public void setLastHarvestDate(WsDateFilter lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    public Optional<WsDateFilter> getTransplantationDate() {
        return Optional.ofNullable(transplantationDate);
    }

    public void setTransplantationDate(WsDateFilter transplantationDate) {
        this.transplantationDate = transplantationDate;
    }

    public Optional<WsDateFilter> getBedOccupancyStartDate() {
        return Optional.ofNullable(bedOccupancyStartDate);
    }

    public void setBedOccupancyStartDate(WsDateFilter bedOccupancyStartDate) {
        this.bedOccupancyStartDate = bedOccupancyStartDate;
    }

    public Optional<WsDateFilter> getBedOccupancyEndDate() {
        return Optional.ofNullable(bedOccupancyEndDate);
    }

    public void setBedOccupancyEndDate(WsDateFilter bedOccupancyEndDate) {
        this.bedOccupancyEndDate = bedOccupancyEndDate;
    }

    public Optional<Boolean> getNursing() {
        return Optional.ofNullable(nursing);
    }

    public void setNursing(Boolean nursing) {
        this.nursing = nursing;
    }

    public Optional<Boolean> getBedPreparation() {
        return Optional.ofNullable(bedPreparation);
    }

    public void setBedPreparation(Boolean bedPreparation) {
        this.bedPreparation = bedPreparation;
    }

    public Optional<WsBedPreparationType> getBedPreparationType() {
        return Optional.ofNullable(bedPreparationType);
    }

    public void setBedPreparationType(WsBedPreparationType bedPreparationType) {
        this.bedPreparationType = bedPreparationType;
    }

    public Optional<String> getNotesQuery() {
        return Optional.ofNullable(notesQuery);
    }

    public void setNotesQuery(String notesQuery) {
        this.notesQuery = notesQuery;
    }

    public Optional<WsDateFilter> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public void setStartDate(WsDateFilter startDate) {
        this.startDate = startDate;
    }

    public Optional<WsDateFilter> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public void setEndDate(WsDateFilter endDate) {
        this.endDate = endDate;
    }
}
