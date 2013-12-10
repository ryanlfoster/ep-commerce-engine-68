package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception can be thrown when there is an attempt to remove a category when it is not empty of products. 
 */
public class EpCategoryNotEmptyException extends EpServiceException {
	/** Serial version id. */
	private static final long serialVersionUID = 6000000001L;

	/**
	 * Creates a new <code>EpCategoryNotEmptyException</code> object with the given message.
	 * 
	 * @param message the reason for this exception
	 */
	public EpCategoryNotEmptyException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpCategoryNotEmptyException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this exception
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public EpCategoryNotEmptyException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
