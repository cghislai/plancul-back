package com.charlyghislain.plancul.domain.request.filter;

import com.charlyghislain.plancul.domain.i18n.Language;

import java.util.Optional;

public class AgrovocProductFilter {

    private String uri;
    private String namesQuery;
    private Language namesQueryLanguage;

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

    public Optional<Language> getNamesQueryLanguage() {
        return Optional.ofNullable(namesQueryLanguage);
    }

    public void setNamesQueryLanguage(Language namesQueryLanguage) {
        this.namesQueryLanguage = namesQueryLanguage;
    }
}
