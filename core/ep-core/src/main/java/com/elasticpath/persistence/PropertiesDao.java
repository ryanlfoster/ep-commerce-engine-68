/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence;

import java.util.Map;
import java.util.Properties;

/**
 * Reads data from properties files.
 *
 * @deprecated Use {@link com.elasticpath.persistence.dao.impl.PropertiesDaoLoaderFactoryImpl}
 */
@Deprecated
public interface PropertiesDao {

	/**
	 * Returns a properties map where the keys are properties file names
	 * and the values are Properties objects.
	 * @return the Map of properties file names to Properties objects
	 */
	Map<String, Properties> loadProperties();

	/**
	 * Sets the properties location. This location is searched last for property files if none have been previously
	 * stored under {@link #setStoredPropertiesLocation(String)}.
	 *
	 * Implementations should rely on the default {@code WEB-INF/conf/resources} if a location has not been set.
	 *
	 * @param propertiesLocation the directory containing properties files
	 * @see #setStoredPropertiesLocation(String)
	 */
	void setPropertiesLocation(String propertiesLocation);

	/**
	 * Sets the location where property files should be stored on {@link #storePropertiesFile(Properties, String)}.
	 * Stored property files will override those found via {@link #setPropertiesLocation(String)}.
	 *
	 * Implementations should rely on the default {@code WEB-INF/conf/resources} if a location has not been set.
	 *
	 * @param storedPropertiesLocation the directory containing properties files
	 * @see #setPropertiesLocation(String)
	 */
	void setStoredPropertiesLocation(String storedPropertiesLocation);

	/**
	 * Get the properties of the given name. The name may or may not have the properties location.
	 *
	 * @param propertyFile the name of the properties file
	 * @return the properties object of the provided file
	 * @see #setStoredPropertiesLocation(String)
	 * @see #setPropertiesLocation(String)
	 */
	Properties getPropertiesFile(String propertyFile);

	/**
	 * Persist the properties object to file, takes filename with properties extension or without extension.
	 *
	 * @param property the properties object to store to file
	 * @param propertyFileName the filename for the properties
	 */
	void storePropertiesFile(final Properties property, final String propertyFileName);

}
