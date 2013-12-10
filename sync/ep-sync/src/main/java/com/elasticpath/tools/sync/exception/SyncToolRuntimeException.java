/**
 * 
 */
package com.elasticpath.tools.sync.exception;



/**
 * <code>SyncToolRuntimeException</code> reports about unrecoverable errors .
 */
public class SyncToolRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message exception description
	 */
	public SyncToolRuntimeException(final String message) {
		super(message);		
	}

	/**
	 * @param message exception description
	 * @param cause previous exception causing this one
	 */
	public SyncToolRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause previous exception causing this one 
	 */
	public SyncToolRuntimeException(final Throwable cause) {
        super(cause);
    }

}
