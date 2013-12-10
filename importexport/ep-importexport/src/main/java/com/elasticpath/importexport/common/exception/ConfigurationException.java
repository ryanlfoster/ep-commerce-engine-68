package com.elasticpath.importexport.common.exception;

/**
 * Thrown to indicate that there is a problem with configuration parsing, using and etc.
 */
public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>ConfigurationException</code> with no detail message.
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * Constructs a new <code>ConfigurationException</code> exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail
	 * message.
	 * 
	 * @param message the detail message
	 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
	 */
	public ConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new <code>ConfigurationException</code> with the specified detail message.
	 * 
	 * @param message the detail message
	 */
	public ConfigurationException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>ConfigurationException</code> exception with the specified cause and a detail message of
	 * <tt>(cause==null ? null : cause.toString())</tt>.
	 * 
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
	 */
	public ConfigurationException(final Throwable cause) {
		super(cause);
	}
}
