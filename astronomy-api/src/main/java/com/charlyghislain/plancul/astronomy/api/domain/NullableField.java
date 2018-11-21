package com.charlyghislain.plancul.astronomy.api.domain;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface NullableField {
}
