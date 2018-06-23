package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocPlant_;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.AgrovocProduct_;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.BedPreparation;
import com.charlyghislain.plancul.domain.BedPreparation_;
import com.charlyghislain.plancul.domain.Bed_;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Crop_;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;
import com.charlyghislain.plancul.domain.CultureNursing_;
import com.charlyghislain.plancul.domain.Culture_;
import com.charlyghislain.plancul.domain.LocalizedMessage;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import java.time.LocalDate;

class CultureSortMappings {

    static SortMapping<Culture, String> CROP_PLANT_NAME = (culture, context) -> {
        Join<Culture, Crop> cropJoin = culture.join(Culture_.crop);
        Join<Crop, AgrovocPlant> plantJoin = cropJoin.join(Crop_.agrovocPlant);
        ListJoin<AgrovocPlant, LocalizedMessage> labelJoin = plantJoin.join(AgrovocPlant_.preferedLabel, JoinType.LEFT);
        return SortUtils.getLocalizedMessagePath(labelJoin, context);
    };

    static SortMapping<Culture, String> CROP_PRODUCT_NAME = (culture, context) -> {
        Join<Culture, Crop> cropJoin = culture.join(Culture_.crop);
        Join<Crop, AgrovocProduct> productJoin = cropJoin.join(Crop_.agrovocProduct);
        ListJoin<AgrovocProduct, LocalizedMessage> labelJoin = productJoin.join(AgrovocProduct_.preferedLabel, JoinType.LEFT);
        return SortUtils.getLocalizedMessagePath(labelJoin, context);
    };

    static SortMapping<Culture, String> CROP_CULTIVAR = (culture, context) -> {
        Path<Crop> cropPath = culture.get(Culture_.crop);
        return new SortMappingResult<>(cropPath.get(Crop_.cultivar));
    };

    static SortMapping<Culture, String> BED_NAME = (culture, context) -> {
        Path<Bed> bedPath = culture.get(Culture_.bed);
        return new SortMappingResult<>(bedPath.get(Bed_.name));
    };

    static SortMapping<Culture, LocalDate> SOWING_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.sowingDate));
    static SortMapping<Culture, LocalDate> GERMINATION_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.germinationDate));
    static SortMapping<Culture, LocalDate> FIRST_HARVEST_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.firstHarvestDate));
    static SortMapping<Culture, LocalDate> LAST_HARVEST_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.lastHarvestDate));
    static SortMapping<Culture, LocalDate> BED_OCCUPANCY_START_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.bedOccupancyStartDate));
    static SortMapping<Culture, LocalDate> BED_OCCUPANCY_END_DATE = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.bedOccupancyEndDate));
    static SortMapping<Culture, String> HTML_NOTES = (culture, context) -> new SortMappingResult<>(culture.get(Culture_.htmlNotes));

    static SortMapping<Culture, Integer> NURSING_DURATION = (culture, context) -> {
        Join<Culture, CultureNursing> nursingJoin = culture.join(Culture_.cultureNursing, JoinType.LEFT);
        return new SortMappingResult<>(nursingJoin.get(CultureNursing_.dayDuration));
    };

    static SortMapping<Culture, LocalDate> NURSING_START_DATE = (culture, context) -> {
        Join<Culture, CultureNursing> nursingJoin = culture.join(Culture_.cultureNursing, JoinType.LEFT);
        return new SortMappingResult<>(nursingJoin.get(CultureNursing_.startDate));
    };

    static SortMapping<Culture, Integer> CULTURE_PREPARATION_DURATION = (culture, context) -> {
        Join<Culture, BedPreparation> bedPreparationJoin = culture.join(Culture_.bedPreparation, JoinType.LEFT);
        return new SortMappingResult<>(bedPreparationJoin.get(BedPreparation_.dayDuration));
    };

    static SortMapping<Culture, LocalDate> CULTURE_PREPARATION_START_DATE = (culture, context) -> {
        Join<Culture, BedPreparation> bedPreparationJoin = culture.join(Culture_.bedPreparation, JoinType.LEFT);
        return new SortMappingResult<>(bedPreparationJoin.get(BedPreparation_.startDate));
    };
}
