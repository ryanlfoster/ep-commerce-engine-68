/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.util.cryptotool;

/**
 * Interface for different types of string filter.
 */
public interface StringFilter {

	/**
	 * Apply some transformation to a String, return the new value.
	 * 
	 * @param str the string to transform
	 * @return the new string
	 */
	String applyTo(String str);
}
