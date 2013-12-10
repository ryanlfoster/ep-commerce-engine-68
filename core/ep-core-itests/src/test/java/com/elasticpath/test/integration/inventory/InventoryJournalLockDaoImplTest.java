package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.dao.impl.InventoryJournalLockDaoImpl;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.domain.impl.InventoryJournalLockImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests for {@link InventoryJournalLockDaoImpl}. 
 */
public class InventoryJournalLockDaoImplTest extends BasicSpringContextTest {

	private static final long WAREHOUSE_10 = 10L;
	private static final String SKU_CODE1 = "skuCode1";

	@Autowired
	private InventoryJournalLockDao inventoryJournalLockDao;
	
	/**
	 * Sets up the test data.
	 */
	@Before
	public void setUp() {
		InventoryJournalLock lock = new InventoryJournalLockImpl();
		lock.setLockCount(1);
		lock.setSkuCode(SKU_CODE1);
		lock.setWarehouseUid(WAREHOUSE_10);
		
		inventoryJournalLockDao.saveOrUpdate(lock);
	}
	
	/**
	 * Tests that we can remove inventory journal locks by keys. 
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveByKey() {
		InventoryKey key = new InventoryKey(SKU_CODE1, WAREHOUSE_10);
		
		InventoryJournalLock inventoryJournalLock = inventoryJournalLockDao.getInventoryJournalLock(key);
		assertNotNull(inventoryJournalLock);
		
		inventoryJournalLockDao.removeByKey(key);
		inventoryJournalLock = inventoryJournalLockDao.getInventoryJournalLock(key);
		assertNull(inventoryJournalLock);
	}
}
