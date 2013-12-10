package com.elasticpath.commons.util;

import java.util.Locale;

/**
 * Message source cache interface. 
 * 
 * Initial implementation for multi-store resource loading. See <code>StoreThemeMessageSource</code>.
 * 
 * @author Jaco van Niekerk
 */
public interface MessageSourceCache {

	/**
	 * Method to add a property to the cache.
	 * 
	 * @param themeCode The theme this store property is associated with.
	 * @param storeCode The store this property is associated with.
	 * @param propertyKey The unique property key.
	 * @param propertyValue The property value.
	 * @param locale The locale of the property value, such as 'en' and 'fr'.
	 */
	void addProperty(final String themeCode, final String storeCode, 
			final String propertyKey, final String propertyValue, final Locale locale);
	
	/**
	 * Returns the property value from the cache.
	 * 
	 * @param themeCode The theme this store property is associated with. 
	 * @param storeCode The store this property is associated with.
	 * @param propertyKey The unique property key.
	 * @param locale The locale of the property value, such as 'en' and 'fr'.
	 * @return The property value. Null if it does not exist.
	 */
	String getProperty(final String themeCode, 
			final String storeCode, final String propertyKey, final Locale locale);
}
