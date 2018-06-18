package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.CropConverter;
import com.charlyghislain.plancul.converter.CropCreationRequestConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.WsCrop;
import com.charlyghislain.plancul.domain.request.CropCreationRequest;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.WsCropCreationRequest;
import com.charlyghislain.plancul.domain.util.WsRef;
import com.charlyghislain.plancul.service.CropService;
import com.charlyghislain.plancul.util.ReferenceNotFoundException;

import javax.ejb.EJB;
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

@Path("/crop")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CropResource {

    @EJB
    private CropService cropService;
    @Inject
    private CropCreationRequestConverter cropCreationRequestConverter;
    @Inject
    private CropConverter cropConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private Pagination pagination;

    @POST
    public WsRef<WsCrop> createCrop(@NotNull @Valid WsCropCreationRequest wsCropCreationRequest) {
        CropCreationRequest cropCreationRequest = cropCreationRequestConverter.fromWsCropCreationRequest(wsCropCreationRequest);
        Crop createdCrop = cropService.createPlot(cropCreationRequest);
        WsRef<WsCrop> createdCropRef = cropConverter.reference(createdCrop);
        return createdCropRef;
    }


    @GET
    @Path("/{id}")
    public WsCrop getCrop(@PathParam("id") long id) {
        Crop crop = cropService.findCropById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsCrop wsCrop = cropConverter.toWsEntity(crop);
        return wsCrop;
    }


}