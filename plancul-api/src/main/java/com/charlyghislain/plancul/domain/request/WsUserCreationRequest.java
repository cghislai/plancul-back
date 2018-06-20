package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.WsTenantRole;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class WsUserCreationRequest implements Serializable {

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
    private WsTenantRole tenantRole;

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
    public WsTenantRole getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(@NotNull WsTenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }
}
