package com.charlyghislain.plancul.astronomy.api.request;


import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimePagination {

    @NotNull
    private LocalDateTime pageStartTime;
    private int pageDuration;
    @NotNull
    private ChronoUnit pageDurationUnit;

    public TimePagination() {
    }

    public TimePagination(@NotNull LocalDateTime pageStartTime, int pageDuration, @NotNull ChronoUnit pageDurationUnit) {
        this.pageStartTime = pageStartTime;
        this.pageDuration = pageDuration;
        this.pageDurationUnit = pageDurationUnit;
    }

    public void setPageDuration(int duration, ChronoUnit unit) {
        this.setPageDuration(duration);
        this.setPageDurationUnit(unit);
    }

    public LocalDateTime getPageStartTime() {
        return pageStartTime;
    }

    public void setPageStartTime(LocalDateTime pageStartTime) {
        this.pageStartTime = pageStartTime;
    }

    public int getPageDuration() {
        return pageDuration;
    }

    public void setPageDuration(int pageDuration) {
        this.pageDuration = pageDuration;
    }

    public ChronoUnit getPageDurationUnit() {
        return pageDurationUnit;
    }

    public void setPageDurationUnit(ChronoUnit pageDurationUnit) {
        this.pageDurationUnit = pageDurationUnit;
    }
}
