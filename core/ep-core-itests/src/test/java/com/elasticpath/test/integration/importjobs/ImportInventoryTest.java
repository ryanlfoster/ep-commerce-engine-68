package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for inventories.
 */
public class ImportInventoryTest extends ImportJobTestCase {

	@Autowired
	private Utility utility;

	@Autowired
	private ProductInventoryManagementService productInventoryManagementService;

	@Autowired
	private InventoryJournalDao ijDao;

	/**
	 * Test import Inventory insert with an old InventoryJournal entry present.
	 */
	@DirtiesDatabase
	@Test
	public void testImportInventoryInsert() throws Exception {
		createCategoryAndImportProducts();
		
		// Manually create an InventoryJournal entry and assert it exists.
		InventoryKey inventoryKey = new InventoryKey("SKU101", scenario.getWarehouse().getUidPk());
		InventoryJournal inventoryJournal = getBeanFactory().getBean(ContextIdNames.INVENTORY_JOURNAL);
		inventoryJournal.setAllocatedQuantityDelta(2);
		inventoryJournal.setQuantityOnHandDelta(3);
		inventoryJournal.setSkuCode(inventoryKey.getSkuCode());
		inventoryJournal.setWarehouseUid(inventoryKey.getWarehouseUid());
		inventoryJournal = ijDao.saveOrUpdate(inventoryJournal);
		
		List<Long> ijUidpks = ijDao.getUidsByKey(inventoryKey);
		assertTrue(ijUidpks.contains(inventoryJournal.getUidPk()));
		
		/** import inventory for the skus. */
		executeImportJob(createInsertInventoryImportJob());
		
		// The import should have deleted the InventoryJournal entry because the Inventory entry didn't exist when the import happened.
		ijUidpks = ijDao.getUidsByKey(inventoryKey);
		assertFalse(ijUidpks.contains(inventoryJournal.getUidPk()));
		
		InventoryDto inventoryDto = getInventory("SKU101");
		/** compare values with ones from csv file. */
		assertEquals(86, inventoryDto.getQuantityOnHand());
		assertEquals(10, inventoryDto.getReservedQuantity());
		assertEquals(22, inventoryDto.getReorderMinimum());
		assertEquals(3, inventoryDto.getReorderQuantity());
		assertEquals(string2Date("Wed Mar 16 15:49:37 2009"), inventoryDto.getRestockDate());
	}

	/**
	 * Test import Inventory insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportInventoryInsertUpdate() throws Exception {
		createCategoryAndImportProducts();

		executeImportJob(createInsertInventoryImportJob());
		executeImportJob(createInsertUpdateInventoryImportJob());

		InventoryDto inventoryDto = getInventory("SKU101");
		assertNotNull("Failed to load inventory for sku [SKU101]", inventoryDto);
		/** assert existing inventory was not changed during update */
		assertEquals(86, inventoryDto.getQuantityOnHand());
		assertEquals(10, inventoryDto.getReservedQuantity());
		assertEquals(22, inventoryDto.getReorderMinimum());
		assertEquals(3, inventoryDto.getReorderQuantity());
		Date march200916154937 = string2Date("Wed Mar 16 15:49:37 2009");
		assertEquals(march200916154937, inventoryDto.getRestockDate());

		/** assert existing inventory has been updated during import */
		inventoryDto = getInventory("SKU102");
		assertNotNull("Failed to load inventory for sku [SKU102]", inventoryDto);
		assertEquals(99, inventoryDto.getQuantityOnHand());
		assertEquals(11, inventoryDto.getReservedQuantity());
		assertEquals(25, inventoryDto.getReorderMinimum());
		assertEquals(5, inventoryDto.getReorderQuantity());
		Date date20090416154937 = string2Date("Wed Apr 16 15:49:37 2009");
		assertEquals(date20090416154937, inventoryDto.getRestockDate());

		/** assert new inventory has been created during import */
		inventoryDto = getInventory("SKU104");
		assertNotNull("Failed to load inventory for sku [SKU104]", inventoryDto);
		assertEquals(21, inventoryDto.getQuantityOnHand());
		assertEquals(10, inventoryDto.getReservedQuantity());
		assertEquals(36, inventoryDto.getReorderMinimum());
		assertEquals(3, inventoryDto.getReorderQuantity());
		assertEquals(march200916154937, inventoryDto.getRestockDate());

	}

	/**
	 * Test import Inventory update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportInventoryUpdate() throws Exception {
		createCategoryAndImportProducts();

		executeImportJob(createInsertInventoryImportJob());
		executeImportJob(createUpdateInventoryImportJob());

		/** assert existing inventory has been updated during import */
		InventoryDto inventoryDto = getInventory("SKU101");
		assertNotNull("Failed to load inventory for sku [SKU101]", inventoryDto);
		assertEquals(99, inventoryDto.getQuantityOnHand());
		assertEquals(11, inventoryDto.getReservedQuantity());
		assertEquals(25, inventoryDto.getReorderMinimum());
		assertEquals(5, inventoryDto.getReorderQuantity());
		assertEquals(string2Date("Wed Apr 16 15:49:37 2009"), inventoryDto.getRestockDate());
	}

	/**
	 * Test input Inventory delete.
	 */
	@DirtiesDatabase
	@Test
	public void testImportInventoryDelete() throws Exception {
		createCategoryAndImportProducts();

		executeImportJob(createInsertInventoryImportJob());
		executeImportJob(createDeleteInventoryImportJob());

		assertNotNull(getInventory("SKU101"));
		assertNull(getInventory("SKU102"));
	}

	public void testImportInventoryAfterOversell() throws Exception {
		createCategoryAndImportProducts();

		// "productSku"|"quantityOnHand"|"reservedQuantity"|"reorderMinimum"|"reorderQuantity"|"restockDate"
		// "SKU101"    |86              |10                |22              |3                |"Wed Mar 16 15:49:37 2009"
		// "SKU102"    |21              |10                |36              |3                |"Wed Mar 16 15:49:37 2008"
		executeImportJob(createInsertInventoryImportJob());

		// Manually reduce stock to simulate an oversell.
		Long warehouseUid = scenario.getWarehouse().getUidPk();
		InventoryDto inventoryDto = productInventoryManagementService.getInventory("SKU102", warehouseUid);
		inventoryDto.setQuantityOnHand(-100);
		productInventoryManagementService.merge(inventoryDto);

		inventoryDto = productInventoryManagementService.getInventory("SKU102", warehouseUid);
		assertEquals(-100, inventoryDto.getQuantityOnHand());

		// "productSku"|"quantityOnHand"|"reservedQuantity"|"reorderMinimum"|"reorderQuantity"|"restockDate"
		// "SKU102"    |99              |11                |25              |5                |"Wed Apr 16 15:49:37 2009"
		// "SKU103"    |21              |10                |36              |3                |"Wed Mar 16 15:49:37 2009"
		// "SKU104"    |21              |10                |36              |3                |"Wed Mar 16 15:49:37 2009"
		executeImportJob(createInsertUpdateInventoryImportJob());

		// assert existing inventory has been updated during import
		inventoryDto = getInventory("SKU102");
		assertEquals(99, inventoryDto.getQuantityOnHand());
		assertEquals(11, inventoryDto.getReservedQuantity());
		assertEquals(25, inventoryDto.getReorderMinimum());
		assertEquals(5, inventoryDto.getReorderQuantity());
		Date date20090416154937 = string2Date("Wed Apr 16 15:49:37 2009");
		assertEquals(date20090416154937, inventoryDto.getRestockDate());
	}

	private void createCategoryAndImportProducts() throws InterruptedException {
		/** prepare products with skus. */
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());
	}

	private InventoryDto getInventory(final String skuCode) {
		Map<Long, InventoryDto> inventoriesForSku = productInventoryManagementService.getInventoriesForSku(skuCode);
		
		if (inventoriesForSku != null) {
			Collection<InventoryDto> values = inventoriesForSku.values();
			switch (values.size()) {
			case 1:
				return values.iterator().next();
			case 0:
				return null;
			default:
				fail("Unexpected number of inventories: " + values.size());
			}
		}
		return null;
	}
}
