/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.commons.util.impl;

/**
 * Another StringUtils helper class for handling special escape logic.
 */
public final class StringEscapeUtilityImpl {

	private static final char QUOTE_CHAR = '"';

	private static final char ESCAPE_CHAR = '\\';

	private StringEscapeUtilityImpl() {
	}

	/**
	 * Escapes quotes in a String that haven't already been escaped.
	 *
	 * @param original the original string.
	 * @return the end of the day.
	 */
	public static String escapeUnescapedQuotes(final String original) {
		StringBuffer escapedString = new StringBuffer("");
		char current;
		char previous = '\u0000';
		for (int i = 0; i < original.length(); i++) {
			current = original.charAt(i);
			if (current == QUOTE_CHAR && previous != ESCAPE_CHAR) {
				escapedString.append(ESCAPE_CHAR);
			}
			escapedString.append(current);
			previous = current;
		}

		return escapedString.toString();
	}
} 
