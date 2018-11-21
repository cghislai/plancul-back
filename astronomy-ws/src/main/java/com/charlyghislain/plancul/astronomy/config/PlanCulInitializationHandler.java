package com.charlyghislain.plancul.astronomy.config;


import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class PlanCulInitializationHandler {

    @Inject
    private LiquibaseChangelogRunner liquibaseChangelogRunner;

    @PostConstruct
    public void init() {
        liquibaseChangelogRunner.runChangeLogs();
    }


}
