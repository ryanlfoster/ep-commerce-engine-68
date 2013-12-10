/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shipping.evaluator;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Thrown when the SingleShipmentTypeEvaluatorStrategy fails to determine a ShipmentType.
 */
public class NoMatchingShipmentTypeFoundException extends EpServiceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>NoMatchingShipmentTypeFoundException</code> object with the given message.
	 *
	 * @param message the reason for this <code>NoMatchingShipmentTypeFoundException</code>.
	 */
	public NoMatchingShipmentTypeFoundException(final String message) {
		super(message);
	}
}
