/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryExecutionResult;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.dao.InventoryJournalLockDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.InventoryJournalLock;
import com.elasticpath.inventory.domain.impl.InventoryJournalLockImpl;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.inventory.strategy.InventoryJournalRollupService;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the implementation of {@link JournalingInventoryStrategy}.
 */
public class JournalingInventoryStrategyTest extends BasicSpringContextTest {

	@Autowired
	private InventoryDao inventoryDao;
	@Autowired
	private InventoryJournalDao inventoryJournalDao;
	@Autowired
	private InventoryJournalLockDao inventoryJournalLockDao;
	@Autowired
	private JournalingInventoryStrategy journalingInventoryStrategy;
	InventoryKey inventoryKey1;
	InventoryKey inventoryKey2;
	InventoryDtoImpl inventoryDto1;
	InventoryDtoImpl inventoryDto2;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		InventoryCommand command = null;
		
		inventoryKey1 = new InventoryKey("skuCode1", 10l);
		inventoryDto1 = new InventoryDtoImpl();
		
		inventoryDto1.setSkuCode(inventoryKey1.getSkuCode());
		inventoryDto1.setWarehouseUid(inventoryKey1.getWarehouseUid());
		
		inventoryKey2 = new InventoryKey("skuCode2", 20l);
		inventoryDto2 = new InventoryDtoImpl();
		inventoryDto2.setSkuCode(inventoryKey2.getSkuCode());
		inventoryDto2.setWarehouseUid(inventoryKey2.getWarehouseUid());
		
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto2);
		journalingInventoryStrategy.executeCommand(command);
		
		allocate(inventoryKey1, 2);
		adjust(inventoryKey1, 3);
		allocate(inventoryKey2, 7);
		adjust(inventoryKey2, 11);
	}
	
	private void allocate(final InventoryKey key, final int qty) {
		InventoryCommand allocateInventoryCommand = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(key, qty);
		journalingInventoryStrategy.executeCommand(allocateInventoryCommand);
	}
	
	private void adjust(final InventoryKey key, final int qty) {
		InventoryCommand adjustInventoryCommand = journalingInventoryStrategy.getCommandFactory().getAdjustInventoryCommand(key, qty);
		journalingInventoryStrategy.executeCommand(adjustInventoryCommand);
	}
	
	/**
	 * Create and allocate a single inventory item.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateAndAllocateSingleInventory() {
		InventoryCommand command = null;
		InventoryExecutionResult executionResult = null;
		InventoryDto inventoryDtoResult = null;
		
		// Create the inventory.
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		executionResult = command.getExecutionResult();
		inventoryDtoResult = journalingInventoryStrategy.getInventory(inventoryKey1);
		
		assertEquals(2, inventoryDtoResult.getAllocatedQuantity());
		assertEquals(1, inventoryDtoResult.getAvailableQuantityInStock());
		assertEquals(3, inventoryDtoResult.getQuantityOnHand());
		assertEquals(0, executionResult.getQuantity());
		
		// Allocate some inventory.
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey1, 5);
		journalingInventoryStrategy.executeCommand(command);
		executionResult = command.getExecutionResult();
		inventoryDtoResult = journalingInventoryStrategy.getInventory(inventoryKey1);
		
		assertEquals(7, inventoryDtoResult.getAllocatedQuantity());
		assertEquals(-4, inventoryDtoResult.getAvailableQuantityInStock());
		assertEquals(3, inventoryDtoResult.getQuantityOnHand());
		assertEquals(5, executionResult.getQuantity());
		
		// The allocation only exists in the journaling table.
		// Read the Inventory directly and assert that it hasn't changed.
		Inventory inventory = inventoryDao.getInventory(inventoryKey1.getSkuCode(), inventoryKey1.getWarehouseUid());
		assertEquals(0, inventory.getAllocatedQuantity());
		assertEquals(0, inventory.getQuantityOnHand());
	}
	
	/**
	 * Create and delete a single inventory.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateAndDeleteSingleInventory() {
		InventoryCommand command = null;
		InventoryDto inventoryDtoResult = null;
		
		// Create the inventory.
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		command.getExecutionResult();
		inventoryDtoResult = journalingInventoryStrategy.getInventory(inventoryKey1);
		
		assertEquals(2, inventoryDtoResult.getAllocatedQuantity());
		assertEquals(1, inventoryDtoResult.getAvailableQuantityInStock());
		assertEquals(3, inventoryDtoResult.getQuantityOnHand());
		
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey1, 5);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getDeleteInventoryCommand(inventoryKey1);
		journalingInventoryStrategy.executeCommand(command);
		
		assertNull(journalingInventoryStrategy.getInventory(inventoryKey1));
	}
	
	/**
	 * Create and allocate two Inventories and then assert that the various getInventory() methods work.
	 */
	@DirtiesDatabase
	@Test
	public void testGetInventories() {
		InventoryCommand command = null;
		
		// Create the inventory.
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto2);
		journalingInventoryStrategy.executeCommand(command);
		
		// Allocate some inventory.
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey1, 37);
		journalingInventoryStrategy.executeCommand(command);
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey2, 41);
		journalingInventoryStrategy.executeCommand(command);
		
		// Read the inventories from the Strategy which includes the rollups.
		assertEquals(-36, journalingInventoryStrategy.getInventory(inventoryKey1).getAvailableQuantityInStock());
		assertEquals(-37, journalingInventoryStrategy.getInventory(inventoryKey2).getAvailableQuantityInStock());
		
		// Read the inventories from the Strategy which includes the rollups.
		Set<InventoryKey> inventoryKeys = new HashSet<InventoryKey>();
		inventoryKeys.add(inventoryKey1);
		inventoryKeys.add(inventoryKey2);
		Map<InventoryKey, InventoryDto> results = journalingInventoryStrategy.getInventories(inventoryKeys);
		assertEquals(-36, results.get(inventoryKey1).getAvailableQuantityInStock());
		assertEquals(-37, results.get(inventoryKey2).getAvailableQuantityInStock());
		
		Map<Long, InventoryDto> inventoriesForSku = journalingInventoryStrategy.getInventoriesForSku(inventoryKey1.getSkuCode());
		assertEquals(1, inventoriesForSku.size());
		InventoryDto inventoryDto = inventoriesForSku.values().iterator().next();
		assertEquals(-36, inventoryDto.getAvailableQuantityInStock());
		
		Set<String> skuCodes = new HashSet<String>();
		skuCodes.add(inventoryKey1.getSkuCode());
		skuCodes.add(inventoryKey2.getSkuCode());
		Map<String, InventoryDto> inventoriesForSkusInWarehouse = journalingInventoryStrategy.getInventoriesForSkusInWarehouse(skuCodes, inventoryKey1.getWarehouseUid());
		assertEquals(1, inventoriesForSkusInWarehouse.size());
		assertEquals(-36, inventoriesForSkusInWarehouse.get(inventoryKey1.getSkuCode()).getAvailableQuantityInStock());
		
		inventoriesForSkusInWarehouse = journalingInventoryStrategy.getInventoriesForSkusInWarehouse(skuCodes, inventoryKey2.getWarehouseUid());
		assertEquals(1, inventoriesForSkusInWarehouse.size());
		assertEquals(-37, inventoriesForSkusInWarehouse.get(inventoryKey2.getSkuCode()).getAvailableQuantityInStock());
		
		
		// testing for rollup service
		// For this case, because for each inventory key there are only one row in TINVENTORYJOURNAL
		// They shall be not touched.
		InventoryJournalRollupService rollupService = getBeanFactory().getBean("inventoryJournalRollupService");
		rollup(rollupService);
		
		List<InventoryKey> allInventoryKeys = inventoryJournalDao.getAllInventoryKeys(0);
		assertEquals(2, allInventoryKeys.size());
		
		Inventory inventory1 = inventoryDao.getInventory(inventoryKey1.getSkuCode(), inventoryKey1.getWarehouseUid());
		assertEquals(0, inventory1.getAllocatedQuantity());
		assertEquals(0, inventory1.getQuantityOnHand());
		Inventory inventory2 = inventoryDao.getInventory(inventoryKey2.getSkuCode(), inventoryKey2.getWarehouseUid());
		assertEquals(0, inventory2.getAllocatedQuantity());
		assertEquals(0, inventory2.getQuantityOnHand());
	}
	
	void rollup(final InventoryJournalRollupService rollupService) {
		List<InventoryKey> inventoryKeys = inventoryJournalDao.getAllInventoryKeys(0);
		for (InventoryKey inventoryKey : inventoryKeys) {
			rollupService.processRollup(inventoryKey);
		}
	}
	
	/**
	 * Tests that availability quantity is calculated correctly and is not negative if we release allocated inventory.
	 */
	@DirtiesDatabase
	@Test
	public void testNonNegativeAvailabilityAfterInventoryRelease() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getReleaseInventoryCommand(inventoryKey1, 2);
		assertEquals(1, journalingInventoryStrategy.getInventory(inventoryKey1).getAvailableQuantityInStock());
	}

	/**
	 * Tests that availability quantity is negative (we oversell) if we release allocated inventory more than was allocated.
	 */
	@DirtiesDatabase
	@Test
	public void testNegativeAvailabilityAfterInventoryRelease() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto2);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getReleaseInventoryCommand(inventoryKey2, 5);
		journalingInventoryStrategy.executeCommand(command);
		InventoryDto inventoryReleased = journalingInventoryStrategy.getInventory(inventoryKey2);
		assertEquals(2, inventoryReleased.getAllocatedQuantity());
		assertEquals(6, inventoryReleased.getQuantityOnHand());
		assertEquals(4, inventoryReleased.getAvailableQuantityInStock());
		
		command = journalingInventoryStrategy.getCommandFactory().getReleaseInventoryCommand(inventoryKey2, 7);
		journalingInventoryStrategy.executeCommand(command);	
		inventoryReleased = journalingInventoryStrategy.getInventory(inventoryKey2);
		assertEquals(-5, inventoryReleased.getAllocatedQuantity());
		assertEquals(-1, inventoryReleased.getQuantityOnHand());
		assertEquals(4, inventoryReleased.getAvailableQuantityInStock());
	}
	
	/**
	 * Deallocates inventory and checks that quantity on hand changes appropriately.
	 */
	@DirtiesDatabase
	@Test
	public void testDeallocateInventory() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getDeallocateInventoryCommand(inventoryKey1, 1);
		journalingInventoryStrategy.executeCommand(command);
		
		InventoryDto inventoryDealloc = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(1, inventoryDealloc.getAllocatedQuantity());
		assertEquals(2, inventoryDealloc.getAvailableQuantityInStock());
		assertEquals(3, inventoryDealloc.getQuantityOnHand());
	}
	
	/**
	 * An exception should be thrown if we deallocate a negative qty.
	 */
	@DirtiesDatabase
	@Test(expected = IllegalArgumentException.class)
	public void testDeallocateWithNegativeQty() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getDeallocateInventoryCommand(inventoryKey1, -1);
	}

	/**
	 * An exception should be thrown if we allocate a negative qty.
	 */
	@DirtiesDatabase
	@Test(expected = IllegalArgumentException.class)
	public void testAllocateWithNegativeQty() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey1, -1);
	}

	/**
	 * An exception should be thrown if we release a zero qty.
	 */
	@DirtiesDatabase
	@Test(expected = IllegalArgumentException.class)
	public void testReleaseWithZeroQty() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getReleaseInventoryCommand(inventoryKey1, 0);
	}
	
	/**
	 * An exception should be thrown if we release a negative qty.
	 */
	@DirtiesDatabase
	@Test(expected = IllegalArgumentException.class)
	public void testReleaseWithNegativeQty() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getReleaseInventoryCommand(inventoryKey1, -1);
	}
	
	/**
	 * Deallocates inventory more than was allocated and checks that allocated quantity is zero in this case.
	 */
	@DirtiesDatabase
	@Test
	public void testDeallocateInventoryMoreThanWasAllocated() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getDeallocateInventoryCommand(inventoryKey1, 3);
		journalingInventoryStrategy.executeCommand(command);
		
		InventoryDto inventoryDealloc = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(-1, inventoryDealloc.getAllocatedQuantity());
		assertEquals(4, inventoryDealloc.getAvailableQuantityInStock());
		assertEquals(3, inventoryDealloc.getQuantityOnHand());
	}
	
	/**
	 * Deallocates inventory more than was allocated, then allocates some back.
	 * Allocated qty should be tracked properly when it was negative and not reset to 0.
	 */
	@DirtiesDatabase
	@Test
	public void testDeallocateAndAllocateInventory() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getDeallocateInventoryCommand(inventoryKey1, 3);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getAllocateInventoryCommand(inventoryKey1, 2);
		journalingInventoryStrategy.executeCommand(command);
		
		InventoryDto inventoryDealloc = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(1, inventoryDealloc.getAllocatedQuantity());
		assertEquals(2, inventoryDealloc.getAvailableQuantityInStock());
		assertEquals(3, inventoryDealloc.getQuantityOnHand());
	}
	
	/**
	 * Adding and removing stock, then check that on-hand and available quantities change appropriately. Allocated qty should stay the same.
	 */
	@DirtiesDatabase
	@Test
	public void testAdjustInventoryAddAndRemoveStock() {
		InventoryCommand command = journalingInventoryStrategy.getCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto1);
		journalingInventoryStrategy.executeCommand(command);
		
		command = journalingInventoryStrategy.getCommandFactory().getAdjustInventoryCommand(inventoryKey1, 3);
		journalingInventoryStrategy.executeCommand(command);
		
		InventoryDto inventoryAdjusted = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(2, inventoryAdjusted.getAllocatedQuantity());
		assertEquals(4, inventoryAdjusted.getAvailableQuantityInStock());
		assertEquals(6, inventoryAdjusted.getQuantityOnHand());
		
		command = journalingInventoryStrategy.getCommandFactory().getAdjustInventoryCommand(inventoryKey1, -3);
		journalingInventoryStrategy.executeCommand(command);
		
		inventoryAdjusted = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(2, inventoryAdjusted.getAllocatedQuantity());
		assertEquals(1, inventoryAdjusted.getAvailableQuantityInStock());
		assertEquals(3, inventoryAdjusted.getQuantityOnHand());
		
		command = journalingInventoryStrategy.getCommandFactory().getAdjustInventoryCommand(inventoryKey1, -5);
		journalingInventoryStrategy.executeCommand(command);
		
		inventoryAdjusted = journalingInventoryStrategy.getInventory(inventoryKey1);
		assertEquals(2, inventoryAdjusted.getAllocatedQuantity());
		assertEquals(-4, inventoryAdjusted.getAvailableQuantityInStock());
		assertEquals(-2, inventoryAdjusted.getQuantityOnHand());

	}
	
	/**
	 * Tests delete inventory command. It should delete the inventory record + related to it journaling rows and locks.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteInventoryCommand() {
		InventoryJournalLock lock = createJournalLock();		
		inventoryJournalLockDao.saveOrUpdate(lock);
		
		InventoryDto dto = journalingInventoryStrategy.getInventory(inventoryKey1);
		List<Long> uidsByKey = inventoryJournalDao.getUidsByKey(inventoryKey1);
		lock = inventoryJournalLockDao.getInventoryJournalLock(inventoryKey1);
		
		//assert that inventory, journal and lock records exist
		assertNotNull(dto); 
		assertNotNull(lock);
		assertFalse(uidsByKey.isEmpty());
		
		//delete inventory
		InventoryCommand deleteCommand = journalingInventoryStrategy.getCommandFactory().getDeleteInventoryCommand(inventoryKey1);
		journalingInventoryStrategy.executeCommand(deleteCommand); 
		
		dto = journalingInventoryStrategy.getInventory(inventoryKey1); 
		uidsByKey = inventoryJournalDao.getUidsByKey(inventoryKey1); 
		lock = inventoryJournalLockDao.getInventoryJournalLock(inventoryKey1);
		
		//assert that that inventory, journal and lock records exist records exist anymore
		assertNull(dto);
		assertNull(lock);
		assertTrue(uidsByKey.isEmpty());
	}

	private InventoryJournalLock createJournalLock() {
		InventoryJournalLock lock = new InventoryJournalLockImpl();
		lock.setLockCount(1);
		lock.setSkuCode(inventoryKey1.getSkuCode());
		lock.setWarehouseUid(inventoryKey1.getWarehouseUid());
		return lock;
	}
	
}
