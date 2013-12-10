/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shipping.evaluator;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown when the SingleShipmentTypeEvaluatorStrategy evaluates an invalid ShoppingItem type.
 */
public class InvalidShoppingItemException extends EpServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>InvalidShoppingItemException</code> object with the given message.
	 *
	 * @param message the reason for this <code>InvalidShoppingItemException</code>.
	 */
	public InvalidShoppingItemException(final String message) {
		super(message);
	}
}
