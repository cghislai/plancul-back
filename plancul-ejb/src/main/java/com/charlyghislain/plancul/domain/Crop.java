package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Entity
public class Crop implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private AgrovocPlant agrovocPlant;
    @NotNull
    @ManyToOne
    private AgrovocProduct agrovocProduct;
    @Size(max = 255)
    @Nullable
    private String cultivar;
    /**
     * An optional tenant this crop would be restricted to
     */
    @Nullable
    @ManyToOne
    private Tenant tenant;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public AgrovocPlant getAgrovocPlant() {
        return agrovocPlant;
    }

    public void setAgrovocPlant(@NotNull AgrovocPlant agrovocPlant) {
        this.agrovocPlant = agrovocPlant;
    }

    @NotNull
    public AgrovocProduct getAgrovocProduct() {
        return agrovocProduct;
    }

    public void setAgrovocProduct(@NotNull AgrovocProduct agrovocProduct) {
        this.agrovocProduct = agrovocProduct;
    }

    public Optional<String> getCultivar() {
        return Optional.ofNullable(cultivar);
    }

    public Optional<Tenant> getTenant() {
        return Optional.ofNullable(tenant);
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
