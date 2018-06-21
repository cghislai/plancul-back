package com.charlyghislain.plancul.converter.util;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;

public interface WsDomainObjectConverter<D extends DomainEntity, W extends WsDomainEntity>
        extends ToWsDomainObjectConverter<D, W>, FromWsDomainObjectConverter<D, W> {

}
