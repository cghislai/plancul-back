package com.charlyghislain.plancul.astronomy.cache.domain;

import com.charlyghislain.plancul.astronomy.api.domain.MoonPhase;

import javax.persistence.*;

@Entity
@DiscriminatorValue("MOON_PHASE_CHANGE")
public class CachedMoonPhaseEvent extends CachedAstronomyEvent {

    @Column(name = "MOONPHASE")
    @Enumerated(EnumType.STRING)
    private MoonPhase moonPhase;

    public MoonPhase getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(MoonPhase moonPhase) {
        this.moonPhase = moonPhase;
    }
}
