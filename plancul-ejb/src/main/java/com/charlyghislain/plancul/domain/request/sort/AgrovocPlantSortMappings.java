package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocPlant_;
import com.charlyghislain.plancul.domain.LocalizedMessage;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;

class AgrovocPlantSortMappings {

    static SortMapping<AgrovocPlant, String> LABEL = (agrovocPlant, context) -> {
        ListJoin<AgrovocPlant, LocalizedMessage> messageJoin = agrovocPlant.join(AgrovocPlant_.preferedLabel, JoinType.LEFT);

        return SortUtils.getLocalizedMessagePath(messageJoin, context);
    };
}
