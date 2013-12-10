/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.dao;

import java.util.List;
import java.util.Set;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;

/**
 * Defines data access methods for <code>InventoryJournal</code>.
 */
public interface InventoryJournalDao {

	/**
	 * Save or update the given inventory journal.
	 *  
	 * @param inventoryJournal The inventory journal to save or update.
	 * @return The updated inventory journal object.
	 */
	InventoryJournal saveOrUpdate(final InventoryJournal inventoryJournal);

	/**
	 * Sums up the quantity on-hand and allocated quantities for the given inventory key.
	 * 
	 * @param inventoryKey The inventory key.
	 * @return An InventoryJournalRollup object with the summed values.
	 */
	InventoryJournalRollup getRollup(final InventoryKey inventoryKey);
	
	/**
	 * Get all uidPk of TINVENTORYJOURNAL for a inventory key.
	 * @param inventoryKey the key of inventory
	 * @return a list of uidpk
	 */
	List<Long> getUidsByKey(InventoryKey inventoryKey);

	/**
	 * Get sum by a list of uids.
	 * @param journalUids to sum
	 * @return InventoryJournalRollup
	 */
	InventoryJournalRollup getRollupByUids(List<Long> journalUids);

	/**
	 * Delete rows from TINVENTORYJOURNAL.
	 * @param journalUids list of uids to remove.
	 */
	void removeAll(List<Long> journalUids);
	
	/**
	 * Deletes a journal row for a given inventory key.
	 * @param inventoryKey inventory key
	 */
	void removeByKey(InventoryKey inventoryKey);

	/**
	 * Get all distinct inventory keys from TINVENTORYJOURNAL table which has at least minimumRollupRows of rows. 
	 * @param minimumRollupRows the minimum rows for each key.
	 * @return List<InventoryKey>.
	 */
	List<InventoryKey> getAllInventoryKeys(int minimumRollupRows);

	/**
	 * Finds inventories which are low stock for the given set of sku codes.
	 * 
	 * @param skuCodes sku codes
	 * @param warehouseUid warehouse uid
	 * @return list of low stock inventory information
	 */
	List<InventoryDto> findLowStockInventories(Set<String> skuCodes, long warehouseUid);

}
