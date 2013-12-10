package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.testscenarios.SingleStoreMultiCatalogScenario;

/**
 * An integration test for the ProductAssociationService.
 */
public class ProductAssociationServiceImplTest extends BasicSpringContextTest {

	/** The main object under test. */
	@Autowired
	private ProductAssociationService service;

	private Store store;

	private Category category;

	private Catalog catalog;

	private CatalogTestPersister catalogTestPersister;

	private SingleStoreMultiCatalogScenario scenario;

	private Catalog virtualCatalog;

	private Product sourceProduct;

	private Product targetProduct;

	private Product productInFirstCategory;

	private Product productInSecondCategory;

	private Product productInNoLinkCategory;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SingleStoreMultiCatalogScenario.class);
		service = getBeanFactory().getBean("productAssociationService");
		store = scenario.getStore();
		category = scenario.getCategory();
		catalog = scenario.getCatalog();
		catalogTestPersister = getTac().getPersistersFactory().getCatalogTestPersister();
		virtualCatalog = scenario.getVirtualCatalog();

		sourceProduct = catalogTestPersister.createDefaultProductWithSkuAndInventory(catalog, category, store.getWarehouse());
		targetProduct = catalogTestPersister.createDefaultProductWithSkuAndInventory(catalog, category, store.getWarehouse());

		Product tempProduct = catalogTestPersister.createDefaultProductWithSkuAndInventory(catalog, category, store.getWarehouse());
		productInFirstCategory = catalogTestPersister.addProductToCategoryAndUpdate(tempProduct, scenario.getFirstLinkedCategory());
		tempProduct = catalogTestPersister.createDefaultProductWithSkuAndInventory(scenario.getSecondMasterCatalog(), scenario
				.getSecondCatalogCategory(), store.getWarehouse());
		productInSecondCategory = catalogTestPersister.addProductToCategoryAndUpdate(tempProduct, scenario.getSecondLinkedCategory());
		productInNoLinkCategory = catalogTestPersister.createDefaultProductWithSkuAndInventory(scenario.getSecondMasterCatalog(), scenario
				.getSecondCatalogNoLinkCategory(), store.getWarehouse());
	}

	/**
	 * Test that calling getAssociationsByType will load a ProductAssociation's TargetProduct and that the TargetProduct's DefaultSku will be
	 * populated.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadPopulatesTargetProductDefaultSku() {
		int associationType = 1;
		ProductAssociation pa = catalogTestPersister.persistProductAssociation(sourceProduct.getMasterCatalog(), sourceProduct, targetProduct,
				associationType);
		Set<ProductAssociation> returnedAssociations = service.getAssociationsByType(sourceProduct.getCode(), associationType, sourceProduct
				.getMasterCatalog().getCode(), false);
		assertNotNull("Returned associations should not be null", returnedAssociations);
		assertEquals("One product association should be returned", 1, returnedAssociations.size());
		ProductAssociation savedAssociation = returnedAssociations.iterator().next();
		assertTrue("The TargetProduct's default sku should not be null", savedAssociation.getTargetProduct().getDefaultSku() != null);
		assertEquals("The returned association should have the same UidPk as the one we saved", pa.getUidPk(), savedAssociation.getUidPk());
	}

	/**
	 * Test that when we ask for associations that are only valid in the given catalog we only get the valid associations.
	 */
	@DirtiesDatabase
	@Test
	public void testVirtualCatalogAssociation() {

		ProductAssociation associationWithinCatalog = catalogTestPersister.persistProductAssociation(virtualCatalog, productInFirstCategory,
				productInSecondCategory, ProductAssociation.ACCESSORY);
		assertTrue("Association in catalog should be recognized as such", service.isAssociationInCatalog(associationWithinCatalog));

		ProductAssociation associationOutsideOfCatalog = catalogTestPersister.persistProductAssociation(virtualCatalog, productInFirstCategory,
				productInNoLinkCategory, ProductAssociation.ACCESSORY);
		assertFalse("Association out of catalog should be recognized as such", service.isAssociationInCatalog(associationOutsideOfCatalog));

		Set<ProductAssociation> allAssociations = service.getAssociations(productInFirstCategory.getCode(), virtualCatalog.getCode(), false);
		assertEquals("There should be a total of 2 associations", 2, allAssociations.size());

		Set<ProductAssociation> associationsWithinCatalog = service
				.getAssociations(productInFirstCategory.getCode(), virtualCatalog.getCode(), true);
		assertEquals("There should only be 1 association within the virtual catalog", 1, associationsWithinCatalog.size());

		ProductAssociation retrievedAssociation = associationsWithinCatalog.iterator().next();
		assertEquals("The virtual catalog's association should be the one that we expected", associationWithinCatalog.getUidPk(),
				retrievedAssociation.getUidPk());

		Set<ProductAssociation> filteredAssociations = service.limitAssociationsToCatalog(allAssociations);
		assertEquals("There should only be 1 association within the filtered list", 1, filteredAssociations.size());
	}
}
