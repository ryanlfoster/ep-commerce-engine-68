/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a localized property value.
 */
public interface LocalizedPropertyValue extends Persistable {

	/**
	 * Get the value.
	 * @return the value
	 */
	String getValue();

	/**
	 * Set the value.
	 *
	 * @param value the value
	 */
	void setValue(final String value);

	/**
	 * Get the localized property key.
	 *
	 * @return the key
	 */
	String getLocalizedPropertyKey();

	/**
	 * Set the localized property key.
	 *
	 * @param localizedPropertyKey the key
	 */
	void setLocalizedPropertyKey(final String localizedPropertyKey);

}