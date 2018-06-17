package com.charlyghislain.plancul.domain.i18n;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Language implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String code;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getCode() {
        return code;
    }

    public void setCode(@NotNull String code) {
        this.code = code;
    }
}
