package com.elasticpath.commons.util;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.domain.attribute.AttributeGroupAttribute;

/**
 * The comparator which sorts the AttributeGroupAttribute object list by order value.
 */
public class AttributeComparator implements Comparator<AttributeGroupAttribute>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(final AttributeGroupAttribute attr0, final AttributeGroupAttribute attr1) {
		if (attr0.getOrdering() == attr1.getOrdering()) {
			return attr0.getAttribute().getKey().compareTo(attr1.getAttribute().getKey());
		}
		return attr0.getOrdering() - attr1.getOrdering();
	}
}
