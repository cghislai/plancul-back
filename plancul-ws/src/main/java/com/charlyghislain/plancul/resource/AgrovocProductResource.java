package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.AgrovocProductConverter;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.api.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/agrovocProduct")
@RolesAllowed({ApplicationGroupNames.TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AgrovocProductResource {

    @Inject
    private AgrovocService agrovocService;
    @Inject
    private AgrovocProductConverter agrovocProductConverter;

    @GET
    @Path("/{id}")
    public WsAgrovocProduct getAgrovocProduct(@PathParam("id") long id) {
        AgrovocProduct agrovocProduct = agrovocService.findAgrovocProductById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsAgrovocProduct wsAgrovocProduct = agrovocProductConverter.toWsEntity(agrovocProduct);
        return wsAgrovocProduct;
    }


}
