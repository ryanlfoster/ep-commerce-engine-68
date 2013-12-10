/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.elasticpath.commons.util.capabilities.CapabilityAware;

/**
 * The interface point for all external interactions with the inventory subsystem.
 */
public interface InventoryFacade extends CapabilityAware {
	
	/**
	 * Executes an atomic inventory business process action that is represented by the given 
	 * {@link InventoryCommand} object. The action is executed within the caller's transaction
	 * context.
	 *  
	 * @param command an {@link InventoryCommand} to execute 
	 */
	void executeInventoryCommand(InventoryCommand command);
	
	/**
	 * Executes a sequence of inventory business process actions that is represented by the given 
	 * {@link InventoryCommand} queue. The actions are executed within the caller's transaction
	 * context.
	 *  
	 * @param commandQueue an {@link InventoryCommand} queue to execute 
	 */
	void executeInventoryCommands(Queue<InventoryCommand> commandQueue);
	
	/**
	 * Provides access to the {@link CommandFactory} to be used to create {@link InventoryCommand} 
	 * objects for the currently active InventoryStrategy.
	 * 
	 * @return a {@link CommandFactory} that belongs to the currently active InventoryStrategy
	 */
	CommandFactory getInventoryCommandFactory();
	
	/**
	 * Returns a DTO with inventory information for a given {@link InventoryKey} (i.e. SKU Code 
	 * & Warehouse ID combination).
	 * 
	 * @param inventoryKey inventory key
	 * @return inventoryDTO or null, if not found
	 */
	InventoryDto getInventory(InventoryKey inventoryKey);

	/**
	 * Returns a map of DTOs with inventory information mapped to the requested set of {@link InventoryKey}
	 * (i.e. SKU Code & Warehouse ID combinations). 
	 * 
	 * @param inventoryKeys set of inventory keys
	 * @return inventoryDTOs mapped to their inventory keys or an empty map if nothing found.
	 *                       The map contains entries only for inventory keys that are found.
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
	Map<String, InventoryDto> getInventoriesForSkusInWarehouse(
			Set<String> skuCodes, long warehouseUid);

	/**
	 * Finds inventories which are low stock for the given set of sku codes.
	 * 
	 * @param skuCodes sku codes
	 * @param warehouseUid warehouse uid
	 * @return list of low stock inventories
	 */
	List<InventoryDto> findLowStockInventories(Set<String> skuCodes, long warehouseUid);
}
