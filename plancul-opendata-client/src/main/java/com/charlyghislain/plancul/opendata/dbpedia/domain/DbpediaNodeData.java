package com.charlyghislain.plancul.opendata.dbpedia.domain;

public class DbpediaNodeData {

    private String nodeUri;
    private String language;
    private String thumbnailUrl;
    private String thumbnailCaption;
    private String abstractText;
    private String wikipediaUrl;

    public String getNodeUri() {
        return nodeUri;
    }

    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailCaption() {
        return thumbnailCaption;
    }

    public void setThumbnailCaption(String thumbnailCaption) {
        this.thumbnailCaption = thumbnailCaption;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public void setWikipediaUrl(String wikipediaUrl) {
        this.wikipediaUrl = wikipediaUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
