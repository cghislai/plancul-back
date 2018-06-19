package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.BedPreparation;
import com.charlyghislain.plancul.domain.BedPreparationType;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsBed;
import com.charlyghislain.plancul.domain.WsBedPreparation;
import com.charlyghislain.plancul.domain.WsCrop;
import com.charlyghislain.plancul.domain.WsCulture;
import com.charlyghislain.plancul.domain.WsCultureNursing;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.filter.WsCultureFilter;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.CultureService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class CultureConverter implements WsDomainObjectConverter<Culture, WsCulture> {

    @EJB
    private CultureService cultureService;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private CropConverter cropConverter;
    @Inject
    private BedConverter bedConverter;
    @Inject
    private BedPreparationConverter bedPreparationConverter;
    @Inject
    private CultureNursingConverter cultureNursingConverter;
    @Inject
    private DateFilterConverter dateFilterConverter;


    @Override
    public Culture load(WsRef<WsCulture> ref) {
        return cultureService.findCultureById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsCulture toWsEntity(Culture entity) {
        Long id = entity.getId();
        Tenant tenant = entity.getTenant();
        Crop crop = entity.getCrop();
        Bed bed = entity.getBed();
        LocalDate sowingDate = entity.getSowingDate();
        LocalDate germinationDate = entity.getGerminationDate();
        LocalDate firstHarvestDate = entity.getFirstHarvestDate();
        LocalDate lastHarvestDate = entity.getLastHarvestDate();
        LocalDate bedOccupancyStartDate = entity.getBedOccupancyStartDate();
        LocalDate bedOccupancyEndDate = entity.getBedOccupancyEndDate();
        String htmlNotes = entity.getHtmlNotes();
        Optional<CultureNursing> cultureNursing = entity.getCultureNursing();
        Optional<BedPreparation> bedPreparation = entity.getBedPreparation();

        WsRef<WsTenant> tenantWsRef = tenantConverter.reference(tenant);
        WsRef<WsCrop> cropWsRef = cropConverter.reference(crop);
        WsRef<WsBed> bedWsRef = bedConverter.reference(bed);
        Optional<WsCultureNursing> wsCultureNursing = cultureNursing.map(cultureNursingConverter::toWsEntity);
        Optional<WsBedPreparation> wsBedPreparation = bedPreparation.map(bedPreparationConverter::toWsEntity);

        WsCulture wsCulture = new WsCulture();
        wsCulture.setId(id);
        wsCulture.setTenantWsRef(tenantWsRef);
        wsCulture.setCropWsRef(cropWsRef);
        wsCulture.setBedWsRef(bedWsRef);
        wsCulture.setSowingDate(sowingDate);
        wsCulture.setGerminationDate(germinationDate);
        wsCulture.setFirstHarvestDate(firstHarvestDate);
        wsCulture.setLastHarvestDate(lastHarvestDate);
        wsCulture.setBedOccupancyStartDate(bedOccupancyStartDate);
        wsCulture.setBedOccupancyEndDate(bedOccupancyEndDate);
        wsCulture.setHtmlNotes(htmlNotes);
        wsCultureNursing.ifPresent(wsCulture::setCultureNursing);
        wsBedPreparation.ifPresent(wsCulture::setBedPreparation);
        return wsCulture;
    }

    @Override
    public Culture fromWsEntity(WsCulture wsEntity) {
        Long id = wsEntity.getId();

        Culture culture = new Culture();
        culture.setId(id);

        updateEntity(culture, wsEntity);
        return culture;
    }

    @Override
    public void updateEntity(Culture entity, WsCulture wsEntity) {
        WsRef<WsTenant> tenantWsRef = wsEntity.getTenantWsRef();
        WsRef<WsCrop> cropWsRef = wsEntity.getCropWsRef();
        WsRef<WsBed> bedWsRef = wsEntity.getBedWsRef();
        LocalDate sowingDate = wsEntity.getSowingDate();
        LocalDate germinationDate = wsEntity.getGerminationDate();
        LocalDate firstHarvestDate = wsEntity.getFirstHarvestDate();
        LocalDate lastHarvestDate = wsEntity.getLastHarvestDate();
//        LocalDate bedOccupancyStartDate = wsEntity.getBedOccupancyStartDate();
//        LocalDate bedOccupancyEndDate = wsEntity.getBedOccupancyEndDate();
        String htmlNotes = wsEntity.getHtmlNotes();
        Optional<WsCultureNursing> wsCultureNursing = wsEntity.getCultureNursing();
        Optional<WsBedPreparation> wsBedPreparation = wsEntity.getBedPreparation();

        Tenant tenant = tenantConverter.load(tenantWsRef);
        Crop crop = cropConverter.load(cropWsRef);
        Bed bed = bedConverter.load(bedWsRef);
        Optional<CultureNursing> cultureNursing = wsCultureNursing.map(cultureNursingConverter::fromWsEntity);
        Optional<BedPreparation> bedPreparation = wsBedPreparation.map(bedPreparationConverter::fromWsEntity);

        entity.setTenant(tenant);
        entity.setCrop(crop);
        entity.setBed(bed);
        entity.setSowingDate(sowingDate);
        entity.setGerminationDate(germinationDate);
        entity.setFirstHarvestDate(firstHarvestDate);
        entity.setLastHarvestDate(lastHarvestDate);
        entity.setHtmlNotes(htmlNotes);
        entity.setCultureNursing(cultureNursing.orElse(null));
        entity.setBedPreparation(bedPreparation.orElse(null));
    }

    public CultureFilter fromWsCultureFilter(WsCultureFilter wsCultureFilter) {
        CultureFilter cultureFilter = new CultureFilter();

        wsCultureFilter.getTenantWsRef()
                .map(tenantConverter::load)
                .ifPresent(cultureFilter::setTenant);

        wsCultureFilter.getCropFilter()
                .map(cropConverter::fromWsCropFilter)
                .ifPresent(cultureFilter::setCropFilter);

        wsCultureFilter.getBedFilter()
                .map(bedConverter::fromWsBedFilter)
                .ifPresent(cultureFilter::setBedFilter);

        wsCultureFilter.getSowingDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setSowingDate);

        wsCultureFilter.getGerminationDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setGerminationDate);

        wsCultureFilter.getFirstHarvestDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setFirstHarvestDate);

        wsCultureFilter.getLastHarvestDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setLastHarvestDate);

        wsCultureFilter.getTransplantationDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setTransplantationDate);

        wsCultureFilter.getBedOccupancyStartDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setBedOccupancyStartDate);

        wsCultureFilter.getBedOccupancyEndDate()
                .map(dateFilterConverter::fromWsDateFilter)
                .ifPresent(cultureFilter::setBedOccupancyEndDate);

        wsCultureFilter.getNursing()
                .ifPresent(cultureFilter::setNursing);

        wsCultureFilter.getBedPreparation()
                .ifPresent(cultureFilter::setBedPreparation);

        wsCultureFilter.getBedPreparationType()
                .map(Enum::name)
                .map(BedPreparationType::valueOf)
                .ifPresent(cultureFilter::setBedPreparationType);

        wsCultureFilter.getNotesQuery()
                .ifPresent(cultureFilter::setNotesQuery);

        return cultureFilter;
    }
}
