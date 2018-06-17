package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Bed implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @NotNull
    @ManyToOne
    private Plot plot;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public Plot getPlot() {
        return plot;
    }

    public void setPlot(@NotNull Plot plot) {
        this.plot = plot;
    }
}
