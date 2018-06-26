package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.api.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.api.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.api.WsCrop;
import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.request.filter.WsCropFilter;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.request.sort.CropSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.service.CropService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class CropConverter implements ToWsDomainObjectConverter<Crop, WsCrop> {

    @EJB
    private CropService cropService;
    @Inject
    private AgrovocPlantConverter agrovocPlantConverter;
    @Inject
    private AgrovocProductConverter agrovocProductConverter;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public Crop load(WsRef<WsCrop> ref) {
        return cropService.findCropById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }


    @Override
    public WsCrop toWsEntity(Crop entity) {
        AgrovocPlant agrovocPlant = entity.getAgrovocPlant();
        AgrovocProduct agrovocProduct = entity.getAgrovocProduct();
        Optional<String> cultivar = entity.getCultivar();
        Long id = entity.getId();
        Optional<Tenant> tenant = entity.getTenant();

        WsRef<WsAgrovocPlant> agrovocPlantWsRef = agrovocPlantConverter.reference(agrovocPlant);
        WsRef<WsAgrovocProduct> agrovocProductWsRef = agrovocProductConverter.reference(agrovocProduct);
        Optional<WsRef<WsTenant>> tenantWsRef = tenant.map(tenantConverter::reference);

        WsCrop wsCrop = new WsCrop();
        wsCrop.setAgrovocPlantWsRef(agrovocPlantWsRef);
        wsCrop.setAgrovocProductWsRef(agrovocProductWsRef);
        wsCrop.setCultivar(cultivar.orElse(null));
        wsCrop.setId(id);
        wsCrop.setTenantRestriction(tenantWsRef.orElse(null));
        return wsCrop;
    }

    public CropFilter fromWsCropFilter(WsCropFilter wsCropFilter) {
        CropFilter cropFilter = new CropFilter();
        cropFilter.setQueryLanguage(acceptedLanguage);

        wsCropFilter.getTenantWsRef()
                .map(tenantConverter::load)
                .ifPresent(cropFilter::setTenant);

        wsCropFilter.getExactCropWsRef()
                .map(this::load)
                .ifPresent(cropFilter::setExactCrop);

        wsCropFilter.getPlantWsRef()
                .map(agrovocPlantConverter::load)
                .ifPresent(cropFilter::setPlant);

        wsCropFilter.getNamesQuery()
                .ifPresent(cropFilter::setNamesQuery);

        wsCropFilter.getPlantQuery()
                .ifPresent(cropFilter::setPlantQuery);

        wsCropFilter.getCultivarQuery()
                .ifPresent(cropFilter::setCultivarQuery);

        wsCropFilter.getShared()
                .ifPresent(cropFilter::setShared);

        return cropFilter;
    }

    @Override
    public Optional<Sort<Crop>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, CropSortField::valueOf);
    }
}
