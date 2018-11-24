package com.charlyghislain.plancul.astronomy.service;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.request.AstronomyEventFilter;
import com.charlyghislain.plancul.astronomy.api.request.TimePagination;
import com.charlyghislain.plancul.astronomy.api.service.AstronomyService;
import com.charlyghislain.plancul.astronomy.cache.service.AstronomyEventDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Named
@ApplicationScoped
public class CachedAstronomyService implements AstronomyService {

    public static final ZoneId UTC_ZONE = ZoneId.of("Z");
    private final static Logger LOG = LoggerFactory.getLogger(CachedAstronomyService.class);

    @Inject
    private AstronomyEventDao cacheDao;
    @Inject
    private AstronomyEventPagerService pagerService;
    @Resource
    private ManagedExecutorService managedExecutorService;
    private final Set<Integer> loadingYears = Collections.synchronizedSet(new HashSet<Integer>());

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
            if (!yearCached) {
                loadAndCacheEventsAsync(year);
            }
            TimePagination yearpagination = createyearpagination(year);
            List<AstronomyEvent> filteredEventsFromCache = cacheDao.searchEvents(filter, yearpagination)
                    .stream()
                    .filter(e -> !e.getDateTime().isBefore(paginationStartTime))
                    .filter(e -> e.getDateTime().isBefore(paginationEndTime))
                    .collect(Collectors.toList());
            eventResults.addAll(filteredEventsFromCache);
        }
        return eventResults;
    }

    public void loadAndCacheEvents(int year) {
        this.loadAndCacheEventsAsync(year).join();
    }

    public CompletableFuture<Void> loadAndCacheEventsAsync(int year) {
        synchronized (loadingYears) {
            if (loadingYears.contains(year)) {
                return CompletableFuture.completedFuture(null);
            }
            loadingYears.add(year);
        }
        LOG.info("Caching events for year {} asynchronously", year);
        TimePagination yearpagination = createyearpagination(year);
        return CompletableFuture.supplyAsync(() -> pagerService.fetchEvents(yearpagination), managedExecutorService)
                .thenAccept(yearEvents -> cacheDao.cacheEvents(year, yearEvents))
                .thenRun(() -> {
                    synchronized (loadingYears) {
                        LOG.info("Cached events for year {}", year);
                        loadingYears.remove(year);
                    }
                });
    }

    private TimePagination createyearpagination(int year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        return new TimePagination(start, 1, ChronoUnit.YEARS);
    }

}
