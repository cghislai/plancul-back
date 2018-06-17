package com.charlyghislain.plancul.opendata.agrovoc.domain;

import java.util.HashSet;
import java.util.Set;

public class AgrovocNodeData {
    private String nodeUri;
    private String language;

    private String preferedLabel;
    private Set<String> alternativeLabels = new HashSet<>();

    private String dbpediaUri;

    public String getNodeUri() {
        return nodeUri;
    }

    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPreferedLabel() {
        return preferedLabel;
    }

    public void setPreferedLabel(String preferedLabel) {
        this.preferedLabel = preferedLabel;
    }

    public Set<String> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(Set<String> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }

    public String getDbpediaUri() {
        return dbpediaUri;
    }

    public void setDbpediaUri(String dbpediaUri) {
        this.dbpediaUri = dbpediaUri;
    }
}
