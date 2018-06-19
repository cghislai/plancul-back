package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.BedPreparationType;
import com.charlyghislain.plancul.domain.Tenant;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class CultureFilter {

    @Nullable
    private Tenant tenant;
    @Nullable
    private CropFilter cropFilter;
    @Nullable
    private BedFilter bedFilter;
    @Nullable
    private DateFilter sowingDate;
    @Nullable
    private DateFilter germinationDate;
    @Nullable
    private DateFilter firstHarvestDate;
    @Nullable
    private DateFilter lastHarvestDate;
    @Nullable
    private DateFilter transplantationDate;
    @Nullable
    private DateFilter bedOccupancyStartDate;
    @Nullable
    private DateFilter bedOccupancyEndDate;
    @Nullable
    private Boolean nursing;
    @Nullable
    private Boolean bedPreparation;
    @Nullable
    private BedPreparationType bedPreparationType;
    @Nullable
    private String notesQuery;

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Optional<CropFilter> getCropFilter() {
        return Optional.ofNullable(cropFilter);
    }

    public void setCropFilter(CropFilter cropFilter) {
        this.cropFilter = cropFilter;
    }

    public Optional<BedFilter> getBedFilter() {
        return Optional.ofNullable(bedFilter);
    }

    public void setBedFilter(BedFilter bedFilter) {
        this.bedFilter = bedFilter;
    }

    public Optional<DateFilter> getSowingDate() {
        return Optional.ofNullable(sowingDate);
    }

    public void setSowingDate(DateFilter sowingDate) {
        this.sowingDate = sowingDate;
    }

    public Optional<DateFilter> getGerminationDate() {
        return Optional.ofNullable(germinationDate);
    }

    public void setGerminationDate(DateFilter germinationDate) {
        this.germinationDate = germinationDate;
    }

    public Optional<DateFilter> getFirstHarvestDate() {
        return Optional.ofNullable(firstHarvestDate);
    }

    public void setFirstHarvestDate(DateFilter firstHarvestDate) {
        this.firstHarvestDate = firstHarvestDate;
    }

    public Optional<DateFilter> getLastHarvestDate() {
        return Optional.ofNullable(lastHarvestDate);
    }

    public void setLastHarvestDate(DateFilter lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    public Optional<DateFilter> getTransplantationDate() {
        return Optional.ofNullable(transplantationDate);
    }

    public void setTransplantationDate(DateFilter transplantationDate) {
        this.transplantationDate = transplantationDate;
    }

    public Optional<Boolean> getNursing() {
        return Optional.ofNullable(nursing);
    }

    public void setNursing(Boolean nursing) {
        this.nursing = nursing;
    }

    public Optional<BedPreparationType> getBedPreparationType() {
        return Optional.ofNullable(bedPreparationType);
    }

    public Optional<Boolean> getBedPreparation() {
        return Optional.ofNullable(bedPreparation);
    }

    public void setBedPreparation(Boolean bedPreparation) {
        this.bedPreparation = bedPreparation;
    }

    public void setBedPreparationType(BedPreparationType bedPreparationType) {
        this.bedPreparationType = bedPreparationType;
    }

    public Optional<DateFilter> getBedOccupancyStartDate() {
        return Optional.ofNullable(bedOccupancyStartDate);
    }

    public void setBedOccupancyStartDate(DateFilter bedOccupancyStartDate) {
        this.bedOccupancyStartDate = bedOccupancyStartDate;
    }

    public Optional<DateFilter> getBedOccupancyEndDate() {
        return Optional.ofNullable(bedOccupancyEndDate);
    }

    public void setBedOccupancyEndDate(DateFilter bedOccupancyEndDate) {
        this.bedOccupancyEndDate = bedOccupancyEndDate;
    }

    public Optional<String> getNotesQuery() {
        return Optional.ofNullable(notesQuery);
    }

    public void setNotesQuery(String notesQuery) {
        this.notesQuery = notesQuery;
    }
}

