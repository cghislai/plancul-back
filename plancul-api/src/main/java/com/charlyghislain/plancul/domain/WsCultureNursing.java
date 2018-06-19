package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.WsDomainEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class WsCultureNursing implements WsDomainEntity {

    private Long id;
    @Min(1)
    private int dayDuration;
    @NotNull
    private LocalDate startdate;
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
    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(@NotNull LocalDate startdate) {
        this.startdate = startdate;
    }

    @NotNull
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
    }
}
