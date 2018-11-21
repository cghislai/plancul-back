package com.charlyghislain.plancul.astronomy.api.service;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.request.AstronomyEventFilter;

import java.util.List;

public interface AstronomyService {

    List<AstronomyEvent> searchEvents(AstronomyEventFilter filter);
}
