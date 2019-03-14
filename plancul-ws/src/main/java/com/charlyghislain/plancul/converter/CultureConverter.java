package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsBed;
import com.charlyghislain.plancul.api.domain.WsBedPreparation;
import com.charlyghislain.plancul.api.domain.WsCrop;
import com.charlyghislain.plancul.api.domain.WsCulture;
import com.charlyghislain.plancul.api.domain.WsCultureNursing;
import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.request.filter.WsCultureFilter;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.BedPreparation;
import com.charlyghislain.plancul.domain.BedPreparationType;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.sort.CultureSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.service.CultureService;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
public class CultureConverter implements WsDomainObjectConverter<Culture, WsCulture> {

    @Inject
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
    @Inject
    private WsTenantConverter wsTenantConverter;


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
        int daysUntilFirstHarvest = entity.getDaysUntilFirstHarvest();
        int harvestDaysDuration = entity.getHarvestDaysDuration();
        int daysUntilGermination = entity.getDaysUntilGermination();
        BigDecimal seedSurfaceQuantity = entity.getSeedSurfaceQuantity();
        BigDecimal harvestSurfaceQuantity = entity.getHarvestSurfaceQuantity();
        LocalDate germinationDate = entity.getGerminationDate();
        LocalDate firstHarvestDate = entity.getFirstHarvestDate();
        LocalDate lastHarvestDate = entity.getLastHarvestDate();
        LocalDate bedOccupancyStartDate = entity.getBedOccupancyStartDate();
        LocalDate bedOccupancyEndDate = entity.getBedOccupancyEndDate();
        Optional<String> htmlNotes = entity.getHtmlNotes();
        Optional<CultureNursing> cultureNursing = entity.getCultureNursing();
        Optional<BedPreparation> bedPreparation = entity.getBedPreparation();
        BigDecimal seedTotalQuantity = entity.getSeedTotalQuantity();
        BigDecimal harvestTotalQuantity = entity.getHarvestTotalQuantity();

        Optional<WsRef<WsTenant>> tenantWsRef = Optional.ofNullable(tenant)
                .map(wsTenantConverter::reference);
        Optional<WsRef<WsCrop>> cropWsRef = Optional.ofNullable(crop)
                .map(cropConverter::reference);
        Optional<WsRef<WsBed>> bedWsRef = Optional.ofNullable(bed)
                .map(bedConverter::reference);
        Optional<WsCultureNursing> wsCultureNursing = cultureNursing.map(cultureNursingConverter::toWsEntity);
        Optional<WsBedPreparation> wsBedPreparation = bedPreparation.map(bedPreparationConverter::toWsEntity);

        WsCulture wsCulture = new WsCulture();
        wsCulture.setId(id);
        wsCulture.setTenantWsRef(tenantWsRef.orElse(null));
        wsCulture.setCropWsRef(cropWsRef.orElse(null));
        wsCulture.setBedWsRef(bedWsRef.orElse(null));
        wsCulture.setSowingDate(sowingDate);
        wsCulture.setDaysUntilGermination(daysUntilGermination);
        wsCulture.setDaysUntilFirstHarvest(daysUntilFirstHarvest);
        wsCulture.setSeedSurfaceQuantity(seedSurfaceQuantity);
        wsCulture.setHarvestSurfaceQuantity(harvestSurfaceQuantity);
        wsCulture.setHarvestDaysDuration(harvestDaysDuration);
        wsCulture.setGerminationDate(germinationDate);
        wsCulture.setFirstHarvestDate(firstHarvestDate);
        wsCulture.setLastHarvestDate(lastHarvestDate);
        wsCulture.setBedOccupancyStartDate(bedOccupancyStartDate);
        wsCulture.setBedOccupancyEndDate(bedOccupancyEndDate);
        wsCulture.setHtmlNotes(htmlNotes.orElse(null));
        wsCulture.setSeedTotalQuantity(seedTotalQuantity);
        wsCulture.setHarvestTotalQuantity(harvestTotalQuantity);
        wsCultureNursing.ifPresent(wsCulture::setCultureNursing);
        wsBedPreparation.ifPresent(wsCulture::setBedPreparation);
        return wsCulture;
    }

    @Override
    public Optional<Sort<Culture>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, CultureSortField::valueOf);
    }

    @Override
    public Culture fromWsEntity(WsCulture wsEntity) {
        Long id = wsEntity.getId();

        Culture culture = new Culture();
        culture.setId(id);

        updateEntity(culture, wsEntity);
        return culture;
    }

    @Deprecated
    public void updateEntity(Culture entity, WsCulture wsEntity) {
        WsRef<WsTenant> tenantWsRef = wsEntity.getTenantWsRef();
        WsRef<WsCrop> cropWsRef = wsEntity.getCropWsRef();
        WsRef<WsBed> bedWsRef = wsEntity.getBedWsRef();
        LocalDate sowingDate = wsEntity.getSowingDate();
        int daysUntilGermination = wsEntity.getDaysUntilGermination();
        int daysUntilFirstHarvest = wsEntity.getDaysUntilFirstHarvest();
        int harvestDaysDuration = wsEntity.getHarvestDaysDuration();
        BigDecimal seedSurfaceQuantity = wsEntity.getSeedSurfaceQuantity();
        BigDecimal harvestSurfaceQuantity = wsEntity.getHarvestSurfaceQuantity();
//        LocalDate germinationDate = wsEntity.getGerminationDate();
//        LocalDate firstHarvestDate = wsEntity.getFirstHarvestDate();
//        LocalDate lastHarvestDate = wsEntity.getLastHarvestDate();
//        LocalDate bedOccupancyStartDate = wsEntity.getBedOccupancyStartDate();
//        LocalDate bedOccupancyEndDate = wsEntity.getBedOccupancyEndDate();
        String htmlNotes = wsEntity.getHtmlNotes();
        WsCultureNursing cultureNursingNullable = wsEntity.getCultureNursing();
        WsBedPreparation bedPreparationNullable = wsEntity.getBedPreparation();

        Optional<Tenant> tenant = Optional.ofNullable(tenantWsRef)
                .map(tenantConverter::load);
        Optional<Crop> crop = Optional.ofNullable(cropWsRef)
                .map(cropConverter::load);
        Optional<Bed> bed = Optional.ofNullable(bedWsRef)
                .map(bedConverter::load);
        Optional<CultureNursing> cultureNursing = Optional.ofNullable(cultureNursingNullable)
                .map(cultureNursingConverter::fromWsEntity);
        Optional<BedPreparation> bedPreparation = Optional.ofNullable(bedPreparationNullable)
                .map(bedPreparationConverter::fromWsEntity);

        entity.setTenant(tenant.orElse(null));
        entity.setCrop(crop.orElse(null));
        entity.setBed(bed.orElse(null));
        entity.setSowingDate(sowingDate);
        entity.setDaysUntilGermination(daysUntilGermination);
        entity.setDaysUntilFirstHarvest(daysUntilFirstHarvest);
        entity.setHarvestDaysDuration(harvestDaysDuration);
        entity.setSeedSurfaceQuantity(seedSurfaceQuantity);
        entity.setHarvestSurfaceQuantity(harvestSurfaceQuantity);
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
