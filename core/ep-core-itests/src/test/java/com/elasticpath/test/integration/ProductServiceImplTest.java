package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * An integration test for ProductServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductServiceImplTest extends DbTestCase {

	private static final String REQUIRED_ATTR_KEY = "my_req_attr_key";

	@Autowired
	@Qualifier("productService")
	/** The main object under test. */
	private ProductService service;

	private static final int TIMESTAMP_TOLERANCE = 1000;

	/**
	 * Test adding categories to products.
	 */
	@DirtiesDatabase
	@Test
	public void testProductCategories() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		assertEquals("Product should have 1 category", 1, product.getCategories().size());

		final Category newCategory = persisterFactory.getCatalogTestPersister().persistCategory(Utils.uniqueCode("newcategory"),
				scenario.getCatalog(), scenario.getCategory().getCategoryType(), "new category", Locale.ENGLISH.toString());
		product.addCategory(newCategory);

		final Product updatedProduct = service.saveOrUpdate(product);
		assertEquals("Updated Product should have 2 categories", 2, updatedProduct.getCategories().size());

		final Product loadedProduct = service.get(product.getUidPk());
		assertEquals("Loaded Product should have 2 categories", 2, loadedProduct.getCategories().size());
	}

	/**
	 * Tests that a new non-persistent instance is created when get(0) is called.
	 */
	@DirtiesDatabase
	@Test
	public void testGetWithUidPk0() {
		final Product product = service.get(0);
		assertFalse(product.isPersisted());
		assertEquals(0, product.getUidPk());
	}

	/**
	 * Tests finding products by category uid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCategoryUid() {

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final List<Product> returnedProducts = service.findByCategoryUid(scenario.getCategory().getUidPk());

		// assertPersistenceInList(product, returnedProducts);

		assertEquals("One product should have been found", 1, returnedProducts.size());
		final Product returnedProduct = returnedProducts.get(0);
		assertEquals("The found product should be the one we expected", product.getUidPk(), returnedProduct.getUidPk());
	}

	/**
	 * Tests finding products by category uid with load tuner.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCategoryUidWithLoadTuner() {
		
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		
		final ProductLoadTuner loadTuner = getBeanFactory().getBean("PRODUCT_LOAD_TUNER_ALL");
		final List<Product> returnedProducts = service.findByCategoryUid(scenario.getCategory().getUidPk(), loadTuner);
		
		// assertPersistenceInList(product, returnedProducts);
		
		assertEquals("One product should have been found", 1, returnedProducts.size());
		final Product returnedProduct = returnedProducts.get(0);
		assertEquals("The found product should be the one we expected", product.getUidPk(), returnedProduct.getUidPk());
		assertNotNull("The returned product should have a default sku", returnedProduct.getDefaultSku());
	}
	
	/**
	 * Tests removing of a product.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		service.removeProductTree(product.getUidPk());
	}

	/**
	 * Tests that products that are retrieved and then have the end date changed persist the changes.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeProductWithDateChange() {
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date date = new Date();
		product.setEndDate(date);

		product = service.saveOrUpdate(product);

		final Product retrievedProduct = service.get(product.getUidPk());

		final Product mergedProduct = service.saveOrUpdate(retrievedProduct);

		assertNotNull(mergedProduct.getEndDate());

		final Product retrievedProduct2 = service.get(product.getUidPk());
		retrievedProduct2.setEndDate(null);

		final Product mergedProduct2 = service.saveOrUpdate(retrievedProduct2);

		assertNull(mergedProduct2.getEndDate());
	}

	 /**
	 * Test whether a catgeory is affected by a product update.
	 *
	 * @throws InterruptedException in case of sleep interruption
	 */
	@DirtiesDatabase
	@Test
	public void testProductUpdateAffectsCategory() throws InterruptedException {

		final Category category = scenario.getCategory();
		final Catalog catalog = scenario.getCatalog();
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(catalog,
					category, scenario.getWarehouse());
		assertEquals("Product should have our category as default", category, product.getDefaultCategory(catalog));

		// Get the last modified date of the category
		final Date lastModified = category.getLastModifiedDate();
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
		assertNotNull("Category should have a last modified date", lastModified);
		final String lastModifiedString = formatter.format(lastModified);
		assertTrue("Date should be able to be formatted as a string", lastModifiedString.length() > 0);

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		// Make a change to the product and save it
		final String productName = "New Product Name";
		product.setDisplayName(productName, Locale.ENGLISH);
		final Product updatedProduct = service.saveOrUpdate(product);
		assertEquals("Updated product should have new product name", productName, updatedProduct.getDisplayName(Locale.ENGLISH));
		final Date newLastModified = updatedProduct.getDefaultCategory(catalog).getLastModifiedDate();
		assertEquals("Category should still have same updated time", lastModified, newLastModified);

		// reload the category
		final CategoryService categoryService = getBeanFactory().getBean("categoryService");
		final Category loadedCategory = categoryService.get(category.getUidPk());
		assertEquals("Loaded category time should still be the same", DateUtils.truncate(lastModified, Calendar.SECOND),
				DateUtils.truncate(loadedCategory.getLastModifiedDate(), Calendar.SECOND));
	 }

	/**
	 * Test that updating something on a child object (eg SKU) is persisted when we persist the product.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateChildObject() {
		// Create a product and get the sku
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ProductSku sku = product.getProductSkus().entrySet().iterator().next().getValue();
		assertNotNull("Product should have a sku", sku);
		final String skuCode = sku.getSkuCode();
		assertFalse("Sku should have a code", skuCode.length() == 0);

		// Make a change to the sku and save the product
		sku.setHeight(BigDecimal.TEN);
		final Product updatedProduct = service.saveOrUpdate(product);
		assertEquals("Updated product's sku should still have same height value", BigDecimal.TEN, updatedProduct.getSkuByCode(skuCode).getHeight());

		// Reload the product and check the value again
		final Product loadedProduct = service.get(product.getUidPk());
		assertTrue("Loaded product's sku should still have same height value", BigDecimal.TEN
				.compareTo(loadedProduct.getSkuByCode(skuCode).getHeight()) == 0);

	}

	/**
	 * Test that the paginated methods retrieve just the required number of products.
	 */
	@DirtiesDatabase
	@Test
	public void testPagination() {
		// Create a new category to contain a bunch of products
		final Category fullCategory = scenario.getCategory();
		assertNotNull("Category should have been created successfully", fullCategory);
		assertTrue("Category should be persistent", fullCategory.isPersisted());

		// Create a bunch of products for a category
		for (int i = 0; i < 10; i++) {
			persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), fullCategory,
					scenario.getWarehouse());
		}

		// Call the product service to request 5 products from this category
		final List<Product> products = service.findByCategoryUidPaginated(fullCategory.getUidPk(), 0, 5, null);
		assertEquals("The list should have 5 products", 5, products.size());
	}

	/**
	 * Tests product sku removal.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveSku() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		product.removeSku(product.getDefaultSku());
		service.saveOrUpdate(product);
		final Product updatedProduct = service.get(product.getUidPk());
		assertNotNull(updatedProduct);
	}

	/**
	 * Tests adding a product.
	 */
	@DirtiesDatabase
	@Test
	public void testAdd() {
		final Product product = persisterFactory.getCatalogTestPersister().createSimpleProduct(
				"TestProductType", Utils.uniqueCode("product"), scenario.getCatalog(),
				getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS),
				scenario.getCategory());

		assertFalse("The product should not be persistent before we add it", product.isPersisted());

		final Product savedProduct = service.saveOrUpdate(product);

		assertEquals("The returned product matches the one we added", product, savedProduct);
		assertTrue("The product should be persistent", savedProduct.isPersisted());
	}

	/**
	 * Test loading a product.
	 */
	@DirtiesDatabase
	@Test
	public void testLoad() {
		final Product newProduct = service.load(0L);
		assertNotNull("Product should not be null", newProduct);
		assertFalse("New Product should not be persistent", newProduct.isPersisted());

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final Product loadedProduct = service.load(productUid);

		assertEquals("The loaded product should match the product we asked for", product, loadedProduct);
		assertEquals("The Uid should match", productUid, loadedProduct.getUidPk());
	}

	/**
	 * Test getting a product object.
	 */
	@DirtiesDatabase
	@Test
	public void testGetObject() {
		final Product newProduct = (Product) service.getObject(0L);
		assertNotNull("Product should not be null", newProduct);
		assertFalse("New Product should not be persistent", newProduct.isPersisted());

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final Product loadedProduct = (Product) service.getObject(productUid);

		assertEquals("The loaded product should match the product we asked for", product, loadedProduct);
		assertEquals("The Uid should match", productUid, loadedProduct.getUidPk());

	}

	/**
	 * Test getting a tuned product.
	 */
	@DirtiesDatabase
	@Test
	public void testGetTunedProduct() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product loadedProduct = service.getProductWithSkus(product);
		assertNotNull(loadedProduct.getProductSkus());
	}


	/**
	 * Test getting a product with categories.
	 */
	@DirtiesDatabase
	@Test
	public void testGetWithCategories() {
		final Product newProduct = service.getWithCategories(0);
		assertNotNull("Product should not be null", newProduct);
		assertFalse("New Product should not be persistent", newProduct.isPersisted());

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final Product loadedProduct = service.getWithCategories(productUid);
		assertEquals("Loaded product should match the product we requested", product, loadedProduct);
		assertNotNull("Categories should not be null", loadedProduct.getCategories());
		assertEquals("Category count should match the original count", 1, loadedProduct.getCategories().size());
		assertEquals("Category should match the original category", scenario.getCategory(), loadedProduct.getCategories().iterator().next());
	}

	/**
	 * Test getting the Sku count for a product.
	 */
	@DirtiesDatabase
	@Test
	public void testGetProductSkuCount() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		final int skuCount = service.getProductSkuCount(productUid);
		assertEquals("Sku Count should be 1", 1, skuCount);

		assertEquals("Non-existant product should have 0 sku count", 0, service.getProductSkuCount(0L));
	}

	/**
	 * Test getting the maximum featured product order within a category.
	 */
	@DirtiesDatabase
	@Test
	public void testGetMaxFeaturedProductOrder() {
		assertEquals("Max featured product order should be 0 for non-existant category", 0, service.getMaxFeaturedProductOrder(0L));
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		product.setFeaturedRank(scenario.getCategory(), 1);
		assertEquals(1, product.getMaxFeaturedProductOrder());
		service.saveOrUpdate(product);
		assertEquals("Max featured product order should match what the product thinks",
				1, service.getMaxFeaturedProductOrder(scenario.getCategory().getUidPk()));
	}

	/**
	 * Test getting a product specifying which fields to load via a fetch group.
	 */
	@DirtiesDatabase
	@Test
	public void testGetTunedWithFetchGroup() {
		assertNull("Request for Tuned non-existant product should return null", service.getTuned(0L, (FetchGroupLoadTuner) null));

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();

		final FetchGroupLoadTuner minimalTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		minimalTuner.addFetchGroup(FetchGroupConstants.PRODUCT_HASH_MINIMAL);
		final Product minimalProduct = service.getTuned(productUid, minimalTuner);
		try {
			assertTrue("Minimally tuned product should not include categories", minimalProduct.getCategories().isEmpty());
			fail("An exception should be generated as we have told JPA not to load categories at all");
		} catch (final NullPointerException expected) {
			// Currently a null pointer will be returned by getCategories() as there is no null checking for the results
			// of getProductCategories() - this is normally OK as it should never be null except in a case like this where
			// we have knowingly told JPA to not load that field. Should null checking be added it is OK to remove this try-catch block.
		}
		assertTrue("Minimally tuned product should not include skus", minimalProduct.getProductSkus().isEmpty());
		assertNull("Minimally tuned product should not include attributes", minimalProduct.getAttributeValueMap());

		final FetchGroupLoadTuner categoryOnlyTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		categoryOnlyTuner.addFetchGroup(FetchGroupConstants.LINK_PRODUCT_CATEGORY);
		final Product categoryOnlyProduct = service.getTuned(productUid, categoryOnlyTuner);
		assertFalse("Category only tuned product should include categories", categoryOnlyProduct.getCategories().isEmpty());
		assertTrue("Category only tuned product should not include skus", categoryOnlyProduct.getProductSkus().isEmpty());
		assertNull("Category only tuned product should not include attributes", categoryOnlyProduct.getAttributeValueMap());

		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		service.saveOrUpdate(product);

		final FetchGroupLoadTuner indexTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		indexTuner.addFetchGroup(FetchGroupConstants.PRODUCT_INDEX);
		final Product indexProduct = service.getTuned(productUid, indexTuner);
		assertFalse("Index tuned product should include categories", indexProduct.getCategories().isEmpty());
		assertFalse("Index tuned product should include skus", indexProduct.getProductSkus().isEmpty());
		assertNotNull("Index tuned product should include attributes", indexProduct.getAttributeValueMap());
		assertEquals("Index tuned product should include availability criteria", AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK,
				indexProduct.getAvailabilityCriteria());
	}

	/**
	 * Test getting a product specifying which fields to load via a load tuner.
	 */
	@DirtiesDatabase
	@Test
	public void testGetTunedWithLoadTuner() {
		assertNull("Request for Tuned non-existant product should return null", service.getTuned(0L, (ProductLoadTuner) null));

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();

		final ProductLoadTuner tuner = getBeanFactory().getBean(ContextIdNames.PRODUCT_LOAD_TUNER);
		tuner.setLoadingAttributeValue(false);
		tuner.setLoadingSkus(false);

		Product tunedProduct = service.getTuned(productUid, tuner);
		assertNull("Tuned product should not include attributes", tunedProduct.getAttributeValueMap());
		assertTrue("Tuned product should not include skus", tunedProduct.getProductSkus().isEmpty());

		tunedProduct = service.getTuned(productUid, tuner);
		assertNull("Tuned product should not include attributes", tunedProduct.getAttributeValueMap());
		assertTrue("Tuned product should not include skus", tunedProduct.getProductSkus().isEmpty());

		tuner.setLoadingAttributeValue(true);
		tunedProduct = service.getTuned(productUid, tuner);
		assertNotNull("Tuned product should include attributes", tunedProduct.getAttributeValueMap());
		assertTrue("Tuned product should not include skus", tunedProduct.getProductSkus().isEmpty());

		tuner.setLoadingSkus(true);
		tunedProduct = service.getTuned(productUid, tuner);
		assertNotNull("Tuned product should include attributes", tunedProduct.getAttributeValueMap());
		assertFalse("Tuned product should include skus", tunedProduct.getProductSkus().isEmpty());
	}

	/**
	 * Test saving and updating a product.
	 *
	 * @throws InterruptedException if sleep is interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveOrUpdate() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createSimpleProduct(
				"TestProductType", Utils.uniqueCode("product"), scenario.getCatalog(),
				getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS),
				scenario.getCategory());
		assertFalse("The product should not be persistent before we save it", product.isPersisted());

		final Product savedProduct = service.saveOrUpdate(product);
		assertTrue("The product should now be persistent", savedProduct.isPersisted());

		final Date preUpdateDate = savedProduct.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		savedProduct.setMinOrderQty(10);
		final Product updatedProduct = service.saveOrUpdate(savedProduct);
		assertTrue("The updated product should have a later last modified date", updatedProduct.getLastModifiedDate().after(preUpdateDate));

		final Product loadedProduct = service.get(savedProduct.getUidPk());
		assertEquals("The updated value should have been persisted", 10, loadedProduct.getMinOrderQty());
	}

	/**
	 * Test removing a product tree.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveProductTree() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		persisterFactory.getCatalogTestPersister().persistProductAssociation(scenario.getCatalog(),
				product1, product2, ProductAssociation.ACCESSORY);
		assertNotNull("Product 1 should be in the database", service.get(product1.getUidPk()));
		assertNotNull("Product 2 should be in the database", service.get(product2.getUidPk()));

		service.removeProductTree(product2.getUidPk());
		assertNotNull("Product 1 should still be in the database", service.get(product1.getUidPk()));
		assertNull("Product 2 should not be in the database", service.get(product2.getUidPk()));
	}

	/**
	 * Test removing a list of products.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveProductList() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		persisterFactory.getCatalogTestPersister().persistProductAssociation(scenario.getCatalog(),
				product1, product2, ProductAssociation.ACCESSORY);
		assertNotNull("Product 1 should be in the database", service.get(product1.getUidPk()));
		assertNotNull("Product 2 should be in the database", service.get(product2.getUidPk()));
		final List<Long> productUidList = new ArrayList<Long>();
		productUidList.add(product1.getUidPk());
		productUidList.add(product2.getUidPk());
		service.removeProductList(productUidList);
		assertNull("Product 1 should not be in the database", service.get(product1.getUidPk()));
		assertNull("Product 2 should not be in the database", service.get(product2.getUidPk()));
	}

	/**
	 * Test finding top seller product for a category.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductTopSellerForCategory() {
		Category category = persisterFactory.getCatalogTestPersister().persistCategory(Utils.uniqueCode("category"),
				scenario.getCatalog(), scenario.getCategory().getCategoryType(), null, null);
		Category subCategory = persisterFactory.getCatalogTestPersister().persistCategory(Utils.uniqueCode("subcategory"),
				scenario.getCatalog(), scenario.getCategory().getCategoryType(), null, null);
		subCategory.setParent(category);
		final CategoryService categoryService = getBeanFactory().getBean("categoryService");
		subCategory = categoryService.saveOrUpdate(subCategory);
		category = subCategory.getParent();

		Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				category, scenario.getWarehouse());
		Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				subCategory, scenario.getWarehouse());

		product1.setSalesCount(10);
		product2.setSalesCount(1);
		product1 = service.saveOrUpdate(product1);
		product2 = service.saveOrUpdate(product2);

		final int topSellersToRetrieve = 3;
		List<Product> topSellers = service.findProductTopSellerForCategory(subCategory.getUidPk(), topSellersToRetrieve);
		assertEquals("We should only receive one product in the subcategory", 1, topSellers.size());
		assertEquals("The top seller should be the product we expected", product2, topSellers.get(0));

		topSellers = service.findProductTopSellerForCategory(category.getUidPk(), topSellersToRetrieve);
		assertEquals("We should receive two as that is how many are in the category tree", 2, topSellers.size());
		assertEquals("The first top seller should be the first product", product1, topSellers.get(0));
		assertEquals("The second top seller should be the first product", product2, topSellers.get(1));
	}

	/**
	 * Testing finding a top seller product.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductTopSeller() {
		Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		product1.setSalesCount(10);
		product2.setSalesCount(1);
		product1 = service.saveOrUpdate(product1);
		product2 = service.saveOrUpdate(product2);

		List<Product> topSellers = service.findProductTopSeller(1, null);
		assertEquals("We should only receive one as that is how many we requested", 1, topSellers.size());
		assertEquals("The top seller should be the product we expected", product1, topSellers.get(0));

		topSellers = service.findProductTopSeller(2, null);
		assertEquals("We should receive two as that is how many we requested", 2, topSellers.size());
		assertEquals("The first top seller should be the first product", product1, topSellers.get(0));
		assertEquals("The second top seller should be the first product", product2, topSellers.get(1));
		assertFalse("The third product should not be in the list", topSellers.contains(product3));

	}

	/**
	 * Test finding a product by last modified date.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByModifiedDate() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();
		assertNotNull("Last modified date should not be null", lastModified);

		final List<Product> foundList = service.findByModifiedDate(lastModified);
		assertEquals("There should be 1 result from the find", 1, foundList.size());
		assertEquals("The product we were looking for should have been found", product, foundList.get(0));

		final Calendar otherDate = Calendar.getInstance();
		otherDate.setTime(lastModified);
		otherDate.add(Calendar.YEAR, 1);
		final List<Product> notFoundList = service.findByModifiedDate(otherDate.getTime());
		assertTrue("No results should have been found for another date", notFoundList.isEmpty());
	}

	/**
	 * Test finding a uid by the deleted date.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidsByDeletedDate() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long productUid = product.getUidPk();
		service.removeProductTree(productUid);
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		final List<Long> deletedList = service.findUidsByDeletedDate(yesterday.getTime());
		assertEquals("There should have been 1 deleted product found", 1, deletedList.size());
		assertEquals("The deleted uid should match the uid of the deleted product", productUid, deletedList.get(0).longValue());
	}


	/**
	 * Test finding a list of products given a list of uids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByUids() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final List<Long> uids = new ArrayList<Long>();
		uids.add(product1.getUidPk());
		uids.add(product2.getUidPk());
		final List<Product> products = service.findByUids(uids, null);
		assertEquals("There should be 2 items returned", 2, products.size());
		assertTrue("The first product should be in the list", products.contains(product1));
		assertTrue("The second product should be in the list", products.contains(product2));
		assertFalse("The third product should not be in the list", products.contains(product3));

	}

	/**
	 * Test finding all uids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllUids() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final List<Long> products = service.findAllUids();
		assertEquals("There should be 3 items returned", 3, products.size());
		assertTrue("The first product should be in the list", products.contains(product1.getUidPk()));
		assertTrue("The second product should be in the list", products.contains(product2.getUidPk()));
		assertTrue("The third product should be in the list", products.contains(product3.getUidPk()));

	}

	/**
	 * Test find a product by the brand uid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByBrandUid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Brand brand = persisterFactory.getCatalogTestPersister().persistProductBrand(scenario.getCatalog(), Utils.uniqueCode("brand"));
		product.setBrand(brand);
		final Product updatedProduct = service.saveOrUpdate(product);

		final List<Product> foundList = service.findByBrandUid(brand.getUidPk());
		assertEquals("We should have found 1 product by brand", 1, foundList.size());
		assertEquals("The found product should match the product we were looking for", updatedProduct, foundList.get(0));

	}

	/**
	 * Test finding a paginated list of products by the category uid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCategoryUidPaginated() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final long categoryUid = scenario.getCategory().getUidPk();

		final List<Product> foundProducts = service.findByCategoryUidPaginated(categoryUid, 0, 2, null);
		assertEquals("There should have been 2 products found", 2, foundProducts.size());

		final List<Product> nextProducts = service.findByCategoryUidPaginated(categoryUid, 2, 10, null);
		assertEquals("There should have been 1 more product found", 1, nextProducts.size());
		assertFalse("The 3rd product should be different to the first two", foundProducts.contains(nextProducts.get(0)));

		final List<Product> allProducts = new ArrayList<Product>();
		allProducts.addAll(foundProducts);
		allProducts.addAll(nextProducts);
		assertTrue("The first product should have been found", allProducts.contains(product1));
		assertTrue("The second product should have been found", allProducts.contains(product2));
		assertTrue("The third product should have been found", allProducts.contains(product3));

		final List<Product> noProducts = service.findByCategoryUidPaginated(categoryUid, 3, 10, null);
		assertTrue("There should be no more products", noProducts.isEmpty());

	}

	/**
	 * Test finding uids for available products.
	 *
	 * @throws InterruptedException in case of sleep interruption
	 */
	@DirtiesDatabase
	@Test
	public void testFindAvailableUids() throws InterruptedException {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);
		product2.setStartDate(lastMonth.getTime());
		product2.setEndDate(lastMonth.getTime());
		service.saveOrUpdate(product2);

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		final List<Long> availableUids = service.findAvailableUids();
		assertEquals("There should only have been 1 product found",1, availableUids.size());
		assertEquals("The found product should be the first product", product1.getUidPk(), availableUids.get(0).longValue());
		assertFalse("The second product should not have been found", availableUids.contains(product2.getUidPk()));
	}

	/**
	 * Test finding uids for available products by modified date.
	 *
	 * @throws InterruptedException in case of sleep interruption
	 */
	@DirtiesDatabase
	@Test
	public void testFindAvailableUidsByModifiedDate() throws InterruptedException {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);
		product2.setStartDate(lastMonth.getTime());
		product2.setEndDate(lastMonth.getTime());
		service.saveOrUpdate(product2);

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		final List<Long> availableUids = service.findAvailableUids();
		assertEquals("There should only have been 1 product found",1, availableUids.size());
		assertEquals("The found product should be the first product", product1.getUidPk(), availableUids.get(0).longValue());
		assertFalse("The second product should not have been found", availableUids.contains(product2.getUidPk()));

		final Calendar otherDate = Calendar.getInstance();
		otherDate.setTime(product1.getLastModifiedDate());
		otherDate.add(Calendar.YEAR, 1);
		final List<Long> notFoundList = service.findAvailableUidsByModifiedDate(otherDate.getTime());
		assertTrue("No results should have been found for another date", notFoundList.isEmpty());
	}

	/**
	 * Test fiding uids for products by modified date.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidsByModifiedDate() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();
		assertNotNull("Last modified date should not be null", lastModified);

		final List<Long> foundList = service.findUidsByModifiedDate(lastModified);
		assertEquals("There should be 1 result from the find", 1, foundList.size());
		assertEquals("The product we were looking for should have been found", product.getUidPk(), foundList.get(0).longValue());

		final Calendar otherDate = Calendar.getInstance();
		otherDate.setTime(lastModified);
		otherDate.add(Calendar.YEAR, 1);
		final List<Long> notFoundList = service.findUidsByModifiedDate(otherDate.getTime());
		assertTrue("No results should have been found for another date", notFoundList.isEmpty());
	}

	/**
	 * Test finding a product given the Id (GUID).
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidById() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long uid = service.findUidById(product.getGuid());
		assertEquals("The product should have been found", product.getUidPk(), uid);
		
		long invalidUid = service.findUidById("");
		assertEquals("An invalid search should have returned 0", 0, invalidUid);
		
		//This tests the scanario where if the funtion is invoked with non empty string id it should return 0 as uidPK.
		invalidUid = service.findUidById("1234");
		assertEquals("An invalid search should have returned 0", 0, invalidUid);
	}

	/**
	 * Test that no entries are returned when the query list is empty.
	 */
	@DirtiesDatabase
	@Test
	public void testEmptyFindCodesByUids() {
		List<Long> productUids = new LinkedList<Long>();
		Map<Long, String> results = service.findCodesByUids(productUids);
		assertTrue("Results map should be empty because the query was empty.", results.isEmpty());
	}
	
	/**
	 * Test that a query with a product uid that exists and one that doesn't exist will 
	 * return a result that only has one entry in it (for the uid that exists).
	 */
	@DirtiesDatabase
	@Test
	public void testFindCodesByUidsWhereUidDoesNotExist() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		final long badUid = product1.getUidPk() + 1;
		
		assertNull("badUid should not exist.", service.get(badUid));
		
		List<Long> productUids = new LinkedList<Long>();
		productUids.add(product1.getUidPk());
		productUids.add(badUid);
		Map<Long, String> results = service.findCodesByUids(productUids);
		assertEquals("Results map should only contain 1 entry.", 1, results.size());
		assertFalse("results map should not contain the badUid.", results.containsKey(badUid));
		assertEquals("product1 uid should map to product1 code.", product1.getCode(), results.get(product1.getUidPk()));
	}
	
	/**
	 * Test finding a list of product codes using a list of product uids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindCodesByUids() {
		final Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		
		List<Long> productUids = new LinkedList<Long>();
		productUids.add(product1.getUidPk());
		productUids.add(product2.getUidPk());
		productUids.add(product3.getUidPk());
		
		Map<Long, String> results = service.findCodesByUids(productUids);
		assertEquals("Results map should contain 3 entries.", 3, results.size());
		assertEquals("product1 uid should map to product1 code.", product1.getCode(), results.get(product1.getUidPk()));
		assertEquals("product2 uid should map to product2 code.", product2.getCode(), results.get(product2.getUidPk()));
		assertEquals("product3 uid should map to product3 code.", product3.getCode(), results.get(product3.getUidPk()));
	}

	/**
	 * Test that a guid exists when it should.
	 */
	@DirtiesDatabase
	@Test
	public void testGuidExists() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		assertTrue("The Guid for this product exists", service.guidExists(product.getGuid()));
		assertFalse("A made up guid should not exist", service.guidExists("badGuid"));
	}

	/**
	 * Test searching for products given the guid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByGuid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Product foundProduct = service.findByGuid(product.getGuid());
		assertEquals("The found product should be the one we looked for", product, foundProduct);

		try {
			service.findByGuid(null);
			fail("Trying to find a null guid should throw an exception");
		} catch (final EpServiceException expected) {
			// This exception should have been thrown
		}

		assertNull("Searching for an invalid guid should return null", service.findByGuid("badGuid"));

	}

	/**
	 * Test finding product uids given a list of category uids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidsByCategoryUids() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final List<Long> categoryUids = new ArrayList<Long>();
		categoryUids.add(scenario.getCategory().getUidPk());
		final List<Long> productUids = service.findUidsByCategoryUids(categoryUids);
		assertEquals("There should have been 1 product found", 1, productUids.size());
		assertEquals("The found product should be the one expected", product.getUidPk(), productUids.get(0).longValue());

		assertTrue("A null catgeory list should return an empty result set", service.findUidsByCategoryUids(null).isEmpty());
		final List<Long> emptyList = new ArrayList<Long>();
		assertTrue("An empty category list should return an empty result set", service.findUidsByCategoryUids(emptyList).isEmpty());
		final List<Long> invalidList = new ArrayList<Long>();
		invalidList.add(0L);
		assertTrue("A list of invalid category uids should return an empty result set", service.findUidsByCategoryUids(invalidList).isEmpty());
	}

	/**
	 * Test updating the order of featured products.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateFeaturedProductOrder() {
		Product product1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Category newCategory = persisterFactory.getCatalogTestPersister().persistCategory(Utils.uniqueCode("category"),
				scenario.getCatalog(), scenario.getCategory().getCategoryType(), null, null);
		final Product product3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				newCategory, scenario.getWarehouse());

		product1.setFeaturedRank(scenario.getCategory(), 1);
		product2.setFeaturedRank(scenario.getCategory(), 2);
		product1 = service.saveOrUpdate(product1);
		product2 = service.saveOrUpdate(product2);
		assertEquals("First product should have featured rank of 1", 1, product1.getFeaturedRank(scenario.getCategory()));
		assertEquals("Second product should have featured rank of 2", 2, product2.getFeaturedRank(scenario.getCategory()));

		service.updateFeaturedProductOrder(product1.getUidPk(), scenario.getCategory().getUidPk(), product2.getUidPk());
		assertEquals("First product should have featured rank of 1", 1, product1.getFeaturedRank(scenario.getCategory()));
		assertEquals("Second product should have featured rank of 2", 2, product2.getFeaturedRank(scenario.getCategory()));

		try {
			service.updateFeaturedProductOrder(product1.getUidPk(), scenario.getCategory().getUidPk(), product3.getUidPk());
			fail("Trying to update a product not in the given category should throw an exception");
		} catch (final EpServiceException expected) {
			// We are expecting an exception to occur
		}

	}

	/**
	 * Test checking if products are in a given category.
	 */
	@DirtiesDatabase
	@Test
	public void testHasProductsInCategory() {
		final Category category = persisterFactory.getCatalogTestPersister().persistCategory(Utils.uniqueCode("category"),
				scenario.getCatalog(), scenario.getCategory().getCategoryType(), null, null);

		persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		assertTrue("Category with our product should have products", service.hasProductsInCategory(scenario.getCategory().getUidPk()));
		assertFalse("Category with no products should return false", service.hasProductsInCategory(category.getUidPk()));
	}

	/**
	 * Test that notification of a product type updates the last modified time of the products in that type.
	 * @throws InterruptedException if sleep interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testNotifyProductTypeUpdated() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		service.notifyProductTypeUpdated(product.getProductType());
		final Product updatedProduct = service.get(product.getUidPk());
		assertTrue("Product's last updated date should be updated", updatedProduct.getLastModifiedDate().after(lastModified));
	}

	/**
	 * Test that notification of product sku updates the last modified time of the products the sku belongs to.
	 * @throws InterruptedException if sleep interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testNotifySkuUpdated() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		service.notifySkuUpdated(product.getDefaultSku());
		final Product updatedProduct = service.get(product.getUidPk());
		assertTrue("Product's last updated date should be updated", updatedProduct.getLastModifiedDate().after(lastModified));
	}

	/**
	 * Test that notification of brand updates the last modified time of products in that brand.
	 * @throws InterruptedException if sleep interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testNotifyBrandUpdated() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Brand brand = persisterFactory.getCatalogTestPersister().persistProductBrand(scenario.getCatalog(), Utils.uniqueCode("brand"));
		product.setBrand(brand);
		Product updatedProduct = service.saveOrUpdate(product);
		final Date lastModified = updatedProduct.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		service.notifyBrandUpdated(updatedProduct.getBrand());
		updatedProduct = service.get(product.getUidPk());
		assertTrue("Product's last updated date should be updated", updatedProduct.getLastModifiedDate().after(lastModified));
	}

	/**
	 * Test that notification of category updates the last modified time of product in that category.
	 * @throws InterruptedException if sleep interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testNotifyCategoryUpdated() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		service.notifyCategoryUpdated(scenario.getCategory());
		final Product updatedProduct = service.get(product.getUidPk());
		assertTrue("Product's last updated date should be updated", updatedProduct.getLastModifiedDate().after(lastModified));
	}

	/**
	 * Test finding product uids given the store uid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidsByStoreUid() {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final long storeUid = scenario.getStore().getUidPk();

		final List<Long> storeProducts = service.findUidsByStoreUid(storeUid);
		assertEquals("There should have been 1 product found", 1, storeProducts.size());
		assertEquals("The found product should be the one we expected", product.getUidPk(), storeProducts.get(0).longValue());
	}

	/**
	 * Test the indicator of whether a product can be deleted.
	 */
	@DirtiesDatabase
	@Test
	public void testCanDelete() {
		Product product1 = this.persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Product product2 = this.persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		persisterFactory.getOrderTestPersister().createOrderWithSkus(scenario.getStore(), product1.getDefaultSku());

		assertFalse("Should not be able to delete first product as it is part of an order", service.canDelete(product1));
		assertTrue("Should be able to delete second product as it is not part of an order", service.canDelete(product2));
	}

	/**
	 * Test that updating a product updates its last modified time.
	 * @throws InterruptedException  if sleep interrupted.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateLastModifiedTime() throws InterruptedException {
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final Date lastModified = product.getLastModifiedDate();

		// TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp
		//                      to the second level of precision (rather than millisecond).
		//                      Further testing needs to be done to determine whether this
		//                      will cause problems or not.
		Thread.sleep(TIMESTAMP_TOLERANCE);

		service.updateLastModifiedTime(product);
		final Product updatedProduct = service.get(product.getUidPk());
		assertTrue("Updated time should be greater than original time", updatedProduct.getLastModifiedDate().after(lastModified));
	}

	/**
	 * Asserts that <code>testItem</code> exist in the given list of persisted <code>items</code>.
	 * Throws an AssertionFailedError exception if the assertion fails.
	 *
	 * @param <T> The type of object to search for.
	 * @param testItem The <code>Persistence</code> item to look for in the list.
	 * @param items A list of peristed items tos search through.
	 */
	public static <T extends Persistable> void assertPersistenceInList(final T testItem, final List<T> items) {
		assertPersistenceInList("", testItem, items);
	}

	/**
	 * Asserts that <code>testItem</code> exist in the given list of persisted <code>items</code>.
	 * Error <code>message</code> is including in the exception if the assertion fails.
	 *
	 * @param <T> The type of object to search for.
	 * @param message Error message to be used on assertion failure.
	 * @param testItem The <code>Persistence</code> item to look for in the list.
	 * @param items A list of peristed items tos search through.
	 */
	public static <T extends Persistable> void assertPersistenceInList(final String message, final T testItem, final List<T> items) {
		for (final Persistable item : items) {
			if (testItem.getUidPk() == item.getUidPk()) {
				return;
			}
		}
		throw new AssertionFailedError(message + ": " + testItem.getUidPk() + " not in list.");
	}


	/**
	 * Tests adding new sku option value to already existing product's product sku.
	 */
	@DirtiesDatabase
	@Test
	public void testAddSkuWithNewSkuOptionValue() {
		final Product product = persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(),
				new BigDecimal("100"), Currency.getInstance(Locale.US), "multiSku1", "prodType123", "product name",
				TaxTestPersister.TAX_CODE_GOODS, AvailabilityCriteria.ALWAYS_AVAILABLE, 9, "sku1");
		final SkuOption skuOption = product.getProductType().getSkuOptions().iterator().next();

		// create new sku option value
		final SkuOptionValue optionValue = new SkuOptionValueImpl();
		final String optionValueKey = "test_option_value_key";
		optionValue.setOptionValueKey(optionValueKey);
		optionValue.setOrdering(1);
		skuOption.addOptionValue(optionValue);

		// update the sku option
		final SkuOptionService skuService = getBeanFactory().getBean(ContextIdNames.SKU_OPTION_SERVICE);
		final SkuOption updatedSkuOption = skuService.saveOrUpdate(skuOption);

		final ProductSku productSku = getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU);

		final String skuCode = "skucode1";
		productSku.setSkuCode(skuCode);
		productSku.setSkuOptionValue(updatedSkuOption, optionValueKey);

		product.addOrUpdateSku(productSku);

		productSku.addOrUpdateSkuOption(updatedSkuOption);

		final Product updatedProduct = service.saveOrUpdate(product);

		assertNotNull(updatedProduct.getProductType().getSkuOptions());
		assertEquals(2, updatedProduct.getProductType().getSkuOptions().size());
		assertTrue(updatedProduct.getProductType().getSkuOptions().contains(skuOption));

		SkuOptionValue skuOptionValue = updatedProduct.getSkuByCode(skuCode).getSkuOptionValue(updatedSkuOption);
		assertEquals(optionValueKey, skuOptionValue.getOptionValueKey());

		final Product loadedProduct = service.get(product.getUidPk());
		assertNotNull(loadedProduct);

		skuOptionValue = loadedProduct.getSkuByCode(skuCode).getSkuOptionValue(updatedSkuOption);
		assertEquals(optionValueKey, skuOptionValue.getOptionValueKey());
	}

	/**
	 * Tests adding a new sku option value to a product's product sku where the product is still not persisted.
	 *
	 * 1. Create a new multi-sku product with populated required fields
	 * 2. Create a new SKU option value
	 * 3. Add the sku option value to already existing SKU option
	 * 4. Set the sku option value to a product SKU and add it to the product
	 * 5. Persist the product
	 */
	@DirtiesDatabase
	@Test
	public void testPersistMultiSkuProductWithNewSkuAndNewSkuOptionValue() {
		final Product product = persisterFactory.getCatalogTestPersister().createMultiSkuProduct(scenario.getCatalog(), scenario.getCategory(),
				null, "multiSku1", "prodType123", "product name", TaxTestPersister.TAX_CODE_GOODS, AvailabilityCriteria.ALWAYS_AVAILABLE, 9, "sku1");
		final SkuOption skuOption = product.getProductType().getSkuOptions().iterator().next();

		// create new sku option value
		final SkuOptionValue optionValue = new SkuOptionValueImpl();
		final String optionValueKey = "test_option_value_key";
		optionValue.setOptionValueKey(optionValueKey);
		optionValue.setOrdering(1);
		skuOption.addOptionValue(optionValue);

		// update the sku option
		final SkuOptionService skuService = getBeanFactory().getBean(ContextIdNames.SKU_OPTION_SERVICE);
		final SkuOption updatedSkuOption = skuService.saveOrUpdate(skuOption);

		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();

		final String skuCode = "skucode1";
		productSku.setSkuCode(skuCode);
		productSku.setSkuOptionValue(updatedSkuOption, optionValueKey);

		product.addOrUpdateSku(productSku);

		Product updatedProduct = service.saveOrUpdate(product);
		assertNotNull(updatedProduct.getProductType().getSkuOptions());
		assertEquals(2, updatedProduct.getProductType().getSkuOptions().size());
		assertTrue(updatedProduct.getProductType().getSkuOptions().contains(skuOption));

		SkuOptionValue skuOptionValue = updatedProduct.getSkuByCode(skuCode).getSkuOptionValue(updatedSkuOption);
		assertEquals(optionValueKey, skuOptionValue.getOptionValueKey());

		updatedProduct = service.get(product.getUidPk());
		assertNotNull(updatedProduct);

		skuOptionValue = updatedProduct.getSkuByCode(skuCode).getSkuOptionValue(updatedSkuOption);
		assertEquals(optionValueKey, skuOptionValue.getOptionValueKey());
	}

	/**
	 * Test finding uid by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidBySkuCode() {

	    final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();


	    final String productSingleSKUCode = Utils.uniqueCode("prodict");
	    final String productSingleSKUskuCode = productSingleSKUCode + "SKU";

	    final Product product =  catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
	            scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
	            "brandCode", productSingleSKUCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

	    final long resultUid = service.findUidBySkuCode(productSingleSKUskuCode);

	    assertEquals("product uid is wrong", product.getUidPk(), resultUid);
	}

	/**
	 * Test finding uid by sku code where sku code not found.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidBySkuCodeNotFound() {

	    final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();


	    final String productSingleSKUCode = Utils.uniqueCode("product");
	    final String productSingleSKUskuCode = productSingleSKUCode + "SKU";

	    catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
	            scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
	            "brandCode", productSingleSKUCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

	    final long resultUid = service.findUidBySkuCode("invalidSkuCode");

	    assertEquals("product uid should not be found for sku", 0, resultUid);
	}
	
	/**
	 * Test validation of existence of values for all required attributes.
	 */
	@DirtiesDatabase
	@Test(expected = AttributeValueIsRequiredException.class)
	public void testValidateExistenceOfValuesForRequiredNonLocaleDependentAttributesFails() {

		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();


		final String productCode = Utils.uniqueCode("product");
		final String productSingleSKUskuCode = productCode + "SKU";

		catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
		scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

		//load the product
		Product product = service.findByGuid(productCode, null);

		assertNotNull("product found", product);
		
		// add the new attribute
		Attribute attr = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(), REQUIRED_ATTR_KEY, REQUIRED_ATTR_KEY, 
				AttributeUsageImpl.PRODUCT_USAGE.toString(), AttributeType.SHORT_TEXT.toString(), false, true, false);
		catalogTestPersister.assignProductAttributesToProductType(new String[]{attr.getKey()}, 
				product.getProductType().getName());
		
		//re-load product after type changed
		product = service.findByGuid(productCode, null);
		// change the product
		product.setDisplayName("new name", scenario.getCatalog().getDefaultLocale());
		
		service.saveOrUpdate(product);
	}
	/**
	 * Test validation of existence of values for all required attributes.
	 */
	@DirtiesDatabase
	@Test
	public void testValidateExistenceOfValuesForRequiredNonLocaleDependentAttributesPass() {

		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();


		final String productCode = Utils.uniqueCode("product");
		final String productSingleSKUskuCode = productCode + "SKU";

		catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
		scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

		//load the product
		Product product = service.findByGuid(productCode, null);

		assertNotNull("product found", product);
		
		// add the new attribute
		Attribute attr = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(), REQUIRED_ATTR_KEY, REQUIRED_ATTR_KEY, 
				AttributeUsageImpl.PRODUCT_USAGE.toString(), AttributeType.SHORT_TEXT.toString(), false, true, false);
		catalogTestPersister.assignProductAttributesToProductType(new String[]{attr.getKey()}, 
				product.getProductType().getName());
		
		//re-load product after type changed
		product = service.findByGuid(productCode, null);
		// add the value of the attribute for the product
		catalogTestPersister.addAttributeValue(product, attr.getKey(), "new value");
		// change the product
		product.setDisplayName("new name", scenario.getCatalog().getDefaultLocale());
		service.saveOrUpdate(product);
	}
	
	/**
	 * Test validation of existence of values for all required attributes.
	 * ProductType was changed from the time the product was load and a new required attribute was added to it  
	 */
	@DirtiesDatabase
	@Test
	public void testValidateExistenceOfValuesForRequiredAttributesWillNotPickupChangesToProductType() {

		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();


		final String productCode = Utils.uniqueCode("product");
		final String productSingleSKUskuCode = productCode + "SKU";

		catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
		scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

		//load the product
		final Product product = service.findByGuid(productCode, null);

		assertNotNull("product found", product);
		
		// add the new attribute
		Attribute attr = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(), REQUIRED_ATTR_KEY, REQUIRED_ATTR_KEY, 
				AttributeUsageImpl.PRODUCT_USAGE.toString(), AttributeType.SHORT_TEXT.toString(), false, true, false);
		catalogTestPersister.assignProductAttributesToProductType(new String[]{attr.getKey()}, 
				product.getProductType().getName());

		// change the product
		product.setDisplayName("new name", scenario.getCatalog().getDefaultLocale());

		// no exception will be thrown because we are not updating the productType inside a save
		service.saveOrUpdate(product);
		}
	
	
	/**
	 * Test validation of existence of values for all required attributes.
	 * ProductType that is loaded into the product has a required attribute that the customer didn't added a value for 
	 */
	@DirtiesDatabase
	@Test(expected = AttributeValueIsRequiredException.class)
	public void testValidateExistenceOfValuesForRequiredLocaleDependentAttributesThatAreLocaleDependedFail() {
		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();

		final String productCode = Utils.uniqueCode("product");
		final String productSingleSKUskuCode = productCode + "SKU";
		final String attrValue = "new value";

		catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

		Product product = service.findByGuid(productCode, null);

		assertNotNull("product found", product);

		final Attribute attr = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(), REQUIRED_ATTR_KEY, REQUIRED_ATTR_KEY, 
				AttributeUsageImpl.PRODUCT_USAGE.toString(), AttributeType.SHORT_TEXT.toString(), true, true, false);

		// refresh the type to be able to see the new attached attributes.
		catalogTestPersister.assignProductAttributesToProductType(new String[]{attr.getKey()}, 
				product.getProductType().getName());

		// reload the product, because in this test the product has the latest productType
		product = service.findByGuid(productCode, null);

		product.setDisplayName("new name", scenario.getCatalog().getDefaultLocale());

		// no exception will be thrown because we are not updating the productType inside a save
		service.saveOrUpdate(product);
}

	/**
	 * Test validation of existence of values for all required attributes.
	 * ProductType that is loaded into the product has a required attribute that the customer didn't added a value for 
	 */
	@DirtiesDatabase
	@Test
	public void testValidateExistenceOfValuesForRequiredLocaleDependentAttributesThatAreLocaleDependedPass() {
		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();

		final String productCode = Utils.uniqueCode("product");
		final String productSingleSKUskuCode = productCode + "SKU";
		final String attrValue = "new value";

		catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(33.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productCode, "Single-Sku Product", productSingleSKUskuCode, "GOODS", null, 0);

		Product product = service.findByGuid(productCode, null);

		assertNotNull("product found", product);

		final Attribute attr = catalogTestPersister.persistAttribute(scenario.getCatalog().getCode(), REQUIRED_ATTR_KEY, REQUIRED_ATTR_KEY, 
				AttributeUsageImpl.PRODUCT_USAGE.toString(), AttributeType.SHORT_TEXT.toString(), true, true, false);

		// refresh the type to be able to see the new attached attributes.
		catalogTestPersister.assignProductAttributesToProductType(new String[]{attr.getKey()}, 
				product.getProductType().getName());

		// reload the product, because in this test the product has the latest productType
		product = service.findByGuid(productCode, null);

		// add value only for the master default locale for all supported locale
		final Locale locale = product.getMasterCatalog().getDefaultLocale();
			final List<AttributeValue> list = product.getFullAttributeValues(locale);
		for (final AttributeValue av : list) {
			if (av.getAttribute().getKey().equals(attr.getKey())) {
				av.setStringValue(attrValue);
				product.getAttributeValueMap().put(av.getLocalizedAttributeKey(), av);
				break;
			}
		}

		service.saveOrUpdate(product);

}
	
}
