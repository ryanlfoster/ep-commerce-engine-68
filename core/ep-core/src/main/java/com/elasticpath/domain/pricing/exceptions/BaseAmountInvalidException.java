package com.elasticpath.domain.pricing.exceptions;

import org.springframework.validation.Errors;

import com.elasticpath.domain.EpDomainException;

/**
 * Represents an attempt to set invalid values on a {@code BaseAmount}.
 */
public class BaseAmountInvalidException extends EpDomainException {

	private static final long serialVersionUID = -5758848637536086245L;

	private final Errors errors;
	
	/**
	 * Constructs a {@code BaseAmountInvalidException} using {@code errors}.
	 * @param message message
	 * @param errors errors
	 */
	public BaseAmountInvalidException(final String message, final Errors errors) {
		super(message);
		this.errors = errors;
	}

	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}
}
