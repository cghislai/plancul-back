package com.charlyghislain.plancul.resource;


import com.charlyghislain.plancul.converter.PlantProductTupleConverter;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.api.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.api.result.WsPlantProductResult;
import com.charlyghislain.plancul.service.AgrovocService;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/plantProductTuple")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlantProductTupleResource {

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private PlantProductTupleConverter plantProductTupleConverter;
    @Inject
    private Pagination pagination;

    @POST
    @Path("/search")
    public List<WsPlantProductResult> searchPlantProductTuples(@NotNull @Valid WsPlantProductTupleFilter wsPlantProductTupleFilter) {
        PlantProductTupleFilter tupleFilter = plantProductTupleConverter.fromWsPlantProductTupleQueryFilter(wsPlantProductTupleFilter);
        // TODO: pass pagination + fetch total count
        List<PlantProductTupleResult> tupleResultList = agrovocService.searchPlantProductTuples(tupleFilter);
        return tupleResultList.stream()
                .map(plantProductTupleConverter::toWsPlantProductResult)
                .collect(Collectors.toList());
    }
}
