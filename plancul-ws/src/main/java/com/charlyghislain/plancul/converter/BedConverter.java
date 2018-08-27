package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.api.domain.WsBed;
import com.charlyghislain.plancul.api.domain.WsPlot;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.api.domain.request.filter.WsBedFilter;
import com.charlyghislain.plancul.domain.request.sort.BedSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.service.BedService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class BedConverter implements WsDomainObjectConverter<Bed, WsBed> {

    @Inject
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
        Optional<String> patch = entity.getPatch();

        WsRef<WsPlot> plotWsRef = plotConverter.reference(plot);

        WsBed wsBed = new WsBed();
        wsBed.setId(id);
        wsBed.setName(name);
        wsBed.setPlotWsRef(plotWsRef);
        wsBed.setPatch(patch.orElse(null));
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

    @Deprecated
    public void updateEntity(Bed entity, WsBed wsEntity) {
        String name = wsEntity.getName();
        WsRef<WsPlot> plotWsRef = wsEntity.getPlotWsRef();
        String patch = wsEntity.getPatch();

        Plot plot = plotConverter.load(plotWsRef);

        entity.setName(name);
        entity.setPlot(plot);
        entity.setPatch(patch);
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

        wsBedFilter.getPatch()
                .ifPresent(bedFilter::setPatch);

        wsBedFilter.getPatchQuery()
                .ifPresent(bedFilter::setPatchQuery);

        return bedFilter;
    }

    @Override
    public Optional<Sort<Bed>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, BedSortField::valueOf);
    }
}
