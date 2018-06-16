package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.WsDomainEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsTenant implements WsDomainEntity {

    private Long id;
    @Size(max = 255)
    @NotNull
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
