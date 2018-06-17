package com.charlyghislain.plancul.domain.filter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsPlantProductTupleFilter implements Serializable {

    @NotNull
    private String language;
    @NotNull
    private String queryString;


    @NotNull
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull String language) {
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
