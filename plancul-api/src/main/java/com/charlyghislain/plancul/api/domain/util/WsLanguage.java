package com.charlyghislain.plancul.api.domain.util;

import java.util.Arrays;
import java.util.Optional;

public enum WsLanguage {
    FRENCH("fr"),
    ENGLISH("en");

    private String code;

    WsLanguage(String code) {
        this.code = code;
    }

    public static Optional<WsLanguage> fromCode(String code) {
        return Arrays.stream(WsLanguage.values())
                .filter(language -> language.getCode().equals(code))
                .findAny();
    }

    public String getCode() {
        return code;
    }
}
