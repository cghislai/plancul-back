package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.TenantRole;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;

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

    public Tenant saveTenant(Tenant tenant) throws OperationNotAllowedException {
        if (tenant.getId() == null) {
            return this.createTenant(tenant);
        }

        validationService.validateLoggedUserHasTenantRole(tenant, TenantRole.ADMIN);
        return this.persistTenant(tenant);
    }

    public Tenant createTenant(Tenant tenant) throws OperationNotAllowedException {
        validationService.validateNullId(tenant);

        tenant.setCreated(LocalDateTime.now());
        Tenant createdTenant = saveTenant(tenant);

        this.createDefaultPlot(createdTenant);
        return createdTenant;
    }

    private void createDefaultPlot(Tenant managedTenant) throws OperationNotAllowedException {
        Plot plot = new Plot();
        plot.setTenant(managedTenant);
        plot.setName("Default");
        plotService.savePlot(plot);
    }

    private Tenant persistTenant(@Valid Tenant tenant) {
        tenant.setUpdated(LocalDateTime.now());
        return entityManager.merge(tenant);
    }
}
