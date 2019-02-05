package com.charlyghislain.plancul.domain.util;

import com.charlyghislain.plancul.domain.validation.ValidDateRange;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ValidDateRange
public class DateRange {

    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;

    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
