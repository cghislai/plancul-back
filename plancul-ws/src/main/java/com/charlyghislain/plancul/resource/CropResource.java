package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsCrop;
import com.charlyghislain.plancul.api.domain.request.WsCropCreationRequest;
import com.charlyghislain.plancul.api.domain.request.filter.WsCropFilter;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.CropConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.converter.request.CropCreationRequestConverter;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.service.CropService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/crop")
@RolesAllowed({ApplicationGroupNames.TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CropResource {

    @Inject
    private CropService cropService;
    @Inject
    private CropCreationRequestConverter cropCreationRequestConverter;
    @Inject
    private CropConverter cropConverter;
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
    public WsRef<WsCrop> createCrop(@NotNull @Valid WsCropCreationRequest wsCropCreationRequest) {
        CropCreationRequest cropCreationRequest = cropCreationRequestConverter.fromWsCropCreationRequest(wsCropCreationRequest);
        try {
            Crop createdCrop = cropService.createCrop(cropCreationRequest);
            WsRef<WsCrop> createdCropRef = cropConverter.reference(createdCrop);
            return createdCropRef;
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }


    @GET
    @Path("/{id}")
    public WsCrop getCrop(@PathParam("id") long id) {
        Crop crop = cropService.findCropById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsCrop wsCrop = cropConverter.toWsEntity(crop);
        return wsCrop;
    }


    @POST
    @Path("/search")
    public WsSearchResult<WsCrop> searchCrops(@NotNull @Valid WsCropFilter wsCropFilter) {
        CropFilter cropFilter = cropConverter.fromWsCropFilter(wsCropFilter);
        List<Sort<Crop>> sorts = cropConverter.fromUntypedSorts(sortList);

        SearchResult<Crop> searchResult = cropService.findCrops(cropFilter, pagination, sorts, acceptedLanguage);
        WsSearchResult<WsCrop> wsSearchResult = searchResultConverter.convertSearchResults(searchResult, cropConverter);
        return wsSearchResult;
    }

}
