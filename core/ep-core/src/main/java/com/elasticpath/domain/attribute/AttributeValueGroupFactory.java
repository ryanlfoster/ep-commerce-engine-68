package com.elasticpath.domain.attribute;

import java.util.Map;

/**
 * Interface for factories that create AttributeValueGroup objects.  Unlike most EP interfaces, the implementation
 * is not specified in spring.  If you want to change the implementing class, then you need to extend the domain objects
 * which use it.
 */
public interface AttributeValueGroupFactory {

	/**
	 * Creates the AttributeValueGroup with an empty backing map.
	 * @return a new AttributeValueGroup
	 */
	AttributeValueGroup createAttributeValueGroup();

	/**
	 * Creates the AttributeValueGroup with the given backing map.
	 * @param attributeMap the map that backs this value group
	 * @return a new AttributeValueGroup
	 */
	AttributeValueGroup createAttributeValueGroup(Map<String, AttributeValue> attributeMap);
}
