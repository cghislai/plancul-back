package com.charlyghislain.plancul.astronomy.config;


import com.charlyghislain.plancul.astronomy.service.CachedAstronomyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.CompletableFuture;

@Singleton
@Startup
public class PlanCulAstronomyInitializationHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PlanCulAstronomyInitializationHandler.class);

    @Inject
    private LiquibaseChangelogRunner liquibaseChangelogRunner;
    @Inject
    private CachedAstronomyService cachedAstronomyService;

    @PostConstruct
    public void init() {
        LOG.info("Running liquibase");
        liquibaseChangelogRunner.runChangeLogs();

        preloadYears();
    }

    public void preloadYears() {
        int thisYear = LocalDateTime.now().get(ChronoField.YEAR);
        int fromYear = thisYear - 5;
        int toYearInclusive = thisYear + 10;
        CompletableFuture<Void> preloadingCompletion = CompletableFuture.completedFuture(null);
        LOG.info("Precaching years {} to {}", fromYear, toYearInclusive);
        for (int year = fromYear; year <= toYearInclusive; year++) {
            final int yearToLoad = year;
            preloadingCompletion = preloadingCompletion.thenCompose(
                    v -> cachedAstronomyService.loadAndCacheEventsAsync(yearToLoad)
            );
        }
        preloadingCompletion.handle((r, error) -> {
            if (error != null) {
                LOG.warn("Event preloading errored", error);
            } else {
                LOG.info("Event preloading complete");
            }
            return null;
        });
    }


}
