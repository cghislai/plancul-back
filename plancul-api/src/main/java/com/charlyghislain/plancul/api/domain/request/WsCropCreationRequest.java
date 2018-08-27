package com.charlyghislain.plancul.api.domain.request;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class WsCropCreationRequest implements Serializable {

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
    private WsRef<WsTenant> tenantRef;
    private boolean shared;

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

    public String getSubSpecies() {
        return subSpecies;
    }

    public void setSubSpecies(String subSpecies) {
        this.subSpecies = subSpecies;
    }

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public String getAgrovocPlantURI() {
        return agrovocPlantURI;
    }

    public void setAgrovocPlantURI(String agrovocPlantURI) {
        this.agrovocPlantURI = agrovocPlantURI;
    }

    public String getAgrovocProductURI() {
        return agrovocProductURI;
    }

    public void setAgrovocProductURI(String agrovocProductURI) {
        this.agrovocProductURI = agrovocProductURI;
    }

    @NotNull
    public WsRef<WsTenant> getTenantRef() {
        return tenantRef;
    }

    public void setTenantRef(@NotNull WsRef<WsTenant> tenantRef) {
        this.tenantRef = tenantRef;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
