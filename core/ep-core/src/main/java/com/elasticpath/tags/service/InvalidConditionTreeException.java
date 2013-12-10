package com.elasticpath.tags.service;

import java.util.Locale;

import com.elasticpath.validation.domain.ValidationResult;

/**
 * Exception to denote invalid condition tree.
 */
public class InvalidConditionTreeException extends Exception {

	private static final long serialVersionUID = -8744558825795751556L;

	private final ValidationResult validationResult;
	
	/**
	 * constructor with initialisation of the validation result to hold the cause
	 * of failure.
	 * @param validationResult the cause of failure
	 */
	public InvalidConditionTreeException(final ValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	@Override
	public String getLocalizedMessage() {
		return validationResult.getMessage(Locale.getDefault());
	}
	
	
	
}
