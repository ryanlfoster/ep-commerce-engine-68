/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.PasswordGenerator;


/**
 * Test <code>PasswordGeneratorImpl</code>.
 */
public class PasswordGeneratorImplTest {
	
	private static final String VALID_REGEX = "(?=.*\\d)(?=.*([a-zA-Z])).*";
	
	private PasswordGenerator passwordGenerator;

	@Before
	public void setUp() throws Exception {
		this.passwordGenerator = new PasswordGeneratorImpl();
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.PasswordGeneratorImpl.getPassword()'.
	 */
	@Test
	public void testGetPassword() {
		final String passwd1 = this.passwordGenerator.getPassword();
		assertEquals(PasswordGeneratorImpl.DEFAULT_LENGTH, passwd1.length());

		final String passwd2 = this.passwordGenerator.getPassword();
		assertFalse(passwd1.equals(passwd2));
		
		assertTrue(passwd1.matches(VALID_REGEX));
	}	
}
