package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Service that validates the conditions using validation service.
 */
public interface ConditionValidationFacade {
	
	/**
	 * @param condition the condition to validate
	 * @return result of the validation (if condition is null the result is invalid).
	 * @throws IllegalArgumentException if condition is null or its tag definition is null or
	 *                                  the tag value type of that definition is null.
	 */
	ValidationResult validate(Condition condition) throws IllegalArgumentException;

	/**
	 * @param condition the condition to validate
	 * @param newValue new value that is about to be set to this condition
	 * @return result of the validation (if condition is null the result is invalid).
	 * @throws IllegalArgumentException if condition is null or its tag definition is null or
	 *                                  the tag value type of that definition is null.
	 */
	ValidationResult validate(Condition condition, final Object newValue) throws IllegalArgumentException;
	
	/**
	 * @param logicalOperatorTreeRootNode the root of the conditions tree.
	 * @return result of the validation (if node is null the result is invalid).
	 * @throws IllegalArgumentException if logical tree operator node is null.
	 */
	ValidationResult validateTree(LogicalOperator logicalOperatorTreeRootNode) throws IllegalArgumentException;

}
