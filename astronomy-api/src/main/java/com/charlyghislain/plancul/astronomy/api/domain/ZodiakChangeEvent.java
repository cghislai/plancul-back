package com.charlyghislain.plancul.astronomy.api.domain;

public class ZodiakChangeEvent extends AstronomyEvent {
    private Zodiac zodiac;

    public Zodiac getZodiac() {
        return zodiac;
    }

    public void setZodiac(Zodiac zodiac) {
        this.zodiac = zodiac;
    }
}
