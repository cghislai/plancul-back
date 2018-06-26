package com.charlyghislain.plancul.domain.request.sort;

import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;

public class SortMappingContext {
    private Language language;
    private CriteriaBuilder criteriaBuilder;
    private Path<Tenant> tenantPath;

    public SortMappingContext(Language language, CriteriaBuilder criteriaBuilder) {
        this.language = language;
        this.criteriaBuilder = criteriaBuilder;
    }

    public Language getLanguage() {
        return language;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }
}
