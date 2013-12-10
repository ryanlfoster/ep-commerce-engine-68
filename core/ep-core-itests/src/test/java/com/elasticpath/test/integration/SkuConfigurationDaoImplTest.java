/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.integration;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.SkuConfigurationDao;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.db.DbTestCase;

/**
 * Tests the implementation of {@link SkuConfigurationDao}.
 */
public class SkuConfigurationDaoImplTest extends DbTestCase {

	private static final String RED = "red";
	private static final String BLUE = "blue";
	private static final String GREEN = "green";
	private static final String LARGE = "large";
	private static final String MEDIUM = "medium";
	private static final String SMALL = "small";
	
	@Autowired
	private SkuConfigurationDao skuConfigurationDao;

	/**
	 * Set up:
	 * 1. Creates a product
	 * 2. Create SKUs of the product
	 * 3. Simulates update of the SKU option values on the product SKUs (this was previously creating duplicate values in T
	 * 4. Tries to retrieve product SKUs from the database using the {@link SkuConfigurationDao} implementation.
	 */
	@DirtiesDatabase
	@Test
	public void testDuplicateSkuOptionValues() {
		final SkuOptionService optionService = getBeanFactory().getBean(ContextIdNames.SKU_OPTION_SERVICE);
		final ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		final ProductSkuService skuService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU_SERVICE);
		final ProductTypeService productTypeService = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
		
		final Catalog catalog = scenario.getCatalog();
		
		SkuOption skuOptionColor = createSkuOption(catalog, "Color", RED, GREEN);
		SkuOption skuOptionSize = createSkuOption(catalog, "Size", SMALL, MEDIUM, LARGE);
		
		optionService.add(skuOptionColor);
		optionService.add(skuOptionSize);
		
		// Create a product type
		ProductType productType = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE);
		populateProductType(catalog, productType, skuOptionColor, skuOptionSize);
		productTypeService.add(productType);

		// (1) create a multi sku product
		Product product = createMultiSkuProduct(catalog, productType);
		assertEquals(0, product.getProductSkus().size());
		
		// (2) create and add SKUs
		@SuppressWarnings("unchecked")
		ProductSku productSkuRedSmall = createAndAddSku(product,
				new Pair<SkuOption, String>(skuOptionColor, RED),
				new Pair<SkuOption, String>(skuOptionSize, SMALL));
		skuService.add(productSkuRedSmall);

		@SuppressWarnings("unchecked")
		ProductSku productSkuRedLarge = createAndAddSku(product,
				new Pair<SkuOption, String>(skuOptionColor, RED),
				new Pair<SkuOption, String>(skuOptionSize, LARGE));
		skuService.add(productSkuRedLarge);

		@SuppressWarnings("unchecked")
		ProductSku productSkuRedMedium = createAndAddSku(product,
				new Pair<SkuOption, String>(skuOptionColor, RED),
				new Pair<SkuOption, String>(skuOptionSize, MEDIUM));
		skuService.add(productSkuRedMedium);

		@SuppressWarnings("unchecked")
		ProductSku productSkuGreenMedium = createAndAddSku(product,
				new Pair<SkuOption, String>(skuOptionColor, GREEN), 
				new Pair<SkuOption, String>(skuOptionSize, MEDIUM));
		skuService.add(productSkuGreenMedium);

		product = productService.saveOrUpdate(product);
		product = productService.get(product.getUidPk());
		
		// (3) update SKU option values
		for (ProductSku productSku : product.getProductSkus().values()) {
			// load from the database
			productSku = skuService.get(productSku.getUidPk());

			final int duplicateEntries = 5;
			for (SkuOptionValue skuOptionValue : productSku.getOptionValueMap().values()) {
				for (int i = 0; i < duplicateEntries; i++) {
					productSku.setSkuOptionValue(skuOptionValue.getSkuOption(), skuOptionValue.getOptionValueKey());
					productSku = skuService.saveOrUpdate(productSku);
				}		
			}		
		}
		
		// (4) Find the SKUs that are expected using the SKU option values 
		long skuUid = skuConfigurationDao.getSkuWithMatchingOptionValues(product.getUidPk(), Arrays.asList(RED, SMALL));
		assertEquals("This SKU exists and it is expected", productSkuRedSmall.getUidPk(), skuUid);

		skuUid = skuConfigurationDao.getSkuWithMatchingOptionValues(product.getUidPk(), Arrays.asList(SMALL, RED));
		assertEquals("This SKU exists and it is expected", productSkuRedSmall.getUidPk(), skuUid);

		skuUid = skuConfigurationDao.getSkuWithMatchingOptionValues(product.getUidPk(), Arrays.asList(BLUE, MEDIUM));
		assertEquals("No SKU exists so no SKU is expected", 0, skuUid);

		skuUid = skuConfigurationDao.getSkuWithMatchingOptionValues(product.getUidPk(), Arrays.asList(RED));
		assertTrue("The expected value is one of the defined SKUS with RED color", 
				Arrays.asList(productSkuRedSmall.getUidPk(), 
						productSkuRedMedium.getUidPk(),
						productSkuRedLarge.getUidPk()).contains(skuUid));

		skuUid = skuConfigurationDao.getSkuWithMatchingOptionValues(product.getUidPk(), Arrays.asList(MEDIUM));
		assertTrue("The expected value is one of the defined SKUS with MEDIUM size",
				Arrays.asList(productSkuGreenMedium.getUidPk(), 
						productSkuRedMedium.getUidPk()).contains(skuUid));
	}

	/**
	 *
	 * @param product
	 * @param string
	 * @param string2
	 * @return
	 */
	private ProductSku createAndAddSku(final Product product, Pair<SkuOption, String>... skuOptionValues) {
		ProductSku productSku = this.getBeanFactory().getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode(new RandomGuidImpl().toString());
		productSku.setStartDate(new Date());

		productSku.setProduct(product);
		for (Pair<SkuOption, String> value : skuOptionValues) {
			productSku.setSkuOptionValue(value.getFirst(), value.getSecond());
		}
		return productSku;
	}

	private SkuOption createSkuOption(final Catalog catalog, final String name, final String... options) {
		// Create a new SKU Option
		SkuOption skuOption = getBeanFactory().getBean(ContextIdNames.SKU_OPTION);
		// create a Color sku option
		populateSkuOption(catalog, skuOption, name);

		for (String option : options) {
			SkuOptionValue greenColorSkuOptionValue = getBeanFactory().getBean(ContextIdNames.SKU_OPTION_VALUE);
			greenColorSkuOptionValue.setOptionValueKey(option);
			skuOption.addOptionValue(greenColorSkuOptionValue);
		}		
		return skuOption;
	}
	
	private void populateSkuOption(final Catalog catalog, final SkuOption skuOption, final String name) {
		skuOption.setCatalog(catalog);
		skuOption.setOptionKey(name);
		skuOption.setDisplayName(name, Locale.ENGLISH);
	}

	private void populateProductType(final Catalog catalog, final ProductType productType, final SkuOption... skuOptions) {
		productType.setName("FR_PC_CARD");
		productType.setTemplate("productTemplate");
		TaxCodeService taxCodeService = getBeanFactory().getBean(ContextIdNames.TAX_CODE_SERVICE);
		TaxCode taxCode = taxCodeService.findByCode("GOODS");
		productType.setTaxCode(taxCode);
		productType.setWithMultipleSkus(true);
		for (SkuOption skuOption : skuOptions) {
			productType.addOrUpdateSkuOption(skuOption);
		}
		productType.setCatalog(catalog);
		productType.setWithMultipleSkus(true);
	}

	private Product createMultiSkuProduct(final Catalog catalog, final ProductType productType) {
		TaxCodeService taxCodeService = getBeanFactory().getBean(ContextIdNames.TAX_CODE_SERVICE);
		TaxCode taxCode = taxCodeService.findByCode("GOODS");
		Category defaultCategory = scenario.getCategory();
		return persisterFactory.getCatalogTestPersister().persistSimpleProduct("product1", productType.getName(), catalog, defaultCategory, taxCode);
	}


}
