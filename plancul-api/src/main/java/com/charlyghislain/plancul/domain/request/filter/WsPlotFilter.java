package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.util.WsRef;

import java.io.Serializable;

public class WsPlotFilter implements Serializable {

    private WsRef<WsTenant> tenantRef;
    private String nameContains;

    public WsRef<WsTenant> getTenantRef() {
        return tenantRef;
    }

    public void setTenantRef(WsRef<WsTenant> tenantRef) {
        this.tenantRef = tenantRef;
    }

    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }
}
