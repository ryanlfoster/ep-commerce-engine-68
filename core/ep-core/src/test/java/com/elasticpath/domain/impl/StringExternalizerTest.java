/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.impl;

import static org.junit.Assert.assertEquals;

import com.elasticpath.persistence.api.StringExternalizer;
import org.junit.Test;

/**
 * Tests that the {@link com.elasticpath.settings.domain.impl.StringExternalizer} class works as expected. 
 */
public class StringExternalizerTest {

	private static final String EMPTY_STRING = "";

	/**
	 * Test method for {@link com.elasticpath.settings.domain.impl.StringExternalizer#toExternalForm(java.lang.String)}.
	 */
	@Test
	public void testToExternalForm() {
		String value = "testValue";
		assertEquals("Value should be equal to the one passed in.", value, StringExternalizer.toExternalForm(value));
	}

	/**
	 * Test method for {@link com.elasticpath.settings.domain.impl.StringExternalizer#toInternalForm(java.lang.String)}.
	 */
	@Test
	public void testToInternalForm() {
		String value = "testValue";
		assertEquals("Value should be equal to the one passed in.", value, StringExternalizer.toInternalForm(value));
	}

	/**
	 * Test method for {@link com.elasticpath.settings.domain.impl.StringExternalizer#toInternalForm(java.lang.String)}.
	 */
	@Test
	public void testToInternalFormWithNull() {
		String value = null;
		assertEquals("Value should be an empty string.", EMPTY_STRING, StringExternalizer.toInternalForm(value));
	}

}
