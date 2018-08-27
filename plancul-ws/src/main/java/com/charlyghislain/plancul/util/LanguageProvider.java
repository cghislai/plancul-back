package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.i18n.Language;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class LanguageProvider {

    @Context
    private HttpHeaders httpHeaders;

    @Produces
    @Dependent
    @ContentLanguage
    @NonNull
    public Language getContentLanguage() {
        return Optional.ofNullable(httpHeaders.getLanguage())
                .flatMap(this::getSupportedLanguage)
                .orElse(Language.DEFAULT_LANGUAGE);
    }

    @Produces
    @Dependent
    @AcceptedLanguage
    @NonNull
    public Language getAcceptedLanguage() {
        List<Locale> acceptableLanguages = httpHeaders.getAcceptableLanguages();
        return acceptableLanguages.stream()
                .map(this::getSupportedLanguage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(Language.DEFAULT_LANGUAGE);
    }


    private Optional<Language> getSupportedLanguage(Locale locale) {
        return Language.fromCode(locale.getLanguage());
    }
}