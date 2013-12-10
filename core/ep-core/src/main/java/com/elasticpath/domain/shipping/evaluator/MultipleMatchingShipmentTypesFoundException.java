/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shipping.evaluator;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown when the SingleShipmentTypeEvaluatorStrategy finds multiple matching ShipmentTypes.
 */
public class MultipleMatchingShipmentTypesFoundException extends EpServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>MultipleMatchingShipmentTypesFoundException</code> object with the given message.
	 *
	 * @param message the reason for this <code>MultipleMatchingShipmentTypesFoundException</code>.
	 */
	public MultipleMatchingShipmentTypesFoundException(final String message) {
		super(message);
	}
}
