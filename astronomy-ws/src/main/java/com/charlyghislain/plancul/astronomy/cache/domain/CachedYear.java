package com.charlyghislain.plancul.astronomy.cache.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CACHED_YEAR")
public class CachedYear {


    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "YEAR")
    @NotNull
    private Integer year;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Integer getYear() {
        return year;
    }

    public void setYear(@NotNull Integer year) {
        this.year = year;
    }
}
