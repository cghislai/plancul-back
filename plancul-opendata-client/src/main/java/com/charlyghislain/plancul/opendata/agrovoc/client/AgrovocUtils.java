package com.charlyghislain.plancul.opendata.agrovoc.client;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.SortCondition;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Bound;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalNot;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.E_Regex;
import org.apache.jena.sparql.expr.E_Str;
import org.apache.jena.sparql.expr.E_StrLowerCase;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_ALT_LABEL_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_BROADER_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_PREFIX;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_PREF_LABEL_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.OpenDataConstants.SKOS_RELATED_PREDICATE_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.AGROONTOLOGY_PREFIX;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.AGROONTOVOC_PREFIX;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PREDICATE_HAS_PROCUCT;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PREDICATE_HAS_TAXONOMIC_RANK;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PREDICATE_IS_MEMBER_OF;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PREDICATE_IS_USED_AS_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PREDICATE_PRODUCES_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.PRODUCT_TYPE_VEGETAL_PRODUCT_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.REIGN_PLANTAE_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocConstants.TAXONOMIC_RANK_FAMILTY_NODE;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocEndpointConfig.AGROVOC_ENDPOINT_URL;
import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocNodeDataClient.QUERY_TIMEOUT;

class AgrovocUtils {

    static OpPath getPlantProducesPath(Var plantVar, Var productVar) {
        Path plantProducesPath = AgrovocUtils.getAlternativeLinksPath(
                PREDICATE_PRODUCES_NODE, PREDICATE_IS_USED_AS_NODE, PREDICATE_HAS_PROCUCT, PREDICATE_IS_MEMBER_OF);
        // We want plant as the species taxon, but sometimes the parent genus is labelled with the produces predicate
        // eg: origan
        Path parentPath = PathFactory.pathLink(SKOS_BROADER_PREDICATE_NODE);
        Path producesPath = AgrovocUtils.getAlternativeLinksPath(PREDICATE_PRODUCES_NODE, PREDICATE_IS_USED_AS_NODE);
        Path parentProducesPath = PathFactory.pathSeq(parentPath, producesPath);
        Path plantOrParentProducesPath = PathFactory.pathAlt(plantProducesPath, parentProducesPath);
        OpPath plantOrParentProducesProductTriplePath = new OpPath(new TriplePath(plantVar, plantOrParentProducesPath, productVar));
        return plantOrParentProducesProductTriplePath;
    }

    static OpPath getIsPlantaeTriplePath(Var subjectVar) {
        Path broaderPredicatePath = PathFactory.pathLink(SKOS_BROADER_PREDICATE_NODE);
        Path predicateBroaderNoLimit = PathFactory.pathOneOrMoreN(broaderPredicatePath);
        TriplePath isPlantaeTriplePath = new TriplePath(subjectVar, predicateBroaderNoLimit, REIGN_PLANTAE_NODE);
        return new OpPath(isPlantaeTriplePath);
    }

    static OpPath getHasRankOfSpeciesOrSubSpeciesTriplePath(Var subjectVar) {
        // Not all subjects have the hasTaxonomicRank predicate, neither their parent genus.
        // The famillly seems to be correctly labelled, although some times (Poaceae), with the
        // skos:related rather than agro:hasTaxonomicRank predicate.

        Path broaderPredicatePath = PathFactory.pathLink(SKOS_BROADER_PREDICATE_NODE);
        Path broaderTwicePath = PathFactory.pathMod(broaderPredicatePath, 2, 3);
        Path taxonomicRankPath = PathFactory.pathLink(PREDICATE_HAS_TAXONOMIC_RANK);
        Path relatedConceptPath = PathFactory.pathLink(SKOS_RELATED_PREDICATE_NODE);
        Path effectiveRankPath = PathFactory.pathAlt(taxonomicRankPath, relatedConceptPath);
        Path grandParentIsFamilyPath = PathFactory.pathSeq(broaderTwicePath, effectiveRankPath);


        TriplePath isSpeciesTriplePath = new TriplePath(subjectVar, grandParentIsFamilyPath, TAXONOMIC_RANK_FAMILTY_NODE);
        return new OpPath(isSpeciesTriplePath);
    }

    static OpPath getIsVegetalProductTripletPath(Var subjectVar) {
        Path broaderPredicatePath = PathFactory.pathLink(SKOS_BROADER_PREDICATE_NODE);
        Path predicateBroaderRwice = PathFactory.pathMod(broaderPredicatePath, 0, 4);
        TriplePath isVegetalProductPath = new TriplePath(subjectVar, predicateBroaderRwice, PRODUCT_TYPE_VEGETAL_PRODUCT_NODE);
        return new OpPath(isVegetalProductPath);
    }

    static OpPath getAnyLabelsTriplePath(Var subjectVar, Var labelVar) {
        Path prefLabelPath = PathFactory.pathLink(SKOS_PREF_LABEL_PREDICATE_NODE);
        Path altLabelPath = PathFactory.pathLink(SKOS_ALT_LABEL_PREDICATE_NODE);
        Path labelsPredicatePath = PathFactory.pathAlt(prefLabelPath, altLabelPath);
        TriplePath labelTriplePath = new TriplePath(subjectVar, labelsPredicatePath, labelVar);
        return new OpPath(labelTriplePath);
    }

    static OpPath getPreferredLabelTriplePath(Var subjectVar, Var labelVar) {
        Path prefLabelPath = PathFactory.pathLink(SKOS_PREF_LABEL_PREDICATE_NODE);
        TriplePath labelTriplePath = new TriplePath(subjectVar, prefLabelPath, labelVar);
        return new OpPath(labelTriplePath);
    }

    static Path getAlternativeLinksPath(Node firstLink, Node... alternatives) {
        Path path = PathFactory.pathLink(firstLink);
        for (Node node: alternatives) {
            Path alternativeLink = PathFactory.pathLink(node);
            path = PathFactory.pathAlt(path, alternativeLink);
        }
        return path;
    }

    static Expr getLogicalAnd(Expr firstExpression, Expr... expressions) {
        Expr expression = firstExpression;
        for (Expr nextExpression: expressions) {
            expression = new E_LogicalAnd(expression, nextExpression);
        }
        return expression;
    }

    static Expr getLabelMatchLangExpression(Var labelVar, String lang) {
        Expr langExpression = new NodeValueString(lang);
        Expr labelExpression = new ExprVar(labelVar);
        Expr labelLangExpression = new E_Lang(labelExpression);
        Expr labelLangMatchExpression = new E_LangMatches(labelLangExpression, langExpression);

        return labelLangMatchExpression;
    }


    static Expr getIsEqualExpression(Var varA, Var varB) {
        Expr exprVarA = new ExprVar(varA);
        Expr exprVarB = new ExprVar(varB);
        return new E_Equals(exprVarA, exprVarB);
    }

    static Expr getLabelMatchExpression(Var labelVar, String query) {
        Expr labelExpression = new ExprVar(labelVar);
        Expr queryExpression = new NodeValueString(query);
        Expr regexFlagsExpression = new NodeValueString("i");

        Expr labelMatchQueryExpression = new E_Regex(labelExpression, queryExpression, regexFlagsExpression);
        return labelMatchQueryExpression;
    }

    static Expr getUnboundVarOrExpr(Var unboundVar, Expr expr) {
        ExprVar exprVar = new ExprVar(unboundVar);
        E_Bound boundExpr = new E_Bound(exprVar);
        E_LogicalNot unboundExpr = new E_LogicalNot(boundExpr);
        return new E_LogicalOr(unboundExpr, expr);
    }

    static List<SortCondition> getLabelsSortConditions(boolean ascending, Var... labelVars) {
        List<SortCondition> sortsList = new ArrayList<>();
        for (Var labelVar: labelVars) {
            Expr labelExpression = new ExprVar(labelVar);
            Expr labelLowerCaseExpression = new E_StrLowerCase(new E_Str(labelExpression));
            SortCondition labelSort = new SortCondition(labelLowerCaseExpression, ascending ? 1 : -1);
            sortsList.add(labelSort);
        }
        return sortsList;
    }

    static <T> List<T> executeSelect(Query query, Function<QuerySolution, T> resultMapper) {
        query.setQuerySelectType();
        query.setPrefix("skos", SKOS_PREFIX);
        query.setPrefix("agro", AGROONTOLOGY_PREFIX);
        query.setPrefix("agrovoc", AGROONTOVOC_PREFIX);

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(AGROVOC_ENDPOINT_URL, query)) {
            queryExecution.setTimeout(QUERY_TIMEOUT);

//            String serialized = query.serialize();
//            System.out.println(serialized);

            ResultSet resultSet = queryExecution.execSelect();
            Iterable<QuerySolution> solutionsIterable = () -> resultSet;
            List<T> resultList = StreamSupport.stream(solutionsIterable.spliterator(), false)
//                    .peek(qs -> System.out.println(qs.toString()))
                    .map(resultMapper::apply)
//                    .peek(r -> System.out.println(r.toString()))
                    .collect(Collectors.toList());
            return resultList;
        }
    }
}
