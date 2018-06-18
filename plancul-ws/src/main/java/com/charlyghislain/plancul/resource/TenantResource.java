package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.TenantConverter;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.WsTenant;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.TenantService;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tenant")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TenantResource {

    @EJB
    private TenantService tenantService;
    @Inject
    private TenantConverter tenantConverter;


    @POST
    public WsRef<WsTenant> createTenant(@NotNull @Valid WsTenant wsTenant) {
        Tenant tenant = tenantConverter.fromWsEntity(wsTenant);
        Tenant createdTenant = tenantService.createTenant(tenant);
        WsRef<WsTenant> reference = tenantConverter.reference(createdTenant);
        return reference;
    }

    @GET
    @Path("/{id}")
    public WsTenant getTenant(@PathParam("id") long id) {
        Tenant tenant = tenantService.findTenantById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsTenant wsTenant = tenantConverter.toWsEntity(tenant);
        return wsTenant;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsTenant> updateTenant(@PathParam("id") long id, @NotNull @Valid WsTenant wsTenant) {
        Tenant tenant = tenantConverter.fromWsEntity(wsTenant);
        Tenant savedTenant = tenantService.saveTenant(tenant);
        WsRef<WsTenant> reference = tenantConverter.reference(savedTenant);
        return reference;
    }


}
