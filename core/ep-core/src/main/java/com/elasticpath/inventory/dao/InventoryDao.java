/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.inventory.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Defines data access methods relating to Inventory.
 */
public interface InventoryDao {

	/**
	 * Save or update the given inventory item. 
	 *  
	 * @param inventory the inventory to save or update
	 * @throws EpServiceException - in case of any errors
	 * @return the updated inventory object
	 */
	Inventory saveOrUpdate(final Inventory inventory) throws EpServiceException;

	/**
	 * Delete the inventory.
	 *
	 * @param inventory the inventory to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final Inventory inventory) throws EpServiceException;
	
	/**
	 * Get a map of sku code to inventory.
	 * 
	 * @param skuCodes the list of sku codes whose inventory is needed
	 * @param warehouseUid the warehouse to get inventory for
	 * @return a map of sku codes to inventory values.
	 */
	Map<String, Inventory> getInventoryMap(final Collection<String> skuCodes, final long warehouseUid);

	/**
	 * Retrieves the inventory in a warehouse with warehouseUid for product SKU identified by skuCode.
	 * 
	 * @param skuCode the code of the product SKU
	 * @param warehouseUid the warehouse UID
	 * @throws EpServiceException - in case of any errors
	 * @return the inventory object for the given sku and warehouse
	 */
	Inventory getInventory(final String skuCode, final long warehouseUid) throws EpServiceException;

	/**
	 * Get a map of Warehouse Uid to Inventory.
	 * 
	 * @param skuCode The SkuCode.
	 * @return A map of Warehouse Uid to Inventory.
	 * @throws EpServiceException For any exceptions.
	 */
	Map<Long, Inventory> getInventoriesForSku(final String skuCode) throws EpServiceException;

	/**
	 * Finds inventories which are low stock for the given set of sku codes.
	 * 
	 * @param skuCodes sku codes
	 * @param warehouseUid warehouse uid
	 * @return list of low stock inventory information
	 */
	List<InventoryDto> findLowStockInventories(Set<String> skuCodes, long warehouseUid);
}
