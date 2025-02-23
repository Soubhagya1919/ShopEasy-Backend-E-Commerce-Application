package com.soubhagya.electronic.store.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * An annotation used to validate image names. This annotation can be applied to fields and constructors to enforce
 * validation rules as defined by the associated ImageNameValidator class.
 *
 * Attributes:
 * - message: The error message that will be displayed if the validation fails. Defaults to "Invalid image name !!".
 * - groups: Allows specification of validation groups to which this constraint belongs.
 * - payload: Provides additional information about the validation failure, typically used to carry
 *   metadata information consumed by a validation client.
 *
 * Usage of this annotation will invoke the ImageNameValidator to check for compliance with defined
 * image name constraints, ensuring that the string is not blank.
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageNameValid {
    //error message
    String message() default "Invalid image name !!";

    //represent group of constraints
    Class<?>[] groups() default {};

    //additional information
    Class<? extends Payload>[] payload() default {};
}
