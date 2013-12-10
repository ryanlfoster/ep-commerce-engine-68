package com.elasticpath.test.common.exception;

/**
 * Datasource initializer runtime exception.
 */
public class DataSourceInitializerException extends TestApplicationException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a datasource initializer exception without a message. The cause remains uninitialized.
	 */
	public DataSourceInitializerException() {
		super();
	}

	/**
	 * Create a datasource initializer exception with a message.
	 * 
	 * @param message the message string
	 */
	public DataSourceInitializerException(final String message) {
		super(message);
	}

	/**
	 * Create a datasource initializer exception with a message and a cause.
	 * 
	 * @param message the message string
	 * @param cause the cause of this exception
	 */
	public DataSourceInitializerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a datasource initializer exception with the given cause.
	 * 
	 * @param cause the cause of this exception
	 */
	public DataSourceInitializerException(final Throwable cause) {
		super(cause);
	}
}