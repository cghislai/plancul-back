package com.charlyghislain.plancul.astronomy.ws.resource;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.request.AstronomyEventFilter;
import com.charlyghislain.plancul.astronomy.api.service.AstronomyService;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/event")
@PermitAll
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AstronomyEventResource {

    @Inject
    private AstronomyService astronomyService;

    @POST
    @Path("/search")
    public List<? extends AstronomyEvent> searchEvents(AstronomyEventFilter filter) {
        List<AstronomyEvent> events = astronomyService.searchEvents(filter);
        return events;
    }

}
