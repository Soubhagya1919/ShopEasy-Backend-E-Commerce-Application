package com.soubhagya.electronic.store.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates image names for compliance with the rules defined by the {@code ImageNameValid} annotation.
 * Implements {@code ConstraintValidator} for performing the actual validation logic.
 *
 * Logs the value being validated for tracking purposes.
 * Ensures the image name is not blank as part of its validation criteria.
 *
 * This validator is typically used in conjunction with fields or constructors annotated with {@code ImageNameValid}
 * to ensure that specified constraints are adhered to within applications involving image name validation.
 */
public class ImageNameValidator implements ConstraintValidator<ImageNameValid, String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        logger.info("Message from isValid : {}", value);
        //logic
        return !value.isBlank();
    }
}
