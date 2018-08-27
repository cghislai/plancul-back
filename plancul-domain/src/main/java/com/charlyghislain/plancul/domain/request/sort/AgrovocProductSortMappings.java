package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.AgrovocProduct_;
import com.charlyghislain.plancul.domain.LocalizedMessage;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;

class AgrovocProductSortMappings {

    static SortMapping<AgrovocProduct, String> LABEL = (agrovocProduct, context) -> {
        ListJoin<AgrovocProduct, LocalizedMessage> messageJoin = agrovocProduct.join(AgrovocProduct_.preferedLabel, JoinType.LEFT);

        return SortUtils.getLocalizedMessagePath(messageJoin, context);
    };
}
