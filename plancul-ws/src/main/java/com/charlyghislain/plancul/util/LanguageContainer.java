package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.domain.i18n.Language;

// Allows CDI profification of Language
public class LanguageContainer {

    private final Language language;
    private final static Language DEFAULT_LANGUAGE = Language.ENGLISH;

    public LanguageContainer() {
        this.language = DEFAULT_LANGUAGE;
    }

    public LanguageContainer(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

}
