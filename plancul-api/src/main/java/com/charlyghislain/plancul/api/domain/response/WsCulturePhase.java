package com.charlyghislain.plancul.api.domain.response;

import com.charlyghislain.plancul.api.domain.WsCulture;
import com.charlyghislain.plancul.api.domain.util.WsRef;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

public class WsCulturePhase implements Serializable {
    @NotNull
    private WsRef<WsCulture> cultureWsRef;
    @NotNull
    private WsCulturePhaseType phaseType;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @NotNull
    public WsRef<WsCulture> getCultureWsRef() {
        return cultureWsRef;
    }

    public void setCultureWsRef(@NotNull WsRef<WsCulture> cultureWsRef) {
        this.cultureWsRef = cultureWsRef;
    }

    @NotNull
    public WsCulturePhaseType getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(@NotNull WsCulturePhaseType phaseType) {
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
