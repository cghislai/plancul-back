package com.charlyghislain.plancul.opendata.agrovoc.client;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.util.NodeUtils;

public class AgrovocConstants {

    public static final Node PRODUCT_TYPE_VEGETABLE_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_8174");
    public static final Node PRODUCT_TYPE_LEGUME_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_4255");
    public static final Node PRODUCT_TYPE_FRUIT_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_3131");
    public static final Node PRODUCT_TYPE_NUTS_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_12873");
    public static final Node PRODUCT_TYPE_SPICES_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_15685");
    public static final Node PRODUCT_TYPE_CEREALS_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_1474");
    public static final Node PRODUCT_TYPE_VEGETAL_PRODUCT_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_8171");
    public static final Node PLANT_TYPE_CULTIVATED_PLANT = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_1972");

    public static final Node TAXONOMIC_RANK_SPECIES_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_331243");
    public static final Node TAXONOMIC_RANK_FAMILTY_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_330937");
    public static final Node REIGN_PLANTAE_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_330074");
    public static final Node CONCEPT_PLANTS_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrovoc/c_5993");

    public static final Node PREDICATE_PRODUCED_BY_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#isProducedBy");
    public static final Node PREDICATE_PRODUCES_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#produces");
    public static final Node PREDICATE_IS_USED_AS_NODE = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#isUsedAs");
    public static final Node PREDICATE_HAS_TAXONOMIC_RANK = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#hasTaxonomicRank");
    public static final Node PREDICATE_HAS_PROCUCT = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#hasProduct");
    public static final Node PREDICATE_IS_MEMBER_OF = NodeUtils.asNode("http://aims.fao.org/aos/agrontology#isMemberOf");


    public static final String AGROONTOLOGY_PREFIX = "http://aims.fao.org/aos/agrontology#";
    public static final String AGROONTOVOC_PREFIX = "http://aims.fao.org/aos/agrovoc/";

}
