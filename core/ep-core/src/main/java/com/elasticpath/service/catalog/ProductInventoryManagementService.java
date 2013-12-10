/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PreOrBackOrderDetails;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides inventory-related services.
 * 
 * @ws.attribute service="ContextIdNames.INVENTORY_SERVICE"
 */
public interface ProductInventoryManagementService {

	/**
	 * Save or update the given inventory item.
	 * Does not save or update the allocated qty or the on-hand qty.
	 * 
	 * Note: if the Inventory did not exist then a remove() is done first to clean up any stale Inventory records.
	 * 
	 * @param inventoryDto The inventory to save or update.
	 * @return InventoryDto Saved or updated inventory.
	 */
	InventoryDto saveOrUpdate(InventoryDto inventoryDto);

	/**
	 * Centralize the process on the inventory update events. All inventory update should be centralized by this method. It will update the inventory
	 * quantity.
	 *  
	 * @param inventoryDto inventory DTO
	 * @param inventoryAudit the InventoryAudit
	 * @return the updated inventory event result
	 */
	InventoryExecutionResult processInventoryUpdate(InventoryDto inventoryDto, InventoryAudit inventoryAudit);
	
	/**
	 * Centralize the process on the inventory update events. All inventory update should be centralized by this method. It will update the inventory
	 * quantity and log the inventory events for auditing.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @param eventType the inventory event type
	 * @param eventOriginator the event originator
	 * @param quantity the quantity
	 * @param order the order related
	 * @param comment the comment
	 * @return the updated inventory event result
	 */
	InventoryExecutionResult processInventoryUpdate(ProductSku productSku, long warehouseUid, 
			InventoryEventType eventType, String eventOriginator,
			int quantity, Order order, String comment);
	
	/**
	 * Calls saveOrUpdate() first.
	 * Then updates the allocated qty and the on-hand qty to the given values.
	 * 
	 * @param inventoryDto The desired values.
	 * @return An InventoryDto with updated values.
	 */
	InventoryDto merge(final InventoryDto inventoryDto);
	
	/**
	 * Delete the inventory.
	 *
	 * @param inventoryDto the inventory to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(InventoryDto inventoryDto) throws EpServiceException;
	
	/**
	 * Returns true if there is sufficient stock of this Inventory to sell the specified quantity of items.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @param quantity the quantity of items to check for availability
	 * @return true if <code>quantity</code> items can be sold
	 */
	boolean hasSufficientInventory(ProductSku productSku, long warehouseUid, int quantity);

	/**
	 * Adds a new listener that will be notified on occurring inventory events.
	 * 
	 * @param inventoryListener the inventory listener
	 */
	void registerInventoryListener(InventoryListener inventoryListener);

	/**
	 * Retrieves the available in stock quantity in a warehouse with 
	 * warehouseUid for the given product SKU.
	 * 
	 * @param productSku the product SKU
	 * @param warehouseUid the warehouse UID
	 * @return the available quantity in stock. Might be a negative result
	 */
	int getAvailableInStockQty(ProductSku productSku, long warehouseUid);

	/**
	 * Retrieves the PreOrBackOrder details for the given product SKU.
	 * 
	 * @param skuCode the code of the product SKU
	 * @return the {@link PreOrBackOrderDetails}
	 */
	PreOrBackOrderDetails getPreOrBackOrderDetails(String skuCode);

	/**
	 * Retrieves the inventory in a warehouse with warehouseUid for product SKU identified by skuCode.
	 * 
	 * @param skuCode the code of the product SKU
	 * @param warehouseUid the warehouse UID
	 * @throws EpServiceException - in case of any errors
	 * @return the inventory object for the given sku and warehouse
	 */
	InventoryDto getInventory(String skuCode, long warehouseUid) throws EpServiceException;
	
	/**
	 * Gets the Inventory subsystem's Capabilities.
	 * 
	 * @return The Capabilities.
	 */
	Capabilities getInventoryCapabilities();

	/**
	* Checks if the order sku has sufficient allocation.
	* @param sku - the order sku
	* @param warehouseUid - uid of warehouse, the sku is part of 
	* @return if the order sku has sufficient allocation
	*/
	boolean isSelfAllocationSufficient(OrderSku sku, long warehouseUid);	
	
	/**
	 * Get a map of Warehouse Uid to Inventory for the given SkuCode.
	 *
	 * @param skuCode The SkuCode.
	 * @return A map of Warehouse Uid to Inventory for the given SkuCode.
	 */
	Map<Long, InventoryDto> getInventoriesForSku(String skuCode);

	/**
	 * Retrieves the {@link InventoryDto} list for all {@link Warehouse}s in the collection for a given SKU.
	 * 
	 * @param skuCode the code of the product SKU
	 * @param warehouses collection of {@link Warehouse}s to find {@link InventoryDto} for.
	 * @throws EpServiceException - in case of any errors
	 * @return the {@link InventoryDto} mapped by specific {@link Warehouse}s.  Should return an empty map if it can't find anything.
	 */
	Map<Warehouse, InventoryDto> getInventoriesForSkuInWarehouses(String skuCode, Collection<Warehouse> warehouses) throws EpServiceException;
	
	/**
	 * Convenient method, delegate to InventoryDtoAssembler.
	 * @param dto to convert
	 * @return Inventory
	 */
	Inventory assembleDomainFromDto(InventoryDto dto);

	/**
	 * Convenient method, delegate to InventoryDtoAssembler.
	 * @param inventory the inventory domain object.
	 * @return InventoryDto
	 */
	InventoryDto assembleDtoFromDomain(Inventory inventory);

	/**
	 * Get a map of sku code to inventory.
	 * 
	 * @param skuCodes the list of sku codes whose inventory is needed
	 * @param warehouseUid the warehouse to get inventory for
	 * @return a map of sku codes to inventory values.
	 */
	Map<String, InventoryDto> getInventoriesForSkusInWarehouse(Set<String> skuCodes, long warehouseUid);
	
	/**
	 * Finds inventories which are low stock for the given set of sku codes.
	 * 
	 * @param skuCodes sku codes
	 * @param warehouseUid warehouse uid
	 * @return list of low stock inventories
	 */
	List<InventoryDto> findLowStockInventories(Set<String> skuCodes, long warehouseUid);
}
