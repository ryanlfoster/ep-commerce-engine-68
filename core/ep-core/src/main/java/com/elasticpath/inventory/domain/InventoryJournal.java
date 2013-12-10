/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.inventory.domain;

import com.elasticpath.persistence.api.Persistable;

/**
 * An InventoryJournal records changes to quantity on-hand and allocated quantity for a particular Sku and Warehouse.
 */
public interface InventoryJournal extends Persistable {

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
	 * Get the skuCode of the productSku associated with this inventory journal.
	 * 
	 * @return productSku's skuCode.
	 */
	String getSkuCode();

	/**
	 * Set the skuCode of the productSku associated with this inventory journal.
	 * 
	 * @param skuCode The productSku's skuCode.
	 */
	void setSkuCode(final String skuCode);
	
	/**
	 * Gets the warehouseUid associated with this inventory journal.
	 * 
	 * @return The warehouseUid.
	 */
	Long getWarehouseUid();
	
	/**
	 * Sets warehouseUid associated with this inventory journal.
	 * 
	 * @param warehouseUid The associated warehouseUid.
	 */
	void setWarehouseUid(final Long warehouseUid);
	
}
