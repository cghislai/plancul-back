package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.WsBed;
import com.charlyghislain.plancul.domain.WsPlot;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.domain.request.filter.WsBedFilter;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.BedService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BedConverter implements WsDomainObjectConverter<Bed, WsBed> {

    @EJB
    private BedService bedService;
    @Inject
    private PlotConverter plotConverter;
    @Inject
    private TenantConverter tenantConverter;


    @Override
    public Bed load(WsRef<WsBed> ref) {
        return bedService.findBedById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsBed toWsEntity(Bed entity) {
        Long id = entity.getId();
        String name = entity.getName();
        Plot plot = entity.getPlot();

        WsRef<WsPlot> plotWsRef = plotConverter.reference(plot);

        WsBed wsBed = new WsBed();
        wsBed.setId(id);
        wsBed.setName(name);
        wsBed.setPlotWsRef(plotWsRef);
        return wsBed;
    }

    @Override
    public Bed fromWsEntity(WsBed wsEntity) {
        Long id = wsEntity.getId();

        Bed bed = new Bed();
        bed.setId(id);

        updateEntity(bed, wsEntity);
        return bed;
    }

    @Override
    public void updateEntity(Bed entity, WsBed wsEntity) {
        String name = wsEntity.getName();
        WsRef<WsPlot> plotWsRef = wsEntity.getPlotWsRef();

        Plot plot = plotConverter.load(plotWsRef);

        entity.setName(name);
        entity.setPlot(plot);
    }

    public BedFilter fromWsBedFilter(WsBedFilter wsBedFilter) {
        BedFilter bedFilter = new BedFilter();

        wsBedFilter.getExactBedWsRef()
                .map(this::load)
                .ifPresent(bedFilter::setExactBed);

        wsBedFilter.getNameQuery()
                .ifPresent(bedFilter::setNameQuery);

        wsBedFilter.getPlotWsRef()
                .map(plotConverter::load)
                .ifPresent(bedFilter::setPlot);

        wsBedFilter.getTenantWsRef()
                .map(tenantConverter::load)
                .ifPresent(bedFilter::setTenant);

        return bedFilter;
    }
}
