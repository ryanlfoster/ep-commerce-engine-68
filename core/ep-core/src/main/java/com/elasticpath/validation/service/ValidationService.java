package com.elasticpath.validation.service;

import java.util.Collection;

import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Service that validates a value using a collection of constraints that are 
 * required to be satisfied.
 */
public interface ValidationService {
	
	/**
	 * validate a value using constraints required.
	 * @param valueToValidate the value
	 * @param validationConstraints the constraints
	 * @return validation result object that contains the result of the validation operation (if the 
	 *         constraints are not provided then the result is valid)
	 */
	ValidationResult validate(Object valueToValidate, Collection<ValidationConstraint> validationConstraints);

}
