package com.charlyghislain.plancul.astronomy.api.domain;

public enum AstronomyEventType {
    SUN_ZODIAK_CHANGE("sun_zodiac"),
    MOON_PHASE_CHANGE("moon_phase"),
    MOON_ZODIAK_CHANGE("moon_zodiac"),
    MOON_ECLIPTIC_SIDE_SWITCH("moon_ecliptic_side");

    private String almanacName;

    AstronomyEventType(String almanacName) {
        this.almanacName = almanacName;
    }

    public String getAlmanacName() {
        return almanacName;
    }
}
