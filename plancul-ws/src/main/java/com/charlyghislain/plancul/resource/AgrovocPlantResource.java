package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.AgrovocPlantConverter;
import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.api.WsAgrovocPlant;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/agrovocPlant")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AgrovocPlantResource {

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private AgrovocPlantConverter agrovocPlantConverter;

    @GET
    @Path("/{id}")
    public WsAgrovocPlant getAgrovocPlant(@PathParam("id") long id) {
        AgrovocPlant agrovocPlant = agrovocService.findAgrovocPlantById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsAgrovocPlant wsAgrovocPlant = agrovocPlantConverter.toWsEntity(agrovocPlant);
        return wsAgrovocPlant;
    }


}
