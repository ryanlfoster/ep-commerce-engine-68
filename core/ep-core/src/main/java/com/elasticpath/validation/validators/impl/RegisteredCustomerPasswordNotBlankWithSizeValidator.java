/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.validation.constraints.RegisteredCustomerPasswordNotBlankWithSize;

/**
 * Validator for checking non blanks and size on a registered customer password.
 */
public class RegisteredCustomerPasswordNotBlankWithSizeValidator implements
		ConstraintValidator<RegisteredCustomerPasswordNotBlankWithSize, Customer> {

	private int min;

	private int max;

	@Override
	public void initialize(final RegisteredCustomerPasswordNotBlankWithSize constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the customer is not null, and if the customer is not anonymous, validates that the password is not blank and is within size
	 * constraints.
	 */
	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		boolean valid = true;

		boolean anonymous = customer.isAnonymous();
		if (!anonymous) {
			String password = customer.getClearTextPassword();
			valid = checkBlank(password, context);
			if (valid) {
				valid = checkSize(password, context);
			}
		}

		return valid;
	}

	private boolean checkBlank(final String password, final ConstraintValidatorContext context) {
		boolean valid = true;
		if (StringUtils.isBlank(password)) {
			addPasswordConstraintViolation(
					"{com.elasticpath.validation.validators.impl.RegisteredCustomerPasswordNotBlankWithSizeValidator.blank.message}",
					context);
			valid = false;
		}
		return valid;
	}

	private boolean checkSize(final String password, final ConstraintValidatorContext context) {
		boolean valid = true;
		if ((password.length() < min) || (password.length() > max)) {
			addPasswordConstraintViolation(
					"{com.elasticpath.validation.validators.impl.RegisteredCustomerPasswordNotBlankWithSizeValidator.size.message}",
					context);
			valid = false;
		}
		return valid;
	}

	private void addPasswordConstraintViolation(final String message, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addNode("password").addConstraintViolation();
	}
}