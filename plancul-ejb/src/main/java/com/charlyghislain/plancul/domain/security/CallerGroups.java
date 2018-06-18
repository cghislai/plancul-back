package com.charlyghislain.plancul.domain.security;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class CallerGroups implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @ManyToOne
    private Caller caller;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "GROUP_NAME")
    private ApplicationGroup group;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public Caller getCaller() {
        return caller;
    }

    public void setCaller(@NotNull Caller caller) {
        this.caller = caller;
    }

    @NotNull
    public ApplicationGroup getGroup() {
        return group;
    }

    public void setGroup(@NotNull ApplicationGroup group) {
        this.group = group;
    }
}
