/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.validator.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the methods of {@link EpCreditCardType}.
 */
public class EpCreditCardTypeTest {

	
	private EpCreditCardType creditCardType;

	@Before
	public void setUp() throws Exception {
		creditCardType = new EpCreditCardType();
	}

	/**
	 * Tests that setPrefixes() parses properly a string of comma separated values.
	 */
	@Test
	public void testSetPrefixes() {
		String prefixes = " 10, 20 , 30,40 , 50 ";
		creditCardType.setPrefixes(prefixes);
		creditCardType.setSupportedLengths("5");
		assertTrue(creditCardType.matches("10222"));
		assertTrue(creditCardType.matches("20222"));
		assertTrue(creditCardType.matches("30222"));
		assertTrue(creditCardType.matches("40222"));
		assertTrue(creditCardType.matches("50222"));
		
		assertFalse(creditCardType.matches("23222"));

	}
	
	/**
	 * Tests that setSupportedLengths() parses properly a string of comma separated values.
	 */
	@Test
	public void testSetSupportedLengths() {
		String prefixes = " 10, 20 , 30,40 , 50 ";
		creditCardType.setPrefixes(prefixes);
		creditCardType.setSupportedLengths("5");

		assertTrue(creditCardType.matches("10123"));
		assertFalse(creditCardType.matches("10"));
		
		creditCardType.setSupportedLengths(" 3 , 4,5, 6 ");

		assertTrue(creditCardType.matches("1013"));
		assertTrue(creditCardType.matches("101344"));
		assertFalse(creditCardType.matches("10"));

	}

}
