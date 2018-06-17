package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.agrovoc.AgrovocPlant;
import com.charlyghislain.plancul.domain.agrovoc.AgrovocProduct;
import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private String cultivar;

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

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }
}
