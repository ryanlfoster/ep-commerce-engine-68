package com.elasticpath.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.elasticpath.validation.validators.impl.NotBlankValidatorForString;

/**
 * A constraint that checks whether a field is blank or not.
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = NotBlankValidatorForString.class)
@Documented
public @interface NotBlank {
	/** Constraint violation message. */
	String message() default "{com.elasticpath.validation.constraints.notBlank}";

	/** Groups associated to this constraint. */
	Class<?>[] groups() default { };

	/** Payload for the constraint. */
	Class<? extends Payload>[] payload() default { };
}
