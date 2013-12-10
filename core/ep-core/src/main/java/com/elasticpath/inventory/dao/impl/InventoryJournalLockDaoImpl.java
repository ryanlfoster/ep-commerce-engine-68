/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.dao.impl;

import java.util.List;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;

/**
 * Data access methods for <code>InventoryJournal</code>.
 */
public class InventoryJournalLockDaoImpl extends AbstractDaoImpl implements InventoryJournalLockDao {

	@Override
	public InventoryJournalLock saveOrUpdate(final InventoryJournalLock inventoryJournalLock) {
		return getPersistenceEngine().saveOrUpdate(inventoryJournalLock);
	}

	@Override
	public InventoryJournalLock getInventoryJournalLock(final InventoryKey inventoryKey) {
		final List<InventoryJournalLock> result = getPersistenceEngine().retrieveByNamedQuery("INVENTORY_JOURNAL_LOCK_BY_SKUCODE_AND_WAREHOUSE_UID",
				inventoryKey.getSkuCode(),
				inventoryKey.getWarehouseUid());
		
		if (result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
	}
	
	@Override
	public void removeByKey(final InventoryKey inventoryKey) {
		getPersistenceEngine().executeNamedQuery("INVENTORY_JOURNAL_LOCK_DELETE_BY_SKUCODE_AND_WAREHOUSE_UID",
				inventoryKey.getSkuCode(),
				inventoryKey.getWarehouseUid());
	}

}
