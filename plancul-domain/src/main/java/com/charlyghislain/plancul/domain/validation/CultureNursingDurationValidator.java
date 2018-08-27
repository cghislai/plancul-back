package com.charlyghislain.plancul.domain.validation;

import com.charlyghislain.plancul.domain.Culture;
import com.charlyghislain.plancul.domain.CultureNursing;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class CultureNursingDurationValidator implements ConstraintValidator<ValidNursingDuration, Culture> {

    @Override
    public void initialize(ValidNursingDuration validNursingDuration) {
    }

    @Override
    public boolean isValid(Culture culture, ConstraintValidatorContext constraintValidatorContext) {
        if (culture == null) {
            return true;
        }
        Optional<CultureNursing> cultureNursingOptional = culture.getCultureNursing();
        if (!cultureNursingOptional.isPresent()) {
            return true;
        }
        CultureNursing cultureNursing = cultureNursingOptional.orElseThrow(IllegalStateException::new);

        int nursingDuration = cultureNursing.getDayDuration();
        int daysUntilGermination = culture.getDaysUntilGermination();

        if (nursingDuration <= daysUntilGermination) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be less than nursing duration") //TODO i18n
                    .addPropertyNode("daysUntilGermination")
                    .addConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be more than germination duration")
                    .addPropertyNode("cultureNursing")
                    .addPropertyNode("dayDuration")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
