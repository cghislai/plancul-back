package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsTenantStats;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.stat.TenantStat;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsTenantStatConverter {

    public WsTenantStats toWsTenantStat(TenantStat tenantStat) {
        Long cultureCount = tenantStat.getCultureCount();
        Tenant tenant = tenantStat.getTenant();

        WsTenantStats wsTenantStats = new WsTenantStats();
        wsTenantStats.setTenantId(tenant.getId());
        wsTenantStats.setCulturesCount(cultureCount);
        return wsTenantStats;
    }

}
