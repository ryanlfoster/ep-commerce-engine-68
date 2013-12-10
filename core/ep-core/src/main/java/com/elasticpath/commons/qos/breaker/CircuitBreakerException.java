package com.elasticpath.commons.qos.breaker;

/**
 * Thrown when the CircuitBreakerAspect is failing fast - not currently
 * delegating to the underlying service. 
 */
public class CircuitBreakerException extends RuntimeException {

	private static final long serialVersionUID = -1002353021137680319L;

	/**
	 * Create an instance with the specified message and cause.
	 * @param message a simple message describing the cause.
	 * @param cause the underlying cause of the problem
	 */
	public CircuitBreakerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/** 
	 * Create an instance with the specified message.
	 * @param message a simple message describing the cause.
	 */
	public CircuitBreakerException(final String message) {
		super(message);
	}

	/**
	 * Create an instance with the specified message and cause.
	 * @param cause the underlying cause of the problem
	 */
	public CircuitBreakerException(final Throwable cause) {
		this("", cause);
	}

}
