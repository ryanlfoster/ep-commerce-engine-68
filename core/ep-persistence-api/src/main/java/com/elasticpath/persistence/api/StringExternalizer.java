/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.api;

import org.apache.commons.lang.StringUtils;

/**
 * The <code>StringExternalizer</code> class helps to provide null-safe loading of string values into 
 * objects via the persistence framework. Specifically, it uses {@link StringUtils#defaultString(String)} to convert
 * values that are null in the database into empty strings. Oracle databases in particular exhibit a problem wherein
 * empty Strings are converted to nulls, but if JPA knows the field may not be null then it can result in an exception
 * unless this externalizer is used.
 */
public final class StringExternalizer {

	/**
	 * Private constructor since this class should never be instantiated.
	 */
	private StringExternalizer() {
		super();
	}

	/**
	 * Returns the same object that was passed in. No conversion is needed, but method is provided if the persistence framework requires an
	 * externalizer.
	 * 
	 * @param value the value to be externalized
	 * @return the same value
	 */
	public static String toExternalForm(final String value) {
		return value;
	}

	/**
	 * Returns the result of {@link StringUtils#defaultString(String)} on the passed in value.
	 * 
	 * @param value the value to be converted
	 * @return the value passed in or an empty string if the value is null
	 */
	public static String toInternalForm(final String value) {
		return StringUtils.defaultString(value);
	}
}
