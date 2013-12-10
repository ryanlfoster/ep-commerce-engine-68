/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

/**
 * Represents the customer orderPayment status.
 *
 */
public enum OrderPaymentStatus {
	/**
	 * The <code>OrderPaymentStatus</code> instance for "Approved".
	 */
	APPROVED,

	/**
	 * The <code>OrderPaymentStatus</code> instance for "Pending".
	 */
	PENDING,

	/**
	 * The <code>OrderPaymentStatus</code> instance for "Failed".
	 */
	FAILED;
}