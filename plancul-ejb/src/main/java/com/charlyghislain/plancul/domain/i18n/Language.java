package com.charlyghislain.plancul.domain.i18n;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum Language {
    ENGLISH("en", ""),
    FRENCH("fr", ".fr");

    public static Language DEFAULT_LANGUAGE = Language.ENGLISH;

    public static Optional<Language> fromCode(String code) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getCode().equals(code))
                .findAny();
    }

    private String code;
    private String fileSuffix;

    Language(String code, String fileSuffix) {
        this.code = code;
        this.fileSuffix = fileSuffix;
    }

    public String getCode() {
        return code;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(getCode());
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    @Override
    public String toString() {
        return code;
    }
}
