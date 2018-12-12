package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.api.domain.WsCulture;
import com.charlyghislain.plancul.api.domain.request.WsDateRange;
import com.charlyghislain.plancul.api.domain.request.filter.WsCultureFilter;
import com.charlyghislain.plancul.api.domain.response.WsCulturePhase;
import com.charlyghislain.plancul.api.domain.response.WsSearchResult;
import com.charlyghislain.plancul.api.domain.util.WsRef;
import com.charlyghislain.plancul.converter.CultureConverter;
import com.charlyghislain.plancul.converter.CulturePhaseConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.exception.NoBedPreparationException;
import com.charlyghislain.plancul.domain.exception.NoNursingException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.security.ApplicationGroupNames;
import com.charlyghislain.plancul.domain.util.CulturePhaseType;
import com.charlyghislain.plancul.service.CultureService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.EntityValidator;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Path("/culture")
@RolesAllowed({ApplicationGroupNames.TENANT_USER})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class CultureResource {

    @Inject
    private CultureService cultureService;
    @Inject
    private CultureConverter cultureConverter;
    @Inject
    private CulturePhaseConverter culturePhaseConverter;
    @Inject
    private SearchResultConverter searchResultConverter;
    @Inject
    private EntityValidator entityValidator;
    @Inject
    private Pagination pagination;
    @Inject
    private List<UntypedSort> sortList;
    @Inject
    @AcceptedLanguage
    private Language acceptedLanguage;

    @POST
    public WsRef<WsCulture> createCulture(@NotNull @Valid WsCulture wsCulture) {
        Culture culture = cultureConverter.fromWsEntity(wsCulture);

        return saveCulture(culture);
    }

    @PUT
    @Path("/validate")
    public WsCulture validateCulture(@NotNull WsCulture wsCulture) {
        Culture culture = cultureConverter.fromWsEntity(wsCulture);
        cultureService.prepareCultureValidation(culture);
        WsCulture adaptedCulture = cultureConverter.toWsEntity(culture);

        entityValidator.validate(adaptedCulture, culture);
        return adaptedCulture;
    }


    @POST
    @Path("/search")
    public WsSearchResult<WsCulture> searchCultures(@NotNull @Valid WsCultureFilter wsCultureFilter) {
        CultureFilter cultureFilter = cultureConverter.fromWsCultureFilter(wsCultureFilter);
        List<Sort<Culture>> sorts = cultureConverter.fromUntypedSorts(sortList);

        SearchResult<Culture> searchResult = cultureService.findCultures(cultureFilter, pagination, sorts, acceptedLanguage);
        WsSearchResult<WsCulture> wsSearchResult = searchResultConverter.convertSearchResults(searchResult, cultureConverter);
        return wsSearchResult;
    }


    @GET
    @Path("/{id}")
    public WsCulture getCulture(@PathParam("id") long id) {
        Culture culture = cultureService.findCultureById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        WsCulture wsCulture = cultureConverter.toWsEntity(culture);
        return wsCulture;
    }

    @PUT
    @Path("/{id}")
    public WsRef<WsCulture> updateCulture(@PathParam("id") long id, @NotNull @Valid WsCulture wsCulture) {
        Culture culture = cultureConverter.fromWsEntity(wsCulture);
        return saveCulture(culture);
    }

    @DELETE
    @Path("/{id}")
    public void deleteCulture(@PathParam("id") long id) {
        Culture culture = cultureService.findCultureById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        try {
            cultureService.deleteCulture(culture);
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }


    @GET
    @Path("/{id}/phases")
    public List<WsCulturePhase> getPhases(@PathParam("id") long id) {
        Culture culture = cultureService.findCultureById(id)
                .orElseThrow(ReferenceNotFoundException::new);
        List<WsCulturePhase> wsCulturePhaseList = cultureService.getCulturePhases(culture)
                .stream()
                .map(culturePhaseConverter::toWsCulturePhase)
                .collect(Collectors.toList());
        return wsCulturePhaseList;
    }

    @PUT
    @Path("/{id}/{phase}")
    public WsRef<WsCulture> updatePhase(@PathParam("id") long id, @PathParam("phase") String culturePhaseName,
                                        @NotNull @Valid WsDateRange wsDateRange) {
        Culture culture = cultureService.findCultureById(id)
                .orElseThrow(ReferenceNotFoundException::new);
        CulturePhaseType phaseType = culturePhaseConverter.fromWsCulturePhaseTypeName(culturePhaseName);
        LocalDate startDate = wsDateRange.getStart();
        LocalDate endDate = wsDateRange.getEnd();

        Culture managedCulture;
        try {
            switch (phaseType) {
                case PREPARATION_COVER:
                case PREPARATION_PRESOWING:
                    managedCulture = cultureService.updateBedPreparationDates(culture, startDate, endDate);
                    break;
                case NURSING:
                    managedCulture = cultureService.updateNursingDates(culture, startDate, endDate);
                    break;
                case GERMINATION:
                    managedCulture = cultureService.updateGerminationDates(culture, startDate, endDate);
                    break;
                case GROWTH:
                    managedCulture = cultureService.updateGrowthDates(culture, startDate, endDate);
                    break;
                case HARVEST:
                    managedCulture = cultureService.updateHarvestDates(culture, startDate, endDate);
                    break;
                default:
                    throw new WsException(Response.Status.NOT_IMPLEMENTED);
            }
            WsRef<WsCulture> cultureWsRef = cultureConverter.reference(managedCulture);
            return cultureWsRef;
        } catch (NoBedPreparationException e) {
            throw new WsException(Response.Status.BAD_REQUEST, "No bed prepartaion for this culture");
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        } catch (NoNursingException e) {
            throw new WsException(Response.Status.BAD_REQUEST, "No nursing for this culture");
        }

    }

    private WsRef<WsCulture> saveCulture(Culture culture) {
        try {
            Culture createdCulture = cultureService.saveCulture(culture);
            WsRef<WsCulture> reference = cultureConverter.reference(createdCulture);
            return reference;
        } catch (OperationNotAllowedException e) {
            throw new WsException(Response.Status.FORBIDDEN);
        }
    }


}
