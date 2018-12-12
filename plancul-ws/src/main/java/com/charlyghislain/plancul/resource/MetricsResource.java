package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.filter.TenantFilter;
import com.charlyghislain.plancul.service.CultureService;
import com.charlyghislain.plancul.service.TenantService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class MetricsResource {

    @Inject
    private TenantService tenantService;
    @Inject
    private CultureService cultureService;


    private void init(@Observes @Initialized(ApplicationScoped.class) Object startEvent) {
        // to trigger instance creation and hence activate the gauge
    }

    @Gauge(unit = MetricUnits.NONE, name = "culturesCount")
    public Long culturesCount() {
        CultureFilter cultureFilter = new CultureFilter();
        return cultureService.countCultures(cultureFilter);
    }

    @Gauge(unit = MetricUnits.NONE, name = "tenantsCount")
    public Long tenantsCount() {
        TenantFilter tenantFilter = new TenantFilter();
        return tenantService.countTenants(tenantFilter);
    }
}
