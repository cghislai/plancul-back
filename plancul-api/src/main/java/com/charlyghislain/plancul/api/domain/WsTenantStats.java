package com.charlyghislain.plancul.api.domain;

import java.io.Serializable;

public class WsTenantStats implements Serializable {

    private Long tenantId;
    private Long culturesCount;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCulturesCount() {
        return culturesCount;
    }

    public void setCulturesCount(Long culturesCount) {
        this.culturesCount = culturesCount;
    }
}
