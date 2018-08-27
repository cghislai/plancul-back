package com.charlyghislain.plancul.converter.param;

import com.charlyghislain.plancul.api.domain.util.WsLanguage;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;

@Provider
public class WsLanguageConverter implements ParamConverter<WsLanguage> {

    @Override
    public WsLanguage fromString(String value) {
        return WsLanguage.fromCode(value)
                .orElse(null);
    }

    @Override
    public String toString(WsLanguage value) {
        return value.getCode();
    }
}
