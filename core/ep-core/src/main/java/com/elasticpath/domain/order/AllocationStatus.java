/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order;

/**
 * Allocation status.
 */
public enum AllocationStatus {

	/**
	 * Used for signifying that the quantity is allocated with items in stock.
	 */
	ALLOCATED_IN_STOCK,
	
	/**
	 * Used for signifying that the quantity can not be allocated.
	 */
	NOT_ALLOCATED,
	
	/**
	 * Used for signifying that the quantity is allocated with items in stock and items for pre/back order.
	 * It is possible that all the items are allocated with items for pre/back order. 
	 */
	AWAITING_ALLOCATION
}
