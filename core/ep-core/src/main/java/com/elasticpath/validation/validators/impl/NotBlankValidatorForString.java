package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.elasticpath.validation.constraints.NotBlank;

/**
 * {@link NotBlank} validator for strings.
 */
public class NotBlankValidatorForString implements ConstraintValidator<NotBlank, String> {

	@Override
	public void initialize(final NotBlank constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return value.trim().length() > 0;
	}
}
