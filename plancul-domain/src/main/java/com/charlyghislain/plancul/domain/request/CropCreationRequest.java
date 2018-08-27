package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

public class CropCreationRequest {

    @NotNull
    @Size(max = 255)
    private String displayName;
    @NotNull
    @Size(max = 255)
    private String family;
    @NotNull
    @Size(max = 255)
    private String species;
    @NotNull
    private Tenant tenant;
    private boolean shared;
    @NotNull
    private Language language;

    @Nullable
    @Size(max = 255)
    private String subSpecies;
    @Nullable
    @Size(max = 255)
    private String cultivar;

    @Nullable
    private String agrovocPlantURI;
    @Nullable
    private String agrovocProductURI;

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String getFamily() {
        return family;
    }

    public void setFamily(@NotNull String family) {
        this.family = family;
    }

    @NotNull
    public String getSpecies() {
        return species;
    }

    public void setSpecies(@NotNull String species) {
        this.species = species;
    }

    @NotNull
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public Optional<String> getSubSpecies() {
        return Optional.ofNullable(subSpecies);
    }

    public void setSubSpecies(String subSpecies) {
        this.subSpecies = subSpecies;
    }

    public Optional<String> getCultivar() {
        return Optional.ofNullable(cultivar);
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public Optional<String> getAgrovocPlantURI() {
        return Optional.ofNullable(agrovocPlantURI);
    }

    public void setAgrovocPlantURI(String agrovocPlantURI) {
        this.agrovocPlantURI = agrovocPlantURI;
    }

    public Optional<String> getAgrovocProductURI() {
        return Optional.ofNullable(agrovocProductURI);
    }

    public void setAgrovocProductURI(String agrovocProductURI) {
        this.agrovocProductURI = agrovocProductURI;
    }

    @NotNull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull Language language) {
        this.language = language;
    }
}
