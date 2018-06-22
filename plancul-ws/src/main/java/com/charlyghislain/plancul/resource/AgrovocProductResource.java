package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.AgrovocProductConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.api.WsAgrovocProduct;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.LanguageContainer;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.UntypedSort;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    @Inject
    private List<UntypedSort> sortList;
    @Inject
    @AcceptedLanguage
    private LanguageContainer acceptedLanguage;

    @GET
    @Path("/{id}")
    public WsAgrovocProduct getAgrovocProduct(@PathParam("id") long id) {
        AgrovocProduct agrovocProduct = agrovocService.findAgrovocProductById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsAgrovocProduct wsAgrovocProduct = agrovocProductConverter.toWsEntity(agrovocProduct);
        return wsAgrovocProduct;
    }


}
