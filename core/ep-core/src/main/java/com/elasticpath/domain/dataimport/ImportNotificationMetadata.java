/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

/**
 * Metadata for an import notification.
 */
public interface ImportNotificationMetadata {

	
	/**
	 * Sets the metadata key.
	 * 
	 * @param key the key name
	 */
	void setKey(String key);
	
	/**
	 * Gets the metadata key.
	 * 
	 * @return the key name
	 */
	String getKey();
	
	/**
	 * Sets the value.
	 * 
	 * @param value the key value
	 */
	void setValue(String value);
	
	/**
	 * Gets the key value.
	 * 
	 * @return the value
	 */
	String getValue();
	
}
