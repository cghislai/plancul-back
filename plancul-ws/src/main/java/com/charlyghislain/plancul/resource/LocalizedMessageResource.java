package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.LocalizedMessageConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.api.WsLocalizedMessage;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.service.I18NService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/localizedMessage")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocalizedMessageResource {

    @EJB
    private I18NService i18nService;
    @Inject
    private LocalizedMessageConverter localizedMessageConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;

    @GET
    @Path("/{id}")
    public WsLocalizedMessage getLocalizedMessage(@PathParam("id") long id) {
        LocalizedMessage localizedMessage = i18nService.findLocalizedMessageById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsLocalizedMessage wsLocalizedMessage = localizedMessageConverter.toWsEntity(localizedMessage);
        return wsLocalizedMessage;
    }


}
