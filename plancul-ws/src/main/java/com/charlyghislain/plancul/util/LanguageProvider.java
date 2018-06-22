package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.i18n.Language;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class LanguageProvider {


    @Context
    private HttpServletRequest httpRequest;

    @Produces
    @RequestScoped
    @ContentLanguage
    public LanguageContainer getContentLanguage() {
        Enumeration<String> headers = httpRequest.getHeaders("Content-Language");
        return Collections.list(headers).stream()
                .map(this::getSupportedLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(LanguageContainer::new)
                .orElseGet(LanguageContainer::new);
    }

    @Produces
    @RequestScoped
    @AcceptedLanguage
    public LanguageContainer getAcceptedLanguage() {
        Enumeration<String> headers = httpRequest.getHeaders("Accept-Language");
        return Collections.list(headers).stream()
                .map(this::getSupportedLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(LanguageContainer::new)
                .orElseGet(LanguageContainer::new);
    }


    private Optional<Language> getSupportedLanguage(String headerValue) {
        String[] splitted = headerValue.split(",");
        return Arrays.stream(splitted)
                .map(Locale::forLanguageTag)
                .map(Locale::getLanguage)
                .map(Language::fromCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}