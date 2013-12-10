/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalog;

/**
 * Inventory listener notifying all the interesting parties in a particular event.
 * The event might be stock received, removed, etc.
 */
public interface InventoryListener {

	/**
	 * Notification for receiving new inventory on a specific sku and warehouse.
	 * 
	 * @param skuCode the SKU code
	 * @param warehouseCode the warehouse code
	 */
	void newInventory(final String skuCode, final String warehouseCode);
}
