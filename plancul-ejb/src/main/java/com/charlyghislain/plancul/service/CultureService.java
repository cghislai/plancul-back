package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.Bed;
import com.charlyghislain.plancul.domain.BedPreparation;
import com.charlyghislain.plancul.domain.BedPreparationType;
import com.charlyghislain.plancul.domain.BedPreparation_;
import com.charlyghislain.plancul.domain.Crop;
import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;
import com.charlyghislain.plancul.domain.CultureNursing_;
import com.charlyghislain.plancul.domain.Culture_;
import com.charlyghislain.plancul.domain.Tenant;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.filter.DateFilter;
import com.charlyghislain.plancul.domain.request.sort.CultureSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Stateless
public class CultureService {


    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @Inject
    private ValidationService validationService;
    @Inject
    private SearchService searchService;
    @EJB
    private CropService cropService;
    @EJB
    private BedService bedService;


    public Culture saveCulture(Culture culture) {
        if (culture.getId() == null) {
            return this.createCulture(culture);
        }

        validationService.validateLoggedUserHasTenantRole(culture.getTenant());
        adjustCultureDates(culture);

        Culture managedCulture = entityManager.merge(culture);
        return managedCulture;
    }

    public void deleteCulture(Culture culture) {
        validationService.validateNonNullId(culture);
        validationService.validateLoggedUserHasTenantRole(culture.getTenant());

        Culture managedCulture = entityManager.merge(culture);
        entityManager.remove(managedCulture);
    }

    public void prepareCultureValidation(Culture culture) {
        adjustCultureDates(culture);
    }


    public Optional<Culture> findCultureById(long id) {
        Culture foundCulture = entityManager.find(Culture.class, id);
        return Optional.ofNullable(foundCulture)
                .filter(culture -> validationService.hasLoggedUserTenantRole(culture.getTenant()));
    }


    public SearchResult<Culture> findCultures(CultureFilter cultureFilter, Pagination pagination, Language language) {
        List<Sort<Culture>> defaultSorts = this.getDefaultSorts();
        return this.findCultures(cultureFilter, pagination, defaultSorts, language);
    }

    public SearchResult<Culture> findCultures(CultureFilter cultureFilter, Pagination pagination, List<Sort<Culture>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Culture> query = criteriaBuilder.createQuery(Culture.class);
        Root<Culture> rootCulture = query.from(Culture.class);

        List<Predicate> predicates = this.createCulturePredicates(cultureFilter, rootCulture);

        return searchService.search(pagination, sorts, language, query, rootCulture, predicates);
    }


    private List<Sort<Culture>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, CultureSortField.BED_OCCUPANCY_START_DATE));
    }

    private Culture createCulture(Culture culture) {
        validationService.validateLoggedUserHasTenantRole(culture.getTenant());
        adjustCultureDates(culture);

        Culture managedCulture = entityManager.merge(culture);
        return managedCulture;
    }

    private void adjustCultureDates(Culture culture) {
        LocalDate sowingDate = culture.getSowingDate();
        if (sowingDate == null) {
            return;
        }

        Optional<LocalDate> transplantingDate = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(sowingDate::plusDays);
        transplantingDate.ifPresent(date -> this.setNursingDates(culture, sowingDate, date));
        LocalDate cultureOnBedStartDate = transplantingDate.orElse(sowingDate);

        Optional<LocalDate> preSowingDate = culture.getBedPreparation()
                .map(BedPreparation::getDayDuration)
                .map(cultureOnBedStartDate::minusDays);
        preSowingDate.ifPresent(date -> this.setBedPreparationDates(culture, date, cultureOnBedStartDate));
        LocalDate bedOccupancyStartDate = preSowingDate.orElse(cultureOnBedStartDate);

        culture.setBedOccupancyStartDate(bedOccupancyStartDate);

        LocalDate occupancyEndTime = culture.getLastHarvestDate();
        culture.setBedOccupancyEndDate(occupancyEndTime);
    }


    private void setNursingDates(Culture culture, LocalDate sowingDate, LocalDate transplantingDate) {
        CultureNursing cultureNursing = culture.getCultureNursing().orElseThrow(IllegalStateException::new);

        cultureNursing.setStartdate(sowingDate);
        cultureNursing.setEndDate(transplantingDate);
    }

    private void setBedPreparationDates(Culture culture, LocalDate preSowingDate, LocalDate cultureOnBedStartDate) {
        BedPreparation bedPreparation = culture.getBedPreparation().orElseThrow(IllegalStateException::new);

        bedPreparation.setStartDate(preSowingDate);
        bedPreparation.setEndDate(cultureOnBedStartDate);
    }

    private List<Predicate> createCulturePredicates(CultureFilter cultureFilter, Root<Culture> cultureSource) {
        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Tenant> tenantPath = cultureSource.get(Culture_.tenant);
        searchService.createLoggedUserTenantsPredicate(tenantPath)
                .ifPresent(predicateList::add);

        cultureFilter.getTenant()
                .map(tenant -> criteriaBuilder.equal(tenantPath, tenant))
                .ifPresent(predicateList::add);

        Join<Culture, Crop> cropJoin = cultureSource.join(Culture_.crop);
        cultureFilter.getCropFilter()
                .map(cropFilter -> cropService.createCropPredicates(cropFilter, cropJoin))
                .ifPresent(predicateList::addAll);

        Join<Culture, Bed> bedJoin = cultureSource.join(Culture_.bed);
        cultureFilter.getBedFilter()
                .map(filter -> bedService.createBedPredicates(filter, bedJoin))
                .ifPresent(predicateList::addAll);

        cultureFilter.getSowingDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.sowingDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getGerminationDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.germinationDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getFirstHarvestDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.firstHarvestDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getLastHarvestDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.lastHarvestDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getTransplantationDate()
                .map(date -> this.createTransplantationDatePredicate(cultureSource, date))
                .ifPresent(predicateList::add);

        cultureFilter.getBedOccupancyStartDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.bedOccupancyStartDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getBedOccupancyEndDate()
                .map(date -> this.createCulturreDatePredicate(cultureSource, Culture_.bedOccupancyEndDate, date))
                .ifPresent(predicateList::add);

        cultureFilter.getNursing()
                .map(hasNursing -> this.createHasNursingPredicate(cultureSource, hasNursing))
                .ifPresent(predicateList::add);

        cultureFilter.getBedPreparation()
                .map(hsPreparation -> this.createHasBedPreparationPredicate(cultureSource, hsPreparation))
                .ifPresent(predicateList::add);

        cultureFilter.getBedPreparationType()
                .map(preparationType -> this.createBedPreparationTypePredicate(cultureSource, preparationType))
                .ifPresent(predicateList::add);

        cultureFilter.getNotesQuery()
                .map(notes -> this.createNotesQueryPredicate(cultureSource, notes))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createNotesQueryPredicate(Root<Culture> cultureSource, String query) {
        Path<String> notesPth = cultureSource.get(Culture_.htmlNotes);
        Predicate predicate = searchService.createTextMatchPredicate(notesPth, query);
        return predicate;
    }

    private Predicate createBedPreparationTypePredicate(Root<Culture> cultureSource, BedPreparationType preparationType) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<BedPreparation> culturePreparationPath = cultureSource.join(Culture_.bedPreparation);
        Path<BedPreparationType> bedPreparationTypePath = culturePreparationPath.get(BedPreparation_.type);
        Predicate predicate = criteriaBuilder.equal(bedPreparationTypePath, preparationType);
        return predicate;
    }

    private Predicate createHasNursingPredicate(Root<Culture> cultureSource, boolean hasNursing) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<CultureNursing> cultureNursingPath = cultureSource.get(Culture_.cultureNursing);
        Predicate hasNursingExpr = criteriaBuilder.isNotNull(cultureNursingPath);
        Predicate predicate = criteriaBuilder.equal(hasNursingExpr, hasNursing);
        return predicate;
    }


    private Predicate createHasBedPreparationPredicate(Root<Culture> cultureSource, boolean hasPreparation) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<BedPreparation> culturePreparationPath = cultureSource.get(Culture_.bedPreparation);
        Predicate hasPreparationExpr = criteriaBuilder.isNotNull(culturePreparationPath);
        Predicate predicate = criteriaBuilder.equal(hasPreparationExpr, hasPreparation);
        return predicate;
    }


    private Predicate createTransplantationDatePredicate(Root<Culture> cultureSource, DateFilter dateFilter) {
        Join<Culture, CultureNursing> cultureNursingJoin = cultureSource.join(Culture_.cultureNursing);
        Path<LocalDate> transplantationDatePath = cultureNursingJoin.get(CultureNursing_.endDate);
        return searchService.createDateFilterPredicate(transplantationDatePath, dateFilter);
    }

    private Predicate createCulturreDatePredicate(Root<Culture> cultureSource, SingularAttribute<Culture, LocalDate> datePath, DateFilter dateFilter) {
        Path<LocalDate> localDatePath = cultureSource.get(datePath);
        return searchService.createDateFilterPredicate(localDatePath, dateFilter);
    }


}
