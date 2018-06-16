package com.charlyghislain.plancul.config;

import com.charlyghislain.plancul.service.SecurityService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class PlanCulInitializationHandler {

    @EJB
    private SecurityService securityService;
    @EJB
    private LiquibaseChangelogRunner liquibaseChangelogRunner;

    @PostConstruct
    public void init() {
        liquibaseChangelogRunner.runChangeLogs();
        securityService.createDefaultAccounts();
    }


}
