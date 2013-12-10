package com.elasticpath.domain.misc;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a group of localized properties. Any DOs can aggregate it to have a group of localized properties. Example: <code>Brand</code>.
 */
public interface LocalizedProperties extends EpDomain {
	/**
	 * Returns the value of the given property and locale. 
	 * If the value of the given locale doesn't exist it will fall back to the value of the system
	 * default locale. 
	 * If the value still doesn't exist, return <code>null</code>.
	 * 
	 * @param propertyName the property name
	 * @param locale the locale
	 * @return the value of the given property and locale, or the default locale, or null 
	 */
	String getValue(String propertyName, Locale locale);

	/**
	 * Gets the value of the given property for the given locale. 
	 * If the value doesn't exist, returns <code>null</code>.
	 * 
	 * @param propertyName the property name
	 * @param locale the locale
	 * @return the value of the given property and locale, or null if it doesn't exist
	 */
	String getValueWithoutFallBack(String propertyName, Locale locale);

	/**
	 * Sets the given value with the given property and locale.
	 * 
	 * @param propertyName the property name
	 * @param locale the locale
	 * @param value the value to set
	 */
	void setValue(String propertyName, Locale locale, String value);

	/**
	 * Returns the localized properties map.
	 * 
	 * @return the localized properties map.
	 */
	Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap();

	/**
	 * Sets the localized properties map.
	 * 
	 * @param map the map to set
	 * @param localizedPropertyValueBean the bean to be used to create new values
	 */
	void setLocalizedPropertiesMap(Map<String, LocalizedPropertyValue> map, String localizedPropertyValueBean);
	
	/**
	 * Get the locale from the key in the map.
	 * 
	 * @param keyInMap the key in the map
	 * @return the locale
	 */
	Locale getLocaleFromKey(String keyInMap);
	
	/**
	 * Get the propertyName from the key in the map.
	 * 
	 * @param keyInMap the key in the map
	 * @return the propertyName
	 */
	String getPropertyNameFromKey(final String keyInMap);
}
