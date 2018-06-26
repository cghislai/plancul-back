package com.charlyghislain.plancul.opendata.agrovoc.client;

import com.charlyghislain.plancul.opendata.agrovoc.domain.AgrovocPlantProductTuple;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

public class AgrovocPlantProductTupleFactory {

    static AgrovocPlantProductTuple createTuple(Resource plant, Resource product,
                                                Literal plantPrefLabel, Literal productPrefLabel,
                                                Literal plantLabel, Literal productLabel,
                                                String searchTerm) {
        String plantURI = plant.getURI();
        String productURI = product.getURI();
        String matchedTerm = findMatchedTerm(searchTerm, plantLabel, productLabel);

        AgrovocPlantProductTuple tuple = new AgrovocPlantProductTuple();
        tuple.setLanguage(plantPrefLabel.getLanguage());
        tuple.setMatchedTerm(matchedTerm);
        tuple.setPlantPreferredLabel(plantPrefLabel.getString());
        tuple.setProductPreferredLabel(productPrefLabel.getString());
        tuple.setPlantURI(plantURI);
        tuple.setProductURI(productURI);
        return tuple;
    }

    private static String findMatchedTerm(String searchTerm, Literal plantLabel, Literal productLabel) {
        String lowerCaseSearch = searchTerm.toLowerCase();

        String plantLabelString = plantLabel.getString();
        String lowerCasePlantLabel = plantLabelString.toLowerCase();
        if (lowerCasePlantLabel.contains(lowerCaseSearch)) {
            return plantLabelString;
        }

        String productLabelString = productLabel.getString();
        String lowerCaseProductlabel = productLabelString.toLowerCase();
        if (lowerCaseProductlabel.contains(lowerCaseSearch)) {
            return productLabelString;
        }

        throw new AssertionError();
    }


}
