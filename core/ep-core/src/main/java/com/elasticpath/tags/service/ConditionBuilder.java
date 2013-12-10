/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.Condition;

/**
 * Builds proper {@link Condition} by looking up the {@link TagDefinition} and its data type.
 */
public interface ConditionBuilder {

	/**
	 * Builds the {@link Condition} object by using given arguments. Downcast the value according to {@link TagDefinition}
	 * data type.
	 * 
	 * @param tagDefinitionName the name of the {@link TagDefinition}
	 * @param operator the operator
	 * @param value the value of the condition - right operand
	 * @return the built Condition
	 * @throws IllegalArgumentException if any of the arguments is null or if java type cannot
	 *                                  be looked up for the tag definition or if value provided
	 *                                  does not match the java type in tag definition
	 */
	Condition build(String tagDefinitionName, String operator, Object value);
}
