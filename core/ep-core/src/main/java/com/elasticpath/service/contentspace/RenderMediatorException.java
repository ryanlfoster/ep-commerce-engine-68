package com.elasticpath.service.contentspace;

/**
 * This exception will be thrown when not all parameters values can be resolved.
 */
public class RenderMediatorException extends Exception {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new object.
	 * 
	 * @param message the message
	 * @param cause the root cause 
	 */
	public RenderMediatorException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Creates a new object.
	 * 
	 * @param message the message
	 */
	public RenderMediatorException(final String message) {
		super(message);
	}
	
	


}
