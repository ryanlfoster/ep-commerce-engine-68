/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.inventory.strategy.impl;

import java.util.List;

import org.apache.log4j.Level;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.log.impl.InventoryLogContext;
import com.elasticpath.inventory.log.impl.InventoryLogSupport;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.InventoryJournalRollupService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * This service do the rollup job for a single inventory key.
 * 
 */
public class InventoryJournalRollupServiceImpl extends AbstractEpServiceImpl implements InventoryJournalRollupService {

	/** 
	 * Minimum roll up rows. If there is only one row, not necessary to rollup.
	 * Furthermore, this number can be increased to improve performance a little bit.
	 */
	protected static final int MINIMUM_ROLLUP_ROWS = 2;

	private InventoryJournalDao inventoryJournalDao;
	
	private InventoryJournalLockDao inventoryJournalLockDao;
	
	private InventoryLogSupport inventoryLogSupport;
	
	/**
	 * @param inventoryLogSupport the inventoryLogSupport to set
	 */
	public void setInventoryLogSupport(final InventoryLogSupport inventoryLogSupport) {
		this.inventoryLogSupport = inventoryLogSupport;
	}
	
	/**
	 * @param journalDao the journalDao to set
	 */
	public void setInventoryJournalDao(final InventoryJournalDao journalDao) {
		this.inventoryJournalDao = journalDao;
	}

	/**
	 * @param inventoryJournalLockDao the inventoryJournalLockDao to set
	 */
	public void setInventoryJournalLockDao(final InventoryJournalLockDao inventoryJournalLockDao) {
		this.inventoryJournalLockDao = inventoryJournalLockDao;
	}

	/**
	 * @return the inventoryJournalDao
	 */
	public InventoryJournalDao getInventoryJournalDao() {
		return inventoryJournalDao;
	}

	/**
	 * @return the inventoryJournalLockDao
	 */
	public InventoryJournalLockDao getInventoryJournalLockDao() {
		return inventoryJournalLockDao;
	}

	@Override
	public void processRollup(final InventoryKey inventoryKey) {
		// This method is configured in service.xml under transaction.
		// InventoryJournalLock has a version field, OpenJPA does optimistic lock on it.
		InventoryJournalLock inventoryJournalLock = inventoryJournalLockDao.getInventoryJournalLock(inventoryKey);
		if (inventoryJournalLock == null) {
			inventoryJournalLock = createInventoryJournalLock(inventoryKey);
		}
		
		List<Long> journalUids = inventoryJournalDao.getUidsByKey(inventoryKey);
		
		// sums the rows in TINVENTORYJOURNALLOCK table.
		InventoryJournalRollup rollup = inventoryJournalDao.getRollupByUids(journalUids);
		
		// rollup may be null, if another rollup thread have done the job (through the removeAll() method below)
		if (rollup != null) {
			// insert a new row with the summed values.
			InventoryJournal summedInventoryJournal = createInventoryJournal(rollup);
			inventoryJournalDao.saveOrUpdate(summedInventoryJournal);
		}		
		
		// delete summed rows.
		inventoryJournalDao.removeAll(journalUids);
		
		// This forces OpenJPA to update TINVENTORYJOURNALLOCK table.
		inventoryJournalLock.setLockCount(inventoryJournalLock.getLockCount() + 1);

		// This will throw an Optimistic Exception if there is conflict with another rollup job.
		// And then the whole work within this method will be rolled back.
		inventoryJournalLockDao.saveOrUpdate(inventoryJournalLock);
		
		trace(inventoryKey, rollup, journalUids.size());
	}

	private void trace(final InventoryKey inventoryKey, final InventoryJournalRollup rollup, final int rolledUpRows) {
		if (inventoryLogSupport.isEnabledFor(Level.TRACE) && rollup != null) {
			InventoryLogContext inventoryLogContext = new InventoryLogContext(inventoryKey);
			inventoryLogContext.addContextAttribute("QuantityOnHandDelta", rollup.getQuantityOnHandDelta());
			inventoryLogContext.addContextAttribute("AllocatedQuantityDelta", rollup.getAllocatedQuantityDelta());
			inventoryLogContext.addContextAttribute("RolledUpRows", rolledUpRows);
			inventoryLogSupport.log(Level.TRACE, 
					InventoryLogContext.ROLLUP_TRACE_MSG,
					inventoryLogContext);
		}
	}
	
	/**
	 * Create a new inventory journal lock.
	 * @param inventoryKey the inventory key.
	 * @return InventoryJournalLock
	 */
	protected InventoryJournalLock createInventoryJournalLock(final InventoryKey inventoryKey) {
		InventoryJournalLock inventoryJournalLock = getBean(ContextIdNames.INVENTORY_JOURNAL_LOCK);
		inventoryJournalLock.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournalLock.setWarehouseUid(inventoryKey.getWarehouseUid());
		return inventoryJournalLock;
	}

	/**
	 * @param rollup InventoryJournalRollup
	 * @return InventoryJournal.
	 */
	protected InventoryJournal createInventoryJournal(final InventoryJournalRollup rollup) {
		InventoryJournal summedInventoryJournal = getBean(ContextIdNames.INVENTORY_JOURNAL);
		summedInventoryJournal.setSkuCode(rollup.getInventoryKey().getSkuCode());
		summedInventoryJournal.setWarehouseUid(rollup.getInventoryKey().getWarehouseUid());
		summedInventoryJournal.setAllocatedQuantityDelta(rollup.getAllocatedQuantityDelta());
		summedInventoryJournal.setQuantityOnHandDelta(rollup.getQuantityOnHandDelta());
		return summedInventoryJournal;
	}

	@Override
	public List<InventoryKey> getAllInventoryKeys() {
		return inventoryJournalDao.getAllInventoryKeys(MINIMUM_ROLLUP_ROWS);
	}
	
}
