package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Mock extension class.
 */
public class ExtAttributeValueFactoryTestImpl extends AbstractAttributeValueFactoryImpl {

	private static final long serialVersionUID = 670L;

	/**
	 * Creates an appropriate AttributeValue for the given attribute and localizedAttributeKey.
	 *
	 * @param attribute             the metadata object for this attributeValue
	 * @param localizedAttributeKey the locale-specific attribute key for this value
	 * @return the attribute value
	 */
	@Override
	public AttributeValue createAttributeValue(final Attribute attribute, final String localizedAttributeKey) {
		AttributeValue value = new ExtAttributeValueTestImpl();
		super.populateAttributeValue(value, attribute, localizedAttributeKey);

		return value;
	}
}
