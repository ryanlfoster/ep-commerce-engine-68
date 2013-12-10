/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.util.capabilities.CapabilityAware;
import com.elasticpath.inventory.CommandFactory;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;

/**
 * Provides an environment for {@link InventoryCommand} execution, by supplying all necessary resources
 * to the commands and executing them as defined by the associated {@link InventoryStrategy}.
 */
public interface InventoryStrategy extends CapabilityAware {

	/**
	 * Executes a {@link InventoryCommand} object.
	 * 
	 * @param command {@link InventoryCommand} to execute.
	 */
	void executeCommand(InventoryCommand command);
	
	/**
	 * Provides access to an instance of the {@link CommandFactory} which is used to create various 
	 * {@link InventoryCommand} objects that are specific to this particular {@link InventoryStrategy}.
	 * 
	 * @return {@link CommandFactory} instance
	 */
	CommandFactory getCommandFactory();
	
	/**
	 * Returns a DTO with an inventory information for a given SKU Code & Warehouse ID combination - 
	 * {@link InventoryKey}. 
	 * 
	 * @param inventoryKey inventory key
	 * @return inventory DTO or null, if not found
	 */
	InventoryDto getInventory(InventoryKey inventoryKey);

	/**
	 * Returns inventory information for multiple {@link InventoryKey}. 
	 * 
	 * @param inventoryKeys inventory keys
	 * @return {@link InventoryDto}s mapped by {@link InventoryKey}s 
	 */
	Map<InventoryKey, InventoryDto> getInventories(Set<InventoryKey> inventoryKeys);

	/**
	 * Get a map of Warehouse Uid to Inventory for the given SkuCode.
	 *
	 * @param skuCode The SkuCode.
	 * @return A map of Warehouse Uid to Inventory for the given SkuCode.
	 */
	Map<Long, InventoryDto> getInventoriesForSku(String skuCode);

	/**
	 * Get a map of sku code to inventory.
	 * 
	 * @param skuCodes the list of sku codes whose inventory is needed
	 * @param warehouseUid the warehouse to get inventory for
	 * @return a map of sku codes to inventory values.
	 */
	Map<String, InventoryDto> getInventoriesForSkusInWarehouse(Set<String> skuCodes,
			long warehouseUid);

	/**
	 * Finds inventories which are low stock for the given set of sku codes.
	 * 
	 * @param skuCodes sku codes
	 * @param warehouseUid warehouse uid
	 * @return list of low stock inventories
	 */
	List<InventoryDto> findLowStockInventories(Set<String> skuCodes, long warehouseUid);
}
