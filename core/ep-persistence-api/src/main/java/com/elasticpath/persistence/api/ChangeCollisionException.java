/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.api;


/**
 * The exception that generally wraps <code>OptimisticLockException</code> and thrown when 
 * concurrent modification noticed. 
 */
public class ChangeCollisionException extends EpPersistenceException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>ChangeCollisionException</code> object.
	 *
	 * @param message the message
	 */
	public ChangeCollisionException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>ChangeCollisionException</code> object.
	 *
	 * @param message the message
	 * @param cause the root cause
	 */
	public ChangeCollisionException(final String message, final Throwable cause) {
		super(message, cause);		
	}

}
