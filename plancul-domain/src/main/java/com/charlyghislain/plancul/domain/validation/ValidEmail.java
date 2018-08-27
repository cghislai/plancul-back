package com.charlyghislain.plancul.domain.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {EmailValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "com.charlyghislain.plancul.validation.ValidEmail.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
