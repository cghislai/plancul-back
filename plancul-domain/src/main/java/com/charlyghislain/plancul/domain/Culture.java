package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.ServiceManaged;
import com.charlyghislain.plancul.domain.validation.ValidHarvestDurations;
import com.charlyghislain.plancul.domain.validation.ValidNursingDuration;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@ValidNursingDuration
@ValidHarvestDurations
public class Culture implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private Tenant tenant;
    @NotNull
    @ManyToOne
    private Crop crop;
    @NotNull
    @ManyToOne
    private Bed bed;

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

    @NotNull
    @ServiceManaged
    private LocalDate firstHarvestDate;
    @NotNull
    @ServiceManaged
    private LocalDate lastHarvestDate;

    @NotNull
    @ServiceManaged
    private LocalDate germinationDate;
    @NotNull
    @ServiceManaged
    private LocalDate bedOccupancyStartDate;
    @NotNull
    @ServiceManaged
    private LocalDate bedOccupancyEndDate;

    @Nullable
    private String htmlNotes = "";

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private CultureNursing cultureNursing;
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private BedPreparation bedPreparation;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public Crop getCrop() {
        return crop;
    }

    public void setCrop(@NotNull Crop crop) {
        this.crop = crop;
    }

    @NotNull
    public Bed getBed() {
        return bed;
    }

    public void setBed(@NotNull Bed bed) {
        this.bed = bed;
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

    public Optional<CultureNursing> getCultureNursing() {
        return Optional.ofNullable(cultureNursing);
    }

    public void setCultureNursing(CultureNursing cultureNursing) {
        this.cultureNursing = cultureNursing;
    }

    public Optional<BedPreparation> getBedPreparation() {
        return Optional.ofNullable(bedPreparation);
    }

    public void setBedPreparation(BedPreparation bedPreparation) {
        this.bedPreparation = bedPreparation;
    }

    public Optional<String> getHtmlNotes() {
        return Optional.ofNullable(htmlNotes);
    }

    public void setHtmlNotes(String htmlNotes) {
        this.htmlNotes = htmlNotes;
    }

    public int getDaysUntilGermination() {
        return daysUntilGermination;
    }

    public void setDaysUntilGermination(int timeUntilGermination) {
        this.daysUntilGermination = timeUntilGermination;
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

    public BigDecimal getSeedSurfaceQuantity() {
        return seedSurfaceQuantity;
    }

    public void setSeedSurfaceQuantity(BigDecimal seedSurfaceQuantity) {
        this.seedSurfaceQuantity = seedSurfaceQuantity;
    }

    public BigDecimal getHarvestSurfaceQuantity() {
        return harvestSurfaceQuantity;
    }

    public void setHarvestSurfaceQuantity(BigDecimal harvestSurfaceQuantity) {
        this.harvestSurfaceQuantity = harvestSurfaceQuantity;
    }
}
