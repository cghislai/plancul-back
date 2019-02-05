package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.*;
import com.charlyghislain.plancul.domain.exception.NoBedPreparationException;
import com.charlyghislain.plancul.domain.exception.NoNursingException;
import com.charlyghislain.plancul.domain.exception.OperationNotAllowedException;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.BedFilter;
import com.charlyghislain.plancul.domain.request.filter.CropFilter;
import com.charlyghislain.plancul.domain.request.filter.CultureFilter;
import com.charlyghislain.plancul.domain.request.filter.DateFilter;
import com.charlyghislain.plancul.domain.request.sort.CultureSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.domain.util.CulturePhase;
import com.charlyghislain.plancul.domain.util.CulturePhaseType;
import com.charlyghislain.plancul.domain.util.DateRange;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    @Inject
    private CropService cropService;
    @Inject
    private BedService bedService;


    public Culture saveCulture(Culture culture) throws OperationNotAllowedException {
        if (culture.getId() == null) {
            return this.createCulture(culture);
        }

        validationService.validateLoggedUserHasTenantRole(culture.getTenant());
        computeCultureDates(culture);

        Culture managedCulture = entityManager.merge(culture);
        return managedCulture;
    }

    public void removeCropCultures(Crop crop) {
        CropFilter cropFilter = new CropFilter();
        cropFilter.setExactCrop(crop);
        CultureFilter cultureFilter = new CultureFilter();
        cultureFilter.setCropFilter(cropFilter);
        removeAllCultures(cultureFilter);
    }

    public void removeTenantCultures(Tenant tenant) {
        CultureFilter cultureFilter = new CultureFilter();
        cultureFilter.setTenant(tenant);
        removeAllCultures(cultureFilter);
    }


    public void removeBedCultures(Bed bed) {
        BedFilter bedFilter = new BedFilter();
        bedFilter.setExactBed(bed);
        CultureFilter cultureFilter = new CultureFilter();
        cultureFilter.setBedFilter(bedFilter);
        removeAllCultures(cultureFilter);
    }

    public void deleteCulture(Culture culture) throws OperationNotAllowedException {
        validationService.validateNonNullId(culture);
        validationService.validateLoggedUserHasTenantRole(culture.getTenant());

        removeCulture(culture);
    }

    public void prepareCultureValidation(Culture culture) {
        computeCultureDates(culture);
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

    public Long countCultures(CultureFilter cultureFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Culture> query = criteriaBuilder.createQuery(Culture.class);
        Root<Culture> rootCulture = query.from(Culture.class);

        List<Predicate> predicates = this.createCulturePredicates(cultureFilter, rootCulture, false);
        return searchService.count(rootCulture, predicates);
    }

    public SearchResult<Culture> findCultures(CultureFilter cultureFilter, Pagination pagination, List<Sort<Culture>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Culture> query = criteriaBuilder.createQuery(Culture.class);
        Root<Culture> rootCulture = query.from(Culture.class);

        List<Predicate> predicates = this.createCulturePredicates(cultureFilter, rootCulture, true);

        return searchService.search(pagination, sorts, language, query, rootCulture, predicates);
    }

    public List<CulturePhase> getCulturePhases(Culture culture) {
        List<CulturePhase> phases = new ArrayList<>();
        LocalDate sowingDate = culture.getSowingDate();
        int daysUntilGermination = culture.getDaysUntilGermination();
        LocalDate germinationDate = sowingDate.plusDays(daysUntilGermination);

        LocalDate growthOnBedStartDate = culture.getCultureNursing()
                .map(CultureNursing::getEndDate)
                .orElse(sowingDate);
        LocalDate firstHarvestDate = culture.getFirstHarvestDate();
        LocalDate lastHarvestDate = culture.getLastHarvestDate();

        culture.getBedPreparation()
                .map(preparation -> this.createPreparationPhase(culture, preparation))
                .ifPresent(phases::add);

        culture.getCultureNursing()
                .map(nursing -> this.createNursingPhase(culture, nursing))
                .ifPresent(phases::add);

        CulturePhase germinationPhase = new CulturePhase();
        germinationPhase.setCulture(culture);
        germinationPhase.setPhaseType(CulturePhaseType.GERMINATION);
        germinationPhase.setStartDate(sowingDate);
        germinationPhase.setEndDate(germinationDate);
        phases.add(germinationPhase);

        CulturePhase growthPhase = new CulturePhase();
        growthPhase.setCulture(culture);
        growthPhase.setPhaseType(CulturePhaseType.GROWTH);
        growthPhase.setStartDate(growthOnBedStartDate);
        growthPhase.setEndDate(firstHarvestDate);
        phases.add(growthPhase);

        CulturePhase harvestPhase = new CulturePhase();
        harvestPhase.setCulture(culture);
        harvestPhase.setPhaseType(CulturePhaseType.HARVEST);
        harvestPhase.setStartDate(firstHarvestDate);
        harvestPhase.setEndDate(lastHarvestDate);
        phases.add(harvestPhase);

        return phases;
    }

    public Culture updateBedPreparationDates(Culture culture, @Valid DateRange dateRange) throws NoBedPreparationException, OperationNotAllowedException {
        BedPreparation bedPreparation = culture.getBedPreparation()
                .orElseThrow(NoBedPreparationException::new);
        LocalDate start = dateRange.getStart();
        LocalDate end = dateRange.getEnd();

        int dayDuration = (int) start.until(end, ChronoUnit.DAYS);
        bedPreparation.setStartDate(start);
        bedPreparation.setEndDate(end);
        bedPreparation.setDayDuration(dayDuration);

        LocalDate sowingDate = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(end::minusDays)
                .orElse(end);
        culture.setSowingDate(sowingDate);

        int daysUntilGermination = culture.getDaysUntilGermination();
        LocalDate germinationDate = sowingDate.plusDays(daysUntilGermination);
        culture.setGerminationDate(germinationDate);

        culture.getCultureNursing()
                .ifPresent(nursing -> this.shiftNursingEndDate(nursing, culture, end));

        this.shiftHarvestDates(culture, sowingDate, end);

        return saveCulture(culture);
    }


    public Culture updateNursingDates(Culture culture, @Valid DateRange dateRange) throws NoNursingException, OperationNotAllowedException {
        CultureNursing nursing = culture.getCultureNursing()
                .orElseThrow(NoNursingException::new);
        LocalDate start = dateRange.getStart();
        LocalDate end = dateRange.getEnd();

        int dayDuration = (int) start.until(end, ChronoUnit.DAYS);

        nursing.setStartDate(start);
        nursing.setEndDate(end);
        nursing.setDayDuration(dayDuration);
        culture.setSowingDate(start);

        int daysUntilGermination = culture.getDaysUntilGermination();
        daysUntilGermination = Math.min(dayDuration - 1, daysUntilGermination);
        LocalDate germinationDate = start.plusDays(daysUntilGermination);
        culture.setGerminationDate(germinationDate);
        culture.setDaysUntilGermination(daysUntilGermination);

        culture.getBedPreparation()
                .ifPresent(prep -> this.shiftBedPreparationEndDate(prep, culture, end));

        shiftHarvestDates(culture, start, end);

        return saveCulture(culture);
    }

    public Culture updateGerminationDates(Culture culture, @Valid DateRange dateRange) throws OperationNotAllowedException {
        LocalDate start = dateRange.getStart();
        LocalDate end = dateRange.getEnd();
        int dayDuration = (int) start.until(end, ChronoUnit.DAYS);
        culture.setSowingDate(start);
        culture.setGerminationDate(end);
        culture.setDaysUntilGermination(dayDuration);

        LocalDate growthOnBedStartDate = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(start::plusDays)
                .orElse(start);

        culture.getBedPreparation()
                .ifPresent(prep -> this.shiftBedPreparationEndDate(prep, culture, growthOnBedStartDate));

        culture.getCultureNursing()
                .ifPresent(nursing -> this.shiftNursingEndDate(nursing, culture, growthOnBedStartDate));

        shiftHarvestDates(culture, start, end);

        return saveCulture(culture);
    }

    public Culture updateGrowthDates(Culture culture, @Valid DateRange dateRange) throws OperationNotAllowedException {
        LocalDate start = dateRange.getStart();
        LocalDate end = dateRange.getEnd();
        culture.getCultureNursing()
                .ifPresent(nursing -> shiftNursingEndDate(nursing, culture, start));

        culture.getBedPreparation()
                .ifPresent(prep -> shiftBedPreparationEndDate(prep, culture, start));

        LocalDate sowingDate = culture.getCultureNursing()
                .map(CultureNursing::getStartDate)
                .orElse(start);
        culture.setSowingDate(sowingDate);

        int daysUntilGermination = culture.getDaysUntilGermination();
        LocalDate germinationDate = sowingDate.plusDays(daysUntilGermination);
        culture.setGerminationDate(germinationDate);

        int daysUntilFirstHarvest = (int) sowingDate.until(end, ChronoUnit.DAYS);
        culture.setDaysUntilFirstHarvest(daysUntilFirstHarvest);

        shiftHarvestDates(culture, sowingDate, start);

        return saveCulture(culture);
    }

    public Culture updateHarvestDates(Culture culture, @Valid DateRange dateRange) throws OperationNotAllowedException {
        LocalDate start = dateRange.getStart();
        LocalDate end = dateRange.getEnd();
        int daysUntilFirstHarvest = culture.getDaysUntilFirstHarvest();
        LocalDate sowingDate = start.minusDays(daysUntilFirstHarvest);
        culture.setSowingDate(sowingDate);

        int daysUntilGermination = culture.getDaysUntilGermination();
        LocalDate germinationDate = sowingDate.plusDays(daysUntilGermination);
        culture.setGerminationDate(germinationDate);

        LocalDate growthOnBedStartDate = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(sowingDate::plusDays)
                .orElse(sowingDate);

        culture.getCultureNursing()
                .ifPresent(nursing -> this.shiftNursingEndDate(nursing, culture, growthOnBedStartDate));

        culture.getBedPreparation()
                .ifPresent(prep -> this.shiftBedPreparationEndDate(prep, culture, growthOnBedStartDate));

        int dayDuration = (int) start.until(end, ChronoUnit.DAYS);
        culture.setHarvestDaysDuration(dayDuration);
        culture.setFirstHarvestDate(start);
        culture.setLastHarvestDate(end);

        return saveCulture(culture);
    }

    private void shiftBedPreparationEndDate(BedPreparation bedPreparation, Culture culture, LocalDate growthOnBedStartDate) {
        int dayDuration = bedPreparation.getDayDuration();
        LocalDate startDate = growthOnBedStartDate.minusDays(dayDuration);
        bedPreparation.setStartDate(startDate);
        bedPreparation.setEndDate(growthOnBedStartDate);
    }

    private void shiftNursingEndDate(CultureNursing nursing, Culture culture, LocalDate growthOnBedStartDate) {
        int dayDuration = nursing.getDayDuration();
        LocalDate startDate = growthOnBedStartDate.minusDays(dayDuration);
        nursing.setStartDate(startDate);
        nursing.setEndDate(growthOnBedStartDate);

        culture.setSowingDate(startDate);
    }

    private void shiftHarvestDates(Culture culture, LocalDate sowingDate, LocalDate minDate) {
        int harvestDaysDuration = culture.getHarvestDaysDuration();
        int daysUntilFirstHarvest = culture.getDaysUntilFirstHarvest();
        int minDaysUntilFirstHarvest = (int) sowingDate.until(minDate, ChronoUnit.DAYS) + 1;
        daysUntilFirstHarvest = Math.max(daysUntilFirstHarvest, minDaysUntilFirstHarvest);
        LocalDate harvestStart = sowingDate.plusDays(daysUntilFirstHarvest);
        LocalDate harvestEnd = harvestStart.plusDays(harvestDaysDuration);

        culture.setFirstHarvestDate(harvestStart);
        culture.setLastHarvestDate(harvestEnd);
        culture.setDaysUntilFirstHarvest(daysUntilFirstHarvest);
    }


    private List<Sort<Culture>> getDefaultSorts() {
        return Collections.singletonList(new Sort<>(true, CultureSortField.BED_OCCUPANCY_START_DATE));
    }

    private Culture createCulture(Culture culture) throws OperationNotAllowedException {
        validationService.validateLoggedUserHasTenantRole(culture.getTenant());
        computeCultureDates(culture);

        Culture managedCulture = entityManager.merge(culture);
        return managedCulture;
    }

    private void computeCultureDates(Culture culture) {
        LocalDate sowingDate = culture.getSowingDate();
        if (sowingDate == null) {
            return;
        }

        int daysUntilGermination = culture.getDaysUntilGermination();
        LocalDate germinationDate = sowingDate.plusDays(daysUntilGermination);
        culture.setGerminationDate(germinationDate);

        Optional<LocalDate> transplantingDate = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(sowingDate::plusDays);
        transplantingDate.ifPresent(date -> this.setNursingDates(culture, sowingDate, date));
        LocalDate cultureOnBedStartDate = transplantingDate.orElse(sowingDate);

        Optional<LocalDate> preparationStartDate = culture.getBedPreparation()
                .map(BedPreparation::getDayDuration)
                .map(cultureOnBedStartDate::minusDays);
        preparationStartDate.ifPresent(date -> this.setBedPreparationDates(culture, date, cultureOnBedStartDate));


        int daysUntilFirstHarvest = culture.getDaysUntilFirstHarvest();
        LocalDate firstHarvestDate = sowingDate.plusDays(daysUntilFirstHarvest);
        int harvestDaysDuration = culture.getHarvestDaysDuration();
        LocalDate lastHarvestDate = firstHarvestDate.plusDays(harvestDaysDuration);

        culture.setFirstHarvestDate(firstHarvestDate);
        culture.setLastHarvestDate(lastHarvestDate);

        LocalDate bedOccupancyStartDate = preparationStartDate.orElse(cultureOnBedStartDate);
        culture.setBedOccupancyStartDate(bedOccupancyStartDate);
        culture.setBedOccupancyEndDate(lastHarvestDate);
    }


    private void setNursingDates(Culture culture, LocalDate sowingDate, LocalDate transplantingDate) {
        CultureNursing cultureNursing = culture.getCultureNursing().orElseThrow(IllegalStateException::new);

        cultureNursing.setStartDate(sowingDate);
        cultureNursing.setEndDate(transplantingDate);
    }

    private void setBedPreparationDates(Culture culture, LocalDate start, LocalDate cultureOnBedStartDate) {
        BedPreparation bedPreparation = culture.getBedPreparation().orElseThrow(IllegalStateException::new);

        bedPreparation.setStartDate(start);
        bedPreparation.setEndDate(cultureOnBedStartDate);
    }

    private List<Predicate> createCulturePredicates(CultureFilter cultureFilter, Root<Culture> cultureSource, boolean restrictUserAccess) {
        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Path<Tenant> tenantPath = cultureSource.get(Culture_.tenant);
        if (restrictUserAccess) {
            searchService.createLoggedUserTenantsPredicate(tenantPath)
                    .ifPresent(predicateList::add);
        }

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


    private CulturePhase createPreparationPhase(Culture culture, BedPreparation bedPreparation) {
        CulturePhase culturePhase = new CulturePhase();
        culturePhase.setCulture(culture);
        culturePhase.setPhaseType(bedPreparation.getType().getPhaseType());
        culturePhase.setStartDate(bedPreparation.getStartDate());
        culturePhase.setEndDate(bedPreparation.getEndDate());
        return culturePhase;
    }

    private CulturePhase createNursingPhase(Culture culture, CultureNursing nursing) {
        CulturePhase culturePhase = new CulturePhase();
        culturePhase.setCulture(culture);
        culturePhase.setPhaseType(CulturePhaseType.NURSING);
        culturePhase.setStartDate(nursing.getStartDate());
        culturePhase.setEndDate(nursing.getEndDate());
        return culturePhase;
    }


    private void removeAllCultures(CultureFilter cultureFilter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Culture> query = criteriaBuilder.createQuery(Culture.class);
        Root<Culture> rootCulture = query.from(Culture.class);

        List<Predicate> predicates = this.createCulturePredicates(cultureFilter, rootCulture, true);
        query.where(predicates.toArray(new Predicate[0]));

        List<Culture> cultures = searchService.getAllResults(query);
        cultures.forEach(this::removeCulture);
    }

    private void removeCulture(Culture culture) {
        Culture managedCulture = entityManager.merge(culture);

        managedCulture.getCultureNursing()
                .ifPresent(entityManager::remove);
        managedCulture.getBedPreparation()
                .ifPresent(entityManager::remove);

        entityManager.remove(managedCulture);
    }

}
