/**
 *
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.EpSystemException;


/** Exception that will be thrown for the bundle items that are in a cart and their structure changed from the time
 * they were added.
 *
 */
public class InvalidBundleTreeStructureForItemInCartException extends EpSystemException {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;


	/**
	 * Creates a new <code>InvalidBundleTreeStructureForItemInCartException</code> object with the given message.
	 *
	 * @param message the reason for this <code>InvalidBundleTreeStructureForItemInCartException</code>.
	 */
	public InvalidBundleTreeStructureForItemInCartException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>InvalidBundleTreeStructureForItemInCartException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>InvalidBundleTreeStructureForItemInCartException</code>
	 * @param cause the <code>Throwable</code> that caused this <code>InvalidBundleTreeStructureForItemInCartException</code>.
	 */
	public InvalidBundleTreeStructureForItemInCartException(final String message, final	Throwable cause) {
		super(message, cause);
	}



}
