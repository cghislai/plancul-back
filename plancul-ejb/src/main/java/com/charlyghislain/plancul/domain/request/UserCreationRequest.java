package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserCreationRequest {

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
    @Size(max = 255)
    private String password;
    @NotNull
    private Tenant tenant;
    @NotNull
    private TenantRole tenantRole;

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
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull Tenant tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public TenantRole getTenantRole() {
        return tenantRole;
    }

    public void setTenantRole(@NotNull TenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}