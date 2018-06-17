package com.charlyghislain.plancul.domain.i18n;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class LocalizedMessage implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private Language language;
    @NotNull
    @Size(max = 2047)
    private String label;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull Language language) {
        this.language = language;
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    public void setLabel(@NotNull String label) {
        this.label = label;
    }
}
