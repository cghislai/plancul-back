package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.util.DomainEntity;

public interface SortField<T extends DomainEntity>  {

    SortMapping<T, ?> getMappingFunction();

}
