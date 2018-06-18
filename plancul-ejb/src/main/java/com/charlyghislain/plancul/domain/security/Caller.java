package com.charlyghislain.plancul.domain.security;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Caller implements DomainEntity {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @NotNull
    @Size(max = 400)
    private String password;
    private boolean passwordNeedsChange;

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
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public boolean isPasswordNeedsChange() {
        return passwordNeedsChange;
    }

    public void setPasswordNeedsChange(boolean passwordExpired) {
        this.passwordNeedsChange = passwordExpired;
    }
}
