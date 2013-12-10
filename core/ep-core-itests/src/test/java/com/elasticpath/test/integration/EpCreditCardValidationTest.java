/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import com.elasticpath.commons.validator.impl.EpCreditCardValidator;
import com.elasticpath.commons.validator.impl.EpFieldChecks;

/**
 * Tests credit card validation.
 */
public class EpCreditCardValidationTest extends BasicSpringContextTest {

	@Autowired
	private EpCreditCardValidator validator;

	/**
	 * Tests that various card numbers of different types pass through the validation.
	 */
	@Test
	public void testCheckValidCreditCards() {
		new EpFieldChecks().setCreditCardValidator(validator);
		String[] creditCardNumbers = {
				"378282246310005", 		// American Express
				"6011111111111117", 	// Discover
				"4111111111111111", 	// VISA
				"5555555555554444", 	// Mastercard
				"38520000023237",   	// Diners Club 
				"30569309025904",   	// Diners Club
				"3530111333300000", 	// JCB
				"3566002020360505", 	// JCB
				"6304985028090561515", 	// Laser
//				"586824160825533338",	// Maestro
//				"50339619890917"		// Maestro
				};
		
		for (String creditCardNumber : creditCardNumbers) {
			final String bean = creditCardNumber;
				
			Field field = new Field();
			Errors errors = new MapBindingResult(new HashMap<Object, Object>(), "");
			
			ValidatorAction validatorAction = new ValidatorAction();
			assertEquals(new Long(creditCardNumber), EpFieldChecks.validateCreditCard(bean, validatorAction, field, errors));
		}
	}

	/**
	 * Tests that various invalid card numbers do not pass through the validation.
	 */
	@Test
	public void testCheckInvalidCreditCards() {
		new EpFieldChecks().setCreditCardValidator(validator);
		String[] creditCardNumbers = {
				"3434444", 		
				"999999999999999999",
				};
		
		for (String creditCardNumber : creditCardNumbers) {
			final String bean = creditCardNumber;
				
			Field field = new Field();
			field.setProperty("creditCard");  // 'Property' is required to be set - otherwise error reporting fails.
			
			Errors errors = new MapBindingResult(new HashMap<Object, Object>(), "");
			assertFalse(errors.hasErrors());
			assertFalse(errors.hasFieldErrors());
			assertFalse(errors.hasGlobalErrors());
			
			ValidatorAction validatorAction = new ValidatorAction();
			assertNull(EpFieldChecks.validateCreditCard(bean, validatorAction, field, errors));
			
			assertTrue(errors.hasErrors());
		}
	}

	/**
	 * Tests credit card validation with null or empty argument.
	 */
	@Test
	public void testValidatateCreditCardNullArgument() {
		new EpFieldChecks().setCreditCardValidator(validator);
		final String bean = null;
			
		Field field = new Field();
		Errors errors = new MapBindingResult(new HashMap<Object, Object>(), "");
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasFieldErrors());
		assertFalse(errors.hasGlobalErrors());
		
		ValidatorAction validatorAction = new ValidatorAction();
		assertNull(EpFieldChecks.validateCreditCard(bean, validatorAction, field, errors));

		assertFalse(errors.hasErrors());
		assertFalse(errors.hasFieldErrors());
		assertFalse(errors.hasGlobalErrors());
	}

}
