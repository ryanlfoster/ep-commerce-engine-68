/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.service.customer.CustomerService;

/**
 * Test class for {@link CustomerUsernameUserIdModeEmailValidator}.
 */
public class CustomerUsernameUserIdModeEmailValidatorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CustomerUsernameUserIdModeEmailValidator validator;

	private CustomerService customerService;

	private ConstraintValidatorContext validatorContext;

	private static final String USERNAME = "hferents@redhat.com";

	/** Test initialization. */
	@Before
	public void setUp() {
		validator = new CustomerUsernameUserIdModeEmailValidator();

		customerService = context.mock(CustomerService.class);
		validator.setCustomerService(customerService);

		validatorContext = context.mock(ConstraintValidatorContext.class);
		context.checking(new Expectations() {
			{
				allowing(validatorContext);
			}
		});
	}

	/**
	 * Should fail without a customer service defined.
	 */
	@Test(expected = NullPointerException.class)
	public void testNoService() {
		validator.setCustomerService(null);
		validator.isValid(null, null);
	}

	/**
	 * Validation should pass without caring about the format when the userIdMode is set to INDEPENDANT_EMAIL_AND_USER_ID_MODE[3].
	 */
	@Test
	public void testValidationPassesWhenUsingIndependantEmailAndUserIdMode() {
		shouldHaveUserIdMode(WebConstants.INDEPENDANT_EMAIL_AND_USER_ID_MODE);
		assertTrue("Validation should not fail when not using USE_EMAIL_AS_USER_ID_MODE.",
				validator.isValid("NON_EMAIL_BASED_USER_ID", validatorContext));
	}

	/**
	 * Test validation when using USE_EMAIL_AS_USER_ID_MODE with a valid email address.
	 */
	@Test
	public void testValidationWhenUsingEmailAsUserIdModeWithAValidEmail() {
		shouldHaveUserIdMode(WebConstants.USE_EMAIL_AS_USER_ID_MODE);
		assertTrue("Validation should pass in USE_EMAIL_AS_USER_ID_MODE with a valid email address.", validator.isValid(USERNAME, validatorContext));
	}

	/**
	 * Test validation when using USE_EMAIL_AS_USER_ID_MODE with an invalid email address.
	 */
	@Test
	public void testValidationWhenUsingEmailAsUserIdModeWithAnInvalidEmail() {
		shouldHaveUserIdMode(WebConstants.USE_EMAIL_AS_USER_ID_MODE);
		assertFalse("Validation should fail in USE_EMAIL_AS_USER_ID_MODE with an invalid email address.",
				validator.isValid("Hardy", validatorContext));
	}

	/**
	 * Test validation when using GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with a valid email address.
	 */
	@Test
	public void testValidationWhenUsingGenerateUniquePermanentUserIdModeWithAValidEmail() {
		shouldHaveUserIdMode(WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE);
		assertTrue("Validation should pass in GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with a valid email address.",
				validator.isValid(USERNAME, validatorContext));
	}

	/**
	 * Test validation when using GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with an invalid email address.
	 */
	@Test
	public void testValidationWhenUsingGenerateUniquePermanentUserIdModeWithAnInvalidEmail() {
		shouldHaveUserIdMode(WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE);
		assertFalse("Validation should fail in GENERATE_UNIQUE_PERMANENT_USER_ID_MODE with an invalid email address.",
				validator.isValid("Hardy", validatorContext));
	}

	private void shouldHaveUserIdMode(final int userIdMode) {
		context.checking(new Expectations() {
			{
				oneOf(customerService).getUserIdMode();
				will(returnValue(userIdMode));
			}
		});
	}

}
