/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * An exception for notifying interested parties of a missing required option.
 */
public class MissingSyncToolOptionException extends SyncToolConfigurationException {

	private static final long serialVersionUID = 5763078051932329571L;

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the message
	 * @param cause the cause of the exception
	 */
	public MissingSyncToolOptionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception.
	 * 
	 * @param message the exception's message
	 */
	public MissingSyncToolOptionException(final String message) {
		super(message);
	}

}
