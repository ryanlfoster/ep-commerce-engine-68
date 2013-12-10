package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;

/**
 * Abstract helper class for create AttributeValueFactory implementations.
 */
public abstract class AbstractAttributeValueFactoryImpl implements AttributeValueFactory {

	private static final long serialVersionUID = 670L;

	/**
	 * Populates a newly created AttributeValue subclass with the basics...
	 *
	 * @param value the attribute value itself
	 * @param attribute the attribute this value fills
	 * @param localizedAttributeKey the localized key used to access the attribute
	 */
	protected void populateAttributeValue(final AttributeValue value, final Attribute attribute, final String localizedAttributeKey) {
		value.setAttribute(attribute);
		value.setAttributeType(attribute.getAttributeType());
		value.setLocalizedAttributeKey(localizedAttributeKey);
	}
}
