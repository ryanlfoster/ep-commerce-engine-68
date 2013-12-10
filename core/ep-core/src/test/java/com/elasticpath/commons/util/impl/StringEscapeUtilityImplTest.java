/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test <code>StringEscapeUtilityImpl</code>.
 */
public class StringEscapeUtilityImplTest {

	private static final String TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES = "\\\"test\\\"";
	private static final String TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES = "\\\"te\\\"st\\\"";

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.StringEscapeUtilityImpl.escapeUnescapedQuotes(String)'.
	 */
	@Test
	public void testEscapeUnescapedQuotes() {

		// test
		assertEquals("Failed to escape string ~ test to test", "test",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("test"));

		// "test
		assertEquals("Failed to escape string ~ \"test to \\\"test", "\\\"test",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\"test"));

		// test"
		assertEquals("Failed to escape string ~ test\" to test\\\"", "test\\\"",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("test\""));

		// "test"
		assertEquals("Failed to escape string ~ \"test\" to \\\"test\\\"", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\"test\""));

		// some "test" string
		assertEquals("Failed to escape string ~ some \"test\" string to some \\\"test\\\" string", "some \\\"test\\\" string",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("some \"test\" string"));

		// some \"test\" string
		assertEquals("Failed to escape string ~ some \\\"test\\\" string to some \\\"test\\\" string", "some \\\"test\\\" string",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("some \\\"test\\\" string"));

		// "te"st"
		assertEquals("Failed to escape string ~ \"te\"st\" to some \\\"te\\\"st\\\"", TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\"te\"st\""));

		// \"test
		assertEquals("Failed to escape string ~ \\\"test to some \\\"test", "\\\"test",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\\\"test"));

		// test\"
		assertEquals("Failed to escape string ~ test\\\" to some test\\\"", "test\\\"",
				StringEscapeUtilityImpl.escapeUnescapedQuotes("test\\\""));

		// \"test\"
		assertEquals("Failed to escape string ~ \\\"test\\\" to some \\\"test\\\"", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes(TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES));

		// \"te\"st\"
		assertEquals("Failed to escape string ~ \\\"te\\\"st\\\" to some \\\"te\\\"st\\\"", TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes(TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES));

		// \"test"
		assertEquals("Failed to escape string ~ \\\"test\" to some \\\"test\\\"", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\\\"test\""));

		// "test\"
		assertEquals("Failed to escape string ~ \"test\\\" to some \\\"test\\\"", TEST_WITH_EVEN_NUMBER_OF_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\"test\\\""));

		// "te\"st"
		assertEquals("Failed to escape string ~ \"te\\\"st\" to some \\\"te\\\"st\\\"", TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\"te\\\"st\""));

		// \"te"st\"
		assertEquals("Failed to escape string ~ \\\"te\"st\\\" to some \\\"te\\\"st\\\"", TEST_WITH_ODD_NUMBER_ESCAPED_QUOTES,
				StringEscapeUtilityImpl.escapeUnescapedQuotes("\\\"te\"st\\\""));
	}
}
