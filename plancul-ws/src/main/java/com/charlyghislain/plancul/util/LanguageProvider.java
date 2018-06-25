package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.i18n.Language;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
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
    @Dependent
    @ContentLanguage
    @NonNull
    public Language getContentLanguage() {
        Enumeration<String> headers = httpRequest.getHeaders("Content-Language");
        return Collections.list(headers).stream()
                .map(this::getSupportedLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(Language.DEFAULT_LANGUAGE);
    }

    @Produces
    @Dependent
    @AcceptedLanguage
    @NonNull
    public Language getAcceptedLanguage() {
        Enumeration<String> headers = httpRequest.getHeaders("Accept-Language");
        return Collections.list(headers).stream()
                .map(this::getSupportedLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(Language.DEFAULT_LANGUAGE);
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