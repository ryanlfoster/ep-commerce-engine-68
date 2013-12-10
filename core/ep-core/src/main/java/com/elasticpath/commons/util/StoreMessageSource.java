package com.elasticpath.commons.util;

import java.util.Locale;

/**
 * Acts as a localized message source for store messages that may be theme-specific.
 */
public interface StoreMessageSource {

	/**
	 * Finds store-theme-specific messages that don't take arguments.
	 *
     *
	 * @param storeCode the store's code
	 * @param themeCode the store's theme's code
	 * @param messageCode the message code
	 * @param locale the locale in which the message should be rendered
	 * @return the message resolved from the code
	 */
	String getMessage(String storeCode, String themeCode, String messageCode, Locale locale);

	/**
	 * Finds store-theme-specific messages that don't take arguments.
	 * The themeCode will be resolved by using the storeCode
     *
	 * @param storeCode the store's code
	 * @param messageCode the message code
	 * @param locale the locale in which the message should be rendered
	 * @return the message resolved from the code
	 */
	String getMessage(String storeCode,  String messageCode, Locale locale);
}
