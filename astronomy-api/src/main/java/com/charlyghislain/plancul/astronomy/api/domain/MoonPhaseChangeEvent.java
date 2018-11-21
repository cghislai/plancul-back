package com.charlyghislain.plancul.astronomy.api.domain;

public class MoonPhaseChangeEvent extends AstronomyEvent {

    private MoonPhase moonPhase;

    public MoonPhase getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(MoonPhase moonPhase) {
        this.moonPhase = moonPhase;
    }
}
