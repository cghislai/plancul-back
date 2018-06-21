package com.charlyghislain.plancul.opendata.dbpedia.client;

import com.charlyghislain.plancul.opendata.OpenDataConstants;
import com.charlyghislain.plancul.opendata.dbpedia.domain.DbpediaNodeData;
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
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;
import org.apache.jena.sparql.expr.nodevalue.NodeValueString;
import org.apache.jena.sparql.util.NodeUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.charlyghislain.plancul.opendata.dbpedia.client.DbPediaEndpointConfig.DBPEDIA_ENDPOINT_URL;

@ApplicationScoped
public class DbpediaNodeDataClient {

    private static final String PREDICATE_VAR_NAME = "predicate";
    private static final String OBJECT_VAR_NAME = "object";
    public static final int QUERY_TIMEOUT = 1000;

    public DbpediaNodeData fetchNodeData(String dbpediaNodeUri, String language) {
        // Nodes & variables. The first ones are hardcoded values of some nodes from http://dbpedia.uniroma2.it/dbpedia/dbpedia

        Var objectVar = Var.alloc(OBJECT_VAR_NAME);
        Var predicateVar = Var.alloc(PREDICATE_VAR_NAME);
        Node subjectNode = NodeUtils.asNode(dbpediaNodeUri);

        Triple triple = Triple.create(subjectNode, predicateVar, objectVar);

        // Filter predicates
        Expr objectExpression = new ExprVar(objectVar);
        Expr predicateExpression = new ExprVar(predicateVar);
        Expr isPrimaryTopicOfExpression = new NodeValueNode(OpenDataConstants.FOAF_IS_PRIMARY_TOPIC_NODE);
        Expr abstractExpression = new NodeValueNode(DbpediaConstants.DBO_ABSTRACT_NODE);
        Expr thumbnailExpression = new NodeValueNode(DbpediaConstants.DBO_THUMBNAIL_NODE);
        Expr thumbnailCaptionExpression = new NodeValueNode(DbpediaConstants.DBO_THUMBNAIL_CAPTION_NODE);

        ExprList resourcePredicateList = new ExprList(Arrays.asList(
                isPrimaryTopicOfExpression, thumbnailExpression
        ));
        Expr isHandledResourcePredicateExpression = new E_OneOf(predicateExpression, resourcePredicateList);

        Expr langExpression = new NodeValueString(language);
        Expr objectLangExpression = new E_Lang(objectExpression);
        Expr objectLangMatchExpression = new E_LangMatches(objectLangExpression, langExpression);
        ExprList labelPredicateList = new ExprList(Arrays.asList(
                abstractExpression, thumbnailCaptionExpression
        ));
        Expr isHandledLabelPredicateExpression = new E_OneOf(predicateExpression, labelPredicateList);
        Expr isLabelMatchingLanguagePredicateExpression = new E_LogicalAnd(objectLangMatchExpression, isHandledLabelPredicateExpression);

        Expr globalPredicate = new E_LogicalOr(isHandledResourcePredicateExpression, isLabelMatchingLanguagePredicateExpression);


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

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(DBPEDIA_ENDPOINT_URL, query)) {
            queryExecution.setTimeout(QUERY_TIMEOUT);

//        String serialized = query.serialize();
//        System.out.println(serialized);

            ResultSet resultSet = queryExecution.execSelect();
            Iterable<QuerySolution> solutionsIterable = () -> resultSet;

            DbpediaNodeData nodeData = new DbpediaNodeData();
            nodeData.setNodeUri(dbpediaNodeUri);

            StreamSupport.stream(solutionsIterable.spliterator(), false)
                    .forEach(result -> this.appendResults(nodeData, result));
            return nodeData;
        }
    }

    private void appendResults(DbpediaNodeData nodeData, QuerySolution result) {
        Resource predicate = result.getResource(PREDICATE_VAR_NAME);
//        System.out.println(result.toString());

        if (this.isPredicate(predicate, DbpediaConstants.DBO_ABSTRACT_NODE)) {
            Literal label = result.getLiteral(OBJECT_VAR_NAME);
            nodeData.setAbstractText(label.getString());
        } else if (this.isPredicate(predicate, DbpediaConstants.DBO_THUMBNAIL_NODE)) {
            Resource resource = result.getResource(OBJECT_VAR_NAME);
            nodeData.setThumbnailUrl(resource.getURI());
        } else if (this.isPredicate(predicate, DbpediaConstants.DBO_THUMBNAIL_CAPTION_NODE)) {
            Literal label = result.getLiteral(OBJECT_VAR_NAME);
            nodeData.setThumbnailCaption(label.getString());
        } else if (this.isPredicate(predicate, OpenDataConstants.FOAF_IS_PRIMARY_TOPIC_NODE)) {
            Resource resource = result.getResource(OBJECT_VAR_NAME);
            nodeData.setWikipediaUrl(resource.getURI());
        }
    }

    private boolean isPredicate(Resource predicate, Node predicateNode) {
        return predicate.getURI().equals(predicateNode.getURI());
    }

    public static void main(String[] args) {
        DbpediaNodeDataClient vocbularyClient = new DbpediaNodeDataClient();
        vocbularyClient.fetchNodeData("http://dbpedia.org/resource/Daucus_carota", "fr");
    }
}
