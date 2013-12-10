/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

/**
 *  A <code>OrderReturnReason</code> represents a reason associated with a <code>OrderReturn</code>.
 *  
 */
public enum OrderReturnReason {
	/**
	 * The <code>OrderReturnReason</code> instance for "Unwanted Gift".
	 */
	UNWANTED_GIFT,

	/**
	 * The <code>OrderReturnReason</code> instance for "Incorrect Item".
	 */
	INCORRECT_ITEM,

	/**
	 * The <code>OrderReturnReason</code> instance for "Faulty".
	 */
	FAULTY;
}