/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.persister;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.builder.ProductSkuBuilder;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AttributeComparator;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.SkuAttributeValueImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.util.Utils;

/**
 * Persister allows to create and save into database catalog dependent domain objects.
 */
public class CatalogTestPersister {

	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private ProductSkuBuilder productSkuBuilder;

	// Services for saving and updating persisted objects.
	@Autowired
	private  ProductService productService;
	
	@Autowired
	private ProductSkuService productSkuService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductTypeService productTypeService;
	
	@Autowired
	private CategoryTypeService categoryTypeService;
	
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
	private SkuOptionService skuOptionService;
	
	@Autowired
	private ProductAssociationService productAssociationService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private ProductInventoryManagementService productInventoryManagementService;

	@Autowired
	private InventoryFacade inventoryFacade;

	@Autowired
	private AttributeService attributeService;
	
	@Autowired
	private PriceListDescriptorService priceListDescriptorService;
	
	@Autowired
	private PriceListAssignmentService plaService;
	
	@Autowired
	private BundleConstituentFactory constituentFactory;
	
	@Autowired
	private TaxTestPersister taxTestPersister;

	private static final int HUNDRED = 100;

	private static final int ZERO = 0;

	private static final double FOUR_NINETY_FIVE = 495.00;

	private static final double TEN = 10.00;

	private static final double FIVE_NINETY_NINE = 599.00;

	private static final double NINE_NINETY_NINE = 999.00;

	private static final double TWO_SIXTY_NINE = 269.00;

	private static final double FOUR_NINETY_NINE = 499.00;

	private static final double THIRTY_NINE = 39.00;

	private static final double ONE_NINETEEN = 119.00;

	private static final double TWENTY_NINE_NINETY_NINE = 29.99;

	private static final String INVENTORY_UPDATE_REASON = "Reason to update inventory";

	/**
	 * Creates persisted catalog with default name, currency, and locale with all other fields set to the given parameter values. No new supported
	 * currencies nor locales are added.
	 *
	 * @param code the unique code associated with created catalog
	 * @param master true if catalog is Master Catalog false if catalog is Virtual Catalog
	 * @return persisted catalog
	 */
	public Catalog persistCatalog(final String code, final boolean master) {
		// let overridden method to set up default values.
		return persistCatalog(code, null, master, null, null, null, null, true);
	}

	/**
	 * Create persisted catalog with given parameter values.
	 *
	 * @param code the unique code associated with catalog being created
	 * @param name the name of the catalog being created
	 * @param master true if catalog is Master Catalog false if catalog is Virtual Catalog
	 * @param defaultCurrency the default currency of catalog being created.
	 * @param defaultLocale the default locale of catalog being created.
	 * @param currencies a collection of supported currencies.
	 * @param locales a collection of supported locales.
	 * @param createPLA TODO
	 * @return persisted catalog
	 */
	public Catalog persistCatalog(final String code, final String name, final boolean master, final String defaultCurrency,
			final String defaultLocale, final Collection<String> currencies, final Collection<String> locales, final boolean createPLA) {

		final Catalog catalog = beanFactory.getBean(ContextIdNames.CATALOG);

		catalog.setMaster(master);
		catalog.setCode(code);
		catalog.setName(name == null ? code : name);

		// Setup supported locales
		if (master && locales != null) {
			for (final String locale : locales) {
				catalog.addSupportedLocale(new Locale(locale));
			}
		}

		catalog.setDefaultLocale(defaultLocale == null ? TestDataPersisterFactory.DEFAULT_LOCALE : new Locale(defaultLocale));

		Currency catalogCurrency = TestDataPersisterFactory.DEFAULT_CURRENCY;
		if (defaultCurrency != null) {
			catalogCurrency = Currency.getInstance(defaultCurrency);
		}
		final Catalog persistedCatalog = catalogService.saveOrUpdate(catalog);

		if (createPLA) {
			PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(
					getPriceListGuidForCatalog(persistedCatalog, catalogCurrency));

			if (priceListDescriptor == null) {
				priceListDescriptor = createPriceListDescriptor(catalogCurrency.getCurrencyCode(), persistedCatalog);
			}
			final PriceListAssignmentPersister plaPersister = new PriceListAssignmentPersister(beanFactory);
			plaPersister.createPriceListAssignment(persistedCatalog.getGuid(), priceListDescriptor.getGuid(),
					persistedCatalog.getGuid() + priceListDescriptor.getGuid() + priceListDescriptor.getCurrencyCode(),
					"", 10);
		}

		return persistedCatalog;
	}

	/**
	 * *
	 * @param defaultCurrency *
	 * @param catalog *
	 * @return *
	 */
	public PriceListDescriptor createPriceListDescriptor(final String defaultCurrency, final Catalog catalog) {
		final String pldName = getPriceListGuidForCatalog(catalog, Currency.getInstance(defaultCurrency));
		return this.createPriceListDescriptor(pldName, pldName, defaultCurrency, pldName);
	}

	/**
	 * Create a persisted virtual catalog.
	 *
	 * @return the persisted virtual catalog
	 */
	public Catalog createPersistedVirtualCatalog() {
		final Catalog catalog = beanFactory.getBean(ContextIdNames.CATALOG);
		catalog.setCode(Utils.uniqueCode("virutalCatalog"));
		catalog.setDefaultLocale(Locale.ENGLISH);
		catalog.setMaster(false);
		catalog.setName("virtualCatalog");

		final PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(getPriceListGuidForCatalog(catalog, Currency
				.getInstance(Locale.US)));

		if (priceListDescriptor == null) {
			createPriceListDescriptor(Currency.getInstance(Locale.US).getCurrencyCode(), catalog);
		}
		return catalogService.saveOrUpdate(catalog);
	}

	/**
	 * Create a persisted master catalog with a unique code.
	 *
	 * @return the catalog
	 */
	public Catalog persistDefaultMasterCatalog() {
		final Catalog catalog = beanFactory.getBean(ContextIdNames.CATALOG);
		catalog.setMaster(true);
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.addSupportedLocale(Locale.ENGLISH);
		catalog.addSupportedLocale(Locale.US);
		catalog.addSupportedLocale(Locale.FRENCH);
		catalog.addSupportedLocale(Locale.FRANCE);
		catalog.setDefaultLocale(Locale.ENGLISH);
		catalog.setName("Test Data Persister Catalog");

		PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(getPriceListGuidForCatalog(catalog, Currency
				.getInstance(Locale.US)));

		if (priceListDescriptor == null) {
			priceListDescriptor = createPriceListDescriptor(Currency.getInstance(Locale.US).getCurrencyCode(), catalog);
		}
		final Catalog catalogSaved = catalogService.saveOrUpdate(catalog);
		final PriceListAssignmentPersister plaPersister = new PriceListAssignmentPersister(beanFactory);
		plaPersister.createPriceListAssignment(catalogSaved.getGuid(), priceListDescriptor.getGuid(),
				catalogSaved.getGuid() + priceListDescriptor.getGuid() + priceListDescriptor.getCurrencyCode(),
				"", 10);

		return catalogSaved;
	}

	/**
	 * Create default persisted categories in the given catalog.
	 *
	 * @param catalog the catalog
	 * @return persisted category
	 */
	public Category persistDefaultCategories(final Catalog catalog) {
		final CategoryType categoryType = persistCategoryType(Utils.uniqueCode("catType"), Utils.uniqueCode("catTypeTemplate"), catalog);
		return persistCategory(Utils.uniqueCode("category"), catalog, categoryType, null, null);
	}

	/**
	 * Create a linked category in the given catalog linked to the given category.
	 *
	 * @param virtualCatalog the catalog to create the linked category in
	 * @param masterCategory the category to link to
	 * @return the persisted linked category
	 */
	public Category persistLinkedCategory(final Catalog virtualCatalog, final Category masterCategory) {
		final Category linkedCategory = beanFactory.getBean(ContextIdNames.LINKED_CATEGORY);
		linkedCategory.setCatalog(virtualCatalog);
		linkedCategory.setMasterCategory(masterCategory);
		linkedCategory.setIncluded(true);
		linkedCategory.setOrdering(masterCategory.getOrdering());
		return categoryService.add(linkedCategory);
	}

	/**
	 * This method creates a linked category to the given master category, and then recursively does the same for any of the sub-categories. For each
	 * product in each of the master categories, the new linked category of the same level is added to the product.
	 *
	 * @param masterCategoryUid the virtual catalog in which the new linked category should be created
	 * @param parentCategoryUid the master catalog code in which the master category currently exists
	 * @param virtualCatalogUid the master category code to be linked to from the virtual catalog
	 */
	public void addLinkedCategory(final long masterCategoryUid, final long parentCategoryUid, final long virtualCatalogUid) {
		categoryService.addLinkedCategory(masterCategoryUid, parentCategoryUid, virtualCatalogUid);
	}

	/**
	 * Creates Shippable Products with SKUs.<br>
	 * <li>Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD
	 * <ul>
	 * SKU: <b>ETX-105PE</b>, Price: <b>$599.00</b>
	 * </ul> <li>Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD
	 * <ul>
	 * SKU: <b>SN-8-AT</b>, Price: <b>$999.00</b>
	 * </ul> <li>Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD
	 * <ul>
	 * SKU: <b>0349B004</b>, Price: <b>$269.00</b><br>
	 * SKU: <b>0348B003</b>, Price: <b>$269.00</b>
	 * </ul>
	 * </li>
	 *
	 * @param catalog the catalog to associate with shippable products
	 * @param category the category to associate with shippable products
	 * @param warehouse the warehouse to associate with shippable products
	 * @return the list of created products
	 */
	public List<Product> persistDefaultShippableProducts(final Catalog catalog, final Category category, final Warehouse warehouse) {
		final List<Product> products = new ArrayList<Product>();

		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(FIVE_NINETY_NINE),
				"Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD", "ETX-105PE"));

		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(NINE_NINETY_NINE),
				"Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD", "SN-8-AT"));

		products.add(persistMultiSkuProduct(catalog, category, warehouse, BigDecimal.valueOf(TWO_SIXTY_NINE),
				TestDataPersisterFactory.DEFAULT_CURRENCY, null, Utils.uniqueCode("product"),
				"Canon PowerShot SD30 Digital ELPH - Digital camera - 5.0 Mpix - optical zoom: 2.4 x - supported memory: MMC, SD", "GOODS", null, 0,
				"0349B004", "0348B003"));

		// For Associations
		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(FOUR_NINETY_NINE),
				"Nikon Coolpix 995 - Digital camera - 3.2 Mpix - optical zoom: 4 x - supported memory: CF - black", "25047"));

		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(THIRTY_NINE),
				"Kingston - Flash memory card - 32 MB - CF", "CF-32"));

		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(ONE_NINETEEN), "Nikon WC E68 - Converter",
				"WC-E68"));

		products.add(persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(TWENTY_NINE_NINETY_NINE),
				"2 Year Extended Service Plan", "2YESPA"));

		return products;
	}
	
	/**
	 * Create product with sku and inventory. <li>Utils.uniqueCode("Canon PowerShot")
	 * <ul>
	 * SKU: <b>Utils.uniqueCode("Sku01")</b>, Price: <b>$10.00</b>
	 * </ul>
	 * </li>
	 *
	 * @param catalog catalog to which the product belongs
	 * @param category category to which the product belongs
	 * @param warehouse warehouse in which product resides
	 * @return created product
	 */
	public Product createDefaultProductWithSkuAndInventory(final Catalog catalog, final Category category, final Warehouse warehouse) {
		return persistProductWithSkuAndInventory(catalog, category, warehouse, BigDecimal.valueOf(TEN), Utils.uniqueCode("Canon PowerShot"), Utils
				.uniqueCode("Sku01"));
	}

	/**
	 * Creates Non Shippable Products with SKUs.<br>
	 * <li>ExpressDigital Darkroom Professional Edition - Complete package - 1 user - Win
	 * <ul>
	 * SKU: <b>105DRPROF</b>, Price: <b>$495.00</b>
	 * </ul>
	 *
	 * @param catalog catalog to which the product belongs
	 * @param category category to which the product belongs
	 * @param warehouse warehouse in which product resides
	 * @return persisted non shippable products
	 */
	public List<Product> persistDefaultNonShippableProducts(final Catalog catalog, final Category category, final Warehouse warehouse) {
		final List<Product> products = new ArrayList<Product>();
		products.add(persistNonShippablePersistedProductWithSku(catalog, category, warehouse, BigDecimal.valueOf(FOUR_NINETY_FIVE),
				"ExpressDigital Darkroom Professional Edition - Complete package - 1 user - Win", "105DRPROF"));
		return products;
	}

	/**
	 * Create product with sku which doesn't required shipping.
	 *
	 * @param catalog product to be placed in
	 * @param defaultCategory the default category of the product
	 * @param warehouse the warehouse in which the product is stored
	 * @param productPrice the price of the product
	 * @param productName displayed name of product
	 * @param skuCode string code of stock-keeping unit
	 * @return persisted product
	 */
	public Product persistNonShippablePersistedProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final String productName, final String skuCode) {
		return persistNonShippablePersistedProductWithSku(catalog, defaultCategory, warehouse, productPrice, productName,
				skuCode + "_productType", skuCode);
	}

	/**
	 * Create product with sku which doesn't required shipping.
	 *
	 * @param catalog product to be placed in
	 * @param defaultCategory default category of the product
	 * @param warehouse warehouse of the product
	 * @param productPrice price of the product
	 * @param productName displayed name of product
	 * @param productTypeName name of existing product type
	 * @param skuCode string code of stock-keeping unit
	 * @return persisted product
	 */
	public Product persistNonShippablePersistedProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final String productName, final String productTypeName, final String skuCode) {
		Product product = createSimpleProduct(productTypeName, skuCode + "_product", catalog,
				getPersistedTaxCode(TaxTestPersister.TAX_CODE_GOODS), defaultCategory);
		
		ProductSku testProductSku = productSkuBuilder.setSkuCode(skuCode)
													 .setStartDate(new Date())
													 .build();

		product = initLocaleDependantFields(product, productName);
		product.setDefaultSku(testProductSku);
		product.getDefaultSku().setShippable(false);

		product = productService.saveOrUpdate(product);

		final Currency currency = Currency.getInstance(Locale.US);
		setPriceOnProduct(catalog, productPrice, currency, product);


		return product;
	}

	/**
	 * Create a map, fill it with locale dependent fields and set that map to the given product.
	 *
	 * @param product to be updated with locale dependent fields map
	 * @param productName locale dependent field
	 * @return updated product
	 */
	private Product initLocaleDependantFields(final Product product, final String productName) {
		product.getLocaleDependantFields(Locale.ENGLISH).setDisplayName(StringEscapeUtils.unescapeHtml(productName));

		return product;
	}

	/**
	 * Persists a brand with the specified brand code.
	 *
	 * @param brandCode the brand code
	 * @param catalog the catalog
	 * @return persisted brand
	 */
	public Brand persistBrand(final String brandCode, final Catalog catalog) {
		final Brand brand = beanFactory.getBean(ContextIdNames.BRAND);
		brand.setCatalog(catalog);
		brand.setCode(brandCode);
		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, brandCode);
		return brandService.add(brand);
	}

	/**
	 * Persists the product type.
	 *
	 * @param name name of the product type
	 * @param catalog the catalog to which the product type belongs
	 * @param taxCode the tax code
	 * @param multipleSkuFlag flag to determine if multi SKU
	 * @return the persisted ProductType
	 */
	public ProductType persistProductType(final String name, final Catalog catalog, final String taxCode, final boolean multipleSkuFlag) {
		final ProductType productType = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setName(name);
		productType.setCatalog(catalog);
		productType.setTaxCode(getPersistedTaxCode(taxCode));
		productType.setWithMultipleSkus(multipleSkuFlag);
		productType.setTemplate(Utils.uniqueCode("productTypeTemplate"));

		final ProductTypeService productTypeService = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
		return productTypeService.add(productType);
	}

	/**
	 * Persists the product type.
	 *
	 * @param name name of the product type
	 * @param catalog the catalog to which the product type belongs
	 * @param taxCode the tax code
	 * @param multipleSkuFlag flag to determine if multi SKU
	 * @return the persisted ProductType
	 */
	public ProductType persistProductTypeWithGuid(final String name, final Catalog catalog, final String taxCode, final String guid,
			final boolean multipleSkuFlag) {
		return persistProductTypeWithGuid(name, catalog, Utils.uniqueCode("productTypeTemplate"), taxCode, guid, multipleSkuFlag);
	}

	/**
	 * Persists the product type.
	 *
	 * @param name name of the product type
	 * @param catalog the catalog to which the product type belongs
	 * @param templateName the template name
	 * @param taxCode the tax code
	 * @param multipleSkuFlag flag to determine if multi SKU
	 * @return the persisted ProductType
	 */
	public ProductType persistProductTypeWithGuid(final String name, final Catalog catalog, final String templateName, final String taxCode,
			final String guid, final boolean multipleSkuFlag) {
		final ProductType productType = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setName(name);
		productType.setCatalog(catalog);
		productType.setGuid(guid);
		productType.setTaxCode(getPersistedTaxCode(taxCode));
		productType.setWithMultipleSkus(multipleSkuFlag);
		productType.setTemplate(templateName);

		final ProductTypeService productTypeService = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
		return productTypeService.add(productType);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final AvailabilityCriteria criteria, final int orderLimit) {
		return persistProductWithSku(catalog, defaultCategory, warehouse, productPrice, currency, brandCode, productCode, productName, skuCode,
				taxCode, null, null, criteria, orderLimit);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable, final AvailabilityCriteria criteria,
			final int orderLimit) {
		return persistProductWithSku(catalog, defaultCategory, warehouse, productPrice, currency, brandCode, productCode, productName,
				productCode + "_prodyctType", skuCode, taxCode, weight, shippable, criteria, orderLimit);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
 	 * @param notSoldSeparately whether sold separately or just in bundle
	 * @param storeVisible whether visible in store
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable, final AvailabilityCriteria criteria,
			final int orderLimit, final boolean notSoldSeparately, final boolean storeVisible) {
		final String productTypeName = productCode + "_prodyctType";
		final Product product = createSimpleProduct(productTypeName, productCode, catalog, getPersistedTaxCode(taxCode), defaultCategory);
		return persistProductWithSku(product, catalog, defaultCategory, warehouse,
				productPrice, currency, brandCode, productCode, productName,
				productTypeName, skuCode, taxCode, weight, shippable, criteria,
				orderLimit, notSoldSeparately, storeVisible);
	}
	
	
	/**
	 * Persists product with a SKU. Adds the product to the category's feature products if necessary.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @param featured is the product featured
	 * @param rank feature rank
	 * @return the persisted product
	 */
	public Product persistFeaturedProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable, final AvailabilityCriteria criteria,
			final int orderLimit, final boolean featured, final int rank) {
		Product product = productService.findByGuid(productCode);

		if (product == null) {
			product = persistProductWithSku(catalog, defaultCategory, warehouse, productPrice, currency,
					brandCode, productCode, productName, skuCode, taxCode, weight, shippable, criteria, orderLimit);
		} else {
			product.addCategory(defaultCategory);
		}

		if (featured) {
			product.setFeaturedRank(defaultCategory, rank);
			productService.setProductCategoryFeatured(product.getUidPk(), defaultCategory.getUidPk());
		}
		return product;
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param prod the product to persist.
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param productTypeName the product type name
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Product prod, final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String productTypeName, final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable,
			final AvailabilityCriteria criteria, final int orderLimit) {
		return persistProductWithSku(prod, catalog, defaultCategory, warehouse,
				productPrice, currency, brandCode, productCode, productName,
				productTypeName, skuCode, taxCode, weight, shippable, criteria,
				orderLimit, prod.isNotSoldSeparately(), true);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param product the product to persist.
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param productTypeName the product type name
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @param notSoldSeparately whether sold separately or just in bundle
	 * @param storeVisible whether visible in store
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Product product, final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String productTypeName, final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable,
			final AvailabilityCriteria criteria, final int orderLimit, final boolean notSoldSeparately, final boolean storeVisible) {
		product.setGuid(productCode);
		product.setNotSoldSeparately(notSoldSeparately);
		product.setHidden(!storeVisible);
		addCategoriesForProduct(defaultCategory, product);
		
		return persistProductWithSku(catalog, defaultCategory, productPrice, currency, brandCode, productName, skuCode, weight, shippable, criteria,
				orderLimit, null, null, product, null, null, -1, -1);
	}
	
	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param productTypeName the product type name
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String productTypeName, final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable,
			final AvailabilityCriteria criteria, final int orderLimit) {
		final Product product = createSimpleProduct(productTypeName, productCode, catalog, getPersistedTaxCode(taxCode), defaultCategory);

		return persistProductWithSku(product, catalog, defaultCategory, warehouse, productPrice, currency, brandCode, productCode, productName,
				productTypeName, skuCode, taxCode, weight, shippable, criteria, orderLimit);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @param isDigital is the product a digital item
	 * @param fileName the file name of the product
	 * @param expiryDays the number of days the product expires in
	 * @param maxDownloadTimes the maximum number of times you can download
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable, final AvailabilityCriteria criteria,
			final int orderLimit, final Boolean isDigital, final String fileName, final int expiryDays, final int maxDownloadTimes) {
		return this.persistProductWithSku(catalog, defaultCategory, warehouse, productCode + "_prodyctType", productPrice, currency, brandCode,
				productCode, productName, skuCode, taxCode, weight, shippable, criteria, orderLimit, isDigital, fileName, expiryDays,
				maxDownloadTimes);
	}

	/**
	 * Persists product with a SKU.
	 *
	 * @param catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse warehouse where the product is stored
	 * @param productType the product type
	 * @param productPrice price of the product
	 * @param currency currency of the price
	 * @param brandCode the brand code
	 * @param productCode the product code
	 * @param productName the displayed name of the product
	 * @param skuCode the sku code of the product
	 * @param taxCode the tax code
	 * @param weight the weight of the product
	 * @param shippable whether the product is able to be shipped
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit per order of quantity
	 * @param isDigital is the product a digital item
	 * @param fileName the file name of the product
	 * @param expiryDays the number of days the product expires in
	 * @param maxDownloadTimes the maximum number of times you can download
	 * @return the persisted product
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse, final String productType,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable, final AvailabilityCriteria criteria,
			final int orderLimit, final Boolean isDigital, final String fileName, final int expiryDays, final int maxDownloadTimes) {
		final Product product = createSimpleProduct(productType, productCode, catalog, getPersistedTaxCode(taxCode), defaultCategory);

		return persistProductWithSku(catalog, defaultCategory, productPrice, currency, brandCode, productName, skuCode, weight, shippable, criteria,
				orderLimit, null, null, product, isDigital, fileName, expiryDays, maxDownloadTimes);
	}

	private void addCategoriesForProduct(final Category defaultCategory, final Product product) {
		product.setCategoryAsDefault(defaultCategory);
		final List<Category> linkedCategories = categoryService.findLinkedCategories(defaultCategory.getUidPk());
		for (final Category currentCategory : linkedCategories) {
			product.addCategory(currentCategory);
		}
	}

	private void setAdditionalProductProperties(final AvailabilityCriteria criteria, final int orderLimit,
			final Product product, final Date startDate, final Date endDate) {
		if (startDate != null) {
			product.setStartDate(startDate);
		}
		if (endDate != null) {
			product.setEndDate(endDate);
		}

		product.setAvailabilityCriteria(criteria);
		product.setPreOrBackOrderLimit(orderLimit);
	}

	public void productSkuDigitalSettings(final Boolean isDigital, final String fileName, final int expiryDays, final int maxDownloadTimes,
			final ProductSku productSku) {
		if (isDigital != null) {
			productSku.setDigital(isDigital);

			if (productSku.isDigital()) {
				final DigitalAsset digAsset = beanFactory.getBean(ContextIdNames.DIGITAL_ASSET);
				digAsset.setExpiryDays(expiryDays);
				digAsset.setFileName(fileName);
				digAsset.setMaxDownloadTimes(maxDownloadTimes);

				productSku.setDigitalAsset(digAsset);
			}
		}
	}

	private ProductSku prepareSku(final String skuCode, final BigDecimal weight, final Boolean shippable, final Date startDate) {
		final ProductSku productSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode(skuCode);
		if (startDate == null) {
			productSku.setStartDate(new Date());
		} else {
			productSku.setStartDate(startDate);
		}

		if (weight != null) {
			productSku.setWeight(weight);
		}
		if (shippable != null) {
			productSku.setShippable(shippable);
		}
		productSku.setGuid(skuCode);
		return productSku;
	}

	private void setPriceOnProduct(final Catalog catalog, final BigDecimal productPrice, final Currency currency, final Product product) {
		if (productPrice != null && currency != null) {
			// Put the same price into the price list
			addOrUpdateProductBaseAmount(catalog, product, BigDecimal.ONE, productPrice, productPrice, currency
					.getCurrencyCode());
		}
	}

	/**
	 * @param guid
	 * @param name
	 * @param currencyCode
	 * @param description
	 * @return the PLD
	 */
	public PriceListDescriptor createPriceListDescriptor(final String guid, final String name, final String currencyCode, final String description) {
		final PriceListDescriptor priceListDescriptor = beanFactory.getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		priceListDescriptor.setGuid(guid);
		priceListDescriptor.setName(name);
		priceListDescriptor.setCurrencyCode(currencyCode);
		priceListDescriptor.setDescription(description);
		return priceListDescriptorService.add(priceListDescriptor);
	}

	private String getPriceListGuidForCatalog(final Catalog catalog, final Currency currency) {
		return catalog.getCode() + "_" + currency.getCurrencyCode();
	}

	/**
	 *  This method will persist a product with sku given the following parameters:
	 * @param catalog
	 * @param defaultCategory
	 * @param warehouse
	 * @param productPrice
	 * @param currency
	 * @param brandCode
	 * @param productCode
	 * @param productName
	 * @param productTypeName
	 * @param skuCode
	 * @param taxCode
	 * @param weight
	 * @param shippable
	 * @param criteria
	 * @param orderLimit
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productName,
			final String productTypeName, final String skuCode, final String taxCode, final BigDecimal weight, final Boolean shippable,
			final AvailabilityCriteria criteria, final int orderLimit, final Date startDate, final Date endDate) {

		final Product product = createSimpleProduct(productTypeName, productCode, catalog, getPersistedTaxCode(taxCode), defaultCategory);

		return persistProductWithSku(catalog, defaultCategory, productPrice, currency, brandCode, productName, skuCode, weight, shippable, criteria,
				orderLimit, startDate, endDate, product, null, null, -1, -1);
	}

	private Product persistProductWithSku(final Catalog catalog, final Category defaultCategory, final BigDecimal productPrice,
			final Currency currency, final String brandCode, final String productName, final String skuCode, final BigDecimal weight,
			final Boolean shippable, final AvailabilityCriteria criteria, final int orderLimit, final Date startDate, final Date endDate,
			final Product product, final Boolean isDigital, final String fileName, final int expiryDays, final int maxDownloadTimes) {
		final ProductSku productSku = prepareSku(skuCode, weight, shippable, startDate);

		Product updatedProduct = prepareProduct(brandCode, productName, endDate, product, productSku);

		productSkuDigitalSettings(isDigital, fileName, expiryDays, maxDownloadTimes, productSku);

		setAdditionalProductProperties(criteria, orderLimit, product, startDate, endDate);

		updatedProduct = productService.saveOrUpdate(product);

		setPriceOnProduct(catalog, productPrice, currency, product);

		return updatedProduct;
	}

	private Product prepareProduct(final String brandCode, final String productName,
			final Date endDate, final Product product, final ProductSku productSku) {
		if (endDate != null) {
			product.setEndDate(endDate);
		}

		final Product updatedProduct = initLocaleDependantFields(product, productName);
		updatedProduct.setDefaultSku(productSku);
		if (brandCode != null) {
			updatedProduct.setBrand(brandService.findByCode(brandCode));
		}
		return updatedProduct;
	}

	/**
	 * Creates Persisted Product With Sku.
	 *
	 * @param catalog the catalog to which the product belongs
	 * @param defaultCategory the default category of the product
	 * @param warehouse the warehouse in which the product is stored
	 * @param productPrice the price of the product
	 * @param productName the name of the product
	 * @param skuCode the sku code of the product
	 * @return the product
	 */
	public Product persistProductWithSkuAndInventory(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final String productName, final String skuCode) {
		final Product product = persistProductWithSku(catalog, defaultCategory, warehouse, productPrice,
				TestDataPersisterFactory.DEFAULT_CURRENCY, null, skuCode + "_product",
				productName, skuCode, TaxTestPersister.TAX_CODE_GOODS, AvailabilityCriteria.ALWAYS_AVAILABLE, 0);
		final ProductSku productSku = product.getProductSkus().values().iterator().next();
		persistInventory(productSku.getSkuCode(), warehouse, HUNDRED, ZERO, ZERO, INVENTORY_UPDATE_REASON);
		return product;
	}

	/**
	 * Create persisted skuOption with a skuOptionValue.
	 *
	 * @param catalog the catalog created sku option belongs to
	 * @param skuOptionKey sku option key
	 * @param the key corresponding to created sku option value
	 * @return persisted skuOption
	 */
	private SkuOption persistSkuOptionAndValue(final Catalog catalog, final String skuOptionKey, final String skuOptionValueKey) {
		final SkuOption skuOption = beanFactory.getBean(ContextIdNames.SKU_OPTION);
		skuOption.setOptionKey(skuOptionKey);
		skuOption.setDisplayName(Utils.uniqueCode("skuDisplayName"), Locale.ENGLISH);
		skuOption.setCatalog(catalog);

		final SkuOptionValue skuOptionValue = beanFactory.getBean(ContextIdNames.SKU_OPTION_VALUE);
		skuOptionValue.setOptionValueKey(skuOptionValueKey);
		skuOptionValue.setDisplayName(Locale.ENGLISH, Utils.uniqueCode("skuValueName"));
		skuOptionValue.setSkuOption(skuOption);

		skuOptionService.add(skuOption);
		skuOption.addOptionValue(skuOptionValue);

		return skuOptionService.saveOrUpdate(skuOption);
	}

	/**
	 * Create persisted multiple sku product with a set of given skus.
	 *
	 * @param catalog the catalog of the multiple sku product
	 * @param defaultCategory the default category of the product
	 * @param warehouse the warehouse in which the product is stored
	 * @param productPrice a price of product to be added as master catalog price
	 * @param currency the currency of the productPrice
	 * @param productCode the code of the product
	 * @param productName the name of the product
	 * @param taxCode the tax code used for the product
	 * @param criteria the availability criteria of the product
	 * @param orderLimit the limit on quantity of orders
	 * @param skus sku codes for multiple skus of the product
	 * @return persisted product with multiple skus
	 */
	public Product persistMultiSkuProduct(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode,
			final String productName, final String taxCode, final AvailabilityCriteria criteria, final int orderLimit, final String... skus) {
		return persistMultiSkuProduct(catalog, defaultCategory, warehouse, productPrice, currency, brandCode, productCode, productCode + "_productType",
				productName, taxCode, criteria, orderLimit, skus);
	}

	/**
	 * Create persisted multiple sku product with a set of given skus.
	 *
	 * @param catalog The catalog to associate the product with
	 * @param defaultCategory The default category for the product
	 * @param warehouse The warehouse for the product
	 * @param productPrice A price of product to be added as master catalog price
	 * @param currency The currency for the product price
	 * @param productCode The code of the product
	 * @param productTypeName The product type name for the product
	 * @param productName The name for the product
	 * @param taxCode The tax code for the product type
	 * @param criteria How available the product is on the storefront
	 * @param orderLimit The order limit for pre and backorders of the product
	 * @param skus Sku codes for multiple skus to associate with the product
	 * @return persisted product with multiple skus
	 */
	public Product persistMultiSkuProduct(final Catalog catalog, final Category defaultCategory, final Warehouse warehouse,
			final BigDecimal productPrice, final Currency currency, final String brandCode, final String productCode, final String productTypeName,
			final String productName, final String taxCode, final AvailabilityCriteria criteria, final int orderLimit, final String... skus) {

		Product product = createMultiSkuProduct(catalog, defaultCategory, brandCode, productCode, productTypeName, productName,
				taxCode, criteria, orderLimit, skus);

		product = productService.saveOrUpdate(product);

		if (productPrice != null && currency != null) {
			addOrUpdateProductBaseAmount(catalog, product, BigDecimal.valueOf(product.getMinOrderQty()),
					productPrice, productPrice, currency.getCurrencyCode());
		}

		return product;
	}



	public Product persistMultiSkuProduct(final Catalog catalog, final Category category, final Warehouse warehouse, final BigDecimal productPrice,
			final Currency currency, final String brandCode, final String productCode, final String productTypeName, final String productName,
			final String taxCode, final AvailabilityCriteria criteria, final int orderLimit, final String[] skus, final String[] skuOptions) {

		Product product = createMultiSkuProduct(catalog, category, brandCode, productCode, productTypeName, productName, taxCode,
				criteria, orderLimit, skus, skuOptions);

		product = productService.saveOrUpdate(product);

		addOrUpdateProductBaseAmount(catalog, product, BigDecimal.valueOf(product.getMinOrderQty()),
				productPrice, productPrice, currency.getCurrencyCode());

		return product;
	}

	public Product createMultiSkuProduct(final Catalog catalog, final Category defaultCategory, final String brandCode,
			final String productCode, final String productTypeName, final String productName, final String taxCode,
			final AvailabilityCriteria criteria, final int orderLimit, final String... skus) {
		return createMultiSkuProduct(catalog, defaultCategory, brandCode, productCode, productTypeName, productName, taxCode, criteria,
				orderLimit, skus, new String[] { Utils.uniqueCode("skuOptionKey1"), Utils.uniqueCode("skuOptionKey2") });
	}

	public Product createMultiSkuProduct(final Catalog catalog, final Category defaultCategory, final String brandCode,
			final String productCode, final String productTypeName, final String productName, final String taxCode,
			final AvailabilityCriteria criteria, final int orderLimit) {
		return createMultiSkuProduct(catalog, defaultCategory, brandCode, productCode, productTypeName, productName, taxCode, criteria,
				orderLimit, null, null);
	}

	/**
	 *
	 */
	public Product createMultiSkuProduct(final Catalog catalog, final Category defaultCategory, final String brandCode,
			final String productCode, final String productTypeName, final String productName, final String taxCode,
			final AvailabilityCriteria criteria, final int orderLimit, final String[] skus, final String[] skuOptions) {
		Product product = createSimpleProduct(productTypeName, true, productCode, catalog, getPersistedTaxCode(taxCode), defaultCategory);

		product = initLocaleDependantFields(product, productName);

		if (brandCode != null) {
			product.setBrand(brandService.findByCode(brandCode));
		}

		// Persist two sku options
		final List<SkuOption> skuOptionList = new ArrayList<SkuOption>();
		for (final String skuOptionKey : skuOptions) {
			// Using the same key for sku option value just to simplify population
			SkuOption skuOption = findOrCreateSkuOptionWithValue(defaultCategory.getCatalog(), product, skuOptionKey, skuOptionKey);
			skuOptionList.add(skuOption);
		}

		// Add the given skus to the product
		for (final String sku : skus) {
			final ProductSku productSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
			productSku.setSkuCode(sku);
			productSku.setStartDate(new Date());
			productSku.setGuid(sku);
			for (final SkuOption skuOption : skuOptionList) {
				productSku.setSkuOptionValue(skuOption, skuOption.getOptionKey());
			}

			product.addOrUpdateSku(productSku);
		}

		product.setAvailabilityCriteria(criteria);
		product.setPreOrBackOrderLimit(orderLimit);

		// Persist the product and return it
		return product;
	}

	public SkuOption findOrCreateSkuOptionWithValue(final Catalog catalog, final Product product, final String skuOptionKey, final String skuOptionValueKey) {
		SkuOption skuOption = skuOptionService.findByKey(skuOptionKey);
		if (skuOption == null) {
			skuOption = persistSkuOptionAndValue(catalog, skuOptionKey, skuOptionValueKey);
		} 
		product.getProductType().addOrUpdateSkuOption(skuOption);
		return skuOption;
	}

	public Product createMultiSkuProductWithWeightAndShippability(final Catalog catalog,
			final Category category, final Currency currency, final String productCode,
			final String productTypeName, final String productName,
			final AvailabilityCriteria alwaysAvailable, final int orderLimit, final String taxCode,
			final BigDecimal weight, final Boolean shippable, final String[] skusCommaSeparated) {

		Product product = createSimpleProduct(productTypeName, true, productCode, catalog, getPersistedTaxCode(taxCode), category);

		product = initLocaleDependantFields(product, productName);


		// Add the given skus to the product
		for (final String sku : skusCommaSeparated) {
			final ProductSku productSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
			productSku.setSkuCode(sku);
			productSku.setStartDate(new Date());

			productSku.setShippable(shippable);

			productSku.setWeight(weight);


			product.addOrUpdateSku(productSku);
		}

		product.setAvailabilityCriteria(alwaysAvailable);
		product.setPreOrBackOrderLimit(orderLimit);

		product = productService.saveOrUpdate(product);


		// Persist the product and return it
		return product;

	}

	/**
	 * Create in DB ProductType instance for Digital Cameras (Multi SKU).
	 *
	 * @param catalog the catalog to which the product belongs
	 * @return the product type
	 */
	public ProductType persistDefaultMultiSkuProductType(final Catalog catalog) {
		final ProductType productType = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setCatalog(catalog);
		productType.setGuid(Utils.uniqueCode("GUID"));
		productType.setName("MultiSkuProductType");
		productType.setTemplate("guidedSkuTemplate");
		productType.setWithMultipleSkus(true);
		final Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		final AttributeGroupAttribute attributeGroupAttribute = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
		attributeGroupAttribute.setAttribute(getProductDescriptionAttribute());
		productAttributeGroupAttributes.add(attributeGroupAttribute);
		productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		final TaxCodeService tcs = beanFactory.getBean(ContextIdNames.TAX_CODE_SERVICE);
		productType.setTaxCode(tcs.findByCode("GOODS"));
		final ProductTypeService productTypeService = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
		return productTypeService.add(productType);
	}

	/**
	 * Create in DB ProductType instance for telescopes.
	 *
	 * @param catalog the catalog to which the product belongs
	 * @return the product type
	 */
	public ProductType persistDefaultSingleSkuProductType(final Catalog catalog) {
		final ProductType productType = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setCatalog(catalog);
		productType.setGuid(Utils.uniqueCode("GUID"));
		productType.setName("SingleSkuProductType");
		productType.setTemplate("productTemplate");
		productType.setWithMultipleSkus(false);
		final Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		final AttributeGroupAttribute attributeGroupAttribute = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
		attributeGroupAttribute.setAttribute(getProductDescriptionAttribute());
		productAttributeGroupAttributes.add(attributeGroupAttribute);
		productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		final TaxCodeService tcs = beanFactory.getBean(ContextIdNames.TAX_CODE_SERVICE);
		productType.setTaxCode(tcs.findByCode("GOODS"));
		final ProductTypeService productTypeService = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_SERVICE);
		return productTypeService.add(productType);
	}

	private Attribute getProductDescriptionAttribute() {
		final String productDescriptionAttributeKey = "description";
		Attribute attribute = attributeService.findByKey(productDescriptionAttributeKey);

		if (attribute == null) {
			attribute = beanFactory.getBean(ContextIdNames.ATTRIBUTE);
			attribute.setAttributeType(AttributeType.LONG_TEXT);
			attribute.setName("Product Description");
			attribute.setKey(productDescriptionAttributeKey);
			attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
			attribute.setLocaleDependant(true);
			attribute.setGlobal(true);
			attribute = attributeService.add(attribute);
		}

		return attribute;
	}

	/**
	 * Add product SKU price.
	 *
	 * @param skuCode the sku code
	 * @param price the price
	 * @param currencyCode the currency code
	 */
	public void addProductSkuPrice(final String skuCode, final BigDecimal price, final String currencyCode) {
		addProductSkuPriceInternal(skuCode, 1, price, price, currencyCode);
	}

	/**
	 * Add product SKU tiered price.
	 *
	 * @param skuCode the sku code
	 * @param minQty the minimum quantity
	 * @param price the price
	 * @param salePrice the sale price
	 * @param currencyCode the currency code
	 */
	public void addProductSkuPriceInternal(final String skuCode, final int minQty, final BigDecimal price, final BigDecimal salePrice,
			final String currencyCode) {
		final ProductSku sku = productSkuService.findBySkuCode(skuCode);
		final Currency currency = Currency.getInstance(currencyCode);

		// TODO: this is just workaround to prevent problem with NPE see
		// MSC-7740
		// it should be removed later, when problem will be fixed in codebase.
//		Price skuPrice = getProductSkuPrice(sku.getProduct().getMasterCatalog(), sku, currency);
//		if (skuPrice == null) {
			addOrUpdateProductSkuBaseAmount(sku.getProduct().getMasterCatalog(), sku,
					BigDecimal.valueOf(minQty), price, salePrice, currency.getCurrencyCode());
//		}
	}

	/**
	 * Add SKU price with tiers.
	 *
	 * @param catalogName the name of the catalog
	 * @param skuCode the sku code
	 * @param currencyCode the currency code
	 * @param minQty the minimum quantity
	 * @param prices the prices
	 * @param salePrices the sale prices
	 */
	public void addSkuPriceWithTiers(final String catalogName, final String skuCode, final String currencyCode, final String[] minQty,
			final String[] prices, final String[] salePrices) {
		final Catalog catalog = catalogService.findByCode(catalogName);
		final ProductSku productSku = productSkuService.findBySkuCode(skuCode);
		addOrUpdateProductSkuBaseAmounts(catalog, productSku, currencyCode, minQty, prices, salePrices);

		productSkuService.saveOrUpdate(productSku);
	}

	/**
	 * Adds a product price.
	 *
	 * @param productCode the code of the product
	 * @param price the price of the product
	 * @param currencyCode the currencyCode for the price
	 */
	public void addProductPrice(final String productCode, final BigDecimal price, final String currencyCode) {
		addProductPriceInternal(productCode, 1, price, price, currencyCode);
	}

	/**
	 * Adds a Product tier price.
	 *
	 * @param productCode the code of the product
	 * @param minQty the minimum quantity
	 * @param price the price of the product
	 * @param salePrice the sale price
	 * @param currencyCode the currency code
	 */
	public void addProductPriceInternal(final String productCode, final int minQty, final BigDecimal price, final BigDecimal salePrice,
			final String currencyCode) {
		final Product product = productService.findByGuid(productCode, null);
		addOrUpdateProductBaseAmount(product.getMasterCatalog(), product, BigDecimal.valueOf(minQty), price, salePrice, currencyCode);
	}


	public void addOrUpdateProductBaseAmount(final Catalog catalog, final Product product, final BigDecimal minQty, final BigDecimal listPrice,
			final BigDecimal salePrice, final String currencyCode) {
		addOrUpdateBaseAmount(createPriceListIfNotExist(catalog, currencyCode), BaseAmountObjectType.PRODUCT.getName(), product.getCode(),
				minQty, listPrice, salePrice);
	}

	private String createPriceListIfNotExist(final Catalog catalog, final String currencyCode) {
		final String priceListDescriptorGuid = getPriceListGuidForCatalog(catalog, Currency.getInstance(currencyCode));
		PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(priceListDescriptorGuid);
		if (priceListDescriptor == null) {
			priceListDescriptor = createPriceListDescriptor(currencyCode, catalog);
		}

		if (plaService.listByCatalogAndCurrencyCode(catalog.getCode(), currencyCode, true).isEmpty()) {
			final PriceListAssignment pla = beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
			pla.setCatalog(catalog);
			pla.setPriceListDescriptor(priceListDescriptor);
			pla.setName(catalog.getName() + "_" + priceListDescriptor.getCurrencyCode());
			pla.setPriority(10);
			plaService.saveOrUpdate(pla);
		}

		return priceListDescriptorGuid;
	}

	/**
	 * Add a base amount for a product.
	 * @param guid base amount guid
	 * @param plGuid price list guid
	 * @param objectGuid product guid
	 * @param minQty minimum quantity
	 * @param listPrice the list price
	 * @param salePrice the sale price
	 */
	public void addProductBaseAmount(final String guid, final String plGuid, final String objectGuid, final BigDecimal minQty, final BigDecimal listPrice,
			final BigDecimal salePrice) {
		addOrUpdateBaseAmount(guid, plGuid, BaseAmountObjectType.PRODUCT.getName(), objectGuid, minQty, listPrice, salePrice);
	}

	/**
	 * Add or update a product sku base amount. Creates a price list for the catalog + currency combination if not present already.
	 * @param catalog catalog for the price list
	 * @param productSku the product sku
	 * @param minQty minimum quantity
	 * @param listPrice the list price
	 * @param salePrice the sale price
	 * @param currencyCode the currency for the price list
	 */
	public void addOrUpdateProductSkuBaseAmount(final Catalog catalog, final ProductSku productSku, final BigDecimal minQty, final BigDecimal listPrice,
			final BigDecimal salePrice, final String currencyCode) {
		addOrUpdateBaseAmount(createPriceListIfNotExist(catalog, currencyCode), BaseAmountObjectType.SKU.getName(), productSku.getSkuCode(),
				minQty, listPrice, salePrice);
	}

	private void addOrUpdateBaseAmount(final String priceListDescriptorGuid,
			final String objectType, final String objectGuid, final BigDecimal minQty,
			final BigDecimal listPrice, final BigDecimal salePrice) {
		final PriceListPersister priceListPersister = new PriceListPersister(beanFactory);
		priceListPersister.addOrUpdateBaseAmount(priceListDescriptorGuid, objectType, objectGuid, minQty, listPrice, salePrice);
	}

	private void addOrUpdateBaseAmount(final String guid, final String priceListDescriptorGuid,
			final String objectType, final String objectGuid, final BigDecimal minQty,
			final BigDecimal listPrice, final BigDecimal salePrice) {
		final PriceListPersister priceListPersister = new PriceListPersister(beanFactory);
		priceListPersister.addOrUpdateBaseAmount(guid, priceListDescriptorGuid, objectType, objectGuid, minQty, listPrice, salePrice);
	}

	/**
	 * Add a product price with tiers.
	 *
	 * @param catalogName the catalog name
	 * @param productCode the product code
	 * @param currencyCode the currency code
	 * @param minQty the minimum quantity
	 * @param prices the prices for the tiers
	 * @param salePrices the sale prices for the tiers
	 */
	public void addProductPriceWithTiers(final String catalogName, final String productCode, final String currencyCode, final String[] minQty,
			final String[] prices, final String[] salePrices) {
		final Catalog catalog = catalogService.findByCode(catalogName);
		final Product product = productService.findByGuid(productCode, null);

		// add to the price list service
		addOrUpdateProductBaseAmounts(catalog, product, currencyCode, minQty, prices, salePrices);
	}

	private void addOrUpdateProductBaseAmounts(final Catalog catalog, final Product product, final String currencyCode, final String[] minQty,
			final String[] prices, final String[] salePrices) {
		for (int i = 0; i < minQty.length; i++) {
			addOrUpdateProductBaseAmount(catalog, product, BigDecimal.valueOf(Double.valueOf(minQty[i])),
					BigDecimal.valueOf(Double.valueOf(prices[i])), BigDecimal.valueOf(Double.valueOf(salePrices[i])), currencyCode);
		}
	}

	private void addOrUpdateProductSkuBaseAmounts(final Catalog catalog, final ProductSku productSku, final String currencyCode, final String[] minQty,
			final String[] prices, final String[] salePrices) {
		for (int i = 0; i < minQty.length; i++) {
			addOrUpdateProductSkuBaseAmount(catalog, productSku, BigDecimal.valueOf(Double.valueOf(minQty[i])),
					BigDecimal.valueOf(Double.valueOf(prices[i])), BigDecimal.valueOf(Double.valueOf(salePrices[i])), currencyCode);
		}
	}

//	/*
//	 * This method is used to create Product and ProductSkuPrice and fill it with ProductPriceTiers and SkuPriceTiers respectively.
//	 */
//	private Price createPriceAndSetPriceTiers(final String priceType, final String tierType, final String currencyCode, final String[] minQty,
//			final String[] prices, final String[] salePrices) {
//		Price productPrice = (Price) beanFactory.getBean(priceType);
//		Map<Integer, PriceTier> priceTiers = productPrice.getPersistentPriceTiers();
//
//		productPrice.setCurrency(Currency.getInstance(currencyCode));
//		for (int i = 0; i < minQty.length; i++) {
//			PriceTier priceTier = (PriceTier) beanFactory.getBean(tierType);
//			priceTier.setMinQty(Integer.parseInt(minQty[i]));
//			priceTier.setListPrice(new BigDecimal(prices[i]));
//			priceTier.setSalePrice(new BigDecimal(salePrices[i]));
//			priceTiers.put(priceTier.getMinQty(), priceTier);
//		}
//		return productPrice;
//	}

	/**
	 * Updates the default category attributes.
	 *
	 * @param category the category whose attributes will be updated
	 * @return the updated category
	 */
	public Category updateDefaultCategoryAttributes(final Category category) {
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		int order = 0;
		for (final Attribute attribute : attributeService.getCategoryAttributes()) {
			final AttributeGroupAttribute groupAttribute = beanFactory.getBean(ContextIdNames.CATEGORY_TYPE_ATTRIBUTE);
			groupAttribute.setAttribute(attribute);
			groupAttribute.setOrdering(order++);
			attributeGroupAttributes.add(groupAttribute);
		}
		final AttributeGroup attributeGroup = beanFactory.getBean(ContextIdNames.ATTRIBUTE_GROUP);
		attributeGroup.setAttributeGroupAttributes(attributeGroupAttributes);

		category.getCategoryType().setAttributeGroup(attributeGroup);

		return categoryService.update(category);
	}

	/**
	 * Find persisted taxCode with "GOODS" code.
	 *
	 * @return taxCode existed in database
	 */
	public TaxCode getPersistedTaxCode(final String taxCode) {
		return taxTestPersister.getTaxCode(taxCode);
	}

	/**
	 * Create persisted product.
	 *
	 * @param code the product code
	 * @param productTypeName the product type name
	 * @param catalog the catalog to which the product belongs
	 * @param category the category of product
	 * @param taxCode the tax code of the product
	 * @return persisted product
	 */
	public Product persistSimpleProduct(final String code, final String productTypeName, final Catalog catalog, final Category category,
			final TaxCode taxCode) {
		final Product product = createSimpleProduct(productTypeName, code, catalog, taxCode, category);
		return productService.saveOrUpdate(product);
	}

	/**
	 * Internal method to handle polymorphic instances of Product.
	 *
	 * @param productTypeName the product type name
	 * @param code the product code
	 * @param catalog product to be placed in
	 * @param taxCode the tax code
	 * @param product instance of product to populate
	 * @return created product instance
	 */
	private Product populateSimpleProduct(final String productTypeName, final boolean isMultiSku, final String code, final Catalog catalog,
			final TaxCode taxCode, final Product product, final Category defaultCategory) {
		product.setLastModifiedDate(new Date());
		product.setStartDate(new Date());
		product.setCode(code);
		product.setTaxCode(taxCode);

		// add default category for the product
		if (defaultCategory == null) {
			throw new IllegalArgumentException("missing category for the product");
		} else {
			addCategoriesForProduct(defaultCategory, product);
		}

		final LocaleDependantFields fields = product.getLocaleDependantFieldsWithoutFallBack(Locale.ENGLISH);
		product.addOrUpdateLocaleDependantFields(fields);

		if (productTypeName != null) {
			ProductType productType = productTypeService.findProductType(productTypeName);

			if (productType == null) {
				/** create testing product type */
				productType = createSimpleProductType(productTypeName, isMultiSku, catalog, taxCode);
				productTypeService.add(productType);
			}

			product.setProductType(productType);
		}

		return product;
	}

	/**
	 * Create simple product but doesn't save it into database.
	 *
	 * @param productTypeName the product type name
	 * @param code the product code
	 * @param catalog product to be placed in
	 * @param taxCode the tax code
	 * @param defaultCategory - the default category of the product
	 * @return created product instance
	 */
	public Product createSimpleProduct(final String productTypeName, final String code, final Catalog catalog, final TaxCode taxCode, 
			final Category defaultCategory) {
		final Product product = beanFactory.getBean(ContextIdNames.PRODUCT);
		return populateSimpleProduct(productTypeName, false, code, catalog, taxCode, product, defaultCategory);
	}
	
	/**
	 * Create simple product but doesn't save it into database.
	 *
	 * @param productTypeName the product type name
	 * @param isMultiSku the multi sku sign
	 * @param code the product code
	 * @param catalog product to be placed in
	 * @param taxCode the tax code
	 * @param defaultCategory - the default category
	 * @return created product instance
	 */
	public Product createSimpleProduct(final String productTypeName, final boolean isMultiSku, final String code, final Catalog catalog,
			final TaxCode taxCode, final Category defaultCategory) {
		final Product product = beanFactory.getBean(ContextIdNames.PRODUCT);
		return populateSimpleProduct(productTypeName, isMultiSku, code, catalog, taxCode, product, defaultCategory);
	}

   /**
     * Create simple product bundle but doesn't save it into database. Product bundle is an extension of Product, and will need extra fields
     * populated.
     *
     * @param productTypeName the product type name
     * @param code the product code
     * @param catalog product to be placed in
     * @param defaultCategory the category
     * @param taxCode the tax code
     * @return created product instance
     */
    public ProductBundle createSimpleProductBundle(final String productTypeName, final String code, final Catalog catalog, 
    																				final Category defaultCategory, final TaxCode taxCode) {
        ProductBundle bundle = beanFactory.getBean(ContextIdNames.PRODUCT_BUNDLE);
        bundle = (ProductBundle) populateSimpleProduct(productTypeName, false, code, catalog, taxCode, bundle, defaultCategory);
        return (ProductBundle) productService.saveOrUpdate(bundle);
    }

	/**
	 * Creates simple bundle constituent but doesn't save it into database.
	 *
	 * @param product the product
	 * @param quantity the quantity
	 * @return bundle constituent
	 */
	public BundleConstituent createSimpleBundleConstituent(final Product product, final int quantity) {
		return constituentFactory.createBundleConstituent(product, quantity);
	}

	/**
	 * Creates simple bundle constituent but doesn't save it into database.
	 *
	 * @param productSku the productSku
	 * @param quantity the quantity
	 * @return bundle constituent
	 */
	public BundleConstituent createSimpleBundleConstituent(final ProductSku productSku, final int quantity) {
		return constituentFactory.createBundleConstituent(productSku, quantity);
	}

	/**
	 * Create product type without saving it into database.
	 *
	 * @param productTypeName
	 * @param isMultiSku
	 * @param catalog
	 * @param taxCode
	 * @return productType instance
	 */
	private ProductType createSimpleProductType(final String productTypeName, final boolean isMultiSku, final Catalog catalog, final TaxCode taxCode) {
		final ProductType productType = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE);
		productType.setName(productTypeName);
		productType.setCatalog(catalog);
		productType.setTemplate("template1");
		productType.setTaxCode(taxCode);
		productType.setWithMultipleSkus(isMultiSku);
		return productType;
	}

	/**
	 * Create persisted product sku and add this sku to the given product as default.
	 *
	 * @param skuCode the sku code
	 * @param listPrice the list price of the product
	 * @param currencyCode the currency code of the price
	 * @param shippable whether the product is shippable or not
	 * @param product the product
	 * @param warehouse the warehouse where the product is stored
	 * @return persisted productSku
	 */
	public ProductSku persistSimpleProductSku(final String skuCode, final double listPrice, final String currencyCode, final boolean shippable,
			final Product product, final Warehouse warehouse) {
		return persistSimpleProductSku(skuCode, listPrice, currencyCode, shippable, product, warehouse, true);
	}

	public ProductSku persistSimpleProductSku(final String skuCode, final double listPrice, final String currencyCode, final boolean shippable,
			final Product product, final Warehouse warehouse, final boolean needPrice) {
		final ProductSku productSku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		productSku.setSkuCode(skuCode);
		productSku.setGuid(skuCode);
		productSku.setStartDate(new Date());
		productSku.setShippable(shippable);
		product.setDefaultSku(productSku);

		final Product persistedProduct = productService.saveOrUpdate(product);

		if (needPrice) {
			addOrUpdateProductBaseAmount(persistedProduct.getMasterCatalog(), persistedProduct, BigDecimal.ONE, BigDecimal.valueOf(listPrice),
					BigDecimal.valueOf(listPrice), currencyCode);
		}

		return persistedProduct.getDefaultSku();
	}

	/**
	 * Create a persisted category type in the given catalog.
	 *
	 * @param categoryTypeName the category type name
	 * @param templateName the template name
	 * @param catalog the catalog to create the category type in
	 * @return the category type
	 */
	public CategoryType persistCategoryType(final String categoryTypeName, final String templateName, final Catalog catalog) {
		final CategoryType categoryType = beanFactory.getBean(ContextIdNames.CATEGORY_TYPE);
		categoryType.setCatalog(catalog);
		categoryType.setName(categoryTypeName);
		categoryType.setGuid(categoryTypeName);
		categoryType.setTemplate(templateName);
		return categoryTypeService.add(categoryType);
	}

	/**
	 * Create a persisted category in the given catalog with the given categoryType.
	 *
	 * @param categoryCode the category code
	 * @param catalog the catalog in which to create the category
	 * @param categoryType the category type to use
	 * @param categoryName the category dispaly name will not be set if null was specified
	 * @param categoryLocale display name's locale
	 * @return the category
	 */
	public Category persistCategory(final String categoryCode, final Catalog catalog, final CategoryType categoryType, final String categoryName,
			final String categoryLocale) {
		final Category category = beanFactory.getBean(ContextIdNames.CATEGORY);
		category.setCode(categoryCode);
		category.setCatalog(catalog);
		category.setGuid(categoryCode);
		category.setCategoryType(categoryType);
		if (categoryName != null) {
			category.setDisplayName(categoryName, new Locale(categoryLocale));
		}
		return categoryService.add(category);
	}

	/**
	 * Creates category with parent category.
	 */
	public Category persistCategory(final String categoryCode, final Catalog catalog, final CategoryType categoryType, final Category parentCategory) {
		final Category category = beanFactory.getBean(ContextIdNames.CATEGORY);
		category.setCode(categoryCode);
		category.setCatalog(catalog);
		category.setCategoryType(categoryType);
		category.setParent(parentCategory);
		final Category updatedCategory = categoryService.add(category);
		categoryService.update(updatedCategory.getParent());
		return updatedCategory;
	}

	/**
	 * Create linked category in the given virtual catalog.
	 *
	 * @param virtualCatalog the virtual catalog in which to have a linked category
	 * @param masterCategory created linked category is derived from
	 * @return not persistent linked category
	 */
	public Category createLinkedCategory(final Catalog virtualCatalog, final Category masterCategory) {
		final Category linkedCategory = beanFactory.getBean(ContextIdNames.LINKED_CATEGORY);
		linkedCategory.setCatalog(virtualCatalog);
		linkedCategory.setMasterCategory(masterCategory);
		linkedCategory.setIncluded(true);
		return linkedCategory;
	}

	/**
	 * Add the given product to the given category and persist.
	 *
	 * @param product the product to put in the category
	 * @param category the category the product is to belong to
	 * @return the updated product
	 */
	public Product addProductToCategoryAndUpdate(final Product product, final Category category) {
		product.addCategory(category);
		return productService.saveOrUpdate(product);
	}

	/**
	 * Creates product association.
	 *
	 * @param associationCatalog catalog of the association
	 * @param sourceProduct source product
	 * @param targetProduct target product
	 * @param associationType type of association
	 * @return the product association
	 */
	public ProductAssociation persistProductAssociation(final Catalog associationCatalog, final Product sourceProduct, final Product targetProduct,
			final int associationType) {
		return persistProductAssociation(associationCatalog, sourceProduct, targetProduct, associationType, 1);
	}

	/**
	 * Creates product association.
	 *
	 * @param associationCatalog catalog of the association
	 * @param sourceProduct source product
	 * @param targetProduct target product
	 * @param associationType type of association
	 * @param defaultQuantity default quantity
	 * @return the product association
	 */
	public ProductAssociation persistProductAssociation(final Catalog associationCatalog, final Product sourceProduct, final Product targetProduct,
			final int associationType, final int defaultQuantity) {
		return persistProductAssociation(associationCatalog, sourceProduct, targetProduct, associationType, defaultQuantity, false);
	}

	/**
	 * Creates product association.
	 *
	 * @param associationCatalog catalog of the association
	 * @param sourceProduct source product
	 * @param targetProduct target product
	 * @param associationType type of association
	 * @param defaultQuantity default quantity
	 * @param sourceProductDependent whether is source product dependent
	 * @return the product association
	 */
	public ProductAssociation persistProductAssociation(final Catalog associationCatalog, final Product sourceProduct, final Product targetProduct,
			final int associationType, final int defaultQuantity, final boolean sourceProductDependent) {
		final ProductAssociation productAssociation = beanFactory.getBean(ContextIdNames.PRODUCT_ASSOCIATION);
		productAssociation.setCatalog(associationCatalog);
		productAssociation.setSourceProduct(sourceProduct);
		productAssociation.setTargetProduct(targetProduct);
		productAssociation.setStartDate(new Date());
		productAssociation.setAssociationType(associationType);
		productAssociation.setDefaultQuantity(defaultQuantity);
		productAssociation.setSourceProductDependent(sourceProductDependent);
		productAssociation.setGuid(new RandomGuidImpl().toString());
		return productAssociationService.add(productAssociation);
	}

	/**
	 * Persists the product brand.
	 *
	 * @param catalog the catalog to which the brand code will belong
	 * @param code the brand code
	 * @return the brand
	 */
	public Brand persistProductBrand(final Catalog catalog, final String code) {
		final Brand brand = beanFactory.getBean(ContextIdNames.BRAND);
		final BrandService brandService = beanFactory.getBean(ContextIdNames.BRAND_SERVICE);
		brand.setCatalog(catalog);
		brand.setCode(code);
		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, code);
		return brandService.add(brand);
	}

	/**
	 * Persists product brand with the given name.
	 *
	 * @param catalog catalog this brand belongs to
	 * @param code brand code
	 * @param name brand name
	 * @param locale brand name locale
	 * @return persisted brand
	 */
	public Brand persistNamedBrand(final Catalog catalog, final String code, final String name, final Locale locale) {
		final Brand brand = beanFactory.getBean(ContextIdNames.BRAND);
		final BrandService brandService = beanFactory.getBean(ContextIdNames.BRAND_SERVICE);
		brand.setCatalog(catalog);
		brand.setCode(code);
		brand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, locale, name);
		return brandService.add(brand);
	}

	/**
	 * Persists brand.
	 *
	 * @param catalog the catalog to which the brand will be persisted
	 * @return the brand
	 */
	public Brand persistDefaultBrand(final Catalog catalog) {
		final Brand brand = new BrandImpl();
		brand.setCatalog(catalog);
		brand.setCode("SUKOFF19");
		return brandService.saveOrUpdate(brand);
	}

	/**
	 * Adjusts the quantity on hand.
	 *
	 * @param skuCode the sku code
	 * @param warehouse the warehouse in which the product is stored
	 * @param qty the quantity to be adjusted to
	 * @return the inventory on hand after adjusting
	 */
	public InventoryDto adjustQuantityOnHand(final String skuCode, final Warehouse warehouse, final int qty) {
		final ProductSku productSku = productSkuService.findBySkuCode(skuCode);
		productInventoryManagementService.processInventoryUpdate(productSku, warehouse.getUidPk(), InventoryEventType.STOCK_ADJUSTMENT,
				"dbtest", qty, null, "stock adjustment");
		return productInventoryManagementService.getInventory(productSku.getSkuCode(), warehouse.getUidPk());
	}

	/**
	 * Persists the inventory.
	 *
	 * @param skuCode the sku code of the product
	 * @param warehouse the warehouse in which the product is stored
	 * @param qtyOnHand the quantity on hand
	 * @param reservedQty the quantity that is reserved
	 * @param allocatedQty the quantity that is allocated
	 */
	public void persistInventory(final String skuCode, final Warehouse warehouse, final int qtyOnHand, final int reservedQty,
			final int allocatedQty, final String reason) {
		InventoryDto inventoryDto = beanFactory.getBean(ContextIdNames.INVENTORYDTO);
		inventoryDto.setReservedQuantity(reservedQty);
		inventoryDto.setSkuCode(skuCode);
		inventoryDto.setWarehouseUid(warehouse.getUidPk());

		executeInventoryCommand(inventoryFacade.getInventoryCommandFactory()
				.getCreateOrUpdateInventoryCommand(inventoryDto));
		executeInventoryCommand(inventoryFacade.getInventoryCommandFactory()
				.getAdjustInventoryCommand(inventoryDto.getInventoryKey(), qtyOnHand));
		executeInventoryCommand(inventoryFacade.getInventoryCommandFactory()
				.getAllocateInventoryCommand(inventoryDto.getInventoryKey(), allocatedQty));
	}

	private void executeInventoryCommand(final InventoryCommand inventoryCommand) {
		inventoryFacade.executeInventoryCommand(inventoryCommand);
	}

	/**
	 * Persists the inventory.
	 *
	 * @param skuCode the sku code of the product
	 * @param warehouse the warehouse in which the product is stored
	 * @param qtyOnHand the quantity on hand
	 * @param reservedQty the quantity that is reserved
	 * @param allocatedQty the quantity that is allocated
	 * @param reorderMinimum the minimum quantity needed to reorder
	 * @param reorderQty the reorder quantity
	 * @return the inventory
	 */
	public InventoryDto persistInventory(final String skuCode, final Warehouse warehouse, final int qtyOnHand, final int reservedQty,
			final int allocatedQty, final int reorderMinimum, final int reorderQty) {
		final InventoryDtoImpl inventoryDto = beanFactory.getBean(ContextIdNames.INVENTORYDTO);
		inventoryDto.setQuantityOnHand(qtyOnHand);
		inventoryDto.setAllocatedQuantity(allocatedQty);
		inventoryDto.setReservedQuantity(reservedQty);
		inventoryDto.setReorderMinimum(reorderMinimum);
		inventoryDto.setReorderQuantity(reorderQty);
		inventoryDto.setSkuCode(skuCode);
		inventoryDto.setWarehouseUid(warehouse.getUidPk());
		executeInventoryCommand(inventoryFacade.getInventoryCommandFactory().getCreateOrUpdateInventoryCommand(inventoryDto));
		return inventoryFacade.getInventory(inventoryDto.getInventoryKey());
	}

	/**
	 * Creates global and catalog attributes.
	 *
	 * @param catalogCode catalog code
	 * @param attributeKey attribute key
	 * @param attributeName attribute name
	 * @param attributeUsage attribute usage
	 * @param attributeType attribute type
	 * @param multiLanguage indicates whether this attribute is locale dependant
	 * @param requiredAttribute indicates whether this attribute is required
	 * @param multipleValuesAllowed indicates whether multiple values are allowed for this attribute
	 * @return persisted <code>Attribute</code>
	 */
	public Attribute persistAttribute(final String catalogCode, final String attributeKey, final String attributeName, final String attributeUsage,
			final String attributeType, final boolean multiLanguage, final boolean requiredAttribute, final boolean multipleValuesAllowed) {
		Attribute attribute = beanFactory.getBean(ContextIdNames.ATTRIBUTE);

		if (catalogCode != null) {
			final Catalog catalog = catalogService.findByCode(catalogCode);
			attribute.setCatalog(catalog);
			attribute.setGlobal(false);
		} else {
			attribute.setGlobal(true);
		}

		attribute.setKey(attributeKey);
		attribute.setName(attributeName);

		final Object[] usageKeys = attributeService.getAttributeUsageMap().keySet().toArray();
		final Object[] typeKeys = attributeService.getAttributeTypeMap().keySet().toArray();

		final Object[] usageValues = attributeService.getAttributeUsageMap().values().toArray();
		final Object[] typeValues = attributeService.getAttributeTypeMap().values().toArray();

		int usageIndex = -1;
		for (int i = 0; i < usageValues.length; i++) {
			if (usageValues[i].equals(attributeUsage)) {
				usageIndex = i;
				break;
			}
		}
		attribute.setAttributeUsage(((AttributeUsageImpl)beanFactory.getBean(ContextIdNames.ATTRIBUTE_USAGE)).
				getAttributeUsageById(Integer.parseInt((String) usageKeys[usageIndex])));

		int typeIndex = -1;
		for (int i = 0; i < typeValues.length; i++) {
			if (typeValues[i].equals(attributeType)) {
				typeIndex = i;
				break;
			}
		}
		attribute.setAttributeType(AttributeType.valueOf(Integer.parseInt((String) typeKeys[typeIndex])));

		attribute.setLocaleDependant(multiLanguage);
		attribute.setRequired(requiredAttribute);
		attribute.setMultiValueEnabled(multipleValuesAllowed);
		attribute = attributeService.add(attribute);
		return attribute;
	}

	/**
	 * Action method <code>assignProductTypeAttributes</code>.
	 *
	 * @param attributeKeys the attribute keys
	 * @param productTypeName the product type name
	 * @return the product type
	 */
	public ProductType assignProductAttributesToProductType(final String[] attributeKeys, final String productTypeName) {
		final ProductType productType = productTypeService.findProductType(productTypeName);
		final AttributeGroup attributeGroup = productType.getProductAttributeGroup();
		final List<Attribute> selectedAttributes = retrieveSelectedProductTypeAttributes(attributeGroup);

		for (final String attributeKey : attributeKeys) {
			selectedAttributes.add(attributeService.findByKey(attributeKey));
		}

		productType.setProductAttributeGroupAttributes(getGroupAttributeFromList(selectedAttributes, ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE));
		return productTypeService.update(productType);
	}

	/**
	 * Action method <code>assignProductTypeSkuAttributes</code>.
	 *
	 * @param skuAttributeKeys the sku Attribute keys
	 * @param productTypeName the product type name
	 * @return the product type
	 */
	public ProductType assignSkuAttributesToProductType(final String[] attributeKeys, final String productTypeName) {
		final ProductType productType = productTypeService.findProductType(productTypeName);
		final AttributeGroup attributeGroup = productType.getSkuAttributeGroup();
		final List<Attribute> selectedAttributes = retrieveSelectedProductTypeAttributes(attributeGroup);

		for (final String attributeKey : attributeKeys) {
			selectedAttributes.add(attributeService.findByKey(attributeKey));
		}

		final AttributeGroup skuAttributeGroup = beanFactory.getBean(ContextIdNames.ATTRIBUTE_GROUP);
		skuAttributeGroup.setAttributeGroupAttributes(getGroupAttributeFromList(selectedAttributes, ContextIdNames.PRODUCT_TYPE_SKU_ATTRIBUTE));
		productType.setSkuAttributeGroup(skuAttributeGroup);

		return productTypeService.update(productType);
	}

	/**
	 * Action method <code>assignProductTypeSkuOptions</code>.
	 *
	 * @param skuOptionKeys the skuOption keys
	 * @param productTypeName the product type name
	 * @return the product type
	 */
	public ProductType assignSkuOptionsToProductType(final String[] skuOptionKeys, final String productTypeName) {
		final ProductType productType = productTypeService.findProductType(productTypeName);

		for (final String skuOptionKey : skuOptionKeys) {
			productType.addOrUpdateSkuOption(skuOptionService.findByKey(skuOptionKey));
		}

		return productTypeService.update(productType);
	}

	/**
	 * Action method <code>assignCategoryTypeAttributes</code>.
	 *
	 * @param attributeKeys the attribute keys
	 * @param categoryTypeName category type name
	 * @return the category type
	 */
	public CategoryType assignAttributesToCategoryType(final String[] attributeKeys, final String categoryTypeName) {
		final CategoryType categoryType = categoryTypeService.findCategoryType(categoryTypeName);
		final List<Attribute> selectedAttributes = retrieveSelectedCategoryTypeAttributes(categoryType);

		for (final String attributeKey : attributeKeys) {
			selectedAttributes.add(attributeService.findByKey(attributeKey));
		}

		final AttributeGroup attributes = categoryType.getAttributeGroup();
		// getAttributeGroupAttributes() returns the OpenJPA generated proxy on the collection
		attributes.getAttributeGroupAttributes().addAll(getGroupAttributeFromList(selectedAttributes, ContextIdNames.CATEGORY_TYPE_ATTRIBUTE));
		return categoryTypeService.update(categoryType);
	}

	/**
	 * Action method <code>assignAttributesSku</code>.
	 *
	 * @param attributeKeys the attribute keys
	 * @param sku the sku code
	 * @return the product sku
	 */
	public ProductSku assignAttributesToSku(final String[] attributeKeys, final String sku) {
		final ProductSku prodSku = productSkuService.findBySkuCode(sku);
		final Map<String, AttributeValue> selectedAttributes = new HashMap<String, AttributeValue>();

		for (final String attributeKey : attributeKeys) {
			final Attribute returnedAttribute = attributeService.findByKey(attributeKey);
			final AttributeValue atvalue = new SkuAttributeValueImpl();
			atvalue.setAttribute(returnedAttribute);
			atvalue.setLocalizedAttributeKey(attributeKey);
			atvalue.setAttributeType(returnedAttribute.getAttributeType());
			selectedAttributes.put(attributeKey, atvalue);
		}

		selectedAttributes.putAll(prodSku.getAttributeValueMap());
		prodSku.setAttributeValueMap(selectedAttributes);
		return productSkuService.saveOrUpdate(prodSku);
	}

	/**
	 * Action method <code>addAttributeValue</code>.
	 *
	 * @param product the product
	 * @param attributeKey the attribute key
	 * @param attributeValue the attribute value
	 * @return the product
	 */
	public Product addAttributeValue(final Product product, final String attributeKey, final String attributeValue) {
		final Locale productDefaultLocale = product.getMasterCatalog().getDefaultLocale();
		return addAttributeValue(product, attributeKey, attributeValue, productDefaultLocale);
	}


	/**
	 * Action method <code>addAttributeValue</code>.
	 *
	 * @param product the product
	 * @param attributeKey the attribute key
	 * @param attributeValue the attribute value
	 * @param locale the locale
	 * @return product the product
	 */
	public Product addAttributeValue(final Product product, final String attributeKey, final String attributeValue, final Locale locale) {
		final List<AttributeValue> list = product.getFullAttributeValues(locale);
		for (final AttributeValue av : list) {
			if (av.getAttribute().getKey().equals(attributeKey)) {
				setAttributeValue(av, attributeValue);
				product.getAttributeValueMap().put(av.getLocalizedAttributeKey(), av);
				break;
			}
		}

		return productService.saveOrUpdate(product);
	}

	/**
	 * Sets the value to SKU attribute with the given key.
	 *
	 * @param productSkuCode code of product SKU
	 * @param skuAttributeKey attribute key to set value to
	 * @param skuAttributeValue attribute value to set
	 */
	public void setSkuAttributeValue(final String productSkuCode, final String skuAttributeKey, final String skuAttributeValue) {
		final ProductSku productSku = productSkuService.findBySkuCode(productSkuCode);
		final Set<String> attributeKeys = productSku.getAttributeValueMap().keySet();
		for (final String attributeKey : attributeKeys) {
			if (attributeKey.equals(skuAttributeKey)) {
				productSku.getAttributeValueMap().get(attributeKey).setStringValue(skuAttributeValue);
				break;
			}
		}
		productSkuService.saveOrUpdate(productSku);
	}

	private void setAttributeValue(final AttributeValue attributeValue, final String stringValue) {
		attributeValue.setStringValue(stringValue);
	}

	/**
	 * Retrieves the selected product type attributes.
	 *
	 * @param productType the product type
	 * @return a list of attributes associated with the product type
	 */
	private List<Attribute> retrieveSelectedProductTypeAttributes(final AttributeGroup attributeGroup) {
		final List<AttributeGroupAttribute> groupAttList = new ArrayList<AttributeGroupAttribute>();

		for (final AttributeGroupAttribute groupAttr : attributeGroup.getAttributeGroupAttributes()) {
			groupAttList.add(groupAttr);
		}
		Collections.sort(groupAttList, new AttributeComparator());

		final List<Attribute> selectedAttributes = new ArrayList<Attribute>();

		for (final AttributeGroupAttribute groupAttr : groupAttList) {
			selectedAttributes.add(groupAttr.getAttribute());
		}
		return selectedAttributes;
	}

	/**
	 * Retrieves the selected category type attributes.
	 *
	 * @param categoryType the category type
	 * @return a list of attributes associated with the product type.
	 */
	private List<Attribute> retrieveSelectedCategoryTypeAttributes(final CategoryType categoryType) {
		final AttributeGroup attributeGroup = categoryType.getAttributeGroup();
		final List<AttributeGroupAttribute> groupAttList = new ArrayList<AttributeGroupAttribute>();

		for (final AttributeGroupAttribute groupAttr : attributeGroup.getAttributeGroupAttributes()) {
			groupAttList.add(groupAttr);
		}
		Collections.sort(groupAttList, new AttributeComparator());

		final List<Attribute> selectedAttributes = new ArrayList<Attribute>();

		for (final AttributeGroupAttribute groupAttr : groupAttList) {
			selectedAttributes.add(groupAttr.getAttribute());
		}
		return selectedAttributes;
	}

	// private List<Attribute> retrieveSelectedSkuAttribues(final ProductSku
	// productSku) {
	// List<Attribute> attributes = new ArrayList<Attribute>();
	// Collection<AttributeValue> values =
	// productSku.getAttributeValueGroup().getAttributeValueMap().values();
	//
	// for(final Iterator<AttributeValue> itr = values.iterator();
	// itr.hasNext();) {
	// attributes.add(itr.next().getAttribute());
	// }
	//
	// return attributes;
	// }

	/**
	 * Gets the group attributes from a list of attributes.
	 *
	 * @param attributes the list of attributes
	 * @param beanName the name of the bean
	 * @return a set of AttributeGroupAttribute
	 */
	private Set<AttributeGroupAttribute> getGroupAttributeFromList(final List<Attribute> attributes, final String beanName) {
		int order = 0;
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();

		for (final Attribute attribute : attributes) {
			final AttributeGroupAttribute groupAttribute = beanFactory.getBean(beanName);
			groupAttribute.setAttribute(attribute);
			groupAttribute.setOrdering(order++);
			attributeGroupAttributes.add(groupAttribute);
		}
		return attributeGroupAttributes;
	}

	/**
	 * Persist sku option.
	 *
	 * @param optionKey the option key
	 * @param catalogCode the catalog code
	 * @param displayName the display name
	 * @return the sku option
	 */
	public SkuOption persistSkuOption(final String optionKey, final String catalogCode, final String displayName) {
		final Catalog catalog = catalogService.findByCode(catalogCode);
		final SkuOption skuOption = beanFactory.getBean(ContextIdNames.SKU_OPTION);
		skuOption.initialize();
		skuOption.setOptionKey(optionKey);
		skuOption.setCatalog(catalog);
		skuOption.setDisplayName(displayName, Locale.ENGLISH);
		return skuOptionService.add(skuOption);
	}

	/**
	 * Persist sku option value.
	 *
	 * @param skuOptionCode the sku option code
	 * @param skuOptionValueKey the sku option value key
	 * @param displayName the display name
	 * @return the sku option value
	 */
	public SkuOptionValue persistSkuOptionValue(final String skuOptionCode, final String skuOptionValueKey, final String displayName) {
		final SkuOption skuOption = skuOptionService.findByKey(skuOptionCode);
		return persistSkuOptionValue(skuOption, skuOptionValueKey, displayName);
	}
	
	/**
	 * Persist sku option value.
	 *
	 * @param skuOption the sku option
	 * @param skuOptionValueKey the sku option value key
	 * @param displayName the display name
	 * @return the sku option value
	 */
	public SkuOptionValue persistSkuOptionValue(final SkuOption skuOption, final String skuOptionValueKey, final String displayName) {
		final SkuOptionValue skuOptionValue = beanFactory.getBean(ContextIdNames.SKU_OPTION_VALUE);
		skuOptionValue.initialize();
		skuOptionValue.setOptionValueKey(skuOptionValueKey);
		skuOptionValue.setDisplayName(Locale.ENGLISH, displayName);
		skuOption.addOptionValue(skuOptionValue);
		skuOptionService.addOptionValue(skuOptionValue, skuOption);
		return skuOptionValue;
	}

	/**
	 * Adds the sku to product.
	 *
	 * @param product the product
	 * @param skuCode the sku code
	 * @param skuOptionValues the sku option values
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return the product
	 */
	public Product addSkuToProduct(final Product product, final String skuCode, final List<SkuOptionValue> skuOptionValues, final Date startDate, final Date endDate) {
		final ProductSku sku = createProductSku(skuCode, skuOptionValues, startDate, endDate, true);
		return addSkuToProduct(product, sku);
	}

	/**
	 * Adds the sku to product.
	 *
	 * @param product the product
	 * @param sku the sku
	 * @return the product
	 */
	public Product addSkuToProduct(final Product product, final ProductSku sku) {
		product.addOrUpdateSku(sku);
		return productService.saveOrUpdate(product);
	}

	/**
	 * Creates the product sku.
	 *
	 * @param skuCode the sku code
	 * @param skuOptionValues the sku option values
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param shippable the shippable
	 * @return the product sku
	 */
	public ProductSku createProductSku(final String skuCode, final List<SkuOptionValue> skuOptionValues, final Date startDate, final Date endDate, final boolean shippable) {
		final ProductSku sku = beanFactory.getBean(ContextIdNames.PRODUCT_SKU);
		sku.setSkuCode(skuCode);
		for (final SkuOptionValue skuOptionValue : skuOptionValues) {
			sku.setSkuOptionValue(skuOptionValue.getSkuOption(), skuOptionValue.getOptionValueKey());
		}
		sku.setStartDate(startDate);
		sku.setEndDate(endDate);
		sku.setShippable(shippable);
		sku.setDigital(!shippable);
		return sku;
	}

}
