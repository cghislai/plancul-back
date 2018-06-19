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
import com.charlyghislain.plancul.domain.result.PlantProductTupleResult;
import com.charlyghislain.plancul.domain.result.SearchResult;
import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocNodeDataClient;
import com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocPlantProductTupleSearchClient;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocNodeData;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductResultTuple;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantResult;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocProductResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
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
    private AgrovocNodeDataClient agrovocNodeDataClient;


    public Optional<AgrovocPlant> findAgrovocPlantById(long id) {
        AgrovocPlant found = entityManager.find(AgrovocPlant.class, id);
        return Optional.ofNullable(found);
    }

    public Optional<AgrovocProduct> findAgrovocProductById(long id) {
        AgrovocProduct found = entityManager.find(AgrovocProduct.class, id);
        return Optional.ofNullable(found);
    }

    public List<PlantProductTupleResult> searchPlantProducts(PlantProductTupleFilter filter) {
        Language language = filter.getLanguage();
        String queryString = filter.getQueryString();

        List<AgrovocPlantProductResultTuple> plantProductTuples = plantProductTupleSearchClient.findPlantProductTuples(queryString, language.getCode());

        return plantProductTuples.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
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


    public SearchResult<AgrovocPlant> findAgrovocPlants(AgrovocPlantFilter filter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgrovocPlant> query = criteriaBuilder.createQuery(AgrovocPlant.class);
        Root<AgrovocPlant> rootPlant = query.from(AgrovocPlant.class);

        List<Predicate> predicates = this.createPlantPredicates(filter, rootPlant);

        return searchService.search(pagination, query, rootPlant, predicates);
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


    public SearchResult<AgrovocProduct> findAgrovocProducts(AgrovocProductFilter filter, Pagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgrovocProduct> query = criteriaBuilder.createQuery(AgrovocProduct.class);
        Root<AgrovocProduct> rootProduct = query.from(AgrovocProduct.class);

        List<Predicate> predicates = this.createProductPredicates(filter, rootProduct);

        return searchService.search(pagination, query, rootProduct, predicates);
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
        ListJoin<AgrovocPlant, LocalizedMessage> preferedLabelsJoin = rootPlant.join(AgrovocPlant_.preferedLabel);
        ListJoin<AgrovocPlant, LocalizedMessage> alternativeLabelsJoin = rootPlant.join(AgrovocPlant_.alternativeLabels);

        Predicate preferedLabelPredicate = searchService.createLocalizedTextMatchPredicate(preferedLabelsJoin, query, language);
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

    public Predicate createProductNameQueryPredicate(String query, Optional<Language> language, From<?, AgrovocProduct> rootProduct) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        ListJoin<AgrovocProduct, LocalizedMessage> preferedLabelsJoin = rootProduct.join(AgrovocProduct_.preferedLabel);
        ListJoin<AgrovocProduct, LocalizedMessage> alternativeLabelsJoin = rootProduct.join(AgrovocProduct_.alternativeLabels);

        Predicate preferedLabelPredicate = searchService.createLocalizedTextMatchPredicate(preferedLabelsJoin, query, language);
        Predicate alternativeLabelPredicate = searchService.createLocalizedTextMatchPredicate(alternativeLabelsJoin, query, language);

        return criteriaBuilder.or(preferedLabelPredicate, alternativeLabelPredicate);
    }


    private PlantProductTupleResult mapToDomain(AgrovocPlantProductResultTuple agrovocPlantProductResultTuple) {
        AgrovocPlantResult plant = agrovocPlantProductResultTuple.getPlant();
        AgrovocProductResult product = agrovocPlantProductResultTuple.getProduct();
        String language = plant.getLanguage();
        String plantLabel = plant.getLabel();
        String plantNodeUri = plant.getNodeUri();
        String productLabel = product.getLabel();
        String productNodeUri = product.getNodeUri();

        PlantProductTupleResult domainTuple = new PlantProductTupleResult();
        domainTuple.setLanguage(language);
        domainTuple.setPlantAgrovocUri(plantNodeUri);
        domainTuple.setPlantLabel(plantLabel);
        domainTuple.setProductLabel(productLabel);
        domainTuple.setProductAgrovocUri(productNodeUri);
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
