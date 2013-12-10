/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.bval.constraints.EmailValidator;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.validation.constraints.CustomerUsernameUserIdModeEmail;

/**
 * Validator for checking the required state of a Customer username.
 */
public class CustomerUsernameUserIdModeEmailValidator implements ConstraintValidator<CustomerUsernameUserIdModeEmail, String> {

	private CustomerService customerService;

	@Override
	public void initialize(final CustomerUsernameUserIdModeEmail constraintAnnotation) {
		// do nothing
	}

	/**
	 * {@inheritDoc} <br>
	 * Validates that the username conforms to the {@link EmailValidator} when user ID mode is either in
	 * {@link WebConstants#USE_EMAIL_AS_USER_ID_MODE} or {@link WebConstants#GENERATE_UNIQUE_PERMANENT_USER_ID_MODE}.
	 */
	@Override
	public boolean isValid(final String username, final ConstraintValidatorContext context) {
		boolean valid = true;
		int userIdMode = getCustomerService().getUserIdMode();

		if (userIdMode == WebConstants.USE_EMAIL_AS_USER_ID_MODE || userIdMode == WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE) {
			valid = new EmailValidator().isValid(username, context);
		}

		return valid;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

}
