package com.elasticpath.test.common.exception;

/**
 * General test application runtime exception.
 */
public class TestApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a test application exception without a message. The cause remains uninitialized.
	 * 
	 * @see #initCause(Throwable)
	 */
	public TestApplicationException() {
		// empty constructor
	}

	/**
	 * Create a test application exception with a message.
	 * 
	 * @param message the message string
	 */
	public TestApplicationException(final String message) {
		super(message);
	}

	/**
	 * Create a test application exception with a message and a cause.
	 * 
	 * @param message the message string
	 * @param cause the cause of this exception
	 */
	public TestApplicationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a test application exception with the given cause.
	 * 
	 * @param cause the cause of this exception
	 */
	public TestApplicationException(final Throwable cause) {
		super(cause);
	}
}