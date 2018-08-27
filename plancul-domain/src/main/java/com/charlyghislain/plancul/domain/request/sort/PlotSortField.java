package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Plot;

public enum PlotSortField implements SortField<Plot> {
    NAME(PlotSortMappings.NAME);

    private final SortMapping<Plot, ?> mappingFunction;

    PlotSortField(SortMapping<Plot, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<Plot, ?> getMappingFunction() {
        return mappingFunction;
    }
}
