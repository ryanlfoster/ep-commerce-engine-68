/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.attribute;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>AttributeValue</code> is a value object which contains an <code>Attribute</code>
 * value of a domain model, such as <code>Category</code> or <code>Product</code>.
 */
public interface AttributeValue extends Persistable {

	/**
	 * Get the <code>Attribute</code> corresponding to this value.
	 *
	 * @return the <code>Attribute</code> corresponding to this value
	 */
	Attribute getAttribute();

	/**
	 * Get the attribute corresponding to this value.
	 *
	 * @param attribute the <code>Attribute</code> corresponding to this value
	 */
	void setAttribute(Attribute attribute);

	/**
	 * Get the string value of the attribute.
	 *
	 * @return the string value
	 */
	String getStringValue();

	/**
	 * Get the value of the attribute.
	 *
	 * @return the value
	 */
	Object getValue();

	/**
	 * Set the value of the attribute.
	 *
	 * @param value the value to set
	 */
	void setValue(Object value);

	/**
	 * Set the value of the attribute based on the given string value.
	 *
	 * @param stringValue the string value to set
	 * @throws EpBindException in case the given string value is invalid
	 */
	void setStringValue(String stringValue) throws EpBindException;

	/**
	 * Return the <code>AttributeType</code> of this attribute.
	 *
	 * @return the <code>AttributeType</code> of this attribute
	 */
	AttributeType getAttributeType();

	/**
	 * Set the <code>AttributeType</code> of this attribute.
	 *
	 * @param attributeType the attribute type.
	 */
	void setAttributeType(AttributeType attributeType);

	/**
	 * Get the localized attribute key used to get a handle on this attribute value, given the
	 * parent object to which the attribute belongs.
	 * For localized attributes, this is the attribute key plus a "_" and language code. For
	 * non-localized attributes, the language code suffix is ommitted.
	 * @return the attribute key
	 */
	String getLocalizedAttributeKey();

	/**
	 * Set the localized attribute key used to get a handle on this attribute value, given the parent object to which the attribute belongs. For
	 * localized attributes, this is the attribute key plus a "_" and language code. For non-localized attributes, the language code suffix is
	 * ommitted.
	 *
	 * @param localizedAttributeKey the attribute key
	 */
	void setLocalizedAttributeKey(final String localizedAttributeKey);
	
	/**
	 * Return whether the attribute value is defined (null and contains non-whitespace characters).
	 *
	 * @return true if the attribute value is not null and contains non-whitespace characters, else false
	 */
	boolean isDefined();
}
