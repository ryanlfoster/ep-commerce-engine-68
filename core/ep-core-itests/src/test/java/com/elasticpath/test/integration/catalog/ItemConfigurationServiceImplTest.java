package com.elasticpath.test.integration.catalog;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.domain.catalog.ItemConfigurationValidationException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ItemConfigurationBuilder;
import com.elasticpath.service.catalog.ItemConfigurationFactory;
import com.elasticpath.service.catalog.ItemConfigurationService;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.impl.SelectionRuleFactoryImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

public class ItemConfigurationServiceImplTest extends BasicSpringContextTest {
	private static final String MULTISKU_PRODUCT_TYPE = "MULTISKU_PRODUCT";
	private static final String PRODUCT_TYPE = "TESTTYPE";
	private static final String BUNDLE_PRODUCT_TYPE = "BUNDLE_TYPE";

	private static final String SKU1 = "product1_sku";
	private static final String MSKU1 = "MSKU1";
	private static final String MSKU2 = "MSKU2";

	private CatalogTestPersister catalogPersister;
	@Autowired
	private ItemConfigurationService itemConfigurationService;
	@Autowired
	private ItemConfigurationFactory itemConfigurationFactory;
	@Autowired
	private ProductService productService;
	private SimpleStoreScenario scenario;
	private TaxCode taxCode;
	private Shopper shopper;

	private Product multiSkuProduct;
	private Product singleSkuProduct;

	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		catalogPersister = getTac().getPersistersFactory().getCatalogTestPersister();

		taxCode = getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		multiSkuProduct = generateMultiSkuProduct();
		singleSkuProduct = generateSimpleProduct();
	}

	/**
	 * Persists a simple product.
	 * Creates the default configuration for the product using both the factory and the service.
	 * Ensures the persistence keeps the item configuration intact.
	 */
	@DirtiesDatabase
	@Test
	public void testGetDefaultConfiguredItemForSimpleProduct() {
		ItemConfiguration itemConfiguration = persistAndAssertDefaultItemConfiguration(singleSkuProduct);
		assertEquals("the configuration of a simple product should not have any children", 0, itemConfiguration.getChildren().size());
	}

	/**
	 * Persists a bundle containing two products.
	 * Creates the default configuration for the product using both the factory and the service.
	 * Ensures the persistence keeps the item configuration intact.
	 */
	@DirtiesDatabase
	@Test
	public void testGetDefaultConfiguredItemFor1LevelBundle() {
		ProductBundle bundle = persist1LevelBundle();
		persistAndAssertDefaultItemConfiguration(bundle);
	}

	/**
	 * Persists a bundle containing one product as well as a nested bundle.
	 * Creates the default configuration for the product using both the factory and the service.
	 * Ensures the persistence keeps the item configuration intact.
	 */
	@DirtiesDatabase
	@Test
	public void testGetDefaultConfiguredItemFor2LevelBundle() {
		ProductBundle bundle = persist2LevelBundle();
		persistAndAssertDefaultItemConfiguration(bundle);
	}

	/**
	 * Tests validation: default configuration is valid.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationDefaultConfiguration() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);
		assertEquals("validation successful.", ItemConfigurationValidationResult.SUCCESS, builder.validate());

		ItemConfiguration builtConfiguration = builder.build();
		ItemConfiguration configurationFromFactory = itemConfigurationFactory.createItemConfiguration(bundle, shopper);
		assertEquals("Builder and factory should give back the same configuration.", configurationFromFactory, builtConfiguration);
	}

	/**
	 * Tests validation: invalid path.
	 */
	@DirtiesDatabase
	@Test(expected = ItemConfigurationValidationException.class)
	public void testBuilderValidationInvalidChildPath() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);
		builder.select(Arrays.asList("invalid Path"), "sku code");
	}

	/**
	 * Tests validation: invalid SKU code for root.
	 */
	@DirtiesDatabase
	@Test(expected = ItemConfigurationValidationException.class)
	public void testBuilderValidationInvalidSkuOnRootThrowsException() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);
		builder.select(Collections.<String>emptyList(), "sku code").build();
	}

	/**
	 * Tests validation: invalid SKU code for root.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationInvalidSkuOnRoot() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		builder.select(Collections.<String>emptyList(), "sku code");
		ItemConfigurationValidationResult result = builder.validate();

		assertEquals("The validation result should show an invalid sku code.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.INVALID_SKU_CODE);
		assertTrue("The error path should be empty, meaning the root node.", result.getErrorPath().isEmpty());
	}

	/**
	 * Tests validation: invalid SKU code for constituent.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationInvalidSkuCodeForConstituent() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		//child1 is a single sku product with SKU code: SKU1
		String child1Id = bundle.getConstituents().get(0).getGuid();
		builder.select(Arrays.asList(child1Id), SKU1);
		assertEquals("validation successful.", ItemConfigurationValidationResult.SUCCESS, builder.validate());

		builder.select(Arrays.asList(child1Id), MSKU1);
		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show an invalid sku code.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.INVALID_SKU_CODE);

		assertEquals("The error path should point to child1.", Arrays.asList(child1Id), result.getErrorPath());
	}

	/**
	 * Tests validation: invalid SKU code for SKU constituent.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationInvalidSkuConstituent() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		//child3 is the sku MSKU1 of the multi sku
		String child3Id = bundle.getConstituents().get(2).getGuid();
		builder.select(Arrays.asList(child3Id), MSKU1);
		assertEquals("validation successful.", ItemConfigurationValidationResult.SUCCESS, builder.validate());

		builder.select(Arrays.asList(child3Id), MSKU2);
		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show an invalid sku code.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.INVALID_SKU_CODE_FOR_SKU_CONSTITUENT);
		assertEquals("The error path should point to child3.", Arrays.asList(child3Id), result.getErrorPath());
	}

	/**
	 * Tests validation: invalid SKU code for multi-SKU product.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationMultiSkuConstituent() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		//child4 is a multi-sku
		String child4Id = bundle.getConstituents().get(3).getGuid();
		builder.select(Arrays.asList(child4Id), MSKU1);
		assertEquals("validation successful.", ItemConfigurationValidationResult.SUCCESS, builder.validate());
		ItemConfiguration item1 = builder.build();
		assertEquals("sku code should be set to MSKU1", MSKU1, item1.getChildById(child4Id).getSkuCode());

		builder.select(Arrays.asList(child4Id), MSKU2);
		assertEquals("validation successful.", ItemConfigurationValidationResult.SUCCESS, builder.validate());
		ItemConfiguration item2 = builder.build();
		assertEquals("sku code should be set to MSKU2", MSKU2, item2.getChildById(child4Id).getSkuCode());

		builder.select(Arrays.asList(child4Id), "invalid-sku-code");
		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show an invalid sku code.",
				ItemConfigurationValidationResult.ItemConfigurationValidationStatus.INVALID_SKU_CODE, result.getStatus());
		assertEquals("The error path should point to child4.", Arrays.asList(child4Id), result.getErrorPath());
	}


	/**
	 * Tests validation: invalid SKU code for a bundle constituent.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationInvalidSkuBundleConstituent() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		//child2 is a bundle.
		String child2Id = bundle.getConstituents().get(1).getGuid();
		builder.select(Arrays.asList(child2Id), MSKU1);

		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show an invalid sku code.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.INVALID_SKU_CODE);
		assertEquals("The error path should point to child3.", Arrays.asList(child2Id), result.getErrorPath());
	}

	/**
	 * Tests validation: selection rule violated.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationSelectionRule() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		String child3Id = bundle.getConstituents().get(2).getGuid();
		builder.deselect(Arrays.asList(child3Id));

		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show a selection rule violated.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.SELECTION_RULE_VIOLATED);
		assertEquals("The error path should point to the root bundle..", Collections.emptyList(), result.getErrorPath());
	}

	/**
	 * Tests validation: selection rule violated in nested bundle.
	 */
	@DirtiesDatabase
	@Test
	public void testBuilderValidationSelectionRuleOnNestedBundle() {
		ProductBundle bundle = persist2LevelBundle();
		ItemConfigurationBuilder builder = persistDefaultConfigurationAndLoadBuilder(bundle);

		//bundle
		BundleConstituent nestedBundleConstituent = bundle.getConstituents().get(1);
		String bundleId = nestedBundleConstituent.getGuid();
		ProductBundle nestedBundle = (ProductBundle) nestedBundleConstituent.getConstituent().getProduct();
		String nestedChild = nestedBundle.getConstituents().get(0).getGuid();

		builder.deselect(Arrays.asList(bundleId, nestedChild));

		ItemConfigurationValidationResult result = builder.validate();
		assertEquals("The validation result should show a selection rule violated.",
				result.getStatus(), ItemConfigurationValidationResult.ItemConfigurationValidationStatus.SELECTION_RULE_VIOLATED);
		assertEquals("The error path should point to the nested bundle..", Arrays.asList(bundleId), result.getErrorPath());
	}


	private ItemConfigurationBuilder persistDefaultConfigurationAndLoadBuilder(final ProductBundle bundle) {
		ItemConfiguration defaultItemConfiguration = itemConfigurationFactory.createItemConfiguration(bundle, shopper);
		ItemConfigurationId bundleConfId = itemConfigurationService.saveConfiguredItem(defaultItemConfiguration);
		return itemConfigurationService.loadBuilder(bundleConfId);
	}

	private ItemConfiguration persistAndAssertDefaultItemConfiguration(final Product product) {
		ItemConfiguration defaultItemConfiguration = itemConfigurationFactory.createItemConfiguration(product, shopper);
		ItemConfigurationId idFromPersisting = itemConfigurationService.saveConfiguredItem(defaultItemConfiguration);

		ItemConfigurationId defaultItemConfigurationId = itemConfigurationService.getDefaultItemConfigurationId(product, shopper);
		assertEquals("ID from persisting the item from the factory should be equal to the one returned from the service",
				idFromPersisting, defaultItemConfigurationId);

		ItemConfiguration loadedItem = itemConfigurationService.load(defaultItemConfigurationId);
		assertEquals("The loaded item should be equal to the item that was persisted", defaultItemConfiguration, loadedItem);
		return loadedItem;
	}

	/**
	 * Creates and persists a bundle containing the following constituents:
	 * <li>a multi sku product</li>
	 * <li>a single sku product</li>
	 * @return the persisted bundle.
	 */
	private ProductBundle persist1LevelBundle() {
		ProductBundle bundle = generateSimpleProductBundle();
		bundle.setSelectionRule(new SelectionRuleFactoryImpl().createSelectAllRule());
		bundle.setCategoryAsDefault(scenario.getCategory());

		bundle.addConstituent(createProductConstituent(multiSkuProduct));
		bundle.addConstituent(createProductConstituent(singleSkuProduct));

//		productService.saveOrUpdate(bundle);

		catalogPersister.persistSimpleProductSku("bundle1Sku", 10.23, "CAD", true, bundle, scenario.getWarehouse());

		return bundle;
	}

	/**
	 * Creates and persists a bundle containing the following constituents:
	 * <li>a single sku product</li>
	 * <li>the 1-level bundle generated in {@link #persist1LevelBundle()}</li>
	 * <li>the sku with code MSKU1 of the multi-sku product</li>
	 * <li>the the multi-sku product</li>
	 * @return the persisted bundle.
	 */
	private ProductBundle persist2LevelBundle() {
		ProductBundle nestedBundle = persist1LevelBundle();
		ProductBundle bundle = generateSimpleProductBundle();
		bundle.setSelectionRule(new SelectionRuleFactoryImpl().createSelectAllRule());
		bundle.setCategoryAsDefault(scenario.getCategory());

		bundle.addConstituent(createProductConstituent(singleSkuProduct));
		bundle.addConstituent(createProductConstituent(productService.get(nestedBundle.getUidPk())));
		bundle.addConstituent(createSkuConstituent(multiSkuProduct.getSkuByCode(MSKU1)));
		bundle.addConstituent(createProductConstituent(multiSkuProduct));
	//	productService.saveOrUpdate(bundle);

		catalogPersister.persistSimpleProductSku("bundle2Sku", 10.23, "CAD", true, bundle, scenario.getWarehouse());
		return bundle;
	}

	private ProductBundle generateSimpleProductBundle() {
		return catalogPersister.createSimpleProductBundle(BUNDLE_PRODUCT_TYPE, Utils.uniqueCode("bundle"), scenario.getCatalog(), 
																											scenario.getCategory(), taxCode);
	}

	private BundleConstituent createProductConstituent(final Product product) {
		return catalogPersister.createSimpleBundleConstituent(product, 1);
	}

	private BundleConstituent createSkuConstituent(final ProductSku sku) {
		return catalogPersister.createSimpleBundleConstituent(sku, 1);
	}

	private Product generateSimpleProduct() {
		Product product = catalogPersister.createSimpleProduct(PRODUCT_TYPE, "product1", scenario.getCatalog(), taxCode, scenario.getCategory());
		product.setCategoryAsDefault(scenario.getCategory());
		catalogPersister.persistSimpleProductSku(SKU1, 1.56, "CAD", true, product, scenario.getWarehouse());
		productService.saveOrUpdate(product);
		return product;
	}

	private Product generateMultiSkuProduct() {
		Product product = catalogPersister.createMultiSkuProduct(scenario.getCatalog(), scenario.getCategory(), null,
				"product2", MULTISKU_PRODUCT_TYPE, "multisku product", taxCode.getCode(),
				AvailabilityCriteria.ALWAYS_AVAILABLE, 100, MSKU1, MSKU2);
		product.setCategoryAsDefault(scenario.getCategory());
		productService.saveOrUpdate(product);
		return product;
	}

}
