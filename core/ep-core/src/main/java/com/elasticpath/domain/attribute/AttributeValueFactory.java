package com.elasticpath.domain.attribute;

import java.io.Serializable;

/**
 * Factory that creates AttributeValue objects.  Unlike most interfaces in EP, the implementation for this
 * interface is not specified through spring.  If you want to override the default implementation, you need
 * to create an extension class for the appropriate domain object (ProductImpl, ProductSkuImpl, CategoryImpl).
 *
 * This avoids problems with domain classes and spring.
 */
public interface AttributeValueFactory extends Serializable {

	/**
	 * Creates an appropriate AttributeValue for the given attribute and localizedAttributeKey.
	 *
	 * @param attribute the metadata object for this attributeValue
	 * @param localizedAttributeKey the locale-specific attribute key for this value
	 * @return the attribute value
	 */
	AttributeValue createAttributeValue(final Attribute attribute, final String localizedAttributeKey);

}
