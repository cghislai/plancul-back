package com.charlyghislain.plancul.domain.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PasswordValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "com.charlyghislain.plancul.validation.ValidPassword.message";
    String lengthMessage() default "com.charlyghislain.plancul.validation.ValidPassword.length.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
