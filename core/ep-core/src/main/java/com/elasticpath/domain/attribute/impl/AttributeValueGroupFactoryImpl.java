package com.elasticpath.domain.attribute.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.AttributeValueGroupFactory;

/**
 * Standard implementation for the AttributeValueGroup factory class.  Creates a default AttributeValueGroup
 * that uses the AttributeValueFactory passed in the constructor to generate attribute value objects.
 */
public class AttributeValueGroupFactoryImpl implements AttributeValueGroupFactory {
	/** The factory used to construct AttributeValues. */
	private final AttributeValueFactory attributeValueFactory;

	/**
	 * Factory constructor.
	 * @param valueFactory the AttributeValueFactory that will be used to create AttributeValues
	 */
	public AttributeValueGroupFactoryImpl(final AttributeValueFactory valueFactory) {
		this.attributeValueFactory = valueFactory;
	}

	/**
	 * Creates the AttributeValueGroup.
	 * @return a new AttributeValueGroup
	 */
	@Override
	public AttributeValueGroup createAttributeValueGroup() {
		return createAttributeValueGroup(null);
	}

	/**
	 * Creates the AttributeValueGroup with the given backing map.
	 *
	 * @param attributeMap the map that backs this value group
	 * @return a new AttributeValueGroup
	 */
	@Override
	public AttributeValueGroup createAttributeValueGroup(final Map<String, AttributeValue> attributeMap) {
		final AttributeValueGroup group = new AttributeValueGroupImpl(getAttributeValueFactory());
		if (attributeMap == null) {
			group.setAttributeValueMap(new HashMap<String, AttributeValue>());
		} else {
			group.setAttributeValueMap(attributeMap);
		}

		return group;
	}

	protected AttributeValueFactory getAttributeValueFactory() {
		return attributeValueFactory;
	}
}
