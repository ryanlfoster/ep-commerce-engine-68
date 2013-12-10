package com.elasticpath.service.catalog.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test that {@link ProductCharacteristicsServiceImpl} behaves as expected.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports" })
public class ProductCharacteristicsServiceImplTest {

	private static final String SKU_CODE = "sku001";
	private static final Long PRODUCT_UID = 1L;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactoryExpectationsFactory expectationsFactory;
	private BeanFactory beanFactory;

	private final ProductCharacteristicsServiceImpl productCharacteristicsService = new ProductCharacteristicsServiceImpl();
	private ProductBundleService productBundleService;
	private ProductTypeService productTypeService;

	/**
	 * Sets up the test case.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
        expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

        productBundleService = context.mock(ProductBundleService.class);
        productTypeService = context.mock(ProductTypeService.class);
        productCharacteristicsService.setProductBundleService(productBundleService);
        productCharacteristicsService.setProductTypeService(productTypeService);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Should return false when non-bundle product is provided.
	 */
	@Test
	public void testIsDynamicBundleReturnsFalseWhenWrappedProductIsNotBundle() {
		Product product = createSingleSkuProduct();
		StoreProduct storeProduct = new StoreProductImpl(product);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertFalse("A normal product is not a dynamic bundle", productCharacteristics.isDynamicBundle());
	}

	/**
	 * isDynamicBundle() should return true when bundle itself is a dynamic bundle.
	 */
	@Test
	public void testIsDynamicBundleReturnsTrueWhenBundleItselfIsDynamic() {
		StoreProduct storeProduct = new StoreProductImpl(createDynamicBundle());
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertTrue("A dynamic bundle should be recognized as such", productCharacteristics.isDynamicBundle());
	}

	/**
	 * isDynamicBundle() should return true when bundle itself has a dynamic bundle in any level.
	 */
	@Test
	public void testIsDynamicBundleReturnsTrueWhenBundleHasDynamicBundleInAnyLevel() {
		ProductBundle bundle = createNestedDynamicBundle();
		StoreProduct storeProduct = new StoreProductImpl(bundle);
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(storeProduct);

		assertTrue("A nested dynamic bundle should be recognized as dynamic", productCharacteristics.isDynamicBundle());
	}
	/**
	 * Ensures a dynamic bundle is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationDynamicBundle() {
		ProductBundle bundle = createDynamicBundle();

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		ProductCharacteristics storeProductCharacteristics = productCharacteristicsService.getProductCharacteristics(new StoreProductImpl(bundle));


		assertTrue("A dynamic bundle has configurations.", productCharacteristics.hasMultipleConfigurations());
		assertTrue("A store product of a dynamic bundle has configurations", storeProductCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a nested dynamic bundle is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationNestedDynamicBundle() {
		ProductBundle bundle = createNestedDynamicBundle();

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		ProductCharacteristics storeProductCharacteristics = productCharacteristicsService.getProductCharacteristics(new StoreProductImpl(bundle));

		assertTrue("A bundle containing a nested dynamic bundle has configurations.", productCharacteristics.hasMultipleConfigurations());
		assertTrue("A store product of a bundle containing a nested dynamic bundle has configurations",
				storeProductCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a single sku product is not marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationsSingleSkuProduct() {
		Product product = createSingleSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(product);

		assertFalse("a single sku product has no configurations.", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a multi sku product is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationsMultiSkuProduct() {
		Product product = createMultiSkuProduct();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(product);

		assertTrue("a multi-sku product has configurations.", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a fixed bundle with no multi sku is not marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationFixedBundle() {
		ProductBundle bundle = createBundle();
		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(createSingleSkuProduct());
		bundle.addConstituent(constituent);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertFalse("a fixed bundle with no multi sku is not configurable.", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a fixed bundle with a multi sku is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationFixedBundleMultiSku() {
		ProductBundle bundle = createConfigurableBundle(createSingleSkuProduct());
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with a multi sku is configurable.", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a fixed bundle with a nested bundle with a multi sku is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationConfigurableNestedBundle() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductBundle nestedBundle = createConfigurableBundle(singleSkuProduct);

		ProductBundle bundle = createBundle();
		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(singleSkuProduct);
		bundle.addConstituent(constituent3);

		BundleConstituent constituent4 = new BundleConstituentImpl();
		constituent4.setConstituent(nestedBundle);
		bundle.addConstituent(constituent4);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with a nested configurable bundle is configurable.", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a fixed bundle with a nested dynamic bundle is marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationDynamicNestedBundle() {
		ProductBundle bundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(createSingleSkuProduct());
		bundle.addConstituent(constituent1);

		ProductBundle nestedBundle = createNestedDynamicBundle();
		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(nestedBundle);
		bundle.addConstituent(constituent2);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertTrue("a fixed bundle with a nested dynamic bundle is configurable.", productCharacteristics.hasMultipleConfigurations());
	}

	private ProductBundle createBundle() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setUidPk(PRODUCT_UID);
		bundle.setCalculated(false);
		bundle.setCode("BUNDLE");
		return bundle;
	}

	private ProductBundle createCalculatedBundle() {
		ProductBundle bundle = createBundle();
		bundle.setCalculated(true);
		return bundle;
	}

	/**
	 * Ensures a fixed bundle with nested bundles but no multissku is not marked as "has multiple configurations".
	 */
	@Test
	public void testHasMultipleConfigurationNestedFixedBundle() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductBundle nestedBundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(singleSkuProduct);
		nestedBundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(singleSkuProduct);
		nestedBundle.addConstituent(constituent2);

		ProductBundle bundle = createBundle();
		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(singleSkuProduct);
		bundle.addConstituent(constituent3);

		BundleConstituent constituent4 = new BundleConstituentImpl();
		constituent4.setConstituent(nestedBundle);
		bundle.addConstituent(constituent4);

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);

		assertFalse("a fixed bundle with a nested non-configurable bundle is not configurable", productCharacteristics.hasMultipleConfigurations());
	}

	/**
	 * Ensures a calculated bundle is marked as calculated.
	 */
	@Test
	public void testCalculatedBundle() {
		ProductBundle bundle = createCalculatedBundle();
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(bundle);
		assertTrue("a calculated bundle should be identified as such", productCharacteristics.isCalculatedBundle());
	}

	/**
	 * Test the behavior of get characteristics for product sku.
	 */
	@Test
	public void testGetCharacteristicsForProductSku() {
		Product singleSkuProduct = createSingleSkuProduct();
		ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(singleSkuProduct);
		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristics(productSku);

		assertFalse("Single sku product has only 1 configuration", productCharacteristics.hasMultipleConfigurations());
		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a single sku product.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithSingleSkuProduct() {
		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(false);
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleUidBySkuCode(SKU_CODE); will(returnValue(null));
				oneOf(productTypeService).findBySkuCode(SKU_CODE); will(returnValue(productType));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertFalse("Single sku product has only 1 configuration", productCharacteristics.hasMultipleConfigurations());
		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a multi sku product.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithMultiSkuProduct() {
		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(true);
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleUidBySkuCode(SKU_CODE); will(returnValue(null));
				oneOf(productTypeService).findBySkuCode(SKU_CODE); will(returnValue(productType));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertTrue("Multi sku product has multiple configurations", productCharacteristics.hasMultipleConfigurations());
		assertFalse("Basic product is not a bundle", productCharacteristics.isBundle());
		assertNull("The bundle uid should be null", productCharacteristics.getBundleUid());
	}

	/**
	 * Test the behavior of get characteristics for sku code with a bundle.
	 */
	@Test
	public void testGetCharacteristicsForSkuCodeWithBundle() {
		final ProductBundle bundle = createDynamicBundle();
		final ProductType productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(false);
		context.checking(new Expectations() {
			{
				oneOf(productBundleService).findBundleUidBySkuCode(SKU_CODE); will(returnValue(PRODUCT_UID));
				oneOf(productBundleService).load(PRODUCT_UID); will(returnValue(bundle));
			}
		});

		ProductCharacteristics productCharacteristics = productCharacteristicsService.getProductCharacteristicsForSkuCode(SKU_CODE);

		assertTrue("A bundle is a bundle", productCharacteristics.isBundle());
		assertTrue("The bundle is dynamic", productCharacteristics.isDynamicBundle());
		assertTrue("A dynamic bundle is configurable", productCharacteristics.hasMultipleConfigurations());
		assertEquals("The bundle uid should match", PRODUCT_UID, productCharacteristics.getBundleUid());
	}

	/**
	 * Test get characteristics for collection.
	 */
	@Test
	public void testGetCharacteristicsForCollection() {
		Product product = createSingleSkuProduct();
		ProductBundle bundle = createDynamicBundle();
		StoreProduct storeProduct1 = new StoreProductImpl(product);
		StoreProduct storeProduct2 = new StoreProductImpl(bundle);

		Map<String, ProductCharacteristics> productCharacteristicsMap = productCharacteristicsService.getProductCharacteristicsMap(
				Arrays.asList(storeProduct1, storeProduct2));

		assertEquals("There should be two map entries", 2, productCharacteristicsMap.size());

		ProductCharacteristics characteristics1 = productCharacteristicsMap.get(product.getCode());
		assertNotNull("There should be an entry for the first product code", characteristics1);
		assertFalse("A normal product is not a dynamic bundle", characteristics1.isDynamicBundle());

		ProductCharacteristics characteristics2 = productCharacteristicsMap.get(bundle.getCode());
		assertNotNull("There should be an entry for the second product code", characteristics2);
		assertTrue("A dynamic bundle should be recognized as such", characteristics2.isDynamicBundle());
	}

	/**
	 * Test get characteristics for a null collection.
	 */
	@Test
	public void testGetCharacteristicsForNullCollection() {
		Map<String, ProductCharacteristics> productCharacteristicsMap = productCharacteristicsService.getProductCharacteristicsMap(null);
		assertNotNull("The result should not be null", productCharacteristicsMap);
		assertTrue("The result should be empty", productCharacteristicsMap.isEmpty());
	}

	/**
	 * Creates the dynamic bundle.
	 *
	 * @return the product bundle
	 */
	private ProductBundle createDynamicBundle() {
		ProductBundle bundle = createBundle();
		bundle.setSelectionRule(new SelectionRuleImpl(1));

		return bundle;
	}

	/**
	 * Creates the nested dynamic bundle.
	 *
	 * @return the product bundle
	 */
	private ProductBundle createNestedDynamicBundle() {
		ProductBundle nested = createDynamicBundle();
		BundleConstituent bundleConstituent = new BundleConstituentImpl();
		bundleConstituent.setConstituent(nested);

		ProductBundle bundle = createBundle();
		bundle.addConstituent(bundleConstituent);
		return bundle;
	}

	/**
	 * Creates the single sku product.
	 *
	 * @return the product
	 */
	private Product createSingleSkuProduct() {
		ProductType singleSkuProductType = createProductType(false, false);
		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setProductType(singleSkuProductType);
		product.setCode("PRODUCT");

		return product;
	}

	/**
	 * Creates the configurable bundle.
	 *
	 * @param singleSkuProduct the single sku product
	 * @return the product bundle
	 */
	private ProductBundle createConfigurableBundle(final Product singleSkuProduct) {
		ProductBundle nestedBundle = createBundle();
		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(singleSkuProduct);
		nestedBundle.addConstituent(constituent1);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(createMultiSkuProduct());
		nestedBundle.addConstituent(constituent2);
		return nestedBundle;
	}


	/**
	 * Creates the multi sku product.
	 *
	 * @return the product
	 */
	private Product createMultiSkuProduct() {
		ProductType multiSkuProductType = createProductType(true, false);
		Product product = new ProductImpl();
		product.setProductType(multiSkuProductType);
		return product;
	}

	/**
	 * Mock product type.
	 *
	 * @param multiSku the multi sku
	 * @return the product type
	 */
	private ProductType createProductType(final boolean multiSku, final boolean giftCertificate) {
		final ProductTypeImpl productType = new ProductTypeImpl();
		productType.setWithMultipleSkus(multiSku);
		if (giftCertificate) {
			productType.setName(GiftCertificate.KEY_PRODUCT_TYPE);
		}
		return productType;
	}

}
