package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductResultTuple;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantResult;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocProductResult;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.SortCondition;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.table.TableN;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.E_Regex;
import org.apache.jena.sparql.expr.E_Str;
import org.apache.jena.sparql.expr.E_StrLowerCase;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocEndpointConfig.AGROVOC_ENDPOINT_URL;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_ALT_LABEL_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_BROADER_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_PREFIX;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_PREF_LABEL_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.predicateProducedByNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.produceTypeNutsNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.productTypeCerealsNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.productTypeFruitsNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.productTypeLegumesNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.productTypeSpicesNode;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.productTypeVegetablesNode;

@ApplicationScoped
public class AgrovocPlantProductTupleSearchClient {

    private static final int DEFAULT_RESULTS_LIMIT = 30;
    private static final String PRODUCT_VAR_NAME = "product";
    private static final String PRODUCT_LABEL_VAR_NAME = "productLabel";
    private static final String PLANT_VAR_NAME = "plant";
    private static final String PLANT_LABEL_VAR_NAME = "plantLabel";

    public List<AgrovocPlantProductResultTuple> findPlantProductTuples(String queryString, String lang) {
        return this.findPlantProductTuples(queryString, lang, DEFAULT_RESULTS_LIMIT);
    }

    public List<AgrovocPlantProductResultTuple> findPlantProductTuples(String queryString, String lang, int limit) {
        return this.findPlantProductTuples(queryString, lang, limit, 0);
    }

    public List<AgrovocPlantProductResultTuple> findPlantProductTuples(String queryString, String lang, int limit, int offset) {

        Path broaderPredicatePath = PathFactory.pathLink(SKOS_BROADER_PREDICATE_NODE);
        Path predicateBroaderUpTo3LevelsPath = PathFactory.pathMod(broaderPredicatePath, 0, 3);
        Path prefLabelPath = PathFactory.pathLink(SKOS_PREF_LABEL_PREDICATE_NODE);
        Path altLabelPath = PathFactory.pathLink(SKOS_ALT_LABEL_PREDICATE_NODE);
        Path labelsPredicatePath = PathFactory.pathAlt(prefLabelPath, altLabelPath);

        Var productVar = Var.alloc(PRODUCT_VAR_NAME);
        Var productLabelVar = Var.alloc(PRODUCT_LABEL_VAR_NAME);
        Var plantVar = Var.alloc(PLANT_VAR_NAME);
        Var plantLabelVar = Var.alloc(PLANT_LABEL_VAR_NAME);
        Var productTypesVar = Var.alloc("productTypes");


        // Triples predicates
        TriplePath productTypeTriplePath = new TriplePath(productVar, predicateBroaderUpTo3LevelsPath, productTypesVar);
        TriplePath productLabelTriplePath = new TriplePath(productVar, labelsPredicatePath, productLabelVar);
        Triple productProducedByTriple = Triple.create(productVar, predicateProducedByNode, plantVar);
        TriplePath plantLabelTriplePath = new TriplePath(plantVar, labelsPredicatePath, plantLabelVar);

        // Filter predicates
        Expr plantLabelExpression = new ExprVar(plantLabelVar);
        Expr productLabelExpression = new ExprVar(productLabelVar);
        Expr queryStringExpression = new NodeValueString(queryString);
        Expr langExpression = new NodeValueString(lang);
        Expr regexFlagsExpression = new NodeValueString("i");

        Expr plantLabelLangExpression = new E_Lang(plantLabelExpression);
        Expr plantLabelLangMatchExpression = new E_LangMatches(plantLabelLangExpression, langExpression);
        Expr productLabelLangExpression = new E_Lang(productLabelExpression);
        Expr productLabelLangMatchExpression = new E_LangMatches(productLabelLangExpression, langExpression);
        Expr bothLabelsLangMatchExpression = new E_LogicalAnd(plantLabelLangMatchExpression, productLabelLangMatchExpression);

        Expr plantLabelMatchQueryExpression = new E_Regex(plantLabelExpression, queryStringExpression, regexFlagsExpression);
        Expr productLabelMatchQueryExpression = new E_Regex(productLabelExpression, queryStringExpression, regexFlagsExpression);
        Expr eitherLabelMatchesQueryExpresion = new E_LogicalOr(plantLabelMatchQueryExpression, productLabelMatchQueryExpression);

        Expr globalpredicate = new E_LogicalAnd(bothLabelsLangMatchExpression, eitherLabelMatchesQueryExpresion);

        // Sorts
        List<SortCondition> sorts = new ArrayList<>();
        Expr plantLabelLowerCaseExpression = new E_StrLowerCase(new E_Str(plantLabelExpression));
        Expr productLabelLowerCaseExpression = new E_StrLowerCase(new E_Str(productLabelExpression));
        SortCondition plantLabelSort = new SortCondition(plantLabelLowerCaseExpression, 1);
        SortCondition productLabelSort = new SortCondition(productLabelLowerCaseExpression, 1);
        sorts.add(plantLabelSort);
        sorts.add(productLabelSort);

        // VALUES table
        TableN productTypesTable = new TableN();
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, productTypeVegetablesNode));
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, productTypeLegumesNode));
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, productTypeFruitsNode));
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, produceTypeNutsNode));
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, productTypeSpicesNode));
        productTypesTable.addBinding(BindingFactory.binding(productTypesVar, productTypeCerealsNode));


        // Algebra query
        List<Var> selection = Arrays.asList(
                plantVar,
                plantLabelVar,
                productVar,
                productLabelVar
        );

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(productProducedByTriple);

        OpTable productTableOp = OpTable.create(productTypesTable);
        Op op = OpSequence.create().copy(Arrays.asList(
                productTableOp,
                new OpPath(productTypeTriplePath),
                new OpPath(productLabelTriplePath),
                new OpBGP(basicPattern),
                new OpPath(plantLabelTriplePath)
                )
        );
        op = OpFilter.filter(globalpredicate, op);
        op = new OpOrder(op, sorts);
        op = new OpProject(op, selection);

        Query query = OpAsQuery.asQuery(op);
        query.setQuerySelectType();
        query.setDistinct(true);
        query.setPrefix("skos", SKOS_PREFIX);
        query.setLimit(limit);
        query.setOffset(offset);

        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(AGROVOC_ENDPOINT_URL, query);

        // TODO: ordering
        String serialized = query.serialize();
        System.out.println(serialized);

        ResultSet resultSet = queryExecution.execSelect();
        Iterable<QuerySolution> solutionsIterable = () -> resultSet;
        List<AgrovocPlantProductResultTuple> resultList = StreamSupport.stream(solutionsIterable.spliterator(), false)
                .map(this::createTuple)
                .collect(Collectors.toList());
        return resultList;
    }

    private AgrovocPlantProductResultTuple createTuple(QuerySolution querySolution) {
        Resource plantNode = querySolution.getResource(PLANT_VAR_NAME);
        Resource productNode = querySolution.getResource(PRODUCT_VAR_NAME);
        Literal plantLabel = querySolution.getLiteral(PLANT_LABEL_VAR_NAME);
        Literal productLabel = querySolution.getLiteral(PRODUCT_LABEL_VAR_NAME);
        String plantURI = plantNode.getURI();
        String productURI = productNode.getURI();

        AgrovocPlantResult agrovocPlant = new AgrovocPlantResult();
        agrovocPlant.setNodeUri(plantURI);
        agrovocPlant.setLabel(plantLabel.getString());
        agrovocPlant.setLanguage(plantLabel.getLanguage());

        AgrovocProductResult agrovocProduct = new AgrovocProductResult();
        agrovocProduct.setNodeUri(productURI);
        agrovocProduct.setLabel(productLabel.getString());
        agrovocProduct.setLanguage(productLabel.getLanguage());

        AgrovocPlantProductResultTuple tuple = new AgrovocPlantProductResultTuple();
        tuple.setPlant(agrovocPlant);
        tuple.setProduct(agrovocProduct);
        return tuple;
    }

    public static void main(String[] args) {
        AgrovocPlantProductTupleSearchClient vocbularyClient = new AgrovocPlantProductTupleSearchClient();
        vocbularyClient.findPlantProductTuples("pois", "fr");
    }
}
