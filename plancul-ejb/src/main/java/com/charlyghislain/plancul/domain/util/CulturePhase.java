package com.charlyghislain.plancul.domain.util;

import com.charlyghislain.plancul.domain.Culture;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class CulturePhase {
    @NotNull
    private Culture culture;
    @NotNull
    private CulturePhaseType phaseType;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @NotNull
    public Culture getCulture() {
        return culture;
    }

    public void setCulture(@NotNull Culture culture) {
        this.culture = culture;
    }

    @NotNull
    public CulturePhaseType getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(@NotNull CulturePhaseType phaseType) {
        this.phaseType = phaseType;
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
