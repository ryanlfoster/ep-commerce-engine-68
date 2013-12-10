/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.inventory;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.domain.store.impl.WarehouseAddressImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.InventoryJournal;
import com.elasticpath.inventory.domain.impl.InventoryJournalImpl;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests {@link ProductInventoryManagementServiceImpl}.
 */
public class ProductInventoryManagementServiceImplTest extends DbTestCase {

	@Autowired private InventoryJournalDao inventoryJournalDao;
	@Autowired private ProductInventoryManagementService pims;
	@Autowired private ProductService productService;
	@Autowired private ProductSkuService productSkuService;
	@Autowired private ProductTypeService productTypeService;
	@Autowired private WarehouseService warehouseService;
	private InventoryDtoImpl inventoryDto1;
	@Autowired private TaxCodeService taxCodeService;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() throws Exception {
		inventoryDto1 = new InventoryDtoImpl();
		inventoryDto1.setAllocatedQuantity(2);
		inventoryDto1.setQuantityOnHand(11);
		
		Warehouse warehouse = new WarehouseImpl();
		warehouse.setCode("warehouseCode");
		warehouse.setName("warehouseName");
		WarehouseAddress address = new WarehouseAddressImpl();
		address.setCity("");
		address.setCountry("");
		address.setStreet1("");
		address.setStreet2("");
		address.setSubCountry("");
		address.setZipOrPostalCode("");
		warehouse.setAddress(address);
		warehouse = warehouseService.saveOrUpdate(warehouse);
		inventoryDto1.setWarehouseUid(warehouse.getUidPk());
		
		ProductSku sku = createProductSku();
		inventoryDto1.setSkuCode(sku.getSkuCode());
	}
	
	/**
	 * The orphan InventoryJournal entry should be deleted when the Inventory entry is created.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveOrUpdateWithOrphan() {
		InventoryJournal inventoryJournalOrphan = new InventoryJournalImpl();
		inventoryJournalOrphan.setAllocatedQuantityDelta(inventoryDto1.getAllocatedQuantity() + 1);
		inventoryJournalOrphan.setQuantityOnHandDelta(inventoryDto1.getQuantityOnHand() + 2);
		inventoryJournalOrphan.setSkuCode(inventoryDto1.getSkuCode());
		inventoryJournalOrphan.setWarehouseUid(inventoryDto1.getWarehouseUid());
		inventoryJournalDao.saveOrUpdate(inventoryJournalOrphan);
		
		InventoryDto result = pims.saveOrUpdate(inventoryDto1);
		assertEquals(0, result.getAllocatedQuantity());
		assertEquals(0, result.getQuantityOnHand());
		assertEquals(0, inventoryJournalDao.getUidsByKey(result.getInventoryKey()).size());
	}
	
	/**
	 * The orphan InventoryJournal entry should be deleted when the Inventory entry is created.
	 * A new InventoryJournal entry is created which of course ignores the deleted journal entry.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeWithOrphan() {
		InventoryJournal inventoryJournalOrphan = new InventoryJournalImpl();
		inventoryJournalOrphan.setAllocatedQuantityDelta(inventoryDto1.getAllocatedQuantity() + 1);
		inventoryJournalOrphan.setQuantityOnHandDelta(inventoryDto1.getQuantityOnHand() + 2);
		inventoryJournalOrphan.setSkuCode(inventoryDto1.getSkuCode());
		inventoryJournalOrphan.setWarehouseUid(inventoryDto1.getWarehouseUid());
		inventoryJournalDao.saveOrUpdate(inventoryJournalOrphan);
		
		InventoryDto result = pims.merge(inventoryDto1);
		assertEquals(inventoryDto1.getAllocatedQuantity(), result.getAllocatedQuantity());
		assertEquals(inventoryDto1.getQuantityOnHand(), result.getQuantityOnHand());
		assertEquals(2, inventoryJournalDao.getUidsByKey(result.getInventoryKey()).size());
	}
	
	/**
	 * An Inventory entry and an InventoryJournal entry already exist.
	 * A new InventoryJournal entry should be created which takes into account the existing entry.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeWithExistingEntries() {
		InventoryDtoImpl result1 = (InventoryDtoImpl) pims.merge(inventoryDto1);
		assertEquals(2, inventoryJournalDao.getUidsByKey(result1.getInventoryKey()).size());
		
		result1.setAllocatedQuantity(inventoryDto1.getAllocatedQuantity() + 1);
		result1.setQuantityOnHand(inventoryDto1.getQuantityOnHand() + 2);
		
		InventoryDto result2 = pims.merge(result1);
		assertEquals(inventoryDto1.getAllocatedQuantity() + 1, result2.getAllocatedQuantity());
		assertEquals(inventoryDto1.getQuantityOnHand() + 2, result2.getQuantityOnHand());
		assertEquals(4, inventoryJournalDao.getUidsByKey(result2.getInventoryKey()).size());
	}
	
	private ProductSku createProductSku() {
		final Catalog catalog = scenario.getCatalog();
		
		// Create a product type
		ProductType productType = populateProductType(catalog);
		productTypeService.add(productType);

		Product product = createMultiSkuProduct(catalog, productType);
		ProductSku productSku = getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode(new RandomGuidImpl().toString());
		productSku.setStartDate(new Date());
		productSku.setProduct(product);
		productSkuService.add(productSku);
		product = productService.saveOrUpdate(product);
		return productSku;
	}
	
	/**
	 */
	private ProductType populateProductType(final Catalog catalog) {
		ProductType productType = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setName("FR_PC_CARD");
		productType.setTemplate("productTemplate");
		TaxCode taxCode = taxCodeService.findByCode("GOODS");
		productType.setTaxCode(taxCode);
		productType.setWithMultipleSkus(true);
		productType.setCatalog(catalog);
		productType.setWithMultipleSkus(true);
		return productType;
	}

	/**
	 */
	private Product createMultiSkuProduct(final Catalog catalog, final ProductType productType) {
		TaxCode taxCode = taxCodeService.findByCode("GOODS");
		Category defaultCategory = scenario.getCategory();
		Product product = persisterFactory.getCatalogTestPersister().persistSimpleProduct("product1", productType.getName(), catalog, defaultCategory, taxCode);
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		return productService.saveOrUpdate(product);
	}

}
