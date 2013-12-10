/**
 * 
 */
package com.elasticpath.domain.catalog;

/**
 * This exception is to be thrown whenever someone attempts to remove
 * a default value from a collection of supported values. Many objects
 * have a concept of a default value that is included in modifiable 
 * collection of supported values.
 */
public class DefaultValueRemovalForbiddenException extends Exception {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private static String msg = "Attempted to remove a default value from supported values. ";
	
	/**
	 * Constructs a new instance with a default message.
	 */
	public DefaultValueRemovalForbiddenException() {
		super(msg);
	}

	/**
	 * Constructs a new instance, appending the default message with the
	 * given message.
	 * @param message any information to add to the default information message.
	 */
	public DefaultValueRemovalForbiddenException(final String message) {
		super(msg + message);
	}
}
