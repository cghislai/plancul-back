package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.WsTenant;
import com.charlyghislain.plancul.converter.util.ToWsDomainObjectConverter;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class WsTenantConverter implements ToWsDomainObjectConverter<Tenant, WsTenant> {

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
    public Optional<Sort<Tenant>> mapSort(UntypedSort untypedSort) {
        return Optional.empty();
    }

}
