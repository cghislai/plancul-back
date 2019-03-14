package com.charlyghislain.plancul.api.domain;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class WsCulture implements WsDomainEntity {

    private Long id;
    @NotNull
    private WsRef<WsTenant> tenantWsRef;
    @NotNull
    private WsRef<WsCrop> cropWsRef;
    @NotNull
    private WsRef<WsBed> bedWsRef;

    @NotNull
    private LocalDate sowingDate;
    @Min(1)
    private int daysUntilGermination;
    @Min(1)
    private int daysUntilFirstHarvest;
    @Min(1)
    private int harvestDaysDuration;
    @NotNull
    private BigDecimal seedSurfaceQuantity;
    @NotNull
    private BigDecimal harvestSurfaceQuantity;

    @Nullable
    private LocalDate firstHarvestDate;
    @Nullable
    private LocalDate lastHarvestDate;
    @Nullable
    private LocalDate germinationDate;
    @Nullable
    private LocalDate bedOccupancyStartDate;
    @Nullable
    private LocalDate bedOccupancyEndDate;

    @Nullable
    private String htmlNotes;

    @Nullable
    private WsCultureNursing cultureNursing;
    @Nullable
    private WsBedPreparation bedPreparation;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public WsRef<WsTenant> getTenantWsRef() {
        return tenantWsRef;
    }

    public void setTenantWsRef(WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }


    public WsRef<WsCrop> getCropWsRef() {
        return cropWsRef;
    }

    public void setCropWsRef(WsRef<WsCrop> cropWsRef) {
        this.cropWsRef = cropWsRef;
    }


    public WsRef<WsBed> getBedWsRef() {
        return bedWsRef;
    }

    public void setBedWsRef(WsRef<WsBed> bedWsRef) {
        this.bedWsRef = bedWsRef;
    }


    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }


    public LocalDate getGerminationDate() {
        return germinationDate;
    }

    public void setGerminationDate(LocalDate germinationDate) {
        this.germinationDate = germinationDate;
    }


    public LocalDate getFirstHarvestDate() {
        return firstHarvestDate;
    }

    public void setFirstHarvestDate(LocalDate firstHarvestDate) {
        this.firstHarvestDate = firstHarvestDate;
    }


    public LocalDate getLastHarvestDate() {
        return lastHarvestDate;
    }

    public void setLastHarvestDate(LocalDate lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    public LocalDate getBedOccupancyStartDate() {
        return bedOccupancyStartDate;
    }

    public void setBedOccupancyStartDate(LocalDate bedOccupancyStartDate) {
        this.bedOccupancyStartDate = bedOccupancyStartDate;
    }

    public LocalDate getBedOccupancyEndDate() {
        return bedOccupancyEndDate;
    }

    public void setBedOccupancyEndDate(LocalDate bedOccupancyEndDate) {
        this.bedOccupancyEndDate = bedOccupancyEndDate;
    }


    public String getHtmlNotes() {
        return htmlNotes;
    }

    public void setHtmlNotes(String htmlNotes) {
        this.htmlNotes = htmlNotes;
    }

    public WsCultureNursing getCultureNursing() {
        return cultureNursing;
    }

    public void setCultureNursing(WsCultureNursing cultureNursing) {
        this.cultureNursing = cultureNursing;
    }

    public WsBedPreparation getBedPreparation() {
        return bedPreparation;
    }

    public void setBedPreparation(WsBedPreparation bedPreparation) {
        this.bedPreparation = bedPreparation;
    }

    public int getDaysUntilGermination() {
        return daysUntilGermination;
    }

    public void setDaysUntilGermination(int daysUntilGermination) {
        this.daysUntilGermination = daysUntilGermination;
    }

    public int getDaysUntilFirstHarvest() {
        return daysUntilFirstHarvest;
    }

    public void setDaysUntilFirstHarvest(int daysUntilFirstHarvest) {
        this.daysUntilFirstHarvest = daysUntilFirstHarvest;
    }

    public int getHarvestDaysDuration() {
        return harvestDaysDuration;
    }

    public void setHarvestDaysDuration(int harvestDaysDuration) {
        this.harvestDaysDuration = harvestDaysDuration;
    }

    @NotNull
    public BigDecimal getSeedSurfaceQuantity() {
        return seedSurfaceQuantity;
    }

    public void setSeedSurfaceQuantity(@NotNull BigDecimal seedSurfaceQuantity) {
        this.seedSurfaceQuantity = seedSurfaceQuantity;
    }

    @NotNull
    public BigDecimal getHarvestSurfaceQuantity() {
        return harvestSurfaceQuantity;
    }

    public void setHarvestSurfaceQuantity(@NotNull BigDecimal harvestSurfaceQuantity) {
        this.harvestSurfaceQuantity = harvestSurfaceQuantity;
    }
}
