package com.charlyghislain.plancul.validation;

import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CultureGerminationDateValidator implements ConstraintValidator<ValidGerminationDate, Culture> {

    @Override
    public void initialize(ValidGerminationDate validGerminationDate) {
    }

    @Override
    public boolean isValid(Culture culture, ConstraintValidatorContext constraintValidatorContext) {
        if (culture == null) {
            return true;
        }
        LocalDate sowingDate = culture.getSowingDate();
        LocalDate germinationDate = culture.getGerminationDate();
        if (sowingDate == null || germinationDate == null) {
            return false;
        }
        boolean isAfterSowing = germinationDate.isAfter(sowingDate);
        if (!isAfterSowing) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be before germination")
                    .addPropertyNode("sowingDate")
                    .addConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be after sowing")
                    .addPropertyNode("germinationDate")
                    .addConstraintViolation();
            return false;
        }

        boolean isBeforeTransplanting = culture.getCultureNursing()
                .map(CultureNursing::getDayDuration)
                .map(sowingDate::plusDays)
                .map(germinationDate::isBefore)
                .orElse(true);
        if (!isBeforeTransplanting) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must last until germination")
                    .addPropertyNode("cultureNursing")
                    .addPropertyNode("dayDuration")
                    .addConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be before transplatation")
                    .addPropertyNode("germinationDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
