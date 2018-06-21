package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.util.DomainEntity;

public class Sort<T extends DomainEntity> {
    private final boolean ascending;
    private final SortMapping<T, ?> mappingFunction;

    public Sort(boolean ascending, SortField<T> sortField) {
        this.ascending = ascending;
        this.mappingFunction = sortField.getMappingFunction();
    }

    public boolean isAscending() {
        return ascending;
    }

    public SortMapping<T, ?> getMappingFunction() {
        return mappingFunction;
    }
}
