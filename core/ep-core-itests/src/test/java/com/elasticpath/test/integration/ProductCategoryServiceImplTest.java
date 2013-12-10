package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.ProductCategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.testscenarios.SingleStoreMultiCatalogScenario;

/**
 * An integration test for ProductCategoryServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductCategoryServiceImplTest extends BasicSpringContextTest {

	/** The main object under test. */
	@Autowired
	private ProductCategoryService productCategoryService;

	@Autowired
	private ProductService productService;

	private SingleStoreMultiCatalogScenario scenario;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SingleStoreMultiCatalogScenario.class);
	}

	/**
	 * This test uses two master catalogs, one virtual catalog, and a mix of products in normal and linked categories. <br>
	 * <br>
	 * Featured products can have different ordering in each category where they appear, and this tests verifies the service maintains the catalog
	 * and category distinctions.
	 */
	@DirtiesDatabase 
	@Test
	public void testScenarioLinkedCategories() {

		Catalog catalog1 = scenario.getCatalog();
		Catalog catalog2 = scenario.getSecondMasterCatalog();
		Catalog virtualCatalog1 = scenario.getVirtualCatalog();
		Category category1 = scenario.getCategory();
		Category category2 = scenario.getSecondCatalogCategory();
		Category category3 = scenario.getSecondCatalogNoLinkCategory();
		Category linkedCategory1 = scenario.getFirstLinkedCategory();
		Category linkedCategory2 = scenario.getSecondLinkedCategory();

		CatalogTestPersister persister = getTac().getPersistersFactory().getCatalogTestPersister();
		Warehouse warehouse = scenario.getWarehouse();

		final Product product1a = persister.createDefaultProductWithSkuAndInventory(catalog1, category1, warehouse);
		
		final Product product1b = persister.createDefaultProductWithSkuAndInventory(catalog1, category1, warehouse);
		product1b.addCategory(linkedCategory1);
		productService.saveOrUpdate(product1b);

		final Product product2a = persister.createDefaultProductWithSkuAndInventory(virtualCatalog1, category2, warehouse);
		product2a.addCategory(linkedCategory2);
		productService.saveOrUpdate(product2a);

		final Product product2b = persister.createDefaultProductWithSkuAndInventory(virtualCatalog1, category2, warehouse);
		
		final Product product3 = persister.createDefaultProductWithSkuAndInventory(catalog2, category3, warehouse);

		verifyFindByCategoryAndCatalog(catalog1, catalog2, virtualCatalog1, category1, category2, category3, linkedCategory1, linkedCategory2,
				product1a, product1b, product2b, product2a, product3);

		verifyFindByCategoryAndProduct(catalog1, catalog2, virtualCatalog1, category1, category2, category3, linkedCategory1, product1a, product2a, product3);
	}

	/**
	 * Verify the findByCategoryAndProduct() methods returns the correct objects for each (category, product) pairing
	 */
	protected void verifyFindByCategoryAndProduct(Catalog catalog1, Catalog catalog2, Catalog virtualCatalog1, Category category1,
			Category category2, Category category3, Category linkedCategory1, final Product product1a,
			final Product product2a, final Product product3) {

		ProductCategory item = productCategoryService.findByCategoryAndProduct(catalog1.getCode(), category1.getCode(), product1a.getCode());
		assertNotNull("Expected productCategory not found for product in catalog1 under category1", item);

		item = productCategoryService.findByCategoryAndProduct(catalog2.getCode(), category3.getCode(), product2a.getCode());
		assertNull("Unexpected product found under catalog2/category3", item);

		item = productCategoryService.findByCategoryAndProduct(catalog2.getCode(), category3.getCode(), product3.getCode());
		assertNotNull("ProductCategory not found for product in catalog2 under category3", item);
	}

	/**
	 *  Verify the findByCategoryAndCatalog() methods returns the correct objects for each (catalog, category) pairing.
	 */
	protected void verifyFindByCategoryAndCatalog(Catalog catalog1, Catalog catalog2, Catalog virtualCatalog1, Category category1,
			Category category2, Category category3, Category linkedCategory1, Category linkedCategory2, final Product product1a,
			final Product product1b, final Product product2b, final Product product2c,
			final Product product3) {

		// catalog1 --------------------------------------------
		Collection<ProductCategory> items = null;

		items = productCategoryService.findByCategoryAndCatalog(catalog1.getCode(), category1.getCode());
		items = assertProductCategories(category1, product1a, items);
		items = assertProductCategories(category1, product1b, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog1.getCode(), category2.getCode());
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog1.getCode(), category3.getCode());
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog1.getCode(), linkedCategory1.getCode());
		items = assertProductCategories(category1, product1a, items);
		items = assertProductCategories(category1, product1b, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog1.getCode(), linkedCategory2.getCode());
		assertNoExtraProductCategories(items);

		// catalog2 --------------------------------------------
		items = productCategoryService.findByCategoryAndCatalog(catalog2.getCode(), category1.getCode());
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog2.getCode(), category2.getCode());
		items = assertProductCategories(category2, product2b, items);
		items = assertProductCategories(category2, product2c, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog2.getCode(), category3.getCode());
		items = assertProductCategories(category3, product3, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog2.getCode(), linkedCategory1.getCode());
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(catalog2.getCode(), linkedCategory2.getCode());
		items = assertProductCategories(category2, product2b, items);
		items = assertProductCategories(category2, product2c, items);
		assertNoExtraProductCategories(items);

		// virtualCatalog1 -------------------------------------
		items = productCategoryService.findByCategoryAndCatalog(virtualCatalog1.getCode(), category1.getCode());
		items = assertProductCategories(linkedCategory1, product1a, items);
		items = assertProductCategories(linkedCategory1, product1b, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(virtualCatalog1.getCode(), category2.getCode());
		items = assertProductCategories(linkedCategory2, product2b, items);
		items = assertProductCategories(linkedCategory2, product2c, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(virtualCatalog1.getCode(), category3.getCode());
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(virtualCatalog1.getCode(), linkedCategory1.getCode());
		items = assertProductCategories(linkedCategory1, product1a, items);
		items = assertProductCategories(linkedCategory1, product1b, items);
		assertNoExtraProductCategories(items);

		items = productCategoryService.findByCategoryAndCatalog(virtualCatalog1.getCode(), linkedCategory2.getCode());
		items = assertProductCategories(linkedCategory2, product2b, items);
		items = assertProductCategories(linkedCategory2, product2c, items);
		assertNoExtraProductCategories(items);
	}

	private void assertNoExtraProductCategories(Collection<ProductCategory> items) {
		if (items == null) {
			return; // null or empty are successful assertions
		}
		assertEquals("We should have removed all the ProductCategory's we expected", 0, items.size());
	}

	private Collection<ProductCategory> assertProductCategories(final Category category, final Product product,
			final Collection<ProductCategory> productCategories) {
		Collection<ProductCategory> items = new ArrayList<ProductCategory>(productCategories);
		for (Iterator<ProductCategory> it = items.iterator(); it.hasNext();) {
			ProductCategory item = it.next();
			boolean matchesCategory = (item.getCategory().getUidPk() == category.getUidPk());
			boolean matchesProduct = (item.getProduct().getUidPk() == product.getUidPk());
			if (matchesCategory && matchesProduct) {
				it.remove();
			}
		}
		// Verify we removed a match during the iteration
		assertEquals("Collection<ProductCategory> should contain an item for the category & product pair.", productCategories.size() - 1,
				items.size());
		return items;
	}
}