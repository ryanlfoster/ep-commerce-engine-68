/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception for when a required attributeValue is missing.
 */
public class AttributeValueIsRequiredException extends EpServiceException {
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private final Set<String> attributesThatAreMissingValues;

	/**
	 * Default constructor.
	 *
	 * @param attributesThatAreMissingValues - the map of missing attributes
	 */
	public AttributeValueIsRequiredException(final Set<String> attributesThatAreMissingValues) {
		super("Not all required attributes have values.");
		
		this.attributesThatAreMissingValues = attributesThatAreMissingValues;
	}

	/**
	 * Returns the required attributes that don't have a value.
	 * The format is:
	 *	"attribute name - locale.getDisplayName()" or "attribute name" if the attribute is not locale dependent.
	 *
	 * @return map of required attributes that don't have a value
	 */
	public  Set<String> getAttributes() {
		return this.attributesThatAreMissingValues;
	}
	
	/**
	 * Gets the attributes as a complete strings.
	 *
	 * @param separator - to separate the attributes for display
	 * @return a string with all attributes that are missing values
	 */
	public String getAttributesAsString(final String separator) {
		return StringUtils.join(this.attributesThatAreMissingValues, separator);
		
	}

}
