package com.charlyghislain.plancul.domain.request.filter;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

public class WsDateFilter implements Serializable {

    @Nullable
    private LocalDate notBefore;
    @Nullable
    private LocalDate notAfter;

    public Optional<LocalDate> getNotBefore() {
        return Optional.ofNullable(notBefore);
    }

    public void setNotBefore(LocalDate notBefore) {
        this.notBefore = notBefore;
    }

    public Optional<LocalDate> getNotAfter() {
        return Optional.ofNullable(notAfter);
    }

    public void setNotAfter(LocalDate notAfter) {
        this.notAfter = notAfter;
    }
}
