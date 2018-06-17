package com.charlyghislain.plancul.resource;


import com.charlyghislain.plancul.converter.PlantProductTupleConverter;
import com.charlyghislain.plancul.domain.WsPlantProductResult;
import com.charlyghislain.plancul.domain.agrovoc.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.filter.WsPlantProductTupleFilter;
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

    @POST
    @Path("/search")
    public List<WsPlantProductResult> searchPlantProductTuples(@NotNull @Valid WsPlantProductTupleFilter wsPlantProductTupleFilter) {
        PlantProductTupleFilter tupleFilter = plantProductTupleConverter.fromWsPlantProductTupleQueryFilter(wsPlantProductTupleFilter);
        List<PlantProductTupleResult> tupleResultList = agrovocService.searchPlantProducts(tupleFilter);
        return tupleResultList.stream()
                .map(plantProductTupleConverter::toWsPlantProductResult)
                .collect(Collectors.toList());
    }
}
