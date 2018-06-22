package com.charlyghislain.plancul.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CultureGerminationDateValidator.class})
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ValidGerminationDate {
    String message() default "com.charlyghislain.plancul.validation.ValidGerminationDate.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
