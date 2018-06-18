package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.AgrovocProductConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/agrovocProduct")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgrovocProductResource {

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private AgrovocProductConverter agrovocProductConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;

    @GET
    @Path("/{id}")
    public WsAgrovocProduct getAgrovocProduct(@PathParam("id") long id) {
        AgrovocProduct agrovocProduct = agrovocService.findAgrovocProductById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsAgrovocProduct wsAgrovocProduct = agrovocProductConverter.toWsEntity(agrovocProduct);
        return wsAgrovocProduct;
    }


}
