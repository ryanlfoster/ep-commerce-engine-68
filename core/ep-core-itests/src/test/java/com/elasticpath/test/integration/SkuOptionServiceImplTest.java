/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.util.Utils;

/**
 * Tests the functionality of {@link SkuOptionService}.
 */
public class SkuOptionServiceImplTest extends DbTestCase {

	private static final String COLOR = "FR_COLOR";

	private static final String SKU_OPTION_KEY2 = "key1";

	private static final String SKU_OPTION_VALUE_KEY2 = "key2";

	private static final String SKU_OPTION_VALUE_KEY3 = "key3";

	private static final String SKU_OPTION_VALUE_KEY4 = "key4";

	@Autowired
	private SkuOptionService optionService;

	@Autowired
	private ProductService productService;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private TaxCodeService taxCodeService;

	@Autowired
	private ProductSkuService skuService;

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private ProductTypeService productTypeService;

	/**
	 * Tests creation of multiple sku options and the addition of option values to them.
	 */
	@DirtiesDatabase
	@Test
	public void testSkuOptionAndValue() {
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		SkuOption skuOption = new SkuOptionImpl();
		skuOption.setCatalog(scenario.getCatalog());

		skuOption.initialize();
		skuOption.setOptionKey(SKU_OPTION_KEY2);
		skuOption.setDisplayName("sku_displayname2", Locale.US);

		final SkuOptionValue skuOptionValue = createSkuOptionValue();
		skuOptionValue.initialize();
		skuOptionValue.setOptionValueKey(SKU_OPTION_VALUE_KEY2);
		skuOptionValue.setDisplayName(Locale.US, "test_sku_value_name2");

		skuOption = optionService.saveOrUpdate(skuOption);

		skuOption.addOptionValue(skuOptionValue);

		final SkuOption updatedSkuOption = optionService.saveOrUpdate(skuOption);

		assertNotNull("Catalog default locale should not be null", updatedSkuOption.getCatalog().getDefaultLocale());
		assertNotNull("Catalog supported locales collection should not be null", updatedSkuOption.getCatalog().getSupportedLocales());
		assertFalse("Catalog supported locales collection should not be null", updatedSkuOption.getCatalog().getSupportedLocales().isEmpty());

		final SkuOptionValue skuOptionValue2 = createSkuOptionValue();
		skuOptionValue2.initialize();
		skuOptionValue2.setOptionValueKey(SKU_OPTION_VALUE_KEY3);

		updatedSkuOption.addOptionValue(skuOptionValue2);

		SkuOption updatedSkuOption2 = optionService.saveOrUpdate(updatedSkuOption);

		SkuOptionValue skuOptionValue3 = createSkuOptionValue();
		skuOptionValue3.initialize();
		skuOptionValue3.setOptionValueKey(SKU_OPTION_VALUE_KEY4);

		SkuOption loadedSkuOption = optionService.get(updatedSkuOption2.getUidPk());
		loadedSkuOption.addOptionValue(skuOptionValue3);

		SkuOption updatedSkuOption4 = optionService.saveOrUpdate(loadedSkuOption);

		product.getProductType().addOrUpdateSkuOption(updatedSkuOption4);

		product = productService.saveOrUpdate(product);

		skuOption = product.getProductType().getSkuOptions().iterator().next();

		skuOptionValue3 = createSkuOptionValue();
		skuOptionValue3.initialize();
		skuOptionValue3.setOptionValueKey(SKU_OPTION_VALUE_KEY4 + 1);

		skuOption.addOptionValue(skuOptionValue3);

		SkuOption updatedSkuOption3 = optionService.saveOrUpdate(skuOption);
		loadedSkuOption = optionService.get(updatedSkuOption3.getUidPk());
		
		// testing out the use case for updating the skuOptionValue and trying to save the SkuOption
		int index = 1;
		for (SkuOptionValue value : loadedSkuOption.getOptionValues()) {
			value.setDisplayName(Locale.US, "test_sku_value_name" + index++);
			value.setSkuOption(loadedSkuOption);
			loadedSkuOption.addOptionValue(value);
		}
		optionService.update(loadedSkuOption);
	}

	/**
	 * Tests reordering of sku option values.
	 */
	@DirtiesDatabase
	@Test
	public void testReorderSkuOptionValues() {
		// SKU option
		SkuOption skuOption = new SkuOptionImpl();
		skuOption.setCatalog(scenario.getCatalog());

		skuOption.initialize();
		skuOption.setOptionKey(SKU_OPTION_KEY2);
		skuOption.setDisplayName("sku_displayname2", Locale.US);

		// SKU option value 1
		final SkuOptionValue skuOptionValue = createSkuOptionValue();
		skuOptionValue.setOptionValueKey(SKU_OPTION_VALUE_KEY2);
		skuOptionValue.setDisplayName(Locale.US, "test_sku_value_name2");
		skuOptionValue.setOrdering(1);

		// SKU option value 2
		final SkuOptionValue skuOptionValue2 = createSkuOptionValue();
		skuOptionValue2.setOptionValueKey(SKU_OPTION_VALUE_KEY3);
		skuOptionValue2.setDisplayName(Locale.US, "test_sku_value_name3");
		skuOptionValue2.setOrdering(2);

		skuOption.addOptionValue(skuOptionValue);
		skuOption.addOptionValue(skuOptionValue2);

		skuOption = optionService.saveOrUpdate(skuOption);

		skuOption.getOptionValue(SKU_OPTION_VALUE_KEY2).setOrdering(2);
		skuOption.getOptionValue(SKU_OPTION_VALUE_KEY3).setOrdering(1);

		skuOption = optionService.saveOrUpdate(skuOption);
	}

	/**
	 * Test adding sku option values the same way that a catalog import.
	 */
	@DirtiesDatabase
	@Test
	public void testDoingWhatCatalogImportDoes() {
		final int number = 50;
		final int numberToUpdate = 20;
		final int numberOfNewValues = 5;
		
		// Create new catalog
		Catalog catalog = getBeanFactory().getBean(ContextIdNames.CATALOG);
		populateCatalog(catalog);
		
		catalogService.saveOrUpdate(catalog);
		
		// Create a new SKU Option
		SkuOption skuOption = getBeanFactory().getBean(ContextIdNames.SKU_OPTION);
		populateSkuOption(catalog, skuOption);
		
		for (int i = 0; i < number; i++) {
			// Add a Sku Option Value
			final SkuOptionValue newSkuOptionValue = createSkuOptionValue();
			populateSkuOptionValue(newSkuOptionValue, "FR_key_Color_ActiveYellow" + i, "Active Yellow", 
					"http://sedev14.east.cybercom.se:8080/ps/file/1.249533.1193024768/spring%7Eyellow_color_image.jpg");
			skuOption.addOptionValue(newSkuOptionValue);
		}
		
		// Save the sku option
		optionService.add(skuOption);
		
		// Create a product type
		skuOption = optionService.findByKey(COLOR);
		ProductType productType = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE);
		populateProductType(catalog, skuOption, productType);
		productTypeService.add(productType);

		
		// Check how many localized properties we have for the sku option
		assertNoDuplicateRows(number);
		
		// Update catalog
		Catalog catalogToUpdate =  catalogService.findByCode("FRCATALOG");
		FetchGroupLoadTuner tuner = new FetchGroupLoadTunerImpl();
		tuner.addFetchGroup(FetchGroupConstants.CATALOG_EDITOR);
		catalogToUpdate = catalogService.load(catalogToUpdate.getUidPk(), tuner, false);
		populateCatalog(catalogToUpdate);
		catalogToUpdate = catalogService.saveOrUpdate(catalogToUpdate);
		
		// Update Sku Option
		SkuOption skuOptionToUpdate = optionService.findByKey(COLOR);
		for (int indexUpdated = 0; indexUpdated < numberToUpdate; indexUpdated++) {
			
			populateSkuOption(catalogToUpdate, skuOptionToUpdate);
			
			for (SkuOptionValue value : skuOptionToUpdate.getOptionValues()) {
				populateSkuOptionValue(value, value.getOptionValueKey(), value.getDisplayName(Locale.ENGLISH, false), 
						value.getImage());
			}
			
			for (int indexNew = 0; indexNew < numberOfNewValues; indexNew++) {
				// Add a Sku Option Value
				final SkuOptionValue newSkuOptionValue = createSkuOptionValue();
				String uniqueId = new RandomGuidImpl().toString();
				// make sure that the name is unique by assigning a number that cannot be reached
				populateSkuOptionValue(newSkuOptionValue, "FR_key_Color_ActiveYellow" + uniqueId, 
						"Active Yellow", 
						"http://sedev14.east.cybercom.se:8080/ps/file/1.249533.1193024768/spring%7Eyellow_color_image.jpg");
				skuOptionToUpdate.addOptionValue(newSkuOptionValue);
			}
			
			skuOptionToUpdate = optionService.update(skuOptionToUpdate);
		}
		
		SkuOption loadedSkuOption = optionService.findByKey(COLOR);
		ProductType productTypeToUpdate = productTypeService.findProductType("FR_PC_CARD");
		populateProductType(catalogToUpdate, loadedSkuOption, productTypeToUpdate);
		productTypeService.update(productTypeToUpdate);
		
		assertNoDuplicateRows(number + numberOfNewValues * numberToUpdate);
		
	}

	private SkuOptionValue createSkuOptionValue() {
		return getBeanFactory().getBean(ContextIdNames.SKU_OPTION_VALUE);
	}

	private void assertNoDuplicateRows(final int number) {
		PersistenceSession session = persistenceEngine.getPersistenceSession();
		Transaction transaction = session.beginTransaction();
		String queryString = "SELECT lp FROM SkuOptionLocalizedPropertyValueImpl lp WHERE lp.localizedPropertyKey = 'skuOptionDisplayName_en'";
		Query query = session.createQuery(queryString);
		transaction.commit();
		
		assertEquals("There should only be one localized property value for the key", 1, query.list().size());

		// Check how many localized properties we have for the sku option value
		transaction = session.beginTransaction();
		queryString = "SELECT lp FROM SkuOptionValueLocalizedPropertyValueImpl lp WHERE lp.localizedPropertyKey = 'skuOptionValueDisplayName_en'";
		query = session.createQuery(queryString);
		transaction.commit();
		
		assertEquals("Exact number of localized property values for the option value should exist", number, query.list().size());
	}

	private void populateSkuOption(final Catalog catalog, final SkuOption skuOption) {
		skuOption.setCatalog(catalog);
		skuOption.setOptionKey(COLOR);
		skuOption.setDisplayName("Color", Locale.ENGLISH);
	}

	private void populateProductType(final Catalog catalog, final SkuOption skuOption, final ProductType productType) {
		productType.setName("FR_PC_CARD");
		productType.setTemplate("productTemplate");
		TaxCode taxCode = taxCodeService.findByCode("GOODS");
		productType.setTaxCode(taxCode);
		productType.setWithMultipleSkus(true);
		productType.addOrUpdateSkuOption(skuOption);
		productType.setCatalog(catalog);
	}

	private void populateSkuOptionValue(final SkuOptionValue skuOptionValue, final String code, final String name, final String image) {
		skuOptionValue.setOptionValueKey(code);
		skuOptionValue.setDisplayName(Locale.ENGLISH, name);
		skuOptionValue.setImage(image);
	}

	private void populateCatalog(final Catalog catalog) {
		catalog.setCode("FRCATALOG");
		catalog.setName("e-Shop France Catalog");
		catalog.setMaster(true);
		catalog.setDefaultLocale(Locale.ENGLISH);
		catalog.addSupportedLocale(Locale.ENGLISH);
	}
	
	/**
	 * Tests that if a product SKU is updated by setting its SKU option values we can
	 * still retrieve the expected number of SKU option values.
	 */
	@DirtiesDatabase
	@Test
	public void testChangingSkuOptionValue() {
		Catalog catalog = scenario.getCatalog();
		
		// Create a new SKU Option
		SkuOption skuOption = getBeanFactory().getBean(ContextIdNames.SKU_OPTION);
		// create a Color sku option
		populateSkuOption(catalog, skuOption);

		// add green and red colours
		// create and add green colour
		SkuOptionValue greenColorSkuOptionValue = createSkuOptionValue();
		String colorGreenString = "green";
		greenColorSkuOptionValue.setOptionValueKey(colorGreenString);
		skuOption.addOptionValue(greenColorSkuOptionValue);
		
		// create and add red colour
		SkuOptionValue redColorSkuOptionValue = createSkuOptionValue();
		String colorRedString = "red";
		redColorSkuOptionValue.setOptionValueKey(colorRedString);
		skuOption.addOptionValue(redColorSkuOptionValue);
		
		// create a multi sku product
		Product product = createMultiSkuProduct(catalog);
		// use the first sku
		ProductSku productSku = product.getDefaultSku();

		// Save the sku option
		optionService.add(skuOption);
		
		// Create a product type
		ProductType productType = productSku.getProduct().getProductType();
		populateProductType(catalog, skuOption, productType);
		productTypeService.update(productType);

		// load from the database
		productSku = skuService.get(productSku.getUidPk());
		// the product sku is created with 2 sku options by default
		assertEquals("The product sku is expected to have 2 predefined sku option values", 2, productSku.getOptionValueMap().size());
		// save the product sku
		productSku = skuService.saveOrUpdate(productSku);
		
		final int numberOfTimesToReSetSkuOptionValue = 5;
		
		for (int i = 0; i < numberOfTimesToReSetSkuOptionValue; i++) {
			// set the product to be green
			productSku.setSkuOptionValue(skuOption, "green");
			
			productSku = skuService.saveOrUpdate(productSku);
		}		
		assertEquals("Exactly 3 option value codes expected", 2 + 1, productSku.getOptionValueCodes().size());
		assertEquals("The expected value code is the one that was set last", "green", productSku.getSkuOptionValue(skuOption).getOptionValueKey());
		
		// set the product to be red
		for (int i = 0; i < numberOfTimesToReSetSkuOptionValue; i++) {
			// set the product to be green
			productSku.setSkuOptionValue(skuOption, "red");
			
			productSku = skuService.saveOrUpdate(productSku);
		}		
		assertEquals("Exactly 3 option value codes expected", 2 + 1, productSku.getOptionValueCodes().size());
		assertEquals("The expected value code is the one that was set last", "red", productSku.getSkuOptionValue(skuOption).getOptionValueKey());

		// retrieve the product sku from the data source
		product = productService.get(product.getUidPk());
		assertNotNull("Product must exist", product);
		
		List<SkuOption> skuOptionList = product.getProductType().getSortedSkuOptionList(productSku);

		assertEquals("Exactly 3 option values are expected for that product sku.", 2 + 1, skuOptionList.size());
		SkuOption retrievedColorSkuOption = skuOptionList.get(skuOptionList.indexOf(skuOption));
		Collection<SkuOptionValue> retrievedColorSkuOptionValues = retrievedColorSkuOption.getOptionValues();
		assertEquals(2, retrievedColorSkuOptionValues.size());
		assertTrue("The red option value is expected to exist", retrievedColorSkuOptionValues.contains(redColorSkuOptionValue));
		assertTrue("The green option value is expected to exist", retrievedColorSkuOptionValues.contains(greenColorSkuOptionValue));
	}

	private Product createMultiSkuProduct(final Catalog catalog) {
		int orderLimit = 0;
		AvailabilityCriteria criteria = AvailabilityCriteria.ALWAYS_AVAILABLE;
		String taxCode = "GOODS";
		String productName = "My MultiSku Product";
		String productCode = "productCode1";
		Currency currency = Currency.getInstance(Locale.US);
		BigDecimal productPrice = BigDecimal.TEN;
		Warehouse warehouse = scenario.getWarehouse();
		Category defaultCategory = scenario.getCategory();
		String[] skus = new String[] {"sku1", "sku2" };
		return persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(catalog, defaultCategory, 
				warehouse, productPrice, currency, null, productCode, productName, taxCode, criteria, orderLimit, skus);
	}
	
    /**
     * Test that notification of product sku option updates the last modified time of related products.
     * @throws InterruptedException if sleep interrupted.
     */
    @DirtiesDatabase
	@Test
    public void testNotifySkuOptionUpdated() throws InterruptedException {
        String skuCode1 = Utils.uniqueCode("sku");
        String skuCode2 = Utils.uniqueCode("sku");
        Product product = this.persisterFactory.getCatalogTestPersister().persistMultiSkuProduct(scenario.getCatalog(),
                scenario.getCategory(), scenario.getWarehouse(), BigDecimal.valueOf(269.00), TestDataPersisterFactory.DEFAULT_CURRENCY,
                null, Utils.uniqueCode("product"), "Multi-Sku Product", "GOODS", null, 0, skuCode1, skuCode2);
        product = productService.saveOrUpdate(product);
        Date lastModified = product.getLastModifiedDate();
        assertFalse("Product should have sku options", product.getProductType().getSkuOptions().isEmpty());
        
        // TODO: Fortification: This sleep has been put in, since MySQL only stores timestamp 
        //                      to the second level of precision (rather than millisecond).
        //                      Further testing needs to be done to determine whether this
        //                      will cause problems or not.
        Thread.sleep(1000);

        SkuOption option = product.getProductType().getSkuOptions().iterator().next();
        assertNotNull("An sku option should be returned.", option);
        optionService.notifySkuOptionUpdated(option);
        Product updatedProduct = productService.get(product.getUidPk());
        Date newLastModifiedDate = updatedProduct.getLastModifiedDate();
        assertTrue("Product's last updated date should be updated", newLastModifiedDate.after(lastModified));
    }
}
