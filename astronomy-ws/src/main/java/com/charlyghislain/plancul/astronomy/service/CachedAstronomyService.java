package com.charlyghislain.plancul.astronomy.service;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.request.AstronomyEventFilter;
import com.charlyghislain.plancul.astronomy.api.request.TimePagination;
import com.charlyghislain.plancul.astronomy.api.service.AstronomyService;
import com.charlyghislain.plancul.astronomy.cache.service.AstronomyEventDao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CachedAstronomyService implements AstronomyService {

    private final static ChronoUnit CACHE_PAGE_SIZE_UNIT = ChronoUnit.YEARS;
    public static final ZoneId UTC_ZONE = ZoneId.of("Z");

    @Inject
    private AstronomyEventDao cacheDao;
    @Inject
    private AstronomyEventPagerService pagerService;

    @Override
    public List<AstronomyEvent> searchEvents(AstronomyEventFilter filter) {
        TimePagination pagination = filter.getTimePagination();
        int startyear = pagination.getPageStartTime().get(ChronoField.YEAR);
        int endYear = pagination.getPageStartTime()
                .plus(pagination.getPageDuration(), pagination.getPageDurationUnit())
                .get(ChronoField.YEAR);
        ZonedDateTime paginationStartTime = pagination.getPageStartTime().atZone(UTC_ZONE);
        ZonedDateTime paginationEndTime = pagination.getPageStartTime()
                .plus(pagination.getPageDuration(), pagination.getPageDurationUnit())
                .atZone(UTC_ZONE);
        List<AstronomyEvent> eventResults = new ArrayList<>();

        for (int year = startyear; year <= endYear; year++) {
            boolean yearCached = cacheDao.isCached(year);
            TimePagination yearpagination = createyearpagination(year);
            if (!yearCached) {
                List<AstronomyEvent> yearEvents = pagerService.fetchEvents(yearpagination);
                cacheDao.cacheEvents(year, yearEvents);
            }
            List<AstronomyEvent> filteredEventsFromCache = cacheDao.searchEvents(filter, yearpagination)
                    .stream()
                    .filter(e -> !e.getDateTime().isBefore(paginationStartTime))
                    .filter(e -> e.getDateTime().isBefore(paginationEndTime))
                    .collect(Collectors.toList());
            eventResults.addAll(filteredEventsFromCache);
        }
        return eventResults;
    }

    private TimePagination createyearpagination(int year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        return new TimePagination(start, 1, ChronoUnit.YEARS);
    }

}
