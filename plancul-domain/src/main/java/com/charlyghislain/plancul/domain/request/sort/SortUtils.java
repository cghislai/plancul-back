package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.LocalizedMessage_;
import com.charlyghislain.plancul.domain.i18n.Language;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

class SortUtils {

    static SortMappingResult<String> getLocalizedMessagePath(ListJoin<?, LocalizedMessage> messageListJoin, SortMappingContext context) {
        Path<Language> languagePath = messageListJoin.get(LocalizedMessage_.language);

        CriteriaBuilder criteriaBuilder = context.getCriteriaBuilder();
        Language language = context.getLanguage();

        Predicate languagePredicate = criteriaBuilder.or(
                criteriaBuilder.isNull(languagePath),
                criteriaBuilder.equal(languagePath, language)
        );

        Path<String> labelPath = messageListJoin.get(LocalizedMessage_.label);

        return new SortMappingResult<>(labelPath, languagePredicate);
    }
}
