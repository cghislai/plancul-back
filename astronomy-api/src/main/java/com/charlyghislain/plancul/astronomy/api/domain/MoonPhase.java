package com.charlyghislain.plancul.astronomy.api.domain;

public enum MoonPhase {
    NEW_MOON(true, 0),
    FIRST_QUARTER(true, 1),
    FULL_MOON(false, 2),
    THIRD_QUARTER(false, 3);

    private boolean ascending;
    private int almanacIndex;

    MoonPhase(boolean ascending, int almanacIndex) {
        this.ascending = ascending;
        this.almanacIndex = almanacIndex;
    }

    public boolean isAscending() {
        return ascending;
    }

    public int getAlmanacIndex() {
        return almanacIndex;
    }
}
