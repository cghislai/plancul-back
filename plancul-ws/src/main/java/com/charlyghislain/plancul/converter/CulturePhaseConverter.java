package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.api.WsCulture;
import com.charlyghislain.plancul.domain.api.response.WsCulturePhase;
import com.charlyghislain.plancul.domain.api.response.WsCulturePhaseType;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.util.CulturePhase;
import com.charlyghislain.plancul.domain.util.CulturePhaseType;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

public class CulturePhaseConverter {
    @Inject
    private CultureConverter cultureConverter;

    public WsCulturePhase toWsCulturePhase(CulturePhase culturePhase) {
        Culture culture = culturePhase.getCulture();
        CulturePhaseType phaseType = culturePhase.getPhaseType();
        LocalDate startDate = culturePhase.getStartDate();
        LocalDate endDate = culturePhase.getEndDate();

        Optional<WsRef<WsCulture>> cultureWsRef = Optional.ofNullable(culture)
                .map(cultureConverter::reference);
        Optional<WsCulturePhaseType> wsCulturePhaseType = Optional.ofNullable(phaseType)
                .map(Enum::name)
                .map(WsCulturePhaseType::valueOf);

        WsCulturePhase wsCulturePhase = new WsCulturePhase();
        wsCulturePhase.setCultureWsRef(cultureWsRef.orElse(null));
        wsCulturePhase.setPhaseType(wsCulturePhaseType.orElse(null));
        wsCulturePhase.setStartDate(startDate);
        wsCulturePhase.setEndDate(endDate);
        return wsCulturePhase;
    }

    // TODO: provide a converter to jax-rs
    public CulturePhaseType fromWsCulturePhaseTypeName(String wsCulturePhaseTypeName) {
        return CulturePhaseType.valueOf(wsCulturePhaseTypeName);
    }
}
