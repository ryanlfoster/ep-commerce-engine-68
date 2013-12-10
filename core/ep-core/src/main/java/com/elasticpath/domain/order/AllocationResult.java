/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.inventory.InventoryExecutionResult;

/**
 * The result used to hold the information on the processing of the allocation.
 */
public interface AllocationResult extends EpDomain {

	/**
	 * Get the quantity allocated on pre/back order.
	 *
	 * @return the quantity allocated on pre/back order
	 */
	int getQuantityAllocatedOnPreOrBackOrder();

	/**
	 * Set the quantity allocated on pre/back order.
	 * @param quantityAllocated the quantityAllocated to set
	 */
	void setQuantityAllocatedOnPreOrBackOrder(final int quantityAllocated);

	/**
	 * Get the inventory result object.
	 * @return the inventory result if inventory operation was processed. Otherwise null.
	 */
	InventoryExecutionResult getInventoryResult();

	/**
	 * Set the inventory result.
	 *
	 * @param inventoryResult the inventory result if inventory operation was processed
	 */
	void setInventoryResult(final InventoryExecutionResult inventoryResult);

	/**
	 * Gets the allocated quantity in stock.
	 *
	 * @return the quantity allocated in stock
	 */
	int getQuantityAllocatedInStock();
}
