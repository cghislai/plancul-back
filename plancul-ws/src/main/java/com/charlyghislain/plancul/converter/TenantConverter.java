package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.util.FromWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.service.TenantService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TenantConverter implements FromWsDomainObjectConverter<Tenant, WsTenant> {

    @Inject
    private TenantService tenantService;

    @Override
    public Tenant load(WsRef<WsTenant> ref) {
        return tenantService.findTenantById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public Tenant fromWsEntity(WsTenant wsEntity) {
        Long id = wsEntity.getId();
        String name = wsEntity.getName();

        Tenant tenant = new Tenant();
        tenant.setId(id);
        tenant.setName(name);
        return tenant;
    }

}
