package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocPlant;

public enum AgrovocPlantSortField implements SortField<AgrovocPlant> {
    LABEL(AgrovocPlantSortMappings.LABEL);

    private final SortMapping<AgrovocPlant, ?> mappingFunction;

    AgrovocPlantSortField(SortMapping<AgrovocPlant, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<AgrovocPlant, ?> getMappingFunction() {
        return mappingFunction;
    }
}
