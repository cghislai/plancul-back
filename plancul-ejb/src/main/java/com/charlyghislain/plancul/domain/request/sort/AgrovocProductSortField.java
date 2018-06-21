package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocProduct;

public enum AgrovocProductSortField implements SortField<AgrovocProduct> {
    LABEL(AgrovocProductSortMappings.LABEL);

    private final SortMapping<AgrovocProduct, ?> mappingFunction;

    AgrovocProductSortField(SortMapping<AgrovocProduct, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<AgrovocProduct, ?> getMappingFunction() {
        return mappingFunction;
    }
}
