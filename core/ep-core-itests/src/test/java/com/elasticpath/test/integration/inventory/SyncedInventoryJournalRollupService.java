package com.elasticpath.test.integration.inventory;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.strategy.InventoryJournalRollupService;

/**
 * Only for testing concurrent rollup.
 * @author mren
 *
 */
public interface SyncedInventoryJournalRollupService extends InventoryJournalRollupService{
	
	/**
	 * Do rollup with inventory.
	 * @param inventoryKey the key of inventory.
	 * @param lockWrapper the synchronization controller.
	 */
	void processRollup(InventoryKey inventoryKey, LockWrapper lockWrapper);
	
}
