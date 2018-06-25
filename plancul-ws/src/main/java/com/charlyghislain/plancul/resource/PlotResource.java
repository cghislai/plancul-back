package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.PlotConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Plot;
import com.charlyghislain.plancul.domain.api.WsPlot;
import com.charlyghislain.plancul.domain.api.request.filter.WsPlotFilter;
import com.charlyghislain.plancul.domain.api.response.WsSearchResult;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.PlotFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.service.PlotService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;

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
import java.util.List;

@Path("/plot")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PlotResource {

    @EJB
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
        Plot createdPlot = plotService.savePlot(plot);
        WsRef<WsPlot> reference = plotConverter.reference(createdPlot);
        return reference;
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
        Plot savedPlot = plotService.savePlot(plot);
        WsRef<WsPlot> reference = plotConverter.reference(savedPlot);
        return reference;
    }

    @DELETE
    @Path("/{id}")
    public void deletePlot(@PathParam("id") long id) {
        Plot plot = plotService.findPlotById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        plotService.deletePlot(plot);
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

}
