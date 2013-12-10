/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import java.util.Locale;

import com.elasticpath.domain.attribute.Attribute;

/**
 * The AttributeFilter represents the filter on a specified attribute.
 * 
 * @param <T> the type of filter
 */
public interface AttributeFilter<T extends Filter<T>> extends Filter<T> {

	/** property key for the attribute. */ 
	String ATTRIBUTE_PROPERTY = "attribute";
	
	/** property key for the attribute key. */ 
	String ATTRIBUTE_KEY_PROPERTY = "attributeKey";

	/** property key for attribute value(s) (SEO) alias. */
	String ATTRIBUTE_VALUES_ALIAS_PROPERTY = "alias";

	/**
	 * Get the locale for this attribute.
	 * @return the locale
	 */
	Locale getLocale();

	/**
	 * Set the locale for this attribute.
	 * @param locale the locale to set
	 */
	void setLocale(Locale locale);

	/**
	 * Get the attribute key.
	 * @return the attributeKey
	 */
	String getAttributeKey();

	/**
	 * Set the attribute Key.
	 * @param attributeKey the attributeKey to set
	 */
	void setAttributeKey(String attributeKey);

	/**
	 * Get the attribute object.
	 * @return the attribute
	 */
	Attribute getAttribute();

	/**
	 * @param attribute the attribute to set
	 */
	void setAttribute(final Attribute attribute);

	/**
	 * Returns the attribute filter prefix plus the attribute key for this AttributeValueFilter.
	 * @return The String containing the attribute filter prefix plus the attribute key for this filter
	 */
	String getAttributePrefixAndKey();
}
