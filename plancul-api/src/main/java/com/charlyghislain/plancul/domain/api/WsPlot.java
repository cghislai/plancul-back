package com.charlyghislain.plancul.domain.api;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsRef;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsPlot implements WsDomainEntity {

    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    @NotNull
    private WsRef<WsTenant> tenantRef;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WsRef<WsTenant> getTenantRef() {
        return tenantRef;
    }

    public void setTenantRef(WsRef<WsTenant> tenantRef) {
        this.tenantRef = tenantRef;
    }
}
