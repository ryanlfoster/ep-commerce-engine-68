package com.elasticpath.inventory;



/**
 * Defines the list of available {@link InventoryCommand} objects and provides creation methods for them. 
 */
public interface CommandFactory {
	
	/**
	 * Creates the {@link InventoryCommand} that deletes an Inventory record.
	 * 
	 * @param inventoryKey inventoryKey identifies the Inventory to delete
	 * @return {@link InventoryCommand} instance
	 */
	InventoryCommand getDeleteInventoryCommand(InventoryKey inventoryKey);

	/**
	 * Creates the {@link InventoryCommand} that creates or updates an Inventory record.
	 * 
	 * @param inventoryDto Contains information for the Inventory that should be persisted.
	 * @return {@link InventoryCommand} instance.
	 */
	InventoryCommand getCreateOrUpdateInventoryCommand(InventoryDto inventoryDto);

	/**
	 * Creates the {@link InventoryCommand} that releases an Inventory with the specified quantity.
	 * 
	 * @param inventoryKey inventoryKey identifies the Inventory to delete
	 * @param quantity to release.
	 * @return {@link InventoryCommand} instance
	 */
	InventoryCommand getReleaseInventoryCommand(InventoryKey inventoryKey,
			int quantity);
	
	/**
	 * Creates the {@link InventoryCommand} that allocates Inventory.
	 * 
	 * @param inventoryKey inventoryKey identifies the Inventory to allocate
	 * @param quantityToAllocate qty to allocate in this transaction
	 * 
	 * @return {@link InventoryCommand} instance.
	 */
	InventoryCommand getAllocateInventoryCommand(InventoryKey inventoryKey, int quantityToAllocate);

	/**
	 * Creates the {@link InventoryCommand} that deallocates Inventory.
	 * 
	 * @param inventoryKey inventoryKey identifies the Inventory to allocate
	 * @param quantityToDeallocate qty to deallocate in this transaction
	 * 
	 * @return {@link InventoryCommand} instance.
	 */
	InventoryCommand getDeallocateInventoryCommand(InventoryKey inventoryKey, int quantityToDeallocate);
	
	/**
	 * Creates the {@link InventoryCommand} that adjusts Inventory.
	 * 
	 * @param inventoryKey inventoryKey identifies the Inventory to adjust
	 * @param quantity qty to adjust
	 * 
	 * @return {@link InventoryCommand} instance.
	 */
	InventoryCommand getAdjustInventoryCommand(InventoryKey inventoryKey, int quantity);
	
}
