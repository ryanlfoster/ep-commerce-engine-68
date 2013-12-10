package com.elasticpath.validation.service.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.service.ValidationEngine;
import com.elasticpath.validation.service.ValidationService;

/**
 * Service that validates a value using a collection of constraints that are 
 * required to be satisfied.
 */
public class DeclarativeValidationServiceImpl implements ValidationService {

	private ValidationEngine validationEngine;
	
	/**
	 * @return validation engine that will be used to evaluate declarative constraints.
	 */
	public ValidationEngine getValidationEngine() {
		return validationEngine;
	}

	/**
	 * @param validationEngine validation engine that will be used to evaluate declarative constraints.
	 */
	public void setValidationEngine(final ValidationEngine validationEngine) {
		this.validationEngine = validationEngine;
	}


	@Override
	public ValidationResult validate(final Object valueToValidate,
			final Collection<ValidationConstraint> validationConstraints) {
		if (CollectionUtils.isNotEmpty(validationConstraints)) {
			for (ValidationConstraint validationConstraint : validationConstraints) {
				final ValidationResult result = getValidationEngine()
					.validate(valueToValidate, validationConstraint);
				if (!result.isValid()) {
					return result;
				}
			}
		}
		return ValidationResult.VALID;
	}

}
