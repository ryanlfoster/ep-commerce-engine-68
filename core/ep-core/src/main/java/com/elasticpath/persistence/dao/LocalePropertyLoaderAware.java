package com.elasticpath.persistence.dao;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Extension of {@link PropertyLoaderAware} which expects properties to be localized.
 * <p>
 * When a specific property does not exist for a particular locale, we consult the fallbacks in this order:
 * <ol>
 * <li>locale without the variant</li>
 * <li>locale without the variant or country</li>
 * <li>the default locale defined by the properties setup in {@link PropertyLoaderAware}</li>
 * </ol>
 */
public interface LocalePropertyLoaderAware extends PropertyLoaderAware {

	/**
	 * Sets a map of override properties.
	 * 
	 * @param properties map of override properties
	 */
	void setInitializingLocaleOverideProperties(Map<Locale, Properties> properties);

	/**
	 * Gets a property for the given {@code propertyName} and {@link Locale}. This method always fallback if a property
	 * does not exist for the given {@link Locale}. Use {@link #getProperty(Locale, String, boolean)} if you do not want
	 * fallback.
	 * 
	 * @param locale locale to get the property for
	 * @param propertyName property name
	 * @return property value
	 * @see #getProperty(Locale, String, boolean)
	 */
	String getProperty(Locale locale, String propertyName);

	/**
	 * Gets a property for the given {@code propertyName} and {@link Locale}. This method may optionally fallback using
	 * an additional boolean flag.
	 * 
	 * @param locale locale to get the property for
	 * @param propertyName property name
	 * @param fallback whether to fallback
	 * @return property value
	 * @see #getProperty(Locale, String)
	 */
	String getProperty(Locale locale, String propertyName, boolean fallback);
}
