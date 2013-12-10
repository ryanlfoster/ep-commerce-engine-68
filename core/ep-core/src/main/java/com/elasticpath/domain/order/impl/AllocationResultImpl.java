/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.order.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.inventory.InventoryExecutionResult;

/**
 * Allocation result object used to hold all the result info after
 * the allocation process has completed.
 */
public class AllocationResultImpl extends AbstractEpDomainImpl implements AllocationResult {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private int preBackOrderQuantity;
	private InventoryExecutionResult inventoryResult;

	/**
	 *
	 * @return the inventory result
	 */
	public InventoryExecutionResult getInventoryResult() {
		return inventoryResult;
	}

	/**
	 *
	 * @return the allocated pre/back order quantity
	 */
	public int getQuantityAllocatedOnPreOrBackOrder() {
		return preBackOrderQuantity;
	}

	/**
	 * Gets the total quantity allocated.
	 *
	 * @return the total allocated quantity
	 */
	public int getQuantityAllocatedInStock() {
		int totalQty = 0;
		if (getInventoryResult() != null) {
			totalQty = getInventoryResult().getQuantity();
		}
		return totalQty;
	}

	/**
	 *
	 * @param inventoryResult the inventory result
	 */
	public void setInventoryResult(final InventoryExecutionResult inventoryResult) {
		this.inventoryResult = inventoryResult;
	}

	/**
	 *
	 * @param quantityAllocated inventory that is allocated on pre/back order
	 */
	public void setQuantityAllocatedOnPreOrBackOrder(final int quantityAllocated) {
		preBackOrderQuantity = quantityAllocated;
	}

}
