package com.charlyghislain.plancul.resource;


import com.charlyghislain.plancul.converter.AgrovocPlantDataConverter;
import com.charlyghislain.plancul.converter.AgrovocPlantProductTupleConverter;
import com.charlyghislain.plancul.domain.api.request.filter.WsPlantProductTupleFilter;
import com.charlyghislain.plancul.domain.api.response.WsAgrovocPlantData;
import com.charlyghislain.plancul.domain.api.response.WsAgrovocPlantProduct;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;
import com.charlyghislain.plancul.service.AgrovocService;
import com.charlyghislain.plancul.util.AcceptedLanguage;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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

@Path("/agrovoc")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AgrovocResource {

    @EJB
    private AgrovocService agrovocService;
    @Inject
    private AgrovocPlantProductTupleConverter plantProductTupleConverter;
    @Inject
    private AgrovocPlantDataConverter agrovocPlantDataConverter;
    @Inject
    private Pagination pagination;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @POST
    @Path("/plantProductTuple/search")
    public List<WsAgrovocPlantProduct> searchPlantProductTuples(@NotNull @Valid WsPlantProductTupleFilter wsPlantProductTupleFilter) {
        PlantProductTupleFilter tupleFilter = plantProductTupleConverter.fromWsPlantProductTupleQueryFilter(wsPlantProductTupleFilter);
        // TODO: fetch total count
        List<PlantProductTupleResult> tupleResultList = agrovocService.searchPlantProductTuples(tupleFilter, pagination);
        return tupleResultList.stream()
                .map(plantProductTupleConverter::toWsPlantProductResult)
                .collect(Collectors.toList());
    }


    @POST
    @Path("/plantData/search")
    @Consumes(MediaType.TEXT_PLAIN)
    public WsAgrovocPlantData searchPlantData(@NotNull String agrovocPlantUri) {
        AgrovocPlantData plantData = agrovocService.searchAgrovocPlantData(agrovocPlantUri, acceptedLanguage);
        WsAgrovocPlantData wsAgrovocPlantData = agrovocPlantDataConverter.toWsAgrovocPlantData(plantData);
        return wsAgrovocPlantData;
    }
}
