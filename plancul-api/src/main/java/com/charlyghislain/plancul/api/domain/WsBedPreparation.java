package com.charlyghislain.plancul.api.domain;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class WsBedPreparation implements WsDomainEntity {

    private Long id;
    @Min(1)
    private int dayDuration;
    @NotNull
    private WsBedPreparationType type;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDayDuration() {
        return dayDuration;
    }

    public void setDayDuration(int dayDuration) {
        this.dayDuration = dayDuration;
    }

    @NotNull
    public WsBedPreparationType getType() {
        return type;
    }

    public void setType(@NotNull WsBedPreparationType type) {
        this.type = type;
    }

    @NotNull
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @NotNull
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
    }
}
