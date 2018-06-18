package com.charlyghislain.plancul.domain.i18n;

import java.util.Arrays;
import java.util.Optional;

public enum Language {
    FRENCH("fr"),
    ENGLISH("en");

    public static Optional<Language> fromCode(String code) {
        return Arrays.stream(Language.values())
                .filter(language -> language.getCode().equals(code))
                .findAny();
    }

    private String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
