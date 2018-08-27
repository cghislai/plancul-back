package com.charlyghislain.plancul.config;


import com.charlyghislain.plancul.service.ApplicationInitializationService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class PlanCulInitializationHandler {

    @Inject
    private LiquibaseChangelogRunner liquibaseChangelogRunner;
    @Inject
    private ApplicationInitializationService applicationInitializationService;

    @PostConstruct
    public void init() {
        liquibaseChangelogRunner.runChangeLogs();
        applicationInitializationService.checkInitializationStatus();
    }


}
