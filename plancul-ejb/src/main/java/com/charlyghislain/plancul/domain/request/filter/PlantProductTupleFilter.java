package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.i18n.Language;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PlantProductTupleFilter implements Serializable {
    @NotNull
    private Language language;
    @NotNull
    private String queryString;

    @NotNull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull Language language) {
        this.language = language;
    }

    @NotNull
    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(@NotNull String queryString) {
        this.queryString = queryString;
    }
}
