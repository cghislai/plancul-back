package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.Tenant_;

public class TenantSortMappings {
    static SortMapping<Tenant, String> NAME = (tenant, context) -> new SortMappingResult<>(tenant.get(Tenant_.name));

}
