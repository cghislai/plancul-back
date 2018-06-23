package com.charlyghislain.plancul.resource;

import com.charlyghislain.plancul.converter.CultureConverter;
import com.charlyghislain.plancul.converter.CulturePhaseConverter;
import com.charlyghislain.plancul.converter.SearchResultConverter;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.api.WsCulture;
import com.charlyghislain.plancul.domain.api.request.WsDateRange;
import com.charlyghislain.plancul.domain.api.request.filter.WsCultureFilter;
import com.charlyghislain.plancul.domain.api.response.WsCulturePhase;
import com.charlyghislain.plancul.domain.api.response.WsSearchResult;
import com.charlyghislain.plancul.domain.api.util.WsRef;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.util.CulturePhaseType;
import com.charlyghislain.plancul.service.CultureService;
import com.charlyghislain.plancul.util.AcceptedLanguage;
import com.charlyghislain.plancul.util.EntityValidator;
import com.charlyghislain.plancul.util.LanguageContainer;
import com.charlyghislain.plancul.util.UntypedSort;
import com.charlyghislain.plancul.util.exception.ReferenceNotFoundException;
import com.charlyghislain.plancul.util.exception.WsException;

import javax.ejb.EJB;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Path("/culture")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CultureResource {

    @EJB
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
    private LanguageContainer acceptedLanguage;

    @POST
    public WsRef<WsCulture> createCulture(@NotNull @Valid WsCulture wsCulture) {
        Culture culture = cultureConverter.fromWsEntity(wsCulture);
        Culture createdCulture = cultureService.saveCulture(culture);
        WsRef<WsCulture> reference = cultureConverter.reference(createdCulture);
        return reference;
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
        Language language = acceptedLanguage.getLanguage();

        SearchResult<Culture> searchResult = cultureService.findCultures(cultureFilter, pagination, sorts, language);
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
        Culture savedCulture = cultureService.saveCulture(culture);
        WsRef<WsCulture> reference = cultureConverter.reference(savedCulture);
        return reference;
    }

    @DELETE
    @Path("/{id}")
    public void deleteCulture(@PathParam("id") long id) {
        Culture culture = cultureService.findCultureById(id)
                .orElseThrow(ReferenceNotFoundException::new);

        cultureService.deleteCulture(culture);
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
    }


}
