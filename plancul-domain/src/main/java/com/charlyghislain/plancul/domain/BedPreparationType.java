package com.charlyghislain.plancul.domain;

import com.charlyghislain.plancul.domain.util.CulturePhaseType;

public enum BedPreparationType {

    COVER(CulturePhaseType.PREPARATION_COVER),
    PRESOWING(CulturePhaseType.PREPARATION_PRESOWING);

    private final CulturePhaseType phaseType;


    BedPreparationType(CulturePhaseType phaseType) {
        this.phaseType = phaseType;
    }

    public CulturePhaseType getPhaseType() {
        return phaseType;
    }
}
