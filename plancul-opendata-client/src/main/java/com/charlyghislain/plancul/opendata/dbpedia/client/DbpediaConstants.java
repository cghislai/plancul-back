package com.charlyghislain.plancul.opendata.dbpedia.client;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.util.NodeUtils;

public class DbpediaConstants {
    public static final Node DBO_ABSTRACT_NODE = NodeUtils.asNode("http://dbpedia.org/ontology/abstract");
    public static final Node DBO_THUMBNAIL_NODE = NodeUtils.asNode("http://dbpedia.org/ontology/thumbnail");
    public static final Node DBO_THUMBNAIL_CAPTION_NODE = NodeUtils.asNode("http://dbpedia.org/property/imageCaption");




}
