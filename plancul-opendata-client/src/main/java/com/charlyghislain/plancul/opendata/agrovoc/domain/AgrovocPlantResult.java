package com.charlyghislain.plancul.opendata.agrovoc.domain;

import java.io.Serializable;

public class AgrovocPlantResult implements Serializable {

    private String nodeUri;
    private String language;
    private String label;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
