package com.charlyghislain.plancul.domain.request.filter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class WsPlantProductTupleFilter implements Serializable {

    @NotNull
    @Size(min = 3)
    private String queryString;

    @NotNull
    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(@NotNull String queryString) {
        this.queryString = queryString;
    }
}
