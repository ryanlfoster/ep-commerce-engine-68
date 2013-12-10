package com.elasticpath.validation.domain;

import java.util.Locale;

/**
 * Stores information on a single error that occured during validation process.
 */
public interface ValidationError {
	
	/**
	 * @return raw message provided during validation not suitable for UI level.
	 */
	String getMessage();
	
	/**
	 * @param locale the locale for which to generate message.
	 * @return human readable localized message suitable for UI.
	 */
	String getMessage(Locale locale);

}
