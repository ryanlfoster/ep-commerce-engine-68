/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory.impl;

import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;

/**
 * Represents the processing result on the <code>Inventory</code> command execution.
 *
 */
public class InventoryExecutionResultImpl extends AbstractEpDomainImpl implements InventoryExecutionResult {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private int quantity;
	
	private InventoryDto inventoryAfter;

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	public InventoryDto getInventoryAfter() {
		return inventoryAfter;
	}

	@Override
	public void setInventoryAfter(final InventoryDto inventoryAfter) {
		this.inventoryAfter = inventoryAfter;
	}
}
