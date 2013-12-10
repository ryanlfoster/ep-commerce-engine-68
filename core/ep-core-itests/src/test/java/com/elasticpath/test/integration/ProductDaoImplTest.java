package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * Tests DAO operations on <code>ProductType</code>.
 */
public class ProductDaoImplTest extends DbTestCase {

	private static final String PRODUCT = "product";

	private static final String GOODS = "GOODS";

	private static final String MULTI_SKU_PRODUCT = "Multi-Sku Product";

	private static final String NEW_SKU = "newSku";

	private static final int I_3 = 3;

	@Autowired
	private ProductDao productDao;

	private static final double PRODUCT_PRICE = 269.00;

// FIXME: Broken test
//	/**
//	 * Tests returning the uids of products associated with a given product sku option.
//	 */
//	public void testFindUidsBySkuOption() {
//
//		// Persist a test multi-sku product
//		String skuCode1 = Utils.uniqueCode("sku");
//		String skuCode2 = Utils.uniqueCode("sku");
//		Product product = this.persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
//				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
//				Utils.uniqueCode("product"), "Multi-Sku Product", "GOODS", null, 0, skuCode1, skuCode2);
//
//		// Get and validate the first sku option associated with the product type
//		ProductType productType = product.getProductType();
//		assertNotNull(productType);
//		Set<SkuOption> skuOptions = productType.getSkuOptions();
//		assertNotNull(skuOptions);
//		assertFalse(skuOptions.isEmpty());
//		SkuOption option = skuOptions.iterator().next();
//		assertNotNull(option);
//
//		// Validate the findUidsBySkuOption() method passing in the sku option
//		List<Long> foundUids = productDao.findUidsBySkuOption(option);
//		assertNotNull("The list of product uids found should not be null.", foundUids);
//		assertFalse("The list of product uids found should not be empty.", foundUids.isEmpty());
//	}


	/**
	 * Test multi sku products can have new skus added and correctly persisted without exception,
	 * and that non-zero uidPk's are assigned.
	 * TODO: this test seems to fail only half the time
	 */
	@DirtiesDatabase
	@Test
	public void testAddSkuToMultiSkuProduct() {
		// Persist a test multi-sku product
		final String skuCode1 = Utils.uniqueCode(NEW_SKU);
		final String skuCode2 = Utils.uniqueCode(NEW_SKU);
		final Product product = persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
				null, Utils.uniqueCode(PRODUCT), MULTI_SKU_PRODUCT, GOODS, null, 0, skuCode1, skuCode2);

		final ProductLoadTuner productLoadTuner = getBeanFactory().getBean(ContextIdNames.PRODUCT_LOAD_TUNER);
        productLoadTuner.setLoadingSkus(true);
		final Product retrievedProduct = productDao.getTuned(product.getUidPk(), productLoadTuner);
		assertNotNull("Retrieved product should not have an empty list of skus", retrievedProduct.getProductSkus());
		assertEquals("Retrieved product should have 2 skus", 2, retrievedProduct.getProductSkus().size());

		// Add a new SKU
		final ProductSku productSku = getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode("ADDANOTHER2");
		productSku.setStartDate(new Date());
		retrievedProduct.addOrUpdateSku(productSku);

		// Persist the product and return it
		final Product loadedProduct = productDao.saveOrUpdate(retrievedProduct);

		for (final ProductSku sku : loadedProduct.getProductSkus().values()) {
			assertFalse("ProductSku " + sku.getSkuCode() + " was persisted with UIDPK=0", sku.getUidPk() == 0);
		}
	}

	/**
	 * This method tests the result of getAllUids() which includes the product bundle uids.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllUidsIncludesProductBundleUids() {
		final String productType = "DUMMY_TYPE";
		final String bundleSkuCode = "DUMMY_BUNDLE_SKU";
		final String code = "DUMMY_PRODUCT";

		final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();

		catalogTestPersister.persistSimpleProduct(
				code,
				productType,
				scenario.getCatalog(),
				scenario.getCategory(),
				catalogTestPersister.getPersistedTaxCode(GOODS)
		);

		final ProductBundle bundle = catalogTestPersister.createSimpleProductBundle(
										productType,
										bundleSkuCode,
										scenario.getCatalog(),
										scenario.getCategory(),
										catalogTestPersister.getPersistedTaxCode(GOODS)
		);
		productDao.saveOrUpdate(bundle);

		final List<Long> uids = productDao.findAllUids();

		final int expectedNumberOfProducts = 2;
		assertTrue("Should have " + expectedNumberOfProducts + " products!", expectedNumberOfProducts == uids.size());
		assertTrue("Last product should be an instance of ProductBundleImpl", productDao.get(uids.get(1)) instanceof ProductBundle);
	}

	/**
	 * This method tests the updateProductLastModifiedTime().
	 * It was rise exception for mysql without
	 * <entry key="openjpa.jdbc.DBDictionary" value="supportsCorrelatedSubselect=false" />
	 */
	@DirtiesDatabase
	@Test
	public void testLastUpdateDate() {
		// Persist a test multi-sku product
		final String skuCode1 = Utils.uniqueCode(NEW_SKU);
		final String skuCode2 = Utils.uniqueCode(NEW_SKU);
		final Product product = persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
				null, Utils.uniqueCode(PRODUCT), MULTI_SKU_PRODUCT, GOODS, null, 0, skuCode1, skuCode2);

		productDao.updateProductLastModifiedTime(product);

	}

	/**
	 * Test enriching data is correctly retrieved for single/multi sku product.
	 */
	@DirtiesDatabase
	@Test
	public void testFindEnrichingData() {

		// Persist a test multi-sku product
		final String skuCode1 = Utils.uniqueCode(NEW_SKU);
		final String skuCode2 = Utils.uniqueCode(NEW_SKU);
		final String productMultiSKUCode = Utils.uniqueCode(PRODUCT);
		persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
				null, productMultiSKUCode, MULTI_SKU_PRODUCT, GOODS, null, 0, skuCode1, skuCode2);

		final Collection<String> collMS = Arrays.asList(productMultiSKUCode);
		final List<Object[]> resultMS = productDao.findEnrichingData("PRODUCT_ENRICH_DTO_BY_GUIDS", collMS, Locale.ENGLISH);
		assertNotNull(resultMS);
		assertEquals(1, resultMS.size());
		assertEquals(productMultiSKUCode, resultMS.get(0)[0]);
		assertEquals(MULTI_SKU_PRODUCT, resultMS.get(0)[1]);
		assertTrue((Boolean) resultMS.get(0)[2]);
		assertEquals(skuCode1, resultMS.get(0)[I_3]);

		final String productSingleSKUCode = Utils.uniqueCode(PRODUCT);
		final String productSingleSKUskuCode = productSingleSKUCode + "SKU";
		persisterFactory.getCatalogTestPersister().persistProductWithSku(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
				"brandCode", productSingleSKUCode, "Single-Sku Product", productSingleSKUskuCode, GOODS, null, 0);

		final Collection<String> collSS = Arrays.asList(productSingleSKUCode);
		final List<Object[]> resultSS = productDao.findEnrichingData("PRODUCT_ENRICH_DTO_BY_GUIDS", collSS, Locale.ENGLISH);
		assertNotNull(resultSS);
		assertEquals(1, resultSS.size());
		assertEquals(productSingleSKUCode, resultSS.get(0)[0]);
		assertEquals("Single-Sku Product", resultSS.get(0)[1]);
		assertFalse((Boolean) resultSS.get(0)[2]);
		assertEquals(productSingleSKUskuCode, resultSS.get(0)[I_3]);

	}

	/**
	 * Tests find uid by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidBySkuCode() {

	    final CatalogTestPersister catalogTestPersister = persisterFactory.getCatalogTestPersister();

	    final String productSingleSKUCode = Utils.uniqueCode(PRODUCT);
	    final String productSingleSKUskuCode = productSingleSKUCode + "SKU";

	    final Product product =  catalogTestPersister.persistProductWithSku(scenario.getCatalog(),
	            scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(PRODUCT_PRICE), TestDataPersisterFactory.DEFAULT_CURRENCY,
	            "brandCode", productSingleSKUCode, "Single-Sku Product", productSingleSKUskuCode, GOODS, null, 0);

	    final long resultUid = productDao.findUidBySkuCode(productSingleSKUskuCode);

	    assertEquals("wrong uid returned", product.getUidPk(), resultUid);
	}
}
