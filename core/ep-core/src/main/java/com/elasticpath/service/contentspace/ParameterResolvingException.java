package com.elasticpath.service.contentspace;

/**
 * This exception will be thrown when not all parameters values can be resolved.
 */
public class ParameterResolvingException extends Exception {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new object.
	 * 
	 * @param message the message
	 * @param cause the root cause 
	 */
	public ParameterResolvingException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Creates a new object.
	 * 
	 * @param message the message
	 */
	public ParameterResolvingException(final String message) {
		super(message);
	}
	
	


}
