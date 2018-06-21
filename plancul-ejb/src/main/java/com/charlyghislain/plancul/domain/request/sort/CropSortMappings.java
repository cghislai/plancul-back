package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocPlant_;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.AgrovocProduct_;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Crop_;
import com.charlyghislain.plancul.domain.LocalizedMessage;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;

class CropSortMappings {

    static SortMapping<Crop, String> PLANT_NAME = (crop, context) -> {
        Join<Crop, AgrovocPlant> plantJoin = crop.join(Crop_.agrovocPlant, JoinType.LEFT);
        ListJoin<AgrovocPlant, LocalizedMessage> messageJoin = plantJoin.join(AgrovocPlant_.preferedLabel, JoinType.LEFT);

        return SortUtils.getLocalizedMessagePath(messageJoin, context);
    };

    static SortMapping<Crop, String> PRODUCT_NAME = (crop, context) -> {
        Join<Crop, AgrovocProduct> productJoin = crop.join(Crop_.agrovocProduct, JoinType.LEFT);
        ListJoin<AgrovocProduct, LocalizedMessage> messageJoin = productJoin.join(AgrovocProduct_.preferedLabel, JoinType.LEFT);

        return SortUtils.getLocalizedMessagePath(messageJoin, context);
    };

    static SortMapping<Crop, String> CULTIVAR = (crop, context) -> new SortMappingResult<>(crop.get(Crop_.cultivar));

}
