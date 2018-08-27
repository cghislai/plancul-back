package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Tenant;

public enum TenantSortField implements SortField<Tenant> {
    NAME(TenantSortMappings.NAME);

    private final SortMapping<Tenant, ?> mappingFunction;

    TenantSortField(SortMapping<Tenant, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<Tenant, ?> getMappingFunction() {
        return mappingFunction;
    }
}
