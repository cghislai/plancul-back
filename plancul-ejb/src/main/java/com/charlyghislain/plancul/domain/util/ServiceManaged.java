package com.charlyghislain.plancul.domain.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates this field value is managed by the backend service.
 * Provided values will be overriden by the backend. They should be considered
 * read-only by the frontends.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ServiceManaged {
}
