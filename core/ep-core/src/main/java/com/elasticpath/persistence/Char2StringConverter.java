/**
 * 
 */
package com.elasticpath.persistence;

import org.apache.commons.lang.CharUtils;

/**
 * A char2string and string2char converter.
 *
 */
public final class Char2StringConverter {

	private static final char DEFAULT_TEXT_QUALIFIER = '"';
	private static final char DEFAULT_COLUMN_DELIMITER = ' ';

	/**
	 * Default constructor.
	 */
	private Char2StringConverter() { 
		super();
	}
	
	/**
	 * A char to string converter.
	 * @param value original char value
	 * @return a String converted value
	 */
	public static String char2String(final char value) {
		return CharUtils.toString(value); 
	}
	
	/**
	 * A String to char converter for ImportJob.textQualifier.
	 * @param value original String value
	 * @return a char converted value
	 */
	public static char string2CharForTextQualifier(final String value) {
		char result;
		try {
			result = CharUtils.toChar(value);
		} catch (final Exception e) {
			result = DEFAULT_TEXT_QUALIFIER;
		}
		return result;
	}

	/**
	 * A String to char converter for ImportJob.columnDelimiter.
	 * @param value original String value
	 * @return a char converted value
	 */
	public static char string2CharForColumnDelimiter(final String value) {
		char result;
		try {
			result = CharUtils.toChar(value);
		} catch (final Exception e) {
			result = DEFAULT_COLUMN_DELIMITER;
		}
		return result;
	}
}
