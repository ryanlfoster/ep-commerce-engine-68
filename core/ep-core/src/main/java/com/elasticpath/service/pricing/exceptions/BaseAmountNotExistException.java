/**
 * 
 */
package com.elasticpath.service.pricing.exceptions;

import com.elasticpath.commons.exception.ObjectNotExistException;

/**
 * Thrown when a persistence operation is attempted on a BaseAmount that
 * is not persistent.
 */
public class BaseAmountNotExistException extends ObjectNotExistException {
	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	
	/**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
	public BaseAmountNotExistException(final String message) {
		super(message);
	}
}
