/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests the implementation of {@link InventoryDao}.
 */
public class InventoryDaoImplTest extends BasicSpringContextTest {

	private static final String SKU_CODE1 = "skuCode1";
	private static final String SKU_CODE2 = "skuCode2";
	private static final String SKU_CODE3 = "skuCode3";	
	private static final long WAREHOUSE_10 = 10L;
	private static final long WAREHOUSE_100 = 100L;
	private static final long WAREHOUSE_1000 = 1000L;		
	@Autowired
	private InventoryDao inventoryDao;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		Date restockDate = getRestockDate();
		addInventory(SKU_CODE1, WAREHOUSE_10, 30, 3, 20, 10, 11, restockDate);
		addInventory(SKU_CODE2, WAREHOUSE_10, 14, 5, 60, 12, 13, restockDate);
		addInventory(SKU_CODE3, WAREHOUSE_100, 14, 5, 60, 12, 13, restockDate);
		addInventory(SKU_CODE3, WAREHOUSE_1000, 14, 5, 60, 12, 13, restockDate);
	}
	
	/**
	 * Test the retrieving low stock functionality for the inventory dao. 
	 */
	@DirtiesDatabase
	@Test
	public void testFindLowStockInventories() {
		List<InventoryDto> lowStockInventories = findLowStockInventories(WAREHOUSE_10, SKU_CODE1, SKU_CODE2);
		assertEquals(1, lowStockInventories.size());
		InventoryDto dto = lowStockInventories.get(0);
		int expectedQuantityOnHand = 14;
		int expectedAllocatedQuantity = 5;
		verifyQuantitiesForInventory(dto, expectedQuantityOnHand, expectedAllocatedQuantity);
	}
	
	/**
	 * Test the retrieving low stock functionality for the inventory dao with empty sku code list.
	 */
	@DirtiesDatabase
	@Test
	public void testFindLowStockInventoriesWithEmptySkuCodeList() {
		List<InventoryDto> lowStockInventories = findLowStockInventories(WAREHOUSE_10);
		assertEquals(1, lowStockInventories.size());
		InventoryDto dto = lowStockInventories.get(0);
		int expectedQuantityOnHand = 14;
		int expectedAllocatedQuantity = 5;
		verifyQuantitiesForInventory(dto, expectedQuantityOnHand, expectedAllocatedQuantity);
	}

	/**
	 * Test deletion of inventory.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		Inventory inventoryToDelete = inventoryDao.getInventory(SKU_CODE1, WAREHOUSE_10);
		inventoryDao.remove(inventoryToDelete);
		
		Inventory deletedInventory = inventoryDao.getInventory(SKU_CODE1, WAREHOUSE_10);
		assertTrue("This Inventory should be deleted.", deletedInventory == null);
		
		Inventory remainingInventory = inventoryDao.getInventory(SKU_CODE2, WAREHOUSE_10);
		assertTrue("This Inventory should not be deleted.", remainingInventory != null);
	}

	/**
	 * Test getting inventories by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetInventoriesForSku() {
		Map<Long, Inventory> inventoryMap = inventoryDao.getInventoriesForSku(SKU_CODE3);
		int expectedInventoryCount = 2;
		assertEquals("Map should contain "+ expectedInventoryCount +" entries", inventoryMap.size(), expectedInventoryCount);
		
		Inventory inventory = inventoryMap.get(WAREHOUSE_100);
		assertEquals(inventory.getSkuCode(), SKU_CODE3);

		inventory = inventoryMap.get(WAREHOUSE_1000);
		assertEquals(inventory.getSkuCode(), SKU_CODE3);
	}
	
	/**
	 * Testing getting a map of inventories keyed by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetInventoryMap() {
		Set<String> skuCodeSet = new HashSet<String>();
		skuCodeSet.add(SKU_CODE1);
		skuCodeSet.add(SKU_CODE2);
		
		Map<String, Inventory> inventoryMap = inventoryDao.getInventoryMap(skuCodeSet, WAREHOUSE_10);
		assertEquals("Inventory map size is incorrect", inventoryMap.size(), 2);
		
		Inventory inventory = inventoryMap.get(SKU_CODE1);
		assertEquals("Inventory for " + SKU_CODE1 + "does not match the one from the map.", inventory, inventoryDao.getInventory(SKU_CODE1, WAREHOUSE_10));

		inventory = inventoryMap.get(SKU_CODE2);
		assertEquals("Inventory for " + SKU_CODE2 + "does not match the one from the map.", inventory, inventoryDao.getInventory(SKU_CODE2, WAREHOUSE_10));
	}
	
	private void verifyQuantitiesForInventory(InventoryDto dto, int quantityOnHand, int allocatedQuantity) {
		assertEquals("On hand quantity doesn't match calculated value", 14, dto.getQuantityOnHand());
		assertEquals("Allocated quantity doesn't match calculated value", 5, dto.getAllocatedQuantity());
	}

	private void addInventory(final String skuCode, final long warehouseUid, final int quantityOnHand,
			final int allocatedQuantity, final int reorderMinimum, final int reservedQuantity,
			final int reorderQuantity, final Date restockDate) {
		InventoryImpl inventory = new InventoryImpl();
		inventory.setSkuCode(skuCode);
		inventory.setWarehouseUid(warehouseUid);
		inventory.setReorderMinimum(reorderMinimum);
		inventory.setReservedQuantity(reservedQuantity);
		inventory.setReorderQuantity(reorderQuantity);
		inventory.setRestockDate(restockDate);
		inventory.setQuantityOnHand(quantityOnHand);
		inventory.setAllocatedQuantity(allocatedQuantity);
		inventoryDao.saveOrUpdate(inventory);
	}
	
	private Date getRestockDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2011);
		calendar.set(Calendar.MONTH, 4);
		calendar.set(Calendar.DATE, 31);
		return calendar.getTime();
	}
	
	private List<InventoryDto> findLowStockInventories(long warehouseUid, String ... skuCodes) {
		Set<String> skuCodeSet = new HashSet<String>();
		for (String skuCode : skuCodes) {
			skuCodeSet.add(skuCode);
		}
		List<InventoryDto> inventories = inventoryDao.findLowStockInventories(skuCodeSet, WAREHOUSE_10);
		return inventories;
	}
	
}
