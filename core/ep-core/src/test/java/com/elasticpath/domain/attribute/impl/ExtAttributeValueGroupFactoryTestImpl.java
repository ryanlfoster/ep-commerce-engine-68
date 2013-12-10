package com.elasticpath.domain.attribute.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;

/**
 * Faux extension class for testing.
 */
public class ExtAttributeValueGroupFactoryTestImpl extends AttributeValueGroupFactoryImpl {
	/**
	 * Constructor.
	 * @param attributeValueFactory param
	 */
	public ExtAttributeValueGroupFactoryTestImpl(final AttributeValueFactory attributeValueFactory) {
		super(attributeValueFactory);
	}

	/**
	 * Creates the AttributeValueGroup with the given backing map.
	 *
	 * @param attributeMap the map that backs this value group
	 * @return the attribute value group
	 */
	@Override
	public AttributeValueGroup createAttributeValueGroup(final Map<String, AttributeValue> attributeMap) {
		final AttributeValueGroup group = new ExtAttributeValueGroupTestImpl(getAttributeValueFactory());
		if (attributeMap == null) {
			group.setAttributeValueMap(new HashMap<String, AttributeValue>());
		} else {
			group.setAttributeValueMap(attributeMap);
		}

		return group;
	}
}
