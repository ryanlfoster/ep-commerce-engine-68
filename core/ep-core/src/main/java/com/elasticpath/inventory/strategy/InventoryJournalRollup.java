/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.inventory.strategy;

import com.elasticpath.inventory.InventoryKey;

/**
 * An InventoryJournalRollup contains the result of summing the quantity on-hand and allocated quantity of the TINVENTORYJOURNAL table
 * for an InventoryKey.
 */
public interface InventoryJournalRollup {

	/**
	 * Get the allocated quantity delta.
	 * 
	 * @return The allocated quantity delta.
	 */
	int getAllocatedQuantityDelta();

	/**
	 * Set the allocated quantity delta.
	 * 
	 * @param delta The allocated quantity delta.
	 */
	void setAllocatedQuantityDelta(int delta);

	/**
	 * Get the quantity on hand delta.
	 * 
	 * @return The quantity on hand delta.
	 */
	int getQuantityOnHandDelta();

	/**
	 * Set the quantity on hand delta.
	 * 
	 * @param delta The quantity on hand delta.
	 */
	void setQuantityOnHandDelta(int delta);
	
	/**
	 * Get the InventoryKey associated with this inventory journal rollup.
	 * 
	 * @return The InventoryKey.
	 */
	InventoryKey getInventoryKey();

	/**
	 * Set the InventoryKey associated with this inventory journal rollup.
	 * 
	 * @param inventoryKey The InventoryKey.
	 */
	void setInventoryKey(final InventoryKey inventoryKey);
	
}
