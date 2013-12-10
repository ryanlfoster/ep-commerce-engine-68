package com.elasticpath.importexport.util;

import java.util.Map;

/**
 * Provides helper methods to access Application Properties.
 */
public interface ApplicationPropertiesHelper {

	/**
	 * Return name and values of those properties that start with the given string.
	 * 
	 * @param prefix the prefix of the name
	 * @return those properties starts with the given string
	 */
	Map<String, String> getPropertiesWithNameStartsWith(String prefix);

}
