package com.charlyghislain.plancul.astronomy.api.domain;

public enum Zodiac {
    ARIES(0),
    TAURUS(1),
    GEMINI(2),
    CANCER(3),
    LEO(4),
    VIRGO(5),
    LIBRA(6),
    SCORPIO(7),
    SAGITTARIUS(8),
    CAPRICORN(9),
    AQUARIUS(10),
    PISCES(11);

    private int almanacIndex;

    Zodiac(int almanacIndex) {
        this.almanacIndex = almanacIndex;
    }

    public int getAlmanacIndex() {
        return almanacIndex;
    }
}
