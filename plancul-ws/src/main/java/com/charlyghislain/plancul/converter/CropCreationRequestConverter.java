package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.WsCropCreationRequest;
import com.charlyghislain.plancul.domain.util.WsRef;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class CropCreationRequestConverter {

    @Inject
    private TenantConverter tenantConverter;

    public CropCreationRequest fromWsCropCreationRequest(WsCropCreationRequest wsCropCreationRequest) {
        String agroPlantUri = wsCropCreationRequest.getAgrovocPlantUri();
        String agroProductUri = wsCropCreationRequest.getAgrovocProductUri();
        Optional<String> cultivar = wsCropCreationRequest.getCultivar();
        Optional<WsRef<WsTenant>> tenantRestrictionRef = wsCropCreationRequest.getTenantRestrictionRef();

        Optional<Tenant> tenant = tenantRestrictionRef.map(tenantConverter::load);

        CropCreationRequest cropCreationRequest = new CropCreationRequest();
        cropCreationRequest.setAgrovocPlantUri(agroPlantUri);
        cropCreationRequest.setAgrovocProductUri(agroProductUri);
        cropCreationRequest.setCultivar(cultivar.orElse(null));
        cropCreationRequest.setTenantRestriction(tenant.orElse(null));

        return cropCreationRequest;
    }
}
