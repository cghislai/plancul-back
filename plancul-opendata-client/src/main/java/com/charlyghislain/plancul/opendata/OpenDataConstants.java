package com.charlyghislain.plancul.opendata;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.util.NodeUtils;

public class OpenDataConstants {
    public static final Node SKOS_BROADER_PREDICATE_NODE = NodeUtils.asNode("http://www.w3.org/2004/02/skos/core#broader");
    public static final Node SKOS_PREF_LABEL_PREDICATE_NODE = NodeUtils.asNode("http://www.w3.org/2004/02/skos/core#prefLabel");
    public static final Node SKOS_ALT_LABEL_PREDICATE_NODE = NodeUtils.asNode("http://www.w3.org/2004/02/skos/core#altLabel");
    public static final Node SKOS_EXACT_MATCH_PREDICATE_NODE = NodeUtils.asNode("http://www.w3.org/2004/02/skos/core#exactMatch");
    public static final Node SKOS_CLOSE_MATCH_PREDICATE_NODE = NodeUtils.asNode("http://www.w3.org/2004/02/skos/core#closeMatch");

    public static final Node FOAF_IS_PRIMARY_TOPIC_NODE = NodeUtils.asNode("http://xmlns.com/foaf/0.1/isPrimaryTopicOf");

    public static final String SKOS_PREFIX = "http://www.w3.org/2004/02/skos/core#";
    public static final String FOAF_PREFIX = "http://xmlns.com/foaf/0.1/";
}
