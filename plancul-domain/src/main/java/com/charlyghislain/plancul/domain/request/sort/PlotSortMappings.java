package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Plot_;

class PlotSortMappings {

    static SortMapping<Plot, String> NAME = (plot, context) -> new SortMappingResult<>(plot.get(Plot_.name));
}
