package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ReferenceNotFoundException;
import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsPlot;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.filter.PlotFilter;
import com.charlyghislain.plancul.domain.filter.WsPlotFilter;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.PlotService;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PlotConverter implements WsDomainObjectConverter<Plot, WsPlot> {

    @EJB
    private PlotService plotService;
    @Inject
    private TenantConverter tenantConverter;


    @Override
    public Plot load(WsRef<WsPlot> ref) {
        return plotService.findPlotById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsPlot toWsEntity(Plot entity) {
        Long id = entity.getId();
        String name = entity.getName();
        Tenant tenant = entity.getTenant();

        WsRef<WsTenant> wsTenantWsRef = tenantConverter.reference(tenant);

        WsPlot wsPlot = new WsPlot();
        wsPlot.setId(id);
        wsPlot.setName(name);
        wsPlot.setTenantRef(wsTenantWsRef);
        return wsPlot;
    }

    @Override
    public Plot fromWsEntity(WsPlot wsEntity) {
        Long id = wsEntity.getId();

        Plot plot = new Plot();
        plot.setId(id);

        updateEntity(plot, wsEntity);
        return plot;
    }

    @Override
    public void updateEntity(Plot entity, WsPlot wsEntity) {
        String name = wsEntity.getName();
        WsRef<WsTenant> tenantRef = wsEntity.getTenantRef();

        Tenant tenant = tenantConverter.load(tenantRef);

        entity.setName(name);
        entity.setTenant(tenant);
    }

    public PlotFilter fromWsPlotFilter(WsPlotFilter wsPlotFilter) {
        String nameContains = wsPlotFilter.getNameContains();
        WsRef<WsTenant> tenantRef = wsPlotFilter.getTenantRef();

        Tenant tenant = tenantConverter.load(tenantRef);

        PlotFilter plotFilter = new PlotFilter();
        plotFilter.setNameContains(nameContains);
        plotFilter.setTenant(tenant);
        return plotFilter;
    }
}
