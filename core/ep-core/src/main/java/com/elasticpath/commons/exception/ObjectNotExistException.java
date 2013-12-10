package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpServiceException;

/**
 * This exception is designed to be thrown when a persistence operation is
 * attempted on an object that is no longer known to the persistence layer
 * (i.e. it's no longer persisted).
 */
public class ObjectNotExistException extends EpServiceException {

	/** Serial version id. */
	private static final long serialVersionUID = 7000000001L;
	
	/**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
	public ObjectNotExistException(final String message) {
		super(message);
	}
}
