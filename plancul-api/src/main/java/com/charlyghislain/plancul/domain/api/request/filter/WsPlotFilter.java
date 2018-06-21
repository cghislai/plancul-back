package com.charlyghislain.plancul.domain.api.request.filter;

import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Optional;

public class WsPlotFilter implements Serializable {

    @Nullable
    private WsRef<WsTenant> tenantRef;
    @Nullable
    private String nameContains;

    public Optional<WsRef<WsTenant>> getTenantRef() {
        return Optional.ofNullable(tenantRef);
    }

    public void setTenantRef(WsRef<WsTenant> tenantRef) {
        this.tenantRef = tenantRef;
    }

    public Optional<String> getNameContains() {
        return Optional.ofNullable(nameContains);
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }
}
