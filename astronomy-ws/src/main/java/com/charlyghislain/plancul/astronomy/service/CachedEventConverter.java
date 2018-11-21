package com.charlyghislain.plancul.astronomy.service;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.domain.EclipticSideSwitchEvent;
import com.charlyghislain.plancul.astronomy.api.domain.MoonPhaseChangeEvent;
import com.charlyghislain.plancul.astronomy.api.domain.ZodiakChangeEvent;
import com.charlyghislain.plancul.astronomy.cache.domain.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CachedEventConverter {

    public static final ZoneId UTC_ZONE = ZoneId.of("Z");

    public static <T extends AstronomyEvent> CachedAstronomyEvent fromEvent(T event) {
        switch (event.getType()) {
            case SUN_ZODIAK_CHANGE:
                return toSunZodialEvent((ZodiakChangeEvent) event);
            case MOON_PHASE_CHANGE:
                return toMoonPhaseEvent((MoonPhaseChangeEvent) event);
            case MOON_ZODIAK_CHANGE:
                return toMoonZodiakEvent((ZodiakChangeEvent) event);
            case MOON_ECLIPTIC_SIDE_SWITCH:
                return toMoonEclipticSideSwitchEvent((EclipticSideSwitchEvent) event);
            default:
                throw new IllegalStateException("Unahdndled event type: " + event.getType());
        }
    }

    public static <T extends CachedAstronomyEvent> AstronomyEvent toEvent(T event) {
        switch (event.getEventType()) {
            case SUN_ZODIAK_CHANGE:
                return fromSunZodialEvent((CachedSunZodiakChangeEvent) event);
            case MOON_PHASE_CHANGE:
                return fromMoonPhaseEvent((CachedMoonPhaseEvent) event);
            case MOON_ZODIAK_CHANGE:
                return fromMoonZodiakEvent((CachedMoonZodiakChangeEvent) event);
            case MOON_ECLIPTIC_SIDE_SWITCH:
                return fromMoonEclipticSideSwitchEvent((CachedMoonEclipticSideSwitchEvent) event);
            default:
                throw new IllegalStateException("Unahdndled event type: " + event.getEventType());
        }
    }

    private static AstronomyEvent fromMoonEclipticSideSwitchEvent(CachedMoonEclipticSideSwitchEvent event) {
        EclipticSideSwitchEvent switchEvent = new EclipticSideSwitchEvent();
        switchEvent.setNorthSide(event.getNorthSide());
        switchEvent.setDateTime(fromLocalDateTimeUTC(event.getLocalDateTimeUTC()));
        switchEvent.setType(event.getEventType());
        return switchEvent;
    }

    private static AstronomyEvent fromMoonPhaseEvent(CachedMoonPhaseEvent event) {
        MoonPhaseChangeEvent changeEvent = new MoonPhaseChangeEvent();
        changeEvent.setMoonPhase(event.getMoonPhase());
        changeEvent.setDateTime(fromLocalDateTimeUTC(event.getLocalDateTimeUTC()));
        changeEvent.setType(event.getEventType());
        return changeEvent;
    }

    private static AstronomyEvent fromSunZodialEvent(CachedSunZodiakChangeEvent event) {
        ZodiakChangeEvent changeEvent = new ZodiakChangeEvent();
        changeEvent.setZodiac(event.getZodiac());
        changeEvent.setDateTime(fromLocalDateTimeUTC(event.getLocalDateTimeUTC()));
        changeEvent.setType(event.getEventType());
        return changeEvent;
    }

    private static AstronomyEvent fromMoonZodiakEvent(CachedMoonZodiakChangeEvent event) {
        ZodiakChangeEvent changeEvent = new ZodiakChangeEvent();
        changeEvent.setZodiac(event.getZodiac());
        changeEvent.setDateTime(fromLocalDateTimeUTC(event.getLocalDateTimeUTC()));
        changeEvent.setType(event.getEventType());
        return changeEvent;
    }

    private static CachedMoonEclipticSideSwitchEvent toMoonEclipticSideSwitchEvent(EclipticSideSwitchEvent event) {
        CachedMoonEclipticSideSwitchEvent switchEvent = new CachedMoonEclipticSideSwitchEvent();
        switchEvent.setNorthSide(event.isNorthSide());
        switchEvent.setEventType(event.getType());
        switchEvent.setLocalDateTimeUTC(toLocalDateTimeUTC(event.getDateTime()));
        return switchEvent;
    }

    private static CachedMoonZodiakChangeEvent toMoonZodiakEvent(ZodiakChangeEvent event) {
        CachedMoonZodiakChangeEvent changeEvent = new CachedMoonZodiakChangeEvent();
        changeEvent.setZodiac(event.getZodiac());
        changeEvent.setLocalDateTimeUTC(toLocalDateTimeUTC(event.getDateTime()));
        changeEvent.setEventType(event.getType());
        return changeEvent;
    }

    private static CachedMoonPhaseEvent toMoonPhaseEvent(MoonPhaseChangeEvent event) {
        CachedMoonPhaseEvent cachedMoonPhaseEvent = new CachedMoonPhaseEvent();
        cachedMoonPhaseEvent.setMoonPhase(event.getMoonPhase());
        cachedMoonPhaseEvent.setLocalDateTimeUTC(toLocalDateTimeUTC(event.getDateTime()));
        cachedMoonPhaseEvent.setEventType(event.getType());
        return cachedMoonPhaseEvent;
    }

    private static CachedSunZodiakChangeEvent toSunZodialEvent(ZodiakChangeEvent event) {
        CachedSunZodiakChangeEvent changeEvent = new CachedSunZodiakChangeEvent();
        changeEvent.setZodiac(event.getZodiac());
        changeEvent.setLocalDateTimeUTC(toLocalDateTimeUTC(event.getDateTime()));
        changeEvent.setEventType(event.getType());
        return changeEvent;
    }

    private static LocalDateTime toLocalDateTimeUTC(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(UTC_ZONE)
                .toLocalDateTime();
    }

    private static ZonedDateTime fromLocalDateTimeUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(UTC_ZONE);
    }
}
