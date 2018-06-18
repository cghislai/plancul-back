package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsAgrovocPlant;
import com.charlyghislain.plancul.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.WsCrop;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.CropService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

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
}
