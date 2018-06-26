package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantData;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpAsQuery;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.util.NodeUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class AgrovocPlantDataSearchClient {

    private static final String FAMILY_VAR_NAME = "plantFamily";
    private static final String SPECIES_VAR_NAME = "plantSpecies";
    private static final String SUBSPECIES_VAR_NAME = "plantSubSpecies";
    private static final String FAMILY_LABEL_VAR_NAME = "plantFamilyLabel";
    private static final String SPECIES_LABEL_VAR_NAME = "plantSpeciesLabel";
    private static final String SUBSPECIES_LABEL_VAR_NAME = "plantSubSpeciesLabel";


    public AgrovocPlantData findPlantData(String plantURI, String lang) {
        Node plantNode = NodeUtils.asNode(plantURI);
        Var familyVar = Var.alloc(FAMILY_VAR_NAME);
        Var speciesVar = Var.alloc(SPECIES_VAR_NAME);
        Var subSpeciesVar = Var.alloc(SUBSPECIES_VAR_NAME);
        Var familyLabelVar = Var.alloc(FAMILY_LABEL_VAR_NAME);
        Var speciesLabelVar = Var.alloc(SPECIES_LABEL_VAR_NAME);
        Var subSpeciesLabelVar = Var.alloc(SUBSPECIES_LABEL_VAR_NAME);

        // Selection
        // Species case
        OpPath plantToSpeciesPath = AgrovocUtils.getHasDistanceOfNFromParent(plantNode, speciesVar, 0, 0);
        // Subspecies case
        OpPath plantToSubSpeciesPath = AgrovocUtils.getHasDistanceOfNFromParent(plantNode, subSpeciesVar, 0, 0);
        OpPath subspeciesToSpeciesPath = AgrovocUtils.getHasDistanceOfNFromParent(subSpeciesVar, speciesVar, 1, 1);
        OpPath subSpeciesLabelPath = AgrovocUtils.getPreferredLabelTriplePath(subSpeciesVar, subSpeciesLabelVar);

        // Common
        OpPath speciesToFamilyPath = AgrovocUtils.getHasDistanceOfNFromParent(speciesVar, familyVar, 2, 2);
        OpPath familyRankPath = AgrovocUtils.getHasRankOfFamilyPath(familyVar);
        OpPath familyLabelPath = AgrovocUtils.getPreferredLabelTriplePath(familyVar, familyLabelVar);
        OpPath speciesLabelPath = AgrovocUtils.getPreferredLabelTriplePath(speciesVar, speciesLabelVar);


        // Filter predicates
        Expr familyLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(familyLabelVar, lang);
        Expr speciesLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(speciesLabelVar, lang);
        Expr subspeciesLabelLangExpression = AgrovocUtils.getLabelMatchLangExpression(subSpeciesLabelVar, lang);
        Expr optionalSubspeciesLabelLangExpresion = AgrovocUtils.getUnboundVarOrExpr(subSpeciesLabelVar, subspeciesLabelLangExpression);

        Expr globalPredicate = AgrovocUtils.getLogicalAnd(familyLabelLangExpression, speciesLabelLangExpression, optionalSubspeciesLabelLangExpresion);

        // Algebra query
        List<Var> selection = Arrays.asList(
                familyVar, familyLabelVar,
                speciesVar, speciesLabelVar,
                subSpeciesVar, subSpeciesLabelVar
        );


        OpSequence subspeciesSequence = OpSequence.create();
        subspeciesSequence.add(plantToSubSpeciesPath);
        subspeciesSequence.add(subspeciesToSpeciesPath);
        subspeciesSequence.add(subSpeciesLabelPath);

        Op speciesAndSubspeciesUnion = OpUnion.create(plantToSpeciesPath, subspeciesSequence);


        OpSequence mainSelectionOp = OpSequence.create();
        mainSelectionOp.add(speciesAndSubspeciesUnion);
        mainSelectionOp.add(speciesToFamilyPath);
        mainSelectionOp.add(speciesLabelPath);
        mainSelectionOp.add(familyRankPath);
        mainSelectionOp.add(familyLabelPath);

        Op op = OpFilter.filter(globalPredicate, mainSelectionOp);
        op = new OpProject(op, selection);

        Query query = OpAsQuery.asQuery(op);
        query.setDistinct(true);
        query.setLimit(1);

        List<AgrovocPlantData> plantDataResult = AgrovocUtils.executeSelect(query, this::createPlantData);
        return plantDataResult.stream().findFirst().orElseThrow(IllegalStateException::new);
    }

    private AgrovocPlantData createPlantData(QuerySolution querySolution) {
        Resource familyResource = querySolution.getResource(FAMILY_VAR_NAME);
        Resource speciesResource = querySolution.getResource(SPECIES_VAR_NAME);
        Resource subSpeciesResource = querySolution.getResource(SUBSPECIES_VAR_NAME);

        Literal familtyLabel = querySolution.getLiteral(FAMILY_LABEL_VAR_NAME);
        Literal speciesLabel = querySolution.getLiteral(SPECIES_LABEL_VAR_NAME);
        Literal subspeciesLabel = querySolution.getLiteral(SUBSPECIES_LABEL_VAR_NAME);

        return AgrovocPlantDataFactory.createAgrovocPlantData(
                familyResource, familtyLabel,
                speciesResource, speciesLabel,
                subSpeciesResource, subspeciesLabel
        );
    }

    public static void main(String[] args) {
        AgrovocPlantDataSearchClient client = new AgrovocPlantDataSearchClient();
        client.findPlantData("http://aims.fao.org/aos/agrovoc/c_1067", "fr");
    }
}
