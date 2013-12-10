package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SingleStoreMultiCatalogScenario;

public class ImportProductMultiCatalogTest extends ImportJobTestCase {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Override
	protected void createTestData() {
		scenario = getTac().useScenario(SingleStoreMultiCatalogScenario.class);
	}

	/**
	 * Test import product insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductMultiCatalogInsert() throws Exception {
		List<ImportBadRow> badRows = executeImportJob(createInsertProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		assertProductOneInsert();
		assertProductThreeInsert();
	}

	/**
	 * Test import product insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductMultiCatalogInsertUpdate() throws Exception {
		List<ImportBadRow> badRows = executeImportJob(createInsertProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		badRows = executeImportJob(createInsertUpdateProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		// check product which has been added to no link catalog
		assertProductOneInsertUpdate();
		// check product which has not been changed
		assertProductThreeInsert();
		// check new product
		assertProductTwoInsertUpdate();
	}

	/**
	 * Test import product update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductMultiCatalogUpdate() throws Exception {
		List<ImportBadRow> badRows = executeImportJob(createInsertProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		badRows = executeImportJob(createUpdateProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		// check product which has been added to no link catalog
		assertProductOneInsertUpdate();
		// check product which has not been changed
		assertProductThreeInsert();
	}

	/**
	 * Test import product delete.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductMultiCatalogDelete() throws Exception {
		List<ImportBadRow> badRows = executeImportJob(createInsertProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		Product product = productService.findByGuid("101", null);
		assertNotNull("Product should have been found", product);
		long productUid = product.getUidPk();
		badRows = executeImportJob(createDeleteProductMultiCatalogImportJob(getMultiCatalogScenario().getSecondMasterCatalog()));
		assertEquals(0, badRows.size());
		// check product which has been deleted
		assertProductOneDelete(productUid);
		// check product which has not been changed
		assertProductThreeInsert();
	}

	public void assertProductOneDelete(final long productUid) {
		// if product not exists in first linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getFirstLinkedCategory().getUidPk()));
		// if product not exists in first category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getCategory().getUidPk()));
		// if product not exists in no link category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogNoLinkCategory().getUidPk()));
		// if product not exists in second master category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogCategory().getUidPk()));
		// if product not exists in second linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondLinkedCategory().getUidPk()));
	}

	private void assertProductOneInsert() {
		Product product = productService.findByGuid("101", null);
		assertNotNull("Product should have been found", product);
		long productUid = product.getUidPk();
		assertNotNull(product);
		assertEquals("101", product.getGuid());

		// if product not exists in first linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getFirstLinkedCategory().getUidPk()));
		// if product not exists in first category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getCategory().getUidPk()));
		// if product not exists in no link category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogNoLinkCategory().getUidPk()));
		// if product exists in second master category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogCategory().getUidPk()));
		// assert if product exists in the virtual catalog
		assertTrue(product.getCatalogs().contains(getMultiCatalogScenario().getVirtualCatalog()));
		// if product exists in second linked category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondLinkedCategory().getUidPk()));
	}

	private void assertProductThreeInsert() {
		Product product3 = productService.findByGuid("103", null);
		long productUid = product3.getUidPk();
		assertNotNull(product3);
		assertEquals("103", product3.getGuid());

		// if product not exists in first linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getFirstLinkedCategory().getUidPk()));
		// if product not exists in first category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getCategory().getUidPk()));
		// if product exists in no link category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogNoLinkCategory().getUidPk()));
		// if product not exists in second master category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogCategory().getUidPk()));
		// assert if product not exists in the virtual catalog
		assertFalse(product3.getCatalogs().contains(getMultiCatalogScenario().getVirtualCatalog()));
		// if product not exists in second linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondLinkedCategory().getUidPk()));

	}

	private void assertProductOneInsertUpdate() {
		Product product1 = productService.findByGuid("101", null);
		assertNotNull("Product should have been found", product1);
		long productUid = product1.getUidPk();
		assertNotNull(product1);
		assertEquals("101", product1.getGuid());

		// if product not exists in first linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getFirstLinkedCategory().getUidPk()));
		// if product not exists in first category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getCategory().getUidPk()));
		// if product exists in no link category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogNoLinkCategory().getUidPk()));
		// if product still exists in second master category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogCategory().getUidPk()));
		// assert if product still exists in the virtual catalog
		assertTrue(product1.getCatalogs().contains(getMultiCatalogScenario().getVirtualCatalog()));
		// if product still exists in second linked category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondLinkedCategory().getUidPk()));
	}

	void assertProductTwoInsertUpdate() {
		Product product2 = productService.findByGuid("102", null);
		long productUid = product2.getUidPk();
		assertNotNull(product2);
		assertEquals("102", product2.getGuid());

		// if product not exists in first linked category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getFirstLinkedCategory().getUidPk()));
		// if product not exists in first category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getCategory().getUidPk()));
		// if product not exists in no link category
		assertFalse(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogNoLinkCategory().getUidPk()));
		// if product exists in second master category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondCatalogCategory().getUidPk()));
		// assert if product exists in the virtual catalog
		assertTrue(product2.getCatalogs().contains(getMultiCatalogScenario().getVirtualCatalog()));
		// if product exists in second linked category
		assertTrue(categoryService.isProductInCategory(productUid, getMultiCatalogScenario().getSecondLinkedCategory().getUidPk()));
	}

	private SingleStoreMultiCatalogScenario getMultiCatalogScenario() {
		return (SingleStoreMultiCatalogScenario) scenario;
	}
}
