package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.Bed_;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Plot_;

import javax.persistence.criteria.Path;

class BedSortMappings {

    static SortMapping<Bed, String> NAME = (bed, context) -> new SortMappingResult<>(bed.get(Bed_.name));

    static SortMapping<Bed, String> PATCH = (bed, context) -> new SortMappingResult<>(bed.get(Bed_.patch));

    static SortMapping<Bed, String> PLOT_NAME = (bed, context) -> {
        Path<Plot> plotPath = bed.get(Bed_.plot);
        Path<String> namePath = plotPath.get(Plot_.name);
        return new SortMappingResult<>(namePath);
    };
}
