package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.util.ReferenceNotFoundException;
import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.TenantService;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantConverter implements WsDomainObjectConverter<Tenant, WsTenant> {

    @EJB
    private TenantService tenantService;

    @Override
    public Tenant load(WsRef<WsTenant> ref) {
        return tenantService.findTenantById(ref.getId())
                .orElseThrow(ReferenceNotFoundException::new);
    }

    @Override
    public WsTenant toWsEntity(Tenant entity) {
        Long id = entity.getId();
        String name = entity.getName();

        WsTenant wsTenant = new WsTenant();
        wsTenant.setId(id);
        wsTenant.setName(name);
        return wsTenant;
    }

    @Override
    public Tenant fromWsEntity(WsTenant wsEntity) {
        Long id = wsEntity.getId();

        Tenant tenant = new Tenant();
        tenant.setId(id);

        updateEntity(tenant, wsEntity);
        return tenant;
    }

    @Override
    public void updateEntity(Tenant entity, WsTenant wsEntity) {
        String name = wsEntity.getName();

        entity.setName(name);
    }
}
