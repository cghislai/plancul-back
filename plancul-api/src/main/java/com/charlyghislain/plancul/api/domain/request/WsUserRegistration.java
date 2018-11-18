package com.charlyghislain.plancul.api.domain.request;

import com.charlyghislain.plancul.api.domain.WsUser;
import com.charlyghislain.plancul.api.domain.util.NullableField;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class WsUserRegistration {
    @NotNull
    private WsUser user;
    @NotNull
    private String password;

    @Nullable
    @NullableField
    private String name;
    @Nullable
    @NullableField
    private String email;
    @Nullable
    @NullableField
    private String adminToken;
    @Nullable
    @NullableField
    private String tenantInvitationToken;

    @NotNull
    public WsUser getUser() {
        return user;
    }

    public void setUser(@NotNull WsUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantInvitationToken() {
        return tenantInvitationToken;
    }

    public void setTenantInvitationToken(String tenantInvitationToken) {
        this.tenantInvitationToken = tenantInvitationToken;
    }
}
