package com.charlyghislain.plancul.astronomy.api.domain;

public class EclipticSideSwitchEvent extends AstronomyEvent {
    boolean northSide;

    public boolean isNorthSide() {
        return northSide;
    }

    public void setNorthSide(boolean northSide) {
        this.northSide = northSide;
    }
}
