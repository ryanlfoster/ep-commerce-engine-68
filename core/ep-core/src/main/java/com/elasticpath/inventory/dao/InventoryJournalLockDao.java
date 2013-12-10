package com.elasticpath.inventory.dao;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.domain.InventoryJournalLock;

/**
 * Inventory Journal Lock DAO.
 */
public interface InventoryJournalLockDao {

	/**
	 * Save or update an InventoryJournalLock.
	 * @param inventoryJournalLock InventoryJournalLock.
	 * @return updated InventoryJournalLock.
	 */
	InventoryJournalLock saveOrUpdate(InventoryJournalLock inventoryJournalLock);

	/**
	 * Get an InventoryJournalLock.
	 * @param inventoryKey the inventory key.
	 * @return InventoryJournalLock.
	 */
	InventoryJournalLock getInventoryJournalLock(InventoryKey inventoryKey);

	/**
	 * Deletes a journal lock for a given inventory key.
	 * @param inventoryKey inventory key
	 */
	void removeByKey(InventoryKey inventoryKey);

}