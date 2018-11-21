package com.charlyghislain.plancul.astronomy.client.almanac.provider;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Provider
public class LocalDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (LocalDateTime.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new LocalDateTimeConverter();
        } else {
            return null;
        }
    }
}
