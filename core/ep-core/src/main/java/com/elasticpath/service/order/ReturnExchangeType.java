/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

/**
 * Enum that represents all available return/exchange type.
 */
public enum ReturnExchangeType {
	/** Physical return isn't required and nor refund, neither additional auth is required. */
	CREATE_WO_PAYMENT,
	/** Physical return. */
	PHYSICAL_RETURN_REQUIRED,
	/** Refund to original payment source. */
	REFUND_TO_ORIGINAL,
	/** Manual refund. */
	MANUAL_RETURN,
	/** Pay from original payment. */
	ORIGINAL_PAYMENT,
	/** Pay from new payment. */
	NEW_PAYMENT
}
