package com.elasticpath.commons.beanframework;

import java.util.Locale;

/**
 * Implementations can provide localized messages. 
 */
public interface MessageSource {

	/**
	 * Returns a formatted, localized message.
	 * 
	 * @param code the code to identify the message
	 * @param args arguments that will be expanded into the returned message.
	 * @param defaultMessage a default message to return if no message is 
	 *        associated with <code>code</code>
	 * @param locale the locale to format the message for.
	 * @return a localized, parameter expanded, string.
	 */
	String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale);
	
}
