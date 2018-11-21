package com.charlyghislain.plancul.astronomy.api.request;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEventType;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class AstronomyEventFilter {

    @NotNull
    private Set<AstronomyEventType> typeWhiteList = new HashSet<>();
    @NotNull
    private TimePagination timePagination;

    public Set<AstronomyEventType> getTypeWhiteList() {
        return typeWhiteList;
    }

    public void setTypeWhiteList(Set<AstronomyEventType> typeWhiteList) {
        this.typeWhiteList = typeWhiteList;
    }

    @NotNull
    public TimePagination getTimePagination() {
        return timePagination;
    }

    public void setTimePagination(@NotNull TimePagination timePagination) {
        this.timePagination = timePagination;
    }
}
