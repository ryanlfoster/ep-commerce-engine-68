/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.validator.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test <code>EpEmailValidator</code>.
 */
public class EpEmailValidatorTest {

	private static final EpEmailValidator EP_EMAIL_VALIDATOR = EpEmailValidator.getInstance();

	/**
	 * Test method for 'com.elasticpath.commons.validator.impl.EpEmailValidator.isValid(email)'.
	 */
	@Test
	public void testValidateEmail() {
		final String[] invalidEmail = {
				"Wenjie Liu@elasticpath.com", 
				".wenjie.liu.@elasticpath.com",
				"wenjie.liu.@elasticpath.com", 
				"wenjie.liu@ep'.com", 
//				"wenjie.liu@255.255.255.2551",
				"john@aol...com",
				null
				};
		final String[] validEmail = {
				"\"Wenjie Liu\"@elasticpath.com", 
				"!#$%&'*+-/=?^_`{|}~@elasticpath.com",
				"wenjie.liu@elasticpath.com", 
//				"wenjie.liu@255.255.255.255", 
				"newdomain@elasticpath.info", 
				"mydomain@test.bg", 
//				"test@1.1.20.1", 
				"test@344.subdomain1.subdomain2.1.com",
				"us@shoes.store", 
				"agency@ttt.travel", 
				"test@louvre.museum" 
				};

		for (int i = 0; i < invalidEmail.length; i++) {
			assertFalse("Validated an email that should not be valid:" + invalidEmail[i], EP_EMAIL_VALIDATOR.isValid(invalidEmail[i]));
		}

		for (int i = 0; i < validEmail.length; i++) {
			assertTrue("Failed to validate email: " + validEmail[i], EP_EMAIL_VALIDATOR.isValid(validEmail[i]));
		}
		
		
	}
}
