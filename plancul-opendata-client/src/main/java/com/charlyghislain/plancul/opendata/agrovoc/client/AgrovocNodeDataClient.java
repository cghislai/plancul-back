package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.OpenDataConstants;
import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocNodeData;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.E_Regex;
import org.apache.jena.sparql.expr.E_Str;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.util.NodeUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.charlyghislain.plancul.opendata.agrovoc.client.AgrovocEndpointConfig.AGROVOC_ENDPOINT_URL;

@ApplicationScoped
public class AgrovocNodeDataClient {

    private static final String PREDICATE_VAR_NAME = "predicate";
    private static final String OBJECT_VAR_NAME = "object";

    public AgrovocNodeData fetchNodeData(String agrovocNodeUri, String lang) {
        // Nodes & variables. The first ones are hardcoded values of some nodes from http://agrovoc.uniroma2.it/agrovoc/agrovoc

        Var objectVar = Var.alloc(OBJECT_VAR_NAME);
        Var predicateVar = Var.alloc(PREDICATE_VAR_NAME);
        Node subjectNode = NodeUtils.asNode(agrovocNodeUri);

        Triple triple = Triple.create(subjectNode, predicateVar, objectVar);

        // Filter predicates
        Expr objectExpression = new ExprVar(objectVar);
        Expr predicateExpression = new ExprVar(predicateVar);
        Expr predicatePreferedLabelExpression = new NodeValueNode(OpenDataConstants.SKOS_PREF_LABEL_PREDICATE_NODE);
        Expr predicateCloseMatchExpression = new NodeValueNode(OpenDataConstants.SKOS_CLOSE_MATCH_PREDICATE_NODE);
        Expr predicateExactMatchExpression = new NodeValueNode(OpenDataConstants.SKOS_EXACT_MATCH_PREDICATE_NODE);
        Expr predicateAlternativeLabelExpression = new NodeValueNode(OpenDataConstants.SKOS_ALT_LABEL_PREDICATE_NODE);
        Expr langExpression = new NodeValueString(lang);

        // Labels objects..
        Expr objectLangExpression = new E_Lang(objectExpression);
        Expr objectLangMatchExpression = new E_LangMatches(objectLangExpression, langExpression);

        Expr isPreferedLabelPrediacteExpression = new E_Equals(predicateExpression, predicatePreferedLabelExpression);
        Expr isAlternativeLabelPredicateExpression = new E_Equals(predicateExpression, predicateAlternativeLabelExpression);
        Expr isLabelPredicateExpression = new E_LogicalOr(isPreferedLabelPrediacteExpression, isAlternativeLabelPredicateExpression);
        Expr globalLabelspredicate = new E_LogicalAnd(objectLangMatchExpression, isLabelPredicateExpression);

        // Matches objects..
        Expr isCloseMatchPrediacteExpression = new E_Equals(predicateExpression, predicateCloseMatchExpression);
        Expr isExactMatchPrediacteExpression = new E_Equals(predicateExpression, predicateExactMatchExpression);
        Expr isMatchPredicateExpression = new E_LogicalOr(isCloseMatchPrediacteExpression, isExactMatchPrediacteExpression);
        Expr objectStringExpression = new E_Str(objectExpression);
        Expr isDbPediaExpression = new E_Regex(objectStringExpression, "dbpedia.org", "i");
        Expr isDbPediaMatchExpression = new E_LogicalAnd(isMatchPredicateExpression, isDbPediaExpression);

        Expr globalPredicate = new E_LogicalOr(globalLabelspredicate, isDbPediaMatchExpression);


        // Algebra query
        List<Var> selection = Arrays.asList(
                objectVar,
                predicateVar
        );

        BasicPattern basicPattern = new BasicPattern();
        basicPattern.add(triple);

        Op op = new OpBGP(basicPattern);
        op = OpFilter.filter(globalPredicate, op);
        op = new OpProject(op, selection);

        Query query = OpAsQuery.asQuery(op);
        query.setQuerySelectType();
        query.setDistinct(true);
        query.setPrefix("skos", OpenDataConstants.SKOS_PREFIX);

        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(AGROVOC_ENDPOINT_URL, query);

//        String serialized = query.serialize();
//        System.out.println(serialized);

        ResultSet resultSet = queryExecution.execSelect();
        Iterable<QuerySolution> solutionsIterable = () -> resultSet;

        AgrovocNodeData nodeData = new AgrovocNodeData();
        nodeData.setNodeUri(agrovocNodeUri);
        nodeData.setLanguage(lang);


        StreamSupport.stream(solutionsIterable.spliterator(), false)
                .forEach(result -> this.appendResults(nodeData, result));
        return nodeData;
    }

    private void appendResults(AgrovocNodeData nodeData, QuerySolution result) {
        Resource predicate = result.getResource(PREDICATE_VAR_NAME);
//        System.out.println(result.toString());

        if (this.isPredicate(predicate, OpenDataConstants.SKOS_PREF_LABEL_PREDICATE_NODE)) {
            Literal label = result.getLiteral(OBJECT_VAR_NAME);
            nodeData.setPreferedLabel(label.getString());
        } else if (this.isPredicate(predicate, OpenDataConstants.SKOS_ALT_LABEL_PREDICATE_NODE)) {
            Literal label = result.getLiteral(OBJECT_VAR_NAME);
            nodeData.getAlternativeLabels().add(label.getString());
        } else if (this.isPredicate(predicate, OpenDataConstants.SKOS_CLOSE_MATCH_PREDICATE_NODE)) {
            Resource resource = result.getResource(OBJECT_VAR_NAME);
            nodeData.setDbpediaUri(resource.getURI());
        } else if (this.isPredicate(predicate, OpenDataConstants.SKOS_EXACT_MATCH_PREDICATE_NODE)) {
            Resource resource = result.getResource(OBJECT_VAR_NAME);
            nodeData.setDbpediaUri(resource.getURI());
        }
    }

    private boolean isPredicate(Resource predicate, Node predicateNode) {
        return predicate.getURI().equals(predicateNode.getURI());
    }

    public static void main(String[] args) {
        AgrovocNodeDataClient vocbularyClient = new AgrovocNodeDataClient();
        vocbularyClient.fetchNodeData("http://aims.fao.org/aos/agrovoc/c_2132", "fr");
    }
}
