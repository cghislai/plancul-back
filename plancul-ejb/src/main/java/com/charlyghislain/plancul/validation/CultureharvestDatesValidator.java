package com.charlyghislain.plancul.validation;

import com.charlyghislain.plancul.domain.Culture;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CultureharvestDatesValidator implements ConstraintValidator<ValidHarvestDates, Culture> {

    @Override
    public void initialize(ValidHarvestDates validGerminationDate) {
    }

    @Override
    public boolean isValid(Culture culture, ConstraintValidatorContext constraintValidatorContext) {
        if (culture == null) {
            return true;
        }
        LocalDate sowingDate = culture.getSowingDate();
        LocalDate germinationDate = culture.getGerminationDate();
        LocalDate firstHarvestDate = culture.getFirstHarvestDate();
        LocalDate lastHarvestDate = culture.getLastHarvestDate();
        if (sowingDate == null
                || germinationDate == null
                || firstHarvestDate == null
                || lastHarvestDate == null) {
            return false;
        }
        if (!firstHarvestDate.isAfter(germinationDate)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must start after germination")
                    .addPropertyNode("firstHarvestDate")
                    .addConstraintViolation();
            return false;
        }
        if (!firstHarvestDate.isAfter(sowingDate)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must start after sowing")
                    .addPropertyNode("firstHarvestDate")
                    .addConstraintViolation();
            return false;
        }
        if (firstHarvestDate.isAfter(lastHarvestDate)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must start before end date")
                    .addPropertyNode("firstHarvestDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
