/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.domain.impl.InventoryJournalImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollup;
import com.elasticpath.inventory.strategy.InventoryJournalRollupService;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the implementation of {@link JournalingInventoryStrategy}.
 */
public class InventoryJournalRollupServiceTest extends BasicSpringContextTest {
	private final static Logger LOG = Logger.getLogger(InventoryJournalRollupServiceTest.class); 
	
	@Autowired private InventoryDao inventoryDao;
	@Autowired private InventoryJournalDao inventoryJournalDao;
	@Autowired private InventoryJournalLockDao inventoryJournalLockDao;
	@Autowired
	@Qualifier("inventoryJournalRollupService")
	private InventoryJournalRollupService inventoryJournalRollupService;
	@Autowired private PersistenceEngine persistenceEngine;
	@Autowired private JournalingInventoryStrategy journalingInventoryStrategy;
	
	InventoryKey inventoryKey1;
	InventoryKey inventoryKey2;

	InventoryKey inventoryKey3;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		inventoryKey1 = new InventoryKey("skuCode1", 10l);
		insertInventory(inventoryKey1, 2, 10);
		
		inventoryKey2 = new InventoryKey("skuCode2", 20l);
		insertInventory(inventoryKey2, 3, 15);
		
		insertInventoryJournal(inventoryKey1, 2, 0);
		insertInventoryJournal(inventoryKey1, 0, 3);
		insertInventoryJournal(inventoryKey1, -4, -4);
		
		insertInventoryJournal(inventoryKey2, 7, 0);
		insertInventoryJournal(inventoryKey2, 0, -2);
		insertInventoryJournal(inventoryKey2, -11, -11);
		
		// This journal only have one row, should not be touched.
		inventoryKey3 = new InventoryKey("sku3", 30l);
		insertInventoryJournal(inventoryKey3, 100, 0);
		
		insertInventoryJournalLock(inventoryKey1);
		
	}

	private void insertInventoryJournalLock(InventoryKey inventoryKey) {
		InventoryJournalLock inventoryJournalLock = getBeanFactory().getBean(ContextIdNames.INVENTORY_JOURNAL_LOCK);
		inventoryJournalLock.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournalLock.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryJournalLockDao.saveOrUpdate(inventoryJournalLock);
	}

	private void insertInventoryJournal(InventoryKey inventoryKey, int allocateDelta, int onHandDelta) {
		InventoryJournal inventoryJournal = new InventoryJournalImpl();
		inventoryJournal.setAllocatedQuantityDelta(allocateDelta);
		inventoryJournal.setQuantityOnHandDelta(onHandDelta);
		inventoryJournal.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournal.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryJournalDao.saveOrUpdate(inventoryJournal);
	}

	private void insertInventory(InventoryKey inventoryKey, int allocatedQuantity, int quantityOnHand) {
		InventoryImpl inventory = new InventoryImpl();
		inventory.setAllocatedQuantity(allocatedQuantity);
		inventory.setQuantityOnHand(quantityOnHand);
		inventory.setSkuCode(inventoryKey.getSkuCode());
		inventory.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryDao.saveOrUpdate(inventory);
	}
		
	/**
	 * rollup inventory journal.
	 */
	@DirtiesDatabase
	@Test
	public void testRollup() {
		// TINVENTORYJOURNAL shall have 6 rows.
		assertEquals(7, getJournalRowCount());
		
		// InventoryJournalRollupService.getAllInventoryKeys() shall only return keys which has more than 1 rows. 
		List<InventoryKey> allInventoryKeys = inventoryJournalRollupService.getAllInventoryKeys();
		assertEquals(2, allInventoryKeys.size());
		assertFalse(allInventoryKeys.contains(inventoryKey3));
		
		rollup(inventoryJournalRollupService);
		
		// TINVENTORYJOURNAL shall only have 3 rows now.
		assertEquals(3, getJournalRowCount());
				
		// TINVENTORY is not modified by roll up service.
		Inventory inventory1 = inventoryDao.getInventory(inventoryKey1.getSkuCode(), inventoryKey1.getWarehouseUid());
		assertEquals(2, inventory1.getAllocatedQuantity());
		assertEquals(10, inventory1.getQuantityOnHand());
		Inventory inventory2 = inventoryDao.getInventory(inventoryKey2.getSkuCode(), inventoryKey2.getWarehouseUid());
		assertEquals(3, inventory2.getAllocatedQuantity());
		assertEquals(15, inventory2.getQuantityOnHand());
		
		InventoryJournalRollup rollup = inventoryJournalDao.getRollup(inventoryKey1);
		assertEquals(-2, rollup.getAllocatedQuantityDelta());
		assertEquals(-1, rollup.getQuantityOnHandDelta());
		
		rollup = inventoryJournalDao.getRollup(inventoryKey2);
		assertEquals(-4, rollup.getAllocatedQuantityDelta());
		assertEquals(-13, rollup.getQuantityOnHandDelta());
		
		InventoryDto inventoryDto1 = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(0, inventoryDto1.getAllocatedQuantity());
		assertEquals(9, inventoryDto1.getQuantityOnHand());
		assertEquals(9, inventoryDto1.getAvailableQuantityInStock());
		
		InventoryDto inventoryDto2 = journalingInventoryStrategy.getInventory(inventoryKey2);
		assertEquals(-1, inventoryDto2.getAllocatedQuantity());
		assertEquals(2, inventoryDto2.getQuantityOnHand());
		assertEquals(3, inventoryDto2.getAvailableQuantityInStock());
	}
	
	private long getJournalRowCount() {
		List<Long> res = persistenceEngine.retrieve("SELECT COUNT(j) FROM InventoryJournalImpl j");
		
		if (res.isEmpty()) {
			return 0;
		}
		
		return res.get(0);
	}

	void rollup(InventoryJournalRollupService inventoryJournalRollupService) {
		LOG.info("InventoryJournal rollup started");
		List<InventoryKey> inventoryKeys = inventoryJournalDao.getAllInventoryKeys(0);
		for (InventoryKey inventoryKey : inventoryKeys) {
			try {
			inventoryJournalRollupService.processRollup(inventoryKey);
			} catch (JpaSystemException e) {
				LOG.info("InventoryJournal rollup contention, task terminated: " + e.getMostSpecificCause().getMessage());
			} catch (JpaOptimisticLockingFailureException e) {
				LOG.info("InventoryJournal rollup contention, task terminated: " + e.getMostSpecificCause().getMessage());
			}
		}
		LOG.info("InventoryJournal rollup ended");
	}
	
}
