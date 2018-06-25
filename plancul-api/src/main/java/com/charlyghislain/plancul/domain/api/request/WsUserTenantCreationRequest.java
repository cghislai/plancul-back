package com.charlyghislain.plancul.domain.api.request;

import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.util.WsLanguage;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class WsUserTenantCreationRequest implements Serializable {

    @NotNull
    @Size(max = 255)
    private String firstName;
    @NotNull
    @Size(max = 255)
    private String lastName;
    @NotNull
    @Size(max = 255)
    private String email;
    @NotNull
    private WsTenant tenant;
    @NotNull
    private WsLanguage language;

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
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public WsTenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull WsTenant tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public WsLanguage getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull WsLanguage language) {
        this.language = language;
    }
}
