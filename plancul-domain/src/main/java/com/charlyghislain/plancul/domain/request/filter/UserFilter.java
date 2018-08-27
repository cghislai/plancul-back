package com.charlyghislain.plancul.domain.request.filter;

import org.checkerframework.checker.nullness.qual.Nullable;

public class UserFilter {
    @Nullable
    private Long userId;
    @Nullable
    private Long authenticatorUid;
    @Nullable
    private Boolean admin;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuthenticatorUid() {
        return authenticatorUid;
    }

    public void setAuthenticatorUid(Long authenticatorUid) {
        this.authenticatorUid = authenticatorUid;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
