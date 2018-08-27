package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.service.ApplicationInitializationService;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/application/status")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@PermitAll
public class ApplicationStatusResource {

    @Inject
    private ApplicationInitializationService applicationInitializationService;

    @GET
    @Path("adminAccountInitialized")
    public boolean isAdminAccountInitialized() {
        return applicationInitializationService.isAdminAccountInitialized();
    }
}
