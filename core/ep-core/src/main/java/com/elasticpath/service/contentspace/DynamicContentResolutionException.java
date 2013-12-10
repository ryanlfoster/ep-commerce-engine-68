package com.elasticpath.service.contentspace;

/**
 * Exception used by Dynamic Content Runtime Service.
 * Denotes an exception within the process of resolving dynamic content 
 * object for a particular content space.
 */
public class DynamicContentResolutionException extends Exception {

	private static final long serialVersionUID = -4786242275988002550L;

	/**
	 * default constructor.
	 */
	public DynamicContentResolutionException() {
		super();
	}
	
	/**
	 * constructor for exception with message text.
	 *
	 * @param message the exception message
	 */
	public DynamicContentResolutionException(final String message) {
		super(message);
	}

	/**
	 * constructor for exception with message text and cause.
	 *
	 * @param message the exception message
	 * @param cause the cause of exception (used for re-thrown exceptions)
	 */
	public DynamicContentResolutionException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
}
