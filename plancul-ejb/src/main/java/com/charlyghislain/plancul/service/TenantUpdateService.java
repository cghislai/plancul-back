package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

@Stateless
public class TenantUpdateService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private PlotService plotService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private TenantUserRolesUpdateService tenantUserRolesUpdateService;

    @Inject
    private SearchService searchService;
    @Inject
    private ValidationService validationService;

    public Tenant createTenant(Tenant tenant) {
        Tenant createdTenant = saveTenant(tenant);
        try {
            this.createDefaultPlot(createdTenant);
        } catch (OperationNotAllowedException e) {
            throw new IllegalStateException(e);
        }
        return createdTenant;
    }

    private void createDefaultPlot(Tenant managedTenant) throws OperationNotAllowedException {
        Plot plot = new Plot();
        plot.setTenant(managedTenant);
        plot.setName("Default");
        plotService.savePlot(plot);
    }

    private Tenant saveTenant(@Valid Tenant tenant) {
        return entityManager.merge(tenant);
    }
}
