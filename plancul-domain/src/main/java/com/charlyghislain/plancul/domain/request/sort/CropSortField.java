package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Crop;

public enum CropSortField implements SortField<Crop> {
    DISPLAY_NAME(CropSortMappings.PLANT_NAME),
    PLANT_NAME(CropSortMappings.PLANT_NAME),
    PRODUCT_NAME(CropSortMappings.PRODUCT_NAME),
    CULTIVAR(CropSortMappings.CULTIVAR);

    private final SortMapping<Crop, ?> mappingFunction;

    CropSortField(SortMapping<Crop, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<Crop, ?> getMappingFunction() {
        return mappingFunction;
    }
}
