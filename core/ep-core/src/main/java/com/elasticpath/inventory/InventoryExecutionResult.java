/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.inventory;

import com.elasticpath.domain.EpDomain;

/**
 * Represents the processing result on the <code>Inventory</code> command execution.
 */
public interface InventoryExecutionResult extends EpDomain {

	/**
	 * Set the quantity.
	 * @param quantity the quantity to set
	 */
	void setQuantity(int quantity);

	/**
	 * Get the quantity.
	 * @return the quantity.
	 */
	int getQuantity();
	
	/**
	 * Get the inventory after the command is run. It is calculated based on the state before the command
	 * is run, and the effects of the command. So the result can be out-of-date by the time it is returned.
	 * @return the inventory
	 */
	InventoryDto getInventoryAfter();
	
	/**
	 * Sets the inventory result.
	 * @param inventoryAfter the inventory
	 */
	void setInventoryAfter(InventoryDto inventoryAfter);

}
