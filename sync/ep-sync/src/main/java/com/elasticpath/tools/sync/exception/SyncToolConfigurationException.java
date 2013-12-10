/**
 * 
 */
package com.elasticpath.tools.sync.exception;


/**
 * <code>SyncToolConfigurationException</code> reports errors in configuration, i.e. those which are unrecoverable and which should lead 
 * to the configuration update.
 */
public class SyncToolConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message exception description
	 */
	public SyncToolConfigurationException(final String message) {
		super(message);		
	}

	/**
	 * @param message exception description
	 * @param cause previous exception causing this one
	 */
	public SyncToolConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
