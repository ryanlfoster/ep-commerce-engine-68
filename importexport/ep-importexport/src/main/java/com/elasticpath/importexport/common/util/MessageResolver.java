package com.elasticpath.importexport.common.util;

import java.util.Locale;

/**
 * <code>MessageResolver</code> resolves messages with the given code and parameterizes them with the given parameters. 
 */
public interface MessageResolver {

	/**
	 * Resolves the given message.
	 * 
	 * @param message <code>Message</code>
	 * @return resolved message
	 */
	String resolve(final Message message);
	
	/**
	 * Sets resolver's locale.
	 * 
	 * @param locale resolver's locale
	 */
	void setLocale(final Locale locale);

}