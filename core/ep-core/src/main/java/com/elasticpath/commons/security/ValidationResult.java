/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <code>ValidationResult</code> represents result of validation made by <code>PasswordPolicy</code>.
 */
public class ValidationResult {
	private Map<String, ValidationError> errors;

	/**
	 * Valid result constant.
	 */
	public static final ValidationResult VALID = createValidResult();

	/**
	 * Constructs the instance of the class.
	 */
	public ValidationResult() {
		errors = new HashMap<String, ValidationError>();
	}

	/**
	 * Gets map containing validation errors.
	 * 
	 * @return map containing validation errors
	 */
	public Map<String, ValidationError> getErrors() {
		return errors;
	}

	/**
	 * Adds new <code>ValidationError</code> to this result.
	 * 
	 * @param error the validationError
	 */
	public void addError(final ValidationError error) {
		errors.put(error.getKey(), error);
	}

	/**
	 * Checks whether this result is valid.
	 * 
	 * @return true in case this result contains no errors, false otherwise
	 */
	public boolean isValid() {
		return errors == null || errors.isEmpty();
	}

	/**
	 * Assembles general <code>ValidationResult</code> from the list of <code>ValidationResult</code>s.
	 * 
	 * @param validationResults <code>ValidationResult</code>s
	 */
	public void assembleResult(final List<ValidationResult> validationResults) {
		for (final ValidationResult validationResult : validationResults) {
			errors.putAll(validationResult.getErrors());
		}
	}
	
	/**
	 * Checks whether this result contains the specified error.
	 * 
	 * @param errorKey error key
	 * @return true in case this result contains specified error, false otherwise
	 */
	public boolean containsError(final String errorKey) {
		return errors.containsKey(errorKey);
	}

	private static ValidationResult createValidResult() {
		final ValidationResult validationResult = new ValidationResult();
		validationResult.errors = Collections.unmodifiableMap(new HashMap<String, ValidationError>(0));
		return validationResult;
	}
}
