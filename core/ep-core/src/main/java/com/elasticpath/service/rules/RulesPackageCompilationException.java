package com.elasticpath.service.rules;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Exception for when rules packages have compilation errors.
 */
public class RulesPackageCompilationException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 */
	public RulesPackageCompilationException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public RulesPackageCompilationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
