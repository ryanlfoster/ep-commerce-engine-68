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

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.validation.constraints.RegisteredCustomerPasswordNotBlankWithSize;

/**
 * Test class for {@link RegisteredCustomerPasswordNotBlankWithSizeValidator}.
 */
public class RegisteredCustomerPasswordNonBlankWithSizeValidatorTest {

	private static final int MIN = 5;

	private static final int MAX = 8;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private RegisteredCustomerPasswordNotBlankWithSizeValidator validator;

	private ConstraintValidatorContext validatorContext;

	private Customer customer;

	private RegisteredCustomerPasswordNotBlankWithSize passwordAnnotation;

	/** Test initialization. */
	@Before
	public void setUp() {
		customer = context.mock(Customer.class);
		validatorContext = context.mock(ConstraintValidatorContext.class);
		validator = new RegisteredCustomerPasswordNotBlankWithSizeValidator();
		passwordAnnotation = context.mock(RegisteredCustomerPasswordNotBlankWithSize.class);

		context.checking(new Expectations() {
			{
				allowing(validatorContext);
				allowing(passwordAnnotation).min(); will(returnValue(MIN));
				allowing(passwordAnnotation).max(); will(returnValue(MAX));
			}
		});

		validator.initialize(passwordAnnotation);
	}

	/**
	 * Test password validity when a customer is anonymous.
	 */
	@Test
	public void testCustomerIsAnonymous() {
		shouldHaveAnonymousCustomer(true);
		assertTrue("A password is not validated when a customer is anonymous.", validator.isValid(customer, validatorContext));
	}

	/**
	 * Test password validity when a customer is not anonymous with valid password.
	 */
	@Test
	public void testCustomerIsNotAnonymousWithValidPassword() {
		shouldHaveAnonymousCustomer(false);
		shouldHaveCustomerPassword("hello");
		assertTrue("The password should be valid when a customer is not anonymous.", validator.isValid(customer, validatorContext));
	}

	/**
	 * Test password validity when a customer is not anonymous with null password.
	 */
	@Test
	public void testCustomerIsNotAnonymousWithNullPassword() {
		shouldHaveAnonymousCustomer(false);
		shouldHaveCustomerPassword(null);
		assertFalse("The password should not be valid when a customer is not anonymous and a password is not defined.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test password validity when a customer is not anonymous with a whitespace only password.
	 */
	@Test
	public void testCustomerIsNotAnonymousWithWhitespaceOnly() {
		shouldHaveAnonymousCustomer(false);
		shouldHaveCustomerPassword("        ");
		assertFalse("The password should not be valid when a customer is not anonymous and a password is all whitespace.",
				validator.isValid(customer, validatorContext));
	}

	/**
	 * Test password validity when a customer is not anonymous with a password that is too short.
	 */
	@Test
	public void testCustomerIsNotAnonymousWithTooShortPassword() {
		shouldHaveAnonymousCustomer(false);
		shouldHaveCustomerPassword("bad");
		assertFalse("The password should not be valid when a customer is not anonymous"
				+ " and a password is too short.", validator.isValid(customer, validatorContext));
	}

	/**
	 * Test password validity when a customer is not anonymous with a password that exceeds the maximum.
	 */
	@Test
	public void testCustomerIsNotAnonymousWithTooLongPassword() {
		shouldHaveAnonymousCustomer(false);
		shouldHaveCustomerPassword("password_too_long");
		assertFalse("The password should not be valid when a customer is not anonymous"
				+ " and a password is too long.", validator.isValid(customer, validatorContext));
	}

	private void shouldHaveAnonymousCustomer(final boolean anonymous) {
		context.checking(new Expectations() {
			{
				oneOf(customer).isAnonymous();
				will(returnValue(anonymous));
			}
		});
	}

	private void shouldHaveCustomerPassword(final String password) {
		context.checking(new Expectations() {
			{
				oneOf(customer).getClearTextPassword();
				will(returnValue(password));
			}
		});
	}
}
