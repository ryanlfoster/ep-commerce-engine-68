/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.inventory;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.domain.impl.InventoryJournalLockImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupServiceImpl;

/**
 * Testing concurrent rollup.
 * This is for the second thread.
 * 
 */
public class SyncedInventoryJournalRollupServiceImpl2 extends InventoryJournalRollupServiceImpl implements SyncedInventoryJournalRollupService {
	
	private static final Logger LOG = Logger.getLogger(SyncedInventoryJournalRollupServiceImpl2.class); 
	
	@Override
	public void processRollup(final InventoryKey inventoryKey, final LockWrapper lockWrapper) {
		lockWrapper.getLock().lock();
		
		try {
			
			// This method is configured in service.xml under transaction.
			// InventoryJournalLock has a version field, OpenJPA does optimistic lock on it.
			InventoryJournalLock inventoryJournalLock = getInventoryJournalLockDao().getInventoryJournalLock(inventoryKey);
			if (inventoryJournalLock == null) {
				inventoryJournalLock = createInventoryJournalLock(inventoryKey);
			}
			
			List<Long> journalUids = getInventoryJournalDao().getUidsByKey(inventoryKey);
			
			// set thread2 get uids done.
			lockWrapper.setThread2GetUidsFinished(true);
			// notify others.
			lockWrapper.getThread2GetUidsCondition().signal();
			
			// wait for thread1 to finish process rollup
			LOG.info("MR: Waiting thread1 to process rollup..." + inventoryKey);
			while (!lockWrapper.isThread1ProcessRollupFinished()) {
				try {
					lockWrapper.getThread1ProcessRollupCondition().await();
				} catch (InterruptedException e) {
					LOG.info(e);
				}
			}
			LOG.info("MR: Notified by thread1 after process rollup done.");
			
			// sums the rows in TINVENTORYJOURNALLOCK table.
			InventoryJournalRollup rollup = getInventoryJournalDao().getRollupByUids(journalUids);
			
			// rollup may be null, if another rollup thread have done the job (through the removeAll() method below)
			LOG.info("MR: We shall expect rollup is null in thread 2: " + rollup);
			if (rollup != null) {
				// insert a new row with the summed values.
				InventoryJournal summedInventoryJournal = createInventoryJournal(rollup);
				getInventoryJournalDao().saveOrUpdate(summedInventoryJournal);
			}		
			
			// delete summed rows.
			getInventoryJournalDao().removeAll(journalUids);
			
			// This forces OpenJPA to update TINVENTORYJOURNALLOCK table.
			inventoryJournalLock.setLockCount(inventoryJournalLock.getLockCount() + 1);
	
			// This will throw an Optimistic Exception if there is conflict with another rollup job.
			// And then the whole work within this method will be rolled back.
			InventoryJournalLockImpl updatedJournalLock = (InventoryJournalLockImpl) getInventoryJournalLockDao().saveOrUpdate(inventoryJournalLock);
			LOG.info("MR: Thread2 saved journal lock with version: " + updatedJournalLock.getVersion() + " " + updatedJournalLock);
			// set thread2 processRollup job done
			lockWrapper.setThread2ProcessRollupFinished(true);
			// notify others
			lockWrapper.getThread2ProcessRollupCondition().signal();
		} catch (RuntimeException e) {
			LOG.info("Error!" + e);
			throw e;
		} finally {
			lockWrapper.getLock().unlock();
	    }
		
	}

}
