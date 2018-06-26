package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductTuple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.SortCondition;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.Expr;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class AgrovocPlantProductTupleSearchClient {

    private static final String PRODUCT_VAR_NAME = "product";
    private static final String PRODUCT_LABEL_VAR_NAME = "productLabel";
    private static final String PLANT_VAR_NAME = "plant";
    private static final String PLANT_LABEL_VAR_NAME = "plantLabel";
    private static final String PLANT_PREF_LABEL_VAR_NAME = "plantPrefLabel";
    private static final String PRODUCT_PREF_LABEL_VAR_NAME = "productPrefLabel";


    public List<AgrovocPlantProductTuple> findPlantProductTuples(String queryString, String lang, int offset, int limit) {
        Var productVar = Var.alloc(PRODUCT_VAR_NAME);
        Var productLabelVar = Var.alloc(PRODUCT_LABEL_VAR_NAME);
        Var plantVar = Var.alloc(PLANT_VAR_NAME);
        Var plantLabelVar = Var.alloc(PLANT_LABEL_VAR_NAME);
        Var plantPrefLabelVar = Var.alloc(PLANT_PREF_LABEL_VAR_NAME);
        Var productPrefLabelVar = Var.alloc(PRODUCT_PREF_LABEL_VAR_NAME);

        // Main selection
        OpPath plantIsPlantaeTriplePath = AgrovocUtils.getIsPlantaeTriplePath(plantVar);
        OpPath plantHasRankOfSpeciesTriplePath = AgrovocUtils.getHasRankOfSpeciesOrSubSpeciesTriplePath(plantVar);
        OpPath plantLabelsTriplePath = AgrovocUtils.getAnyLabelsTriplePath(plantVar, plantLabelVar);
        OpPath plantPreferredLabelTriplePath = AgrovocUtils.getPreferredLabelTriplePath(plantVar, plantPrefLabelVar);
        OpPath productPreferredLabelTriplePath = AgrovocUtils.getPreferredLabelTriplePath(productVar, productPrefLabelVar);

        // TODO: sometimes there is no product which is subtype of the vegetable products node,
        // but the plant is labelled as 'isUsedAs' oilseed crops, or isUsedAs/broader = crops (c_1972)
        OpPath productIsVegetableProductTriplePath = AgrovocUtils.getIsVegetalProductTripletPath(productVar);
        OpPath productLabelsTriplePath = AgrovocUtils.getAnyLabelsTriplePath(productVar, productLabelVar);
        OpPath plantProducesProductTriplePath = AgrovocUtils.getPlantProducesPath(plantVar, productVar);

        // Filter predicates
        Expr plantLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(plantLabelVar, lang);
        Expr plantPreferredLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(plantPrefLabelVar, lang);
        Expr productLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(productLabelVar, lang);
        Expr productPreferredLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(productPrefLabelVar, lang);

        Expr plantLabelExpression = AgrovocUtils.getLabelMatchExpression(plantLabelVar, queryString);
        Expr productLabelExpression = AgrovocUtils.getLabelMatchExpression(productLabelVar, queryString);
        Expr labelsMatchQueryExpression = new E_LogicalOr(plantLabelExpression, productLabelExpression);

        Expr globalPredicate = AgrovocUtils.getLogicalAnd(
                plantLabelLangExpression,
                plantPreferredLabelLangExpression,
                productLabelLangExpression,
                productPreferredLabelLangExpression,
                labelsMatchQueryExpression);


        // Sorts
        List<SortCondition> sorts = AgrovocUtils.getLabelsSortConditions(true, plantLabelVar, productLabelVar);


        // Algebra query
        List<Var> selection = Arrays.asList(
                plantVar,
                plantLabelVar,
                productVar,
                productLabelVar,
                plantPrefLabelVar,
                productPrefLabelVar
        );

        OpSequence mainSelectionOp = OpSequence.create();
        mainSelectionOp.add(productIsVegetableProductTriplePath);
        mainSelectionOp.add(productLabelsTriplePath);
        mainSelectionOp.add(plantProducesProductTriplePath);
        mainSelectionOp.add(plantLabelsTriplePath);
        mainSelectionOp.add(plantHasRankOfSpeciesTriplePath);
        mainSelectionOp.add(plantIsPlantaeTriplePath);
        mainSelectionOp.add(plantPreferredLabelTriplePath);
        mainSelectionOp.add(productPreferredLabelTriplePath);

        Op op = mainSelectionOp;
        op = OpFilter.filter(globalPredicate, op);
        op = new OpOrder(op, sorts);
        op = new OpProject(op, selection);

        Query query = OpAsQuery.asQuery(op);
        query.setOffset(offset);
        query.setLimit(limit);
//        query.setDistinct(true);

        List<AgrovocPlantProductTuple> productResultTuples = AgrovocUtils.executeSelect(query, (solution) -> this.createTuple(solution, queryString));
        return productResultTuples;
    }

    private AgrovocPlantProductTuple createTuple(QuerySolution querySolution, String queryString) {
        Resource plantNode = querySolution.getResource(PLANT_VAR_NAME);
        Resource productNode = querySolution.getResource(PRODUCT_VAR_NAME);
        Literal plantLabel = querySolution.getLiteral(PLANT_LABEL_VAR_NAME);
        Literal plantPrefLabel = querySolution.getLiteral(PLANT_PREF_LABEL_VAR_NAME);
        Literal productLabel = querySolution.getLiteral(PRODUCT_LABEL_VAR_NAME);
        Literal productPrefLabel = querySolution.getLiteral(PRODUCT_PREF_LABEL_VAR_NAME);
        return AgrovocPlantProductTupleFactory.createTuple(plantNode, productNode,
                plantPrefLabel, productPrefLabel, plantLabel, productLabel, queryString);
    }

    public static void main(String[] args) {
        AgrovocPlantProductTupleSearchClient vocbularyClient = new AgrovocPlantProductTupleSearchClient();
        vocbularyClient.findPlantProductTuples("aube", "fr", 0, 20);
    }
}
