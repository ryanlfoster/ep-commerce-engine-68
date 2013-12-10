package com.elasticpath.persistence.dao;

import java.util.Properties;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a resource which should be aware of properties during its lifecycle. These properties should be set at
 * object initialization.
 */
public interface PropertyLoaderAware extends EpDomain {

	/**
	 * Sets the properties the object should use for its lifecycle.
	 * 
	 * @param properties properties to use
	 */
	void setInitializingProperties(Properties properties);

	/**
	 * Gets the property defined by {@code name}.
	 * 
	 * @param name property name
	 * @return property for {@code name} or {@code null}
	 */
	String getProperty(String name);
}
