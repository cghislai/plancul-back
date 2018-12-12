package com.charlyghislain.plancul.domain.stat;

import com.charlyghislain.plancul.domain.Tenant;

public class TenantStat {

    private Tenant tenant;
    private Long cultureCount;

    public TenantStat() {
    }

    public TenantStat(Tenant tenant, Long cultureCount) {
        this.tenant = tenant;
        this.cultureCount = cultureCount;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Long getCultureCount() {
        return cultureCount;
    }

    public void setCultureCount(Long cultureCount) {
        this.cultureCount = cultureCount;
    }
}
