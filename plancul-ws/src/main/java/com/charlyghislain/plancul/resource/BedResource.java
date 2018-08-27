package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsBed;
import com.charlyghislain.plancul.api.domain.request.filter.WsBedFilter;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.BedConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.BedService;
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

@Path("/bed")
@RolesAllowed({ApplicationGroupNames.TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class BedResource {

    @Inject
    private BedService bedService;
    @Inject
    private BedConverter bedConverter;
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
    public WsRef<WsBed> createBed(@NotNull @Valid WsBed wsBed) {
        Bed bed = bedConverter.fromWsEntity(wsBed);
        return saveBed(bed);
    }

    @GET
    @Path("/{id}")
    public WsBed getBed(@PathParam("id") long id) {
        Bed bed = bedService.findBedById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsBed wsBed = bedConverter.toWsEntity(bed);
        return wsBed;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsBed> updateBed(@PathParam("id") long id, @NotNull @Valid WsBed wsBed) {
        Bed bed = bedConverter.fromWsEntity(wsBed);
        return saveBed(bed);
    }

    @DELETE
    @Path("/{id}")
    public void deleteBed(@PathParam("id") long id) {
        Bed bed = bedService.findBedById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        try {
            bedService.deleteBed(bed);
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }

    @POST
    @Path("/search")
    public WsSearchResult<WsBed> searchBeds(@NotNull @Valid WsBedFilter wsBedFilter) {
        BedFilter bedFilter = bedConverter.fromWsBedFilter(wsBedFilter);
        List<Sort<Bed>> sorts = bedConverter.fromUntypedSorts(sortList);

        SearchResult<Bed> searchResult = bedService.findBeds(bedFilter, pagination, sorts, acceptedLanguage);
        WsSearchResult<WsBed> wsSearchResult = searchResultConverter.convertSearchResults(searchResult, bedConverter);
        return wsSearchResult;
    }


    @POST
    @Path("/patch/search")
    public List<String> searchBedPatches(@NotNull @Valid WsBedFilter wsBedFilter) {
        BedFilter bedFilter = bedConverter.fromWsBedFilter(wsBedFilter);

        List<String> bedPatches = bedService.findBedPatches(bedFilter);
        return bedPatches;
    }


    private WsRef<WsBed> saveBed(Bed bed) {
        try {
            Bed createdBed = bedService.saveBed(bed);
            WsRef<WsBed> reference = bedConverter.reference(createdBed);
            return reference;
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }
}
