package com.elasticpath.inventory.strategy;

import java.util.List;

import com.elasticpath.inventory.InventoryKey;

/**
 * This service rolls up and cleans the TINVENTORYJOURNAL table.
 */
public interface InventoryJournalRollupService {
	/**
	 * Convenient method, get all inventory keys from TINVENTORYJOURNAL which has at least 
	 * InventoryJournalRollupServiceImpl.MINIMUM_ROLLUP_ROWS.
	 * @return List<InventoryKey>.
	 */
	List<InventoryKey> getAllInventoryKeys();

	/**
	 * Sums allocated quantity delta and quantity on hand delta in TINVENTORYJOURNAL table
	 * for the given InventoryKey, merge to TINVENTORY, delete records in TINVENTORYJOURNAL.
	 * 
	 * @param inventoryKey The key of inventory.
	 */
	void processRollup(InventoryKey inventoryKey);
	
}