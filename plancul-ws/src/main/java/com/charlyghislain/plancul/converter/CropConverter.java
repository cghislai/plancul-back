package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.api.domain.WsAgrovocPlant;
import com.charlyghislain.plancul.api.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.api.domain.WsCrop;
import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.request.filter.WsCropFilter;
import com.charlyghislain.plancul.api.domain.util.WsRef;
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
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CropConverter implements ToWsDomainObjectConverter<Crop, WsCrop> {

    @Inject
    private CropService cropService;
    @Inject
    private AgrovocPlantConverter agrovocPlantConverter;
    @Inject
    private AgrovocProductConverter agrovocProductConverter;
    @Inject
    private TenantConverter tenantConverter;
    @Inject
    private LocalizedMessageConverter localizedMessageConverter;
    @Inject
    private WsTenantConverter wsTenantConverter;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public Crop load(WsRef<WsCrop> ref) {
        return cropService.findCropById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }


    @Override
    public WsCrop toWsEntity(Crop entity) {
        Long id = entity.getId();
        String family = entity.getFamily();
        String species = entity.getSpecies();
        Optional<String> subSpecies = entity.getSubSpecies();
        Optional<String> cultivar = entity.getCultivar();
        Optional<AgrovocPlant> agrovocPlant = entity.getAgrovocPlant();
        Optional<AgrovocProduct> agrovocProduct = entity.getAgrovocProduct();
        Optional<Tenant> tenant = entity.getTenant();
        List<LocalizedMessage> displayName = entity.getDisplayName();

        Optional<WsRef<WsAgrovocPlant>> wsAgrovocPlantWsRef = agrovocPlant.map(agrovocPlantConverter::reference);
        Optional<WsRef<WsAgrovocProduct>> wsAgrovocProductWsRef = agrovocProduct.map(agrovocProductConverter::reference);
        Optional<WsRef<WsTenant>> tenantWsRef = tenant.map(wsTenantConverter::reference);
        String displayNameValue = localizedMessageConverter.toLocalizedStrings(displayName, acceptedLanguage)
                .stream().findFirst()
                .orElseGet(() -> this.createFallbackDisplayName(entity, acceptedLanguage));

        WsCrop wsCrop = new WsCrop();
        wsCrop.setId(id);
        wsCrop.setFamily(family);
        wsCrop.setSpecies(species);
        subSpecies.ifPresent(wsCrop::setSubSpecies);
        cultivar.ifPresent(wsCrop::setCultivar);
        wsAgrovocPlantWsRef.ifPresent(wsCrop::setAgrovocPlantWsRef);
        wsAgrovocProductWsRef.ifPresent(wsCrop::setAgrovocProductWsRef);
        tenantWsRef.ifPresent(wsCrop::setTenantRestriction);
        wsCrop.setDisplayName(displayNameValue);
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

    private String createFallbackDisplayName(Crop entity, Language acceptedLanguage) {
        String fallbackName = entity.getAgrovocProduct()
                .map(AgrovocProduct::getPreferedLabel)
                .flatMap(labels -> localizedMessageConverter.toLocalizedStrings(labels, acceptedLanguage)
                        .stream().findFirst())
                .orElse(entity.getSpecies());
        return fallbackName;
    }

    @Override
    public Optional<Sort<Crop>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, CropSortField::valueOf);
    }
}
