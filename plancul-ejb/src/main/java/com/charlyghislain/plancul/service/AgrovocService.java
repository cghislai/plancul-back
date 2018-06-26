package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.AgrovocPlant;
import com.charlyghislain.plancul.domain.AgrovocPlant_;
import com.charlyghislain.plancul.domain.AgrovocProduct;
import com.charlyghislain.plancul.domain.AgrovocProduct_;
import com.charlyghislain.plancul.domain.LocalizedMessage;
import com.charlyghislain.plancul.domain.i18n.Language;
import com.charlyghislain.plancul.domain.request.Pagination;
import com.charlyghislain.plancul.domain.request.filter.AgrovocPlantFilter;
import com.charlyghislain.plancul.domain.request.filter.AgrovocProductFilter;
import com.charlyghislain.plancul.domain.request.filter.PlantProductTupleFilter;
import com.charlyghislain.plancul.domain.request.sort.AgrovocPlantSortField;
import com.charlyghislain.plancul.domain.request.sort.AgrovocProductSortField;
import com.charlyghislain.plancul.domain.request.sort.Sort;
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocNodeDataClient;
import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocPlantDataSearchClient;
import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocPlantProductTupleSearchClient;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocNodeData;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductTuple;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class AgrovocService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    @EJB
    private I18NService i18NService;
    @Inject
    private SearchService searchService;
    @Inject
    private AgrovocPlantProductTupleSearchClient plantProductTupleSearchClient;
    @Inject
    private AgrovocPlantDataSearchClient agrovocPlantDataSearchClient;
    @Inject
    private AgrovocNodeDataClient agrovocNodeDataClient;


    public Optional<AgrovocPlant> findAgrovocPlantById(long id) {
        AgrovocPlant found = entityManager.find(AgrovocPlant.class, id);
        return Optional.ofNullable(found);
    }

    public Optional<AgrovocProduct> findAgrovocProductById(long id) {
        AgrovocProduct found = entityManager.find(AgrovocProduct.class, id);
        return Optional.ofNullable(found);
    }

    // TODO: pass through a queue to avoid request spamming
    public List<PlantProductTupleResult> searchPlantProductTuples(PlantProductTupleFilter filter, Pagination pagination) {
        Language language = filter.getLanguage();
        String queryString = filter.getQueryString();
        int offset = pagination.getOffset();
        int size = pagination.getSize();

        List<AgrovocPlantProductTuple> plantProductTuples = plantProductTupleSearchClient.findPlantProductTuples(
                queryString, language.getCode(), offset, size);

        return plantProductTuples.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }


    public AgrovocPlantData searchAgrovocPlantData(String agrovocNodeId, Language language) {
        AgrovocPlantData plantData = agrovocPlantDataSearchClient.findPlantData(agrovocNodeId, language.getCode());
        return plantData;
    }


    public AgrovocPlant createAgrovocPlant(String agrovocUri) {
        List<AgrovocNodeData> localizedNodeData = Arrays.stream(Language.values())
                .map(lang -> this.agrovocNodeDataClient.fetchNodeData(agrovocUri, lang.getCode()))
                .collect(Collectors.toList());

        AgrovocPlant agrovocPlant = new AgrovocPlant();
        agrovocPlant.setAgrovocNodeId(agrovocUri);
        localizedNodeData.stream()
                .forEach(data -> this.appendLocalizedData(agrovocPlant, data));

        AgrovocPlant managedPlant = entityManager.merge(agrovocPlant);
        return managedPlant;
    }


    public AgrovocProduct createAgrovocProduct(String agrovocUri) {
        List<AgrovocNodeData> localizedNodeData = Arrays.stream(Language.values())
                .map(lang -> this.agrovocNodeDataClient.fetchNodeData(agrovocUri, lang.getCode()))
                .collect(Collectors.toList());

        AgrovocProduct agrovocProduct = new AgrovocProduct();
        agrovocProduct.setAgrovocNodeId(agrovocUri);
        localizedNodeData.stream()
                .forEach(data -> this.appendLocalizedData(agrovocProduct, data));


        AgrovocProduct managedProduct = entityManager.merge(agrovocProduct);
        return managedProduct;
    }

    public SearchResult<AgrovocPlant> findAgrovocPlants(AgrovocPlantFilter agrovocPlantFilter, Pagination pagination, Language language) {
        List<Sort<AgrovocPlant>> defaultSorts = this.getDefaultPlantSorts();
        return this.findAgrovocPlants(agrovocPlantFilter, pagination, defaultSorts, language);
    }

    public SearchResult<AgrovocPlant> findAgrovocPlants(AgrovocPlantFilter agrovocPlantFilter, Pagination pagination, List<Sort<AgrovocPlant>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgrovocPlant> query = criteriaBuilder.createQuery(AgrovocPlant.class);
        Root<AgrovocPlant> rootAgrovocPlant = query.from(AgrovocPlant.class);

        List<Predicate> predicates = this.createPlantPredicates(agrovocPlantFilter, rootAgrovocPlant);

        return searchService.search(pagination, sorts, language, query, rootAgrovocPlant, predicates);
    }


    public SearchResult<AgrovocProduct> findAgrovocProducts(AgrovocProductFilter agrovocProductFilter, Pagination pagination, Language language) {
        List<Sort<AgrovocProduct>> defaultSorts = this.getDefaultProductSorts();
        return this.findAgrovocProducts(agrovocProductFilter, pagination, defaultSorts, language);
    }

    public SearchResult<AgrovocProduct> findAgrovocProducts(AgrovocProductFilter agrovocProductFilter, Pagination pagination, List<Sort<AgrovocProduct>> sorts, Language language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgrovocProduct> query = criteriaBuilder.createQuery(AgrovocProduct.class);
        Root<AgrovocProduct> rootAgrovocProduct = query.from(AgrovocProduct.class);

        List<Predicate> predicates = this.createProductPredicates(agrovocProductFilter, rootAgrovocProduct);

        return searchService.search(pagination, sorts, language, query, rootAgrovocProduct, predicates);
    }

    private List<Sort<AgrovocPlant>> getDefaultPlantSorts() {
        return Collections.singletonList(new Sort<>(true, AgrovocPlantSortField.LABEL));
    }

    private List<Sort<AgrovocProduct>> getDefaultProductSorts() {
        return Collections.singletonList(new Sort<>(true, AgrovocProductSortField.LABEL));
    }


    private List<Predicate> createPlantPredicates(AgrovocPlantFilter filter, Root<AgrovocPlant> rootPlant) {
        Optional<Language> namesQueryLanguage = filter.getNamesQueryLanguage();
        List<Predicate> predicateList = new ArrayList<>();

        filter.getNamesQuery()
                .map(query -> this.createPlantNameQueryPredicate(query, namesQueryLanguage, rootPlant))
                .ifPresent(predicateList::add);

        filter.getUri()
                .map(uri -> this.createPlantUriPredicate(uri, rootPlant))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createPlantUriPredicate(String uri, Root<AgrovocPlant> rootPlant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<String> nodeUriPath = rootPlant.get(AgrovocPlant_.agrovocNodeId);

        return criteriaBuilder.equal(nodeUriPath, uri);
    }

    public Predicate createPlantNameQueryPredicate(String query, Optional<Language> language, From<?, AgrovocPlant> rootPlant) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        ListJoin<AgrovocPlant, LocalizedMessage> preferredLabelsJoin = rootPlant.join(AgrovocPlant_.preferedLabel, JoinType.LEFT);
        ListJoin<AgrovocPlant, LocalizedMessage> alternativeLabelsJoin = rootPlant.join(AgrovocPlant_.alternativeLabels, JoinType.LEFT);

        Predicate preferedLabelPredicate = searchService.createLocalizedTextMatchPredicate(preferredLabelsJoin, query, language);
        Predicate alternativeLabelPredicate = searchService.createLocalizedTextMatchPredicate(alternativeLabelsJoin, query, language);

        return criteriaBuilder.or(preferedLabelPredicate, alternativeLabelPredicate);
    }


    public Predicate createProductNameQueryPredicate(String query, Optional<Language> language, From<?, AgrovocProduct> rootProduct) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        ListJoin<AgrovocProduct, LocalizedMessage> preferredLabelsJoin = rootProduct.join(AgrovocProduct_.preferedLabel, JoinType.LEFT);
        ListJoin<AgrovocProduct, LocalizedMessage> alternativeLabelsJoin = rootProduct.join(AgrovocProduct_.alternativeLabels, JoinType.LEFT);

        Predicate preferedLabelPredicate = searchService.createLocalizedTextMatchPredicate(preferredLabelsJoin, query, language);
        Predicate alternativeLabelPredicate = searchService.createLocalizedTextMatchPredicate(alternativeLabelsJoin, query, language);

        return criteriaBuilder.or(preferedLabelPredicate, alternativeLabelPredicate);
    }



    private List<Predicate> createProductPredicates(AgrovocProductFilter filter, Root<AgrovocProduct> rootProduct) {
        Optional<Language> namesQueryLanguage = filter.getNamesQueryLanguage();
        List<Predicate> predicateList = new ArrayList<>();

        filter.getNamesQuery()
                .map(query -> this.createProductNameQueryPredicate(query, namesQueryLanguage, rootProduct))
                .ifPresent(predicateList::add);

        filter.getUri()
                .map(uri -> this.createProductUriPredicate(uri, rootProduct))
                .ifPresent(predicateList::add);

        return predicateList;
    }

    private Predicate createProductUriPredicate(String uri, Root<AgrovocProduct> rootProduct) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Path<String> nodeUriPath = rootProduct.get(AgrovocProduct_.agrovocNodeId);

        return criteriaBuilder.equal(nodeUriPath, uri);
    }
    private PlantProductTupleResult mapToDomain(AgrovocPlantProductTuple agrovocPlantProductTuple) {
        String matchedTerm = agrovocPlantProductTuple.getMatchedTerm();
        String plantPreferredLabel = agrovocPlantProductTuple.getPlantPreferredLabel();
        String plantURI = agrovocPlantProductTuple.getPlantURI();
        String productURI = agrovocPlantProductTuple.getProductURI();
        String languageCode = agrovocPlantProductTuple.getLanguage();
        String productPreferredLabel = agrovocPlantProductTuple.getProductPreferredLabel();

        Language language = Language.fromCode(languageCode)
                .orElseThrow(IllegalStateException::new);

        PlantProductTupleResult domainTuple = new PlantProductTupleResult();
        domainTuple.setLanguage(language);
        domainTuple.setMatchedTerm(matchedTerm);
        domainTuple.setPlantPreferredLabel(plantPreferredLabel);
        domainTuple.setProductURI(productURI);
        domainTuple.setPlantURI(plantURI);
        domainTuple.setProductPreferredLabel(productPreferredLabel);
        return domainTuple;
    }


    private void appendLocalizedData(AgrovocPlant agrovocPlant, AgrovocNodeData data) {
        List<LocalizedMessage> preferedLabel = agrovocPlant.getPreferedLabel();
        List<LocalizedMessage> alternativeLabels = agrovocPlant.getAlternativeLabels();
        appendLocalizedData(data, preferedLabel, alternativeLabels);
    }


    private void appendLocalizedData(AgrovocProduct agrovocProduct, AgrovocNodeData data) {
        List<LocalizedMessage> preferedLabel = agrovocProduct.getPreferedLabel();
        List<LocalizedMessage> alternativeLabels = agrovocProduct.getAlternativeLabels();
        appendLocalizedData(data, preferedLabel, alternativeLabels);
    }

    private void appendLocalizedData(AgrovocNodeData data, List<LocalizedMessage> preferedLabels, List<LocalizedMessage> alternativeLabels) {
        String languageCode = data.getLanguage();
        Language language = Language.fromCode(languageCode).orElseThrow(IllegalStateException::new);
        String dataPreferedLabel = data.getPreferedLabel();
        Set<String> dataAlternativeLabels = data.getAlternativeLabels();

        LocalizedMessage preferedLbelMessage = i18NService.createLocalizedMessage(language, dataPreferedLabel);
        preferedLabels.add(preferedLbelMessage);

        dataAlternativeLabels.stream()
                .map(label -> i18NService.createLocalizedMessage(language, label))
                .forEach(alternativeLabels::add);
    }

}
