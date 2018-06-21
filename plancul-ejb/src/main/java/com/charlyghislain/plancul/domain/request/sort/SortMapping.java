package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.util.DomainEntity;

import javax.persistence.criteria.Root;
import java.util.function.BiFunction;

public interface SortMapping<T extends DomainEntity, U extends Comparable<?>> extends BiFunction<Root<T>, SortMappingContext, SortMappingResult<U>> {
}
