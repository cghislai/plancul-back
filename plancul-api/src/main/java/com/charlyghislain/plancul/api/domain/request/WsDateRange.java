package com.charlyghislain.plancul.api.domain.request;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

// TODO: validator end > start
public class WsDateRange {
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;

    @NotNull
    public LocalDate getStart() {
        return start;
    }

    public void setStart(@NotNull LocalDate start) {
        this.start = start;
    }

    @NotNull
    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(@NotNull LocalDate end) {
        this.end = end;
    }
}
