/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * Catalog service test.
 */
public class CatalogServiceImplTest extends BasicSpringContextTest {

	private static final String VIRTUAL = "VIRTUAL";

	@Autowired
	private CatalogService service;

	private CatalogTestPersister catalogTestPersister;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception an exception
	 */
	@Before
	public void setUp() throws Exception {
		TestDataPersisterFactory persisterFactory = getTac().getPersistersFactory();
		catalogTestPersister = persisterFactory.getCatalogTestPersister();
	}

	/**
	 * Test for whether the catalog is in use of not.
	 */
	@DirtiesDatabase
	@Test
	public void testIsCatalogInUse() {
		// create empty catalog and check if in use
		Catalog catalog = catalogTestPersister.persistCatalog(Utils.uniqueCode("Canada"), false);
		assertFalse("Catalog should not be in use", service.catalogInUse(catalog.getUidPk()));

		Catalog masterCatalog = catalogTestPersister.persistCatalog(Utils.uniqueCode("Canada"), true);

		catalogTestPersister.persistCategoryType(Utils.uniqueCode("catType"), Utils.uniqueCode("catTypeTemplate"),
				masterCatalog);

		// check if master catalog is in use
		assertTrue("Catalog should be in use as there is a category type defined", service.catalogInUse(masterCatalog.getUidPk()));

		Catalog masterCatalog1 = catalogTestPersister.persistCatalog(Utils.uniqueCode("Canada"), true);

		// check if master catalog is not in use
		assertFalse("Catalog should not be in use as there no category type defined", service.catalogInUse(masterCatalog1.getUidPk()));
	}
	
	/**
	 * Test getting master catalogs associated with a virtual catalog when there are linked categories.
	 */
	@DirtiesDatabase
	@Test
	public void testGetMasterForVirtualUsingLinkedCategories() {
		Catalog master1 = catalogTestPersister.persistCatalog("MASTER-1", true);
		Catalog master2 = catalogTestPersister.persistCatalog("MASTER-2", true);
		Catalog master3 = catalogTestPersister.persistCatalog("MASTER-3", true);
		
		CategoryType m1CategoryType = catalogTestPersister.persistCategoryType("M1CT", "t1", master1);
		CategoryType m2CategoryType = catalogTestPersister.persistCategoryType("M2CT", "t2", master2);
		CategoryType m3CategoryType = catalogTestPersister.persistCategoryType("M3CT", "t3", master3);
		Category master1category = catalogTestPersister.persistCategory("M1CAT", master1, m1CategoryType, null, null);
		Category master2category = catalogTestPersister.persistCategory("M2CAT", master2, m2CategoryType, null, null);
		catalogTestPersister.persistCategory("M3CAT", master3, m3CategoryType, null, null);
		
		Catalog virtual = catalogTestPersister.persistCatalog(VIRTUAL, false);
		catalogTestPersister.persistLinkedCategory(virtual, master1category);
		catalogTestPersister.persistLinkedCategory(virtual, master2category);
		
		List<Catalog> masters = service.findMastersUsedByVirtualCatalog(VIRTUAL);
		assertEquals("There should be 2 masters returned", 2, masters.size());
		assertTrue("master 1 should be in the collection", masters.contains(master1));
		assertTrue("master 2 should be in the collection", masters.contains(master2));
		assertFalse("master 3 should not be in the collection", masters.contains(master3));
	}
	
	/**
	 * Test getting master catalogs associated with a virtual catalog when there are virtual categories.
	 */
	@DirtiesDatabase
	@Test
	public void testGetMasterForVirtualUsingVirtualCategories() {
		Catalog master1 = catalogTestPersister.persistCatalog("MASTER-1", true);
		Catalog master2 = catalogTestPersister.persistCatalog("MASTER-2", true);
		Catalog master3 = catalogTestPersister.persistCatalog("MASTER-3", true);
		
		CategoryType m1CategoryType = catalogTestPersister.persistCategoryType("M1CT", "t1", master1);
		CategoryType m2CategoryType = catalogTestPersister.persistCategoryType("M2CT", "t2", master2);
		CategoryType m3CategoryType = catalogTestPersister.persistCategoryType("M3CT", "t3", master3);
		Category master1category = catalogTestPersister.persistCategory("M1CAT", master1, m1CategoryType, null, null);
		Category master2category = catalogTestPersister.persistCategory("M2CAT", master2, m2CategoryType, null, null);
		catalogTestPersister.persistCategory("M3CAT", master3, m3CategoryType, null, null);
		
		TaxCode taxCode = catalogTestPersister.getPersistedTaxCode("GOODS");
		Product product1 = catalogTestPersister.persistSimpleProduct("PRODUCT1", "pt1", master1, master1category, taxCode);
		Product product2 = catalogTestPersister.persistSimpleProduct("PRODUCT2", "pt2", master2, master2category, taxCode);
		
		Catalog virtual = catalogTestPersister.persistCatalog(VIRTUAL, false);
		Category virtualCategory = catalogTestPersister.persistCategory("VCAT", virtual, m1CategoryType, null, null);
		CategoryService categoryService = getBeanFactory().getBean(ContextIdNames.CATEGORY_SERVICE);
		virtualCategory.setVirtual(true);
		categoryService.saveOrUpdate(virtualCategory);
		
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		product1.addCategory(virtualCategory);
		product2.addCategory(virtualCategory);
		productService.saveOrUpdate(product1);
		productService.saveOrUpdate(product2);
		
		List<Catalog> masters = service.findMastersUsedByVirtualCatalog(VIRTUAL);
		assertEquals("There should be 2 masters returned", 2, masters.size());
		assertTrue("master 1 should be in the collection", masters.contains(master1));
		assertTrue("master 2 should be in the collection", masters.contains(master2));
		assertFalse("master 3 should not be in the collection", masters.contains(master3));
	}
	
}
