package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.api.domain.WsPlot;
import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.domain.request.filter.PlotFilter;
import com.charlyghislain.plancul.api.domain.request.filter.WsPlotFilter;
import com.charlyghislain.plancul.domain.request.sort.PlotSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.service.PlotService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class PlotConverter implements WsDomainObjectConverter<Plot, WsPlot> {

    @Inject
    private PlotService plotService;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private WsTenantConverter wsTenantConverter;


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

        WsRef<WsTenant> wsTenantWsRef = wsTenantConverter.reference(tenant);

        WsPlot wsPlot = new WsPlot();
        wsPlot.setId(id);
        wsPlot.setName(name);
        wsPlot.setTenantRef(wsTenantWsRef);
        return wsPlot;
    }

    @Override
    public Optional<Sort<Plot>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, PlotSortField::valueOf);
    }

    @Override
    public Plot fromWsEntity(WsPlot wsEntity) {
        Long id = wsEntity.getId();

        Plot plot = new Plot();
        plot.setId(id);

        updateEntity(plot, wsEntity);
        return plot;
    }

    @Deprecated
    public void updateEntity(Plot entity, WsPlot wsEntity) {
        String name = wsEntity.getName();
        WsRef<WsTenant> tenantRef = wsEntity.getTenantRef();

        Tenant tenant = tenantConverter.load(tenantRef);

        entity.setName(name);
        entity.setTenant(tenant);
    }

    public PlotFilter fromWsPlotFilter(WsPlotFilter wsPlotFilter) {
        Optional<String> nameContains = wsPlotFilter.getNameContains();
        Optional<WsRef<WsTenant>> tenantRef = wsPlotFilter.getTenantRef();

        Optional<Tenant> tenant = tenantRef.map(tenantConverter::load);

        PlotFilter plotFilter = new PlotFilter();
        nameContains.ifPresent(plotFilter::setNameContains);
        tenant.ifPresent(plotFilter::setTenant);
        return plotFilter;
    }
}
