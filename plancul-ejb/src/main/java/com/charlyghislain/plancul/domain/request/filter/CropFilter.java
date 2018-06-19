package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class CropFilter {

    @Nullable
    private Tenant tenant;
    @Nullable
    private Crop exactCrop;
    @Nullable
    private AgrovocPlant plant;
    @Nullable
    private String namesQuery;
    @Nullable
    private String plantQuery;
    @Nullable
    private String cultivarQuery;
    @Nullable
    private Language queryLanguage;
    @Nullable
    private Boolean shared;

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Optional<String> getNamesQuery() {
        return Optional.ofNullable(namesQuery);
    }

    public void setNamesQuery(String namesQuery) {
        this.namesQuery = namesQuery;
    }

    public Optional<String> getPlantQuery() {
        return Optional.ofNullable(plantQuery);
    }

    public void setPlantQuery(String plantQuery) {
        this.plantQuery = plantQuery;
    }

    public Optional<String> getCultivarQuery() {
        return Optional.ofNullable(cultivarQuery);
    }

    public void setCultivarQuery(String cultivarQuery) {
        this.cultivarQuery = cultivarQuery;
    }

    public Optional<Boolean> getShared() {
        return Optional.ofNullable(shared);
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Optional<Crop> getExactCrop() {
        return Optional.ofNullable(exactCrop);
    }

    public void setExactCrop(Crop exactCrop) {
        this.exactCrop = exactCrop;
    }

    public Optional<AgrovocPlant> getPlant() {
        return Optional.ofNullable(plant);
    }

    public void setPlant(AgrovocPlant plant) {
        this.plant = plant;
    }

    public Optional<Language> getQueryLanguage() {
        return Optional.ofNullable(queryLanguage);
    }

    public void setQueryLanguage(Language queryLanguage) {
        this.queryLanguage = queryLanguage;
    }
}
