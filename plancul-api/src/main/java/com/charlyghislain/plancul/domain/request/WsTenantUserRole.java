package com.charlyghislain.plancul.domain.request;

import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.WsTenantRole;
import com.charlyghislain.plancul.domain.WsUser;
import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;

import javax.validation.constraints.NotNull;

public class WsTenantUserRole implements WsDomainEntity {

    private Long id;
    @NotNull
    private WsRef<WsTenant> tenantWsRef;
    @NotNull
    private WsRef<WsUser> userWsRef;
    @NotNull
    private WsTenantRole role;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public WsRef<WsTenant> getTenantWsRef() {
        return tenantWsRef;
    }

    public void setTenantWsRef(@NotNull WsRef<WsTenant> tenantWsRef) {
        this.tenantWsRef = tenantWsRef;
    }

    @NotNull
    public WsRef<WsUser> getUserWsRef() {
        return userWsRef;
    }

    public void setUserWsRef(@NotNull WsRef<WsUser> userWsRef) {
        this.userWsRef = userWsRef;
    }

    @NotNull
    public WsTenantRole getRole() {
        return role;
    }

    public void setRole(@NotNull WsTenantRole role) {
        this.role = role;
    }
}
