package com.elasticpath.domain.attribute.impl;

import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;

/**
 * Faux extension class for testing purposes.
 */
public class ExtAttributeValueGroupTestImpl extends AttributeValueGroupImpl implements AttributeValueGroup {
	private static final long serialVersionUID = -846800546123421256L;

	/**
	 * Faux extension class constructor.
	 * @param attributeValueFactory the factory
	 */
	public ExtAttributeValueGroupTestImpl(final AttributeValueFactory attributeValueFactory) {
		super(attributeValueFactory);
	}
}
