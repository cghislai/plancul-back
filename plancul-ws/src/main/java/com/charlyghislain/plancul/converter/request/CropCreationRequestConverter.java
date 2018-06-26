package com.charlyghislain.plancul.converter.request;

import com.charlyghislain.plancul.converter.TenantConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.api.request.WsCropCreationRequest;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.util.AcceptedLanguage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class CropCreationRequestConverter {

    @Inject
    private TenantConverter tenantConverter;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    public CropCreationRequest fromWsCropCreationRequest(WsCropCreationRequest wsCropCreationRequest) {
        String displayName = wsCropCreationRequest.getDisplayName();
        String family = wsCropCreationRequest.getFamily();
        String species = wsCropCreationRequest.getSpecies();
        WsRef<WsTenant> tenantRef = wsCropCreationRequest.getTenantRef();
        boolean shared = wsCropCreationRequest.isShared();
        String subSpecies = wsCropCreationRequest.getSubSpecies();
        String cultivar = wsCropCreationRequest.getCultivar();
        String agrovocPlantURI = wsCropCreationRequest.getAgrovocPlantURI();
        String agrovocProductURI = wsCropCreationRequest.getAgrovocProductURI();

        Optional<Tenant> tenant = Optional.ofNullable(tenantRef)
                .map(tenantConverter::load);

        CropCreationRequest cropCreationRequest = new CropCreationRequest();
        cropCreationRequest.setDisplayName(displayName);
        cropCreationRequest.setFamily(family);
        cropCreationRequest.setSpecies(species);
        cropCreationRequest.setTenant(tenant.orElse(null));
        cropCreationRequest.setShared(shared);
        cropCreationRequest.setSubSpecies(subSpecies);
        cropCreationRequest.setCultivar(cultivar);
        cropCreationRequest.setAgrovocPlantURI(agrovocPlantURI);
        cropCreationRequest.setAgrovocProductURI(agrovocProductURI);
        cropCreationRequest.setLanguage(acceptedLanguage);

        return cropCreationRequest;
    }
}
