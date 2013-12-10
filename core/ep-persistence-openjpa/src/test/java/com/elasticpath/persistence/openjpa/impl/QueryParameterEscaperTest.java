package com.elasticpath.persistence.openjpa.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the escape methods of {@link QueryParameterEscaper}. 
 */
@SuppressWarnings("PMD.UselessOverridingMethod")
public class QueryParameterEscaperTest {

	/**
	 * Test extension for {@link QueryParameterEscaper}.
	 */
	private final class TestQueryParameterEscaper extends QueryParameterEscaperImpl {
		@Override
		public String escapeQuotes(final String param) {
			return super.escapeQuotes(param);
		}
	}

	private final TestQueryParameterEscaper escaper = new TestQueryParameterEscaper();
	
	/**
	 * Tests escaping single quotes: ' => ''.
	 */
	@Test
	public void testEscapeQuotes() {
		String result = escaper.escapeQuotes("test' string'");
		assertEquals("The single quote should be escaped", "test'' string''", result);
		result = escaper.escapeQuotes("test''");
		assertEquals("The single quote should be escaped", "test''''", result);
	}
	
	/**
	 * Tests escaping strings. Now it escapes only single quotes. Update the test if escaping rules change.
	 */
	@Test
	public void testEscapeString() {
		String noEscapeString = "no escapeString";
		String result = escaper.escapeStringParameter(noEscapeString);
		assertEquals("This string shouldn't be escaped", noEscapeString, result);
		String escapeString = "string' to'' escape";
		result = escaper.escapeStringParameter(escapeString);
		assertEquals("This string should be escaped", "string'' to'''' escape", result);
	}
	
}
