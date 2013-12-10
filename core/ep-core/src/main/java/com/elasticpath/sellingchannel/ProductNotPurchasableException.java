package com.elasticpath.sellingchannel;

import com.elasticpath.base.exception.EpSystemException;

/**
 * Exception thrown when product is not able to purchased.
 */
public class ProductNotPurchasableException extends EpSystemException {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;
	
	
	/**
	 * Creates a new <code>EpWebException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 */
	public ProductNotPurchasableException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpWebException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpWebException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpServiceException</code>.
	 */
	public ProductNotPurchasableException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
