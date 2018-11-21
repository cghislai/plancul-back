package com.charlyghislain.plancul.astronomy.cache.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MOON_ECLIPTIC_SIDE_SWITCH")
public class CachedMoonEclipticSideSwitchEvent extends CachedAstronomyEvent {

    @Column(name = "NORTH_SIDE")
    private Boolean northSide;

    public Boolean getNorthSide() {
        return northSide;
    }

    public void setNorthSide(Boolean northSide) {
        this.northSide = northSide;
    }
}
