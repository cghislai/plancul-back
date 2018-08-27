package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsPlot;
import com.charlyghislain.plancul.api.domain.request.filter.WsPlotFilter;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.PlotConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.PlotFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.PlotService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/plot")
@RolesAllowed({ApplicationGroupNames.TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PlotResource {

    @Inject
    private PlotService plotService;
    @Inject
    private PlotConverter plotConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;
    @Inject
    private List<UntypedSort> sortList;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @POST
    public WsRef<WsPlot> createPlot(@NotNull @Valid WsPlot wsPlot) {
        Plot plot = plotConverter.fromWsEntity(wsPlot);
        return savePlot(plot);
    }

    @GET
    @Path("/{id}")
    public WsPlot getPlot(@PathParam("id") long id) {
        Plot plot = plotService.findPlotById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsPlot wsPlot = plotConverter.toWsEntity(plot);
        return wsPlot;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsPlot> updatePlot(@PathParam("id") long id, @NotNull @Valid WsPlot wsPlot) {
        Plot plot = plotConverter.fromWsEntity(wsPlot);
        return savePlot(plot);

    }

    @DELETE
    @Path("/{id}")
    public void deletePlot(@PathParam("id") long id) {
        Plot plot = plotService.findPlotById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        try {
            plotService.deletePlot(plot);
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }

    @POST
    @Path("/search")
    public WsSearchResult<WsPlot> searchPlots(@NotNull @Valid WsPlotFilter wsPlotFilter) {
        PlotFilter plotFilter = plotConverter.fromWsPlotFilter(wsPlotFilter);
        List<Sort<Plot>> sorts = plotConverter.fromUntypedSorts(sortList);

        SearchResult<Plot> searchResult = plotService.findPlots(plotFilter, pagination, sorts, acceptedLanguage);
        WsSearchResult<WsPlot> wsSearchResult = searchResultConverter.convertSearchResults(searchResult, plotConverter);
        return wsSearchResult;
    }


    private WsRef<WsPlot> savePlot(Plot plot) {
        try {
            Plot createdPlot = plotService.savePlot(plot);
            WsRef<WsPlot> reference = plotConverter.reference(createdPlot);
            return reference;
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }
}
