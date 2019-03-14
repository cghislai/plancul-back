package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Bed;

public enum BedSortField implements SortField<Bed> {
    NAME(BedSortMappings.NAME),
    PATCH(BedSortMappings.PATCH),
    SURFACE(BedSortMappings.SURFACE),
    PLOT(BedSortMappings.PLOT_NAME);

    private final SortMapping<Bed, ?> mappingFunction;

    BedSortField(SortMapping<Bed, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<Bed, ?> getMappingFunction() {
        return mappingFunction;
    }
}
