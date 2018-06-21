package com.charlyghislain.plancul.domain.api;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private LocalDate germinationDate;
    @NotNull
    private LocalDate firstHarvestDate;
    @NotNull
    private LocalDate lastHarvestDate;
    @NotNull
    private LocalDate bedOccupancyStartDate;
    @NotNull
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

    @NotNull
    public WsRef<WsTenant> getTenantWsRef() {
        return tenantWsRef;
    }

    public void setTenantWsRef(@NotNull WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }

    @NotNull
    public WsRef<WsCrop> getCropWsRef() {
        return cropWsRef;
    }

    public void setCropWsRef(@NotNull WsRef<WsCrop> cropWsRef) {
        this.cropWsRef = cropWsRef;
    }

    @NotNull
    public WsRef<WsBed> getBedWsRef() {
        return bedWsRef;
    }

    public void setBedWsRef(@NotNull WsRef<WsBed> bedWsRef) {
        this.bedWsRef = bedWsRef;
    }

    @NotNull
    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(@NotNull LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }

    @NotNull
    public LocalDate getGerminationDate() {
        return germinationDate;
    }

    public void setGerminationDate(@NotNull LocalDate germinationDate) {
        this.germinationDate = germinationDate;
    }

    @NotNull
    public LocalDate getFirstHarvestDate() {
        return firstHarvestDate;
    }

    public void setFirstHarvestDate(@NotNull LocalDate firstHarvestDate) {
        this.firstHarvestDate = firstHarvestDate;
    }

    @NotNull
    public LocalDate getLastHarvestDate() {
        return lastHarvestDate;
    }

    public void setLastHarvestDate(@NotNull LocalDate lastHarvestDate) {
        this.lastHarvestDate = lastHarvestDate;
    }

    @NotNull
    public LocalDate getBedOccupancyStartDate() {
        return bedOccupancyStartDate;
    }

    public void setBedOccupancyStartDate(@NotNull LocalDate bedOccupancyStartDate) {
        this.bedOccupancyStartDate = bedOccupancyStartDate;
    }

    @NotNull
    public LocalDate getBedOccupancyEndDate() {
        return bedOccupancyEndDate;
    }

    public void setBedOccupancyEndDate(@NotNull LocalDate bedOccupancyEndDate) {
        this.bedOccupancyEndDate = bedOccupancyEndDate;
    }

    @NotNull
    public String getHtmlNotes() {
        return htmlNotes;
    }

    public void setHtmlNotes(@NotNull String htmlNotes) {
        this.htmlNotes = htmlNotes;
    }

    public Optional<WsCultureNursing> getCultureNursing() {
        return Optional.ofNullable(cultureNursing);
    }

    public void setCultureNursing(WsCultureNursing cultureNursing) {
        this.cultureNursing = cultureNursing;
    }

    public Optional<WsBedPreparation> getBedPreparation() {
        return Optional.ofNullable(bedPreparation);
    }

    public void setBedPreparation(WsBedPreparation bedPreparation) {
        this.bedPreparation = bedPreparation;
    }
}
