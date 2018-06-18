package com.charlyghislain.plancul.domain.request.filter;

import java.util.Optional;

public class AgrovocProductFilter {

    private String uri;
    private String namesQuery;
    private String namesQueryLanguage;

    public Optional<String> getUri() {
        return Optional.ofNullable(uri);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Optional<String> getNamesQuery() {
        return Optional.ofNullable(namesQuery);
    }

    public void setNamesQuery(String namesQuery) {
        this.namesQuery = namesQuery;
    }

    public Optional<String> getNamesQueryLanguage() {
        return Optional.ofNullable(namesQueryLanguage);
    }

    public void setNamesQueryLanguage(String namesQueryLanguage) {
        this.namesQueryLanguage = namesQueryLanguage;
    }
}
