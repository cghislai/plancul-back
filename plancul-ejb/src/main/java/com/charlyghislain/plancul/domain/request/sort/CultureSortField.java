package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Culture;

public enum CultureSortField implements SortField<Culture> {
    CROP_PLANT_NAME(CultureSortMappings.CROP_PLANT_NAME),
    CROP_PRODUCT_NAME(CultureSortMappings.CROP_PRODUCT_NAME),
    CROP_CULTIVAR(CultureSortMappings.CROP_CULTIVAR),
    BED_NAME(CultureSortMappings.BED_NAME),
    SOWING_DATE(CultureSortMappings.SOWING_DATE),
    GERMINATION_DATE(CultureSortMappings.GERMINATION_DATE),
    FIRST_HARVEST_DATE(CultureSortMappings.FIRST_HARVEST_DATE),
    LAST_HARVEST_DATE(CultureSortMappings.LAST_HARVEST_DATE),
    BED_OCCUPANCY_START_DATE(CultureSortMappings.BED_OCCUPANCY_START_DATE),
    BED_OCCUPANCY_END_DATE(CultureSortMappings.BED_OCCUPANCY_END_DATE),
    HTML_NOTES(CultureSortMappings.HTML_NOTES),
    NURSERING_DURATION(CultureSortMappings.NURSING_DURATION),
    NURSERING_START_DATE(CultureSortMappings.NURSING_START_DATE),
    CULTURE_PREPARATION_DURATION(CultureSortMappings.CULTURE_PREPARATION_DURATION),
    CULTURE_PREPARATION_START_DATE(CultureSortMappings.CULTURE_PREPARATION_START_DATE);

    private final SortMapping<Culture, ?> mappingFunction;

    CultureSortField(SortMapping<Culture, ?> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public SortMapping<Culture, ?> getMappingFunction() {
        return mappingFunction;
    }
}
