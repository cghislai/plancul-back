package com.charlyghislain.plancul.domain.api;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
    @NotNull
    private LocalDate germinationDate;
    @NotNull
    private LocalDate firstHarvestDate;
    @NotNull
    private LocalDate lastHarvestDate;
    @Nullable
    private LocalDate bedOccupancyStartDate;
    @Nullable
    private LocalDate bedOccupancyEndDate;

    @NotNull
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

    public void setTenantWsRef( WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }


    public WsRef<WsCrop> getCropWsRef() {
        return cropWsRef;
    }

    public void setCropWsRef( WsRef<WsCrop> cropWsRef) {
        this.cropWsRef = cropWsRef;
    }


    public WsRef<WsBed> getBedWsRef() {
        return bedWsRef;
    }

    public void setBedWsRef( WsRef<WsBed> bedWsRef) {
        this.bedWsRef = bedWsRef;
    }


    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate( LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }


    public LocalDate getGerminationDate() {
        return germinationDate;
    }

    public void setGerminationDate( LocalDate germinationDate) {
        this.germinationDate = germinationDate;
    }


    public LocalDate getFirstHarvestDate() {
        return firstHarvestDate;
    }

    public void setFirstHarvestDate( LocalDate firstHarvestDate) {
        this.firstHarvestDate = firstHarvestDate;
    }


    public LocalDate getLastHarvestDate() {
        return lastHarvestDate;
    }

    public void setLastHarvestDate( LocalDate lastHarvestDate) {
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

    public void setHtmlNotes( String htmlNotes) {
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
}
