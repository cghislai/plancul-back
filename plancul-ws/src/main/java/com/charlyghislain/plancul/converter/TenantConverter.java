package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.converter.util.WsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.api.WsTenant;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.request.sort.TenantSortField;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.service.TenantService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

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

    @Override
    public Optional<Sort<Tenant>> mapSort(UntypedSort untypedSort) {
        return this.mapSort(untypedSort, TenantSortField::valueOf);
    }
}
