/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support;

import com.elasticpath.domain.attribute.Attribute;

/**
 * Creates criterion for querying the persistence layer for distinct
 * lists of attribute values.
 *
 */
public interface DistinctAttributeValueCriterion {

	/**
	 * Creates a criterion String for querying the persistence layer for a distinct
	 * list of values that are present for a given <code>AttributeValue</code>.
	 *
	 * @param attribute the <code>Attribute</code> whose values are to be returned
	 * @return a distinct list of attribute values for that attribute
	 */
	String getDistinctAttributeValueCriterion(final Attribute attribute);

	/**
	 * Creates a criterion String for querying the persistence layer for a distinct
	 * list of values that are present for a given <code>AttributeValue</code>.
	 * These search all the multi values for short text type.
	 *
	 * @param attribute the <code>Attribute</code> whose values are to be returned
	 * @return a distinct list of attribute values for that attribute
	 */
	String getDistinctAttributeMultiValueCriterion(final Attribute attribute);

}
