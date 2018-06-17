package com.charlyghislain.plancul.opendata.agrovoc.client;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.util.NodeUtils;

public class AgrovocConstants {

    public static final Node productTypeVegetablesNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_8174");
    public static final Node productTypeLegumesNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_4255");
    public static final Node productTypeFruitsNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_3131");
    public static final Node produceTypeNutsNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_12873");
    public static final Node productTypeSpicesNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_15685");
    public static final Node productTypeCerealsNode = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_1474");

    public static final Node predicateProducedByNode = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#isProducedBy");

}
