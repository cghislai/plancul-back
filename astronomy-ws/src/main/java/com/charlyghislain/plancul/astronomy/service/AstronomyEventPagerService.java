package com.charlyghislain.plancul.astronomy.service;

import com.charlyghislain.plancul.astronomy.api.domain.*;
import com.charlyghislain.plancul.astronomy.api.request.TimePagination;
import com.charlyghislain.plancul.astronomy.client.almanac.AlmanacClient;
import com.charlyghislain.plancul.astronomy.client.almanac.domain.Event;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AstronomyEventPagerService {

    @Inject
    private AlmanacClient almanacClient;

    public List<AstronomyEvent> fetchEvents(TimePagination pagination) {
        LocalDateTime pageStartTime = pagination.getPageStartTime();
        LocalDateTime pageEndTime = pageStartTime.plus(pagination.getPageDuration(), pagination.getPageDurationUnit());

        List<Event> events = almanacClient.saerchMoonZodiacEvents(pageStartTime, pageEndTime);
        List<Event> moonPhaseEvents = almanacClient.searchMoonPhaseEvents(pageStartTime, pageEndTime);
        events.addAll(moonPhaseEvents);

        List<AstronomyEvent> astronomyEvents = events.stream()
                .map(this::createEvent)
                .sorted(Comparator.comparing(AstronomyEvent::getDateTime))
                .collect(Collectors.toList());
        return astronomyEvents;
    }

    private AstronomyEvent createEvent(Event event) {
        String body = event.getBody();
        AstronomyEventType eventType = parseEventTYpe(event.getEventType())
                .orElseThrow(() -> new RuntimeException("Unhandled event type: " + event.getEventType()));
        if ("moon".equals(body)) {
            return this.createMoonEvent(event, eventType);
        } else {
            throw new RuntimeException("Unhandled event body: " + body);
        }
    }

    private AstronomyEvent createMoonEvent(Event event, AstronomyEventType eventType) {
        ZonedDateTime dateTime = event.getDateTime()
                .atZone(ZoneId.of("Z"));
        Integer indexValue = event.getIndexValue();

        switch (eventType) {
            case MOON_PHASE_CHANGE: {
                MoonPhase moonPhase = getAlmanacMoonPhase(indexValue)
                        .orElseThrow(() -> new RuntimeException("Unhandled moon phase index: " + indexValue));
                MoonPhaseChangeEvent moonPhaseChangeEvent = new MoonPhaseChangeEvent();
                moonPhaseChangeEvent.setMoonPhase(moonPhase);
                moonPhaseChangeEvent.setDateTime(dateTime);
                moonPhaseChangeEvent.setType(eventType);
                return moonPhaseChangeEvent;
            }
            case MOON_ZODIAK_CHANGE: {
                // We want the zodiac opposite to the moon position...
                int oppositeIndexValue  = (indexValue + 6) % 12;
                Zodiac zodiac = getAlmanacZodiac(oppositeIndexValue)
                        .orElseThrow(() -> new RuntimeException("Unhandled zodiac index: " + indexValue));
                ZodiakChangeEvent zodiakChangeEvent = new ZodiakChangeEvent();
                zodiakChangeEvent.setZodiac(zodiac);
                zodiakChangeEvent.setDateTime(dateTime);
                zodiakChangeEvent.setType(eventType);
                return zodiakChangeEvent;
            }
            default: {
                throw new RuntimeException("Unhandled moon event type: " + eventType);
            }
        }
    }


    private Optional<AstronomyEventType> parseEventTYpe(String typeString) {
        return Arrays.stream(AstronomyEventType.values())
                .filter(v -> v.getAlmanacName().equals(typeString))
                .findAny();
    }

    private Optional<MoonPhase> getAlmanacMoonPhase(Integer index) {
        return Arrays.stream(MoonPhase.values())
                .filter(v -> v.getAlmanacIndex() == index)
                .findAny();
    }

    private Optional<Zodiac> getAlmanacZodiac(Integer index) {
        return Arrays.stream(Zodiac.values())
                .filter(v -> v.getAlmanacIndex() == index)
                .findAny();
    }

}
