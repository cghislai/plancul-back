package com.charlyghislain.plancul.converter.util;

import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.request.sort.SortField;
import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.util.UntypedSort;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ToWsDomainObjectConverter<D extends DomainEntity, W extends WsDomainEntity> {

    default WsRef<W> reference(D entity) {
        return new WsRef<>(entity.getId());
    }

    W toWsEntity(D entity);


    Optional<Sort<D>> mapSort(UntypedSort untypedSort);

    default List<Sort<D>> fromUntypedSorts(List<UntypedSort> untypedSortList) {
        return untypedSortList.stream()
                .map(this::mapSort)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    default Optional<Sort<D>> mapSort(UntypedSort untypedSort, Function<String, SortField<D>> fieldMappingFunction) {
        String fieldName = untypedSort.getFieldName();
        boolean ascending = untypedSort.isAscending();
        try {
            SortField<D> bedSortField = fieldMappingFunction.apply(fieldName);
            Sort<D> bedSort = new Sort<>(ascending, bedSortField);
            return Optional.of(bedSort);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
