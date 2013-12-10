/**
 *
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;

/** Exception that will be thrown for the bundle items had structure changed from the time
 * they the product page was displayed.
 *
 */
public class InvalidBundleTreeStructureException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new <code>InvalidBundleTreeStructureException</code> object with the given message.
	 *
	 * @param message the reason for this <code>InvalidBundleTreeStructureException</code>.
	 */
	public InvalidBundleTreeStructureException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>InvalidBundleTreeStructureException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>InvalidBundleTreeStructureException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>InvalidBundleTreeStructureException</code>.
	 */
	public InvalidBundleTreeStructureException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
