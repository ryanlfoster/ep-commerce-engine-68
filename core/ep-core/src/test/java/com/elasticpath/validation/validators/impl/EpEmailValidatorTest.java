package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link EpEmailValidator}.
 */
public class EpEmailValidatorTest {

	private EpEmailValidator validator;
	private boolean emailValid;

	/** Test initialization. */
	@Before
	public void initialize() {
		validator = new EpEmailValidator() {
			@Override
			protected boolean isEmailValid(final String value, final ConstraintValidatorContext context) {
				return emailValid;
			}
		};
	}

	/** Our internal email validator tells us it is valid. */
	@Test
	public void testValidEmail() {
		emailValid = true;
		assertTrue(validator.isValid("some string that looks like an email", null));
	}

	/** Our internal email validator tells us it is not valid. */
	@Test
	public void testInvalidEmail() {
		emailValid = false;
		assertFalse(validator.isValid("some invalid email string", null));
	}
}
