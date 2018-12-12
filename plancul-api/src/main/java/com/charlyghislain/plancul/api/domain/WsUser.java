package com.charlyghislain.plancul.api.domain;

import com.charlyghislain.plancul.api.domain.util.NullableField;
import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsLanguage;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsUser implements WsDomainEntity {

    @Nullable
    @NullableField
    private Long id;
    @NotNull
    @Size(max = 255)
    private String firstName;
    @NotNull
    @Size(max = 255)
    private String lastName;
    @NotNull
    private WsLanguage language;
    @Nullable
    @NullableField
    private Long authenticatorUid;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    public WsLanguage getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull WsLanguage language) {
        this.language = language;
    }

    public Long getAuthenticatorUid() {
        return authenticatorUid;
    }

    public void setAuthenticatorUid(Long authenticatorUid) {
        this.authenticatorUid = authenticatorUid;
    }
}
