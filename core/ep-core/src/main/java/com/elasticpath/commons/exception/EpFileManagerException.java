/**
 * 
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/**
 * This exception will be thrown when an error occurs with the FileSystemManager.
 */
public class EpFileManagerException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 6000000001L;

	/**
	 * Creates a new <code>EpFileManagerException</code> object with the given message.
	 * 
	 * @param message the reason for this exception
	 */
	public EpFileManagerException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpFileManagerException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this exception
	 * @param cause the <code>Throwable</code> that caused this exception
	 */
	public EpFileManagerException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
