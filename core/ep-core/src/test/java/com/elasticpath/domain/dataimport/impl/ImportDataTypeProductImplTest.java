package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.impl.CatalogLocaleFallbackPolicyFactory;
import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.exception.EpIntBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpTooLongBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductImpl.ImportFieldProductDefaultCategory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Test <code>ImportDataTypeProductImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.ExcessiveClassLength", "PMD.TooManyFields",
		"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects" })
public class ImportDataTypeProductImplTest {

	private static final String DISPLAY_NAME = "displayName(";

	private static final String PRODUCT_CODE = "productCode";

	private static final String INT_STRING = "1";

	private static final String BIGDECIMAL_STRING = "12.56";

	private static final String NEW_INT_STRING = "456";

	private static final String EP_BIND_EXCEPTION_NON_NULL_EXPECTED = "EpBindExceptionNonNull expected.";

	private static final String BBB = "bbb";

	private static final String AAA = "aaa";

	private static final String CAD = "CAD";

	private static final String USD = "USD";

	private static final String FALSE_STRING = "false";

	private static final String TRUE_STRING = "true";

	private static final String NON_EXISTING_GUID = "NON_EXISTING_GUID";

	private static final String DEFAULT_LOCALE_STRING = new Locale("en", "CA").toString();

	private static final Locale DEFAULT_LOCALE = new Locale("en", "CA");

	private static final String ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL = "productImageSmall";

	private static final String ATTRIBUTE_KEY_PRODUCT_SIZE = "productSize";

	private static final String PRODUCT_TYPE_NAME = "A test product type";

	private static final String INVALID_DATE_STRING = "asdf";

	private static final String INVALID_DECIMAL_STRING = "kasdjf";

	private static final String ATTRIBUTE_KEY_PRODUCT_COLOR = "productSkuColor";

	private static final String SKU_OPTION_NAME = "SkuOptionName";

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private ImportDataTypeProductImpl productImportType;

	private Map<String, AttributeValue> productAttributeValueMap;

	private Product product;

	private Set<AttributeGroupAttribute> productAttributes;

	private ProductTypeImpl productType;

	private ImportGuidHelper importGuidHelper;

	private Attribute attributeSize;

	private Attribute attributeImageSmall;

	private Attribute attributeColor;

	private Set<AttributeGroupAttribute> productSkuAttributes;

	private Map<String, AttributeValue> productSkuAttributeValueMap;

	private SkuOptionValueImpl skuOptionValue1;

	private String valueCode1;

	private SkuOptionValueImpl skuOptionValue2;

	private String valueCode2;

	private SkuOption skuOption;

	private Catalog masterCatalog;

	private final Utility utility = new UtilityImpl() {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getDefaultDateFormatPattern() {
			return DATE_FORMAT;
		}
	};

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private ValidatorUtils validatorUtils;

	private BeanFactoryExpectationsFactory expectationsFactory;

	/** Test initialization. */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);

		validatorUtils = context.mock(ValidatorUtils.class);

		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.VALIDATOR_UTILS, validatorUtils);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, utility);

		productImportType = createImportDataTypeProduct();

		importGuidHelper = context.mock(ImportGuidHelper.class);

		CatalogLocaleFallbackPolicyFactory localePolicyFactory = new CatalogLocaleFallbackPolicyFactory();
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, localePolicyFactory);

		setupSkuOption();

		productAttributeValueMap = new HashMap<String, AttributeValue>();
		productAttributes = new HashSet<AttributeGroupAttribute>();

		productSkuAttributeValueMap = new HashMap<String, AttributeValue>();
		productSkuAttributes = new HashSet<AttributeGroupAttribute>();
		setupAttributeImageSmall();
		setupAttributeSize();
		setupAttributeColor();

		product = createProduct();
		product.getAttributeValueGroup().setAttributeValueMap(productAttributeValueMap);
		product.setGuid("ProductGuid");

		productType = createProductType();

		productImportType.init(productType);

		Collection<Locale> supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(DEFAULT_LOCALE);
		productImportType.setSupportedLocales(supportedLocales);

		Collection<Currency> supportedCurrencies = new ArrayList<Currency>();
		supportedCurrencies.add(Currency.getInstance(USD));
		supportedCurrencies.add(Currency.getInstance(CAD));
		productImportType.setSupportedCurrencies(supportedCurrencies);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test initialization with null.
	 */
	@Test(expected = EpDomainException.class)
	public void testInitializationWithNull() {
		productImportType.init(null);
	}

	/**
	 * Test initialization with wrong type.
	 */
	@Test(expected = EpDomainException.class)
	public void testInitializationWithWrongType() {
		productImportType.init(new Object());
	}

	/**
	 * Test get required import fields.
	 */
	@Test
	public void testGetRequiredImportFields() {
		List<ImportField> requiredImportFields = productImportType.getRequiredImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertTrue(importField.isRequired());
		}
	}

	/**
	 * Test get optional import fields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<ImportField> requiredImportFields = productImportType.getOptionalImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertFalse(importField.isRequired());
		}
	}

	/**
	 * Test get import field of guid.
	 */
	@Test
	public void testGetImportFieldOfGuid() {
		final String guid = AAA;
		product.setGuid(guid);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + PRODUCT_CODE);
		assertEquals(guid, importField.getStringValue(product));

		final String newGuid = BBB;
		importField.setStringValue(product, newGuid, importGuidHelper);
		assertEquals("Updating the guid during an import is not allowed", guid, product.getGuid());
	}

	/**
	 * Test get import field of GUID with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfGuidWithNull() {
		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ PRODUCT_CODE);
		importField.setStringValue(product, null, importGuidHelper);
	}

	/**
	 * Test get import field of GUID with an invalid GUID.
	 */
	@Test(expected = EpInvalidGuidBindException.class)
	public void testGetImportFieldOfGuidWithInvalidGuid() {
		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ PRODUCT_CODE);
		importField.setStringValue(product, "asdf-123 ", importGuidHelper);
	}

	/**
	 * Test import product in catalog.
	 */
	@Test
	public void testImportProductInCatalog() {
		final String existingProductCode = "existingProductCode";

		final Product existingProduct = createProduct();

		existingProduct.setCode(existingProductCode);

		CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setUidPk(1);
		catalogImpl.setMaster(true);

		Category category = new CategoryImpl();
		category.setCatalog(catalogImpl);

		Set<Category> categories = new HashSet<Category>();
		categories.add(category);

		existingProduct.setCategories(categories);

		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).isProductGuidExist(with(any(String.class)));
				will(returnValue(true));

				allowing(importGuidHelper).findProductByGuid(with(any(String.class)),
						with(any(boolean.class)),
						with(any(boolean.class)),
						with(any(boolean.class)));
				will(returnValue(existingProduct));
			}
		});

		// first the happy case when the imported product exists and has the same master catalog
		// as the one that will be updated with
		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ PRODUCT_CODE);

		importField.setCatalog(catalogImpl);
		importField.checkStringValue(product, existingProductCode, importGuidHelper);

		Catalog catalog2 = new CatalogImpl();
		catalog2.setUidPk(2);
		importField.setCatalog(catalog2);

		try {
			importField.checkStringValue(product, existingProductCode, importGuidHelper);
			fail("The product exists in another catalog and we should have got an exception");
		} catch (EpInvalidValueBindException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test import field of default category.
	 */
	@Test
	public void testImportFieldOfDefaultCategory() {
		final String defaultCategoryGuid = "DefaultCategoryGuid";

		Catalog catalog = getCatalog();

		final Category defaultCategory = createCategory();
		defaultCategory.setGuid(defaultCategoryGuid);
		defaultCategory.setCatalog(catalog);

		final Category linkedCategory = new LinkedCategoryImpl();
		linkedCategory.setMasterCategory(defaultCategory);
		linkedCategory.setCatalog(getCatalog());

		final List<Category> linkedCategories = new ArrayList<Category>();
		linkedCategories.add(linkedCategory);

		ImportDataTypeProductImpl importDataTypeProduct = createImportDataTypeProduct();
		ImportFieldProductDefaultCategory field = importDataTypeProduct.new ImportFieldProductDefaultCategory("categoryImportField",
				String.class.toString(), true, false) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Category getCategory(final ImportGuidHelper service, final String categoryGuid) {
				return defaultCategory;
			}

			@Override
			protected List<Category> getLinkedCategories(final ImportGuidHelper service, final Category masterCategory) {
				return linkedCategories;
			}
		};

		Product product = createProduct();

		field.setStringValue(product, defaultCategoryGuid, null);

		// Test that setStringValue sets the product's primary category
		assertEquals("The default category in the master catalog should be set", defaultCategory, product.getDefaultCategory(catalog));

		// Test that setStringValue sets adds categories to the product that are linked to the primary category
		assertTrue("The category linked to the default category should also be added to the product",
				product.getCategories().contains(linkedCategory));
	}

	/**
	 * Test get import field of default category with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfDefaultCategoryWithNull() {
		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ "defaultCategoryCode");
		importField.setCatalog(new CatalogImpl());
		importField.setStringValue(product, null, importGuidHelper);
	}

	/**
	 * Test get import field of default category with non existing GUID.
	 */
	@Test(expected = EpInvalidGuidBindException.class)
	public void testGetImportFieldOfDefaultCategoryWithNonExistingGuid() {
		shouldReturnCategoryFromFindCategoryByGuidAndCatalogGuid(null);

		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ "defaultCategoryCode");
		importField.setCatalog(new CatalogImpl());
		importField.setStringValue(product, NON_EXISTING_GUID, importGuidHelper);
	}

	/**
	 * Test get import field of start date.
	 */
	@Test
	public void testGetImportFieldOfStartDate() {
		final Date now = new Date();
		product.setStartDate(now);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		assertFalse(importField.isRequired());
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(product));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(product, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(product.getStartDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(product.getStartDate(), DATE_FORMAT, Locale.getDefault()));

		// If a null value is given, the current date is set.
		importField.setStringValue(product, GlobalConstants.NULL_VALUE, importGuidHelper);
		assertNotNull(product.getStartDate());
	}

	/**
	 * Test get import field of start date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfStartDateWithError() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		importField.setStringValue(product, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of end date.
	 */
	@Test
	public void testGetImportFieldOfEndDate() {
		final Date now = new Date();
		product.setEndDate(now);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(product));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(product, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(product.getEndDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(product.getEndDate(), DATE_FORMAT, Locale.getDefault()));

		// set it to null
		importField.setStringValue(product, "null", importGuidHelper);
		assertNull(product.getEndDate());
	}

	/**
	 * Test get import field of end date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfEndDateWithError() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		importField.setStringValue(product, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of size.
	 */
	@Test
	public void testGetImportFieldOfSize() {
		final Integer size = Integer.valueOf(3);
		product.getAttributeValueGroup().setAttributeValue(attributeSize, null, size);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_SIZE);
		assertEquals(size.toString(), importField.getStringValue(product));

		final String newSize = "5";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(product, newSize, importGuidHelper);
		assertEquals(newSize, product.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_PRODUCT_SIZE, null));
	}

	/**
	 * Test get import field of size with error.
	 */
	@Test(expected = EpIntBindException.class)
	public void testGetImportFieldOfSizeWithError() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_SIZE);
		importField.setStringValue(product, INVALID_DECIMAL_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of small image.
	 */
	@Test
	public void testGetImportFieldOfSmallImage() {
		final String imageSmall = "aaa.jpg";
		product.getAttributeValueGroup().setAttributeValue(attributeImageSmall, DEFAULT_LOCALE, imageSmall);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL + '(' + DEFAULT_LOCALE_STRING + ')');
		assertEquals(imageSmall, importField.getStringValue(product));

		final String newImageSmall = "bbb.jpg";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(product, newImageSmall, importGuidHelper);
		assertEquals(newImageSmall, product.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL, DEFAULT_LOCALE));
	}

	/**
	 * Test get name.
	 */
	@Test
	public void testGetName() {
		assertEquals(ImportDataTypeProductImpl.PREFIX_OF_IMPORT_DATA_TYPE_NAME + ImportDataType.SEPARATOR + PRODUCT_TYPE_NAME,
				productImportType.getName());
	}

	/**
	 * Test get guid field name.
	 */
	@Test
	public void testGetGuidFieldName() {
		final String guid = AAA;
		product.setGuid(guid);
		assertEquals(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + PRODUCT_CODE, productImportType.getGuidFieldName());
	}

	/**
	 * Test get import field of display name.
	 */
	@Test
	public void testGetImportFieldOfDisplayName() {
		Product myProduct = createProduct();

		final String displayNameDefaultLocale = AAA;

		// Set the product's display name in the default locale
		LocaleDependantFields ldf = myProduct.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE);
		ldf.setDisplayName(displayNameDefaultLocale);
		myProduct.addOrUpdateLocaleDependantFields(ldf);

		// Get the import field for the display name in the default locale
		ImportDataTypeProductImpl importDataTypeProductImpl = createImportDataTypeProduct();
		Collection<Locale> locales = new ArrayList<Locale>();
		locales.add(DEFAULT_LOCALE);
		importDataTypeProductImpl.init(createProductTypeForTestingDisplayName());
		importDataTypeProductImpl.setSupportedLocales(locales);
		ImportField importField = importDataTypeProductImpl.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + DISPLAY_NAME
				+ DEFAULT_LOCALE_STRING + ')');
		assertEquals("ImportField getStringValue should return the display name", displayNameDefaultLocale, importField.getStringValue(myProduct));

		final String newValue = BBB;
		importField.setStringValue(myProduct, newValue, importGuidHelper);
		assertEquals("ImportField.setStringValue for display name should have set the display name", newValue, myProduct
				.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE).getDisplayName());
	}

	/**
	 * Test that the import tool will throw {@link EpNonNullBindException} when there is a required locale and the set locale value = null.
	 */
	@Test
	public void testGetImportFieldOfDisplayNameNullForRequiredLocale() {
		productImportType.setRequiredLocale(DEFAULT_LOCALE);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + DISPLAY_NAME
				+ DEFAULT_LOCALE_STRING + ')');
		try {
			importField.setStringValue(product, null, importGuidHelper);
			fail(EP_BIND_EXCEPTION_NON_NULL_EXPECTED);
		} catch (EpNonNullBindException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test that the import tool will NOT throw {@link EpNonNullBindException} when the required locale is different from the one used to set a
	 * string value for.
	 */
	@Test
	public void testGetImportFieldOfDisplayNameNullForNonRequiredLocale() {
		productImportType.setRequiredLocale(Locale.ITALIAN);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + DISPLAY_NAME + DEFAULT_LOCALE
				+ ')');
		try {
			importField.setStringValue(product, null, importGuidHelper);
		} catch (EpNonNullBindException e) {
			assertNull(e);
		}
	}

	/**
	 * Test get import field of display name error too long.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testGetImportFieldOfDisplayNameErrorTooLong() {
		final ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + DISPLAY_NAME
				+ DEFAULT_LOCALE_STRING + ')');
		final String invalidDisplayName = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		importField.setStringValue(product, invalidDisplayName, importGuidHelper);
	}

	/**
	 * Test get import field of URL.
	 */
	@Test
	public void testGetImportFieldOfUrl() {
		final String value = AAA;

		Product product = createProduct();

		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE);
		ldf.setUrl(value);
		product.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoUrl("
				+ DEFAULT_LOCALE.toString() + ')');
		assertEquals(value, importField.getStringValue(product));

		final String newValue = BBB;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE).getUrl());
	}

	/**
	 * Test setting url with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingUrlWithTextExceedingMaximumLength() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoUrl("
				+ DEFAULT_LOCALE.toString() + ')');
		final String invalidUrl = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		importField.setStringValue(product, invalidUrl, importGuidHelper);
	}

	/**
	 * Test get import field of title.
	 */
	@Test
	public void testGetImportFieldOfTitle() {
		final String value = AAA;

		Product product = createProduct();

		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE);
		ldf.setTitle(value);
		product.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoTitle("
				+ DEFAULT_LOCALE.toString() + ')');
		assertEquals(value, importField.getStringValue(product));

		final String newValue = BBB;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE).getTitle());
	}

	/**
	 * Test setting title with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingTitleWithTextExceedingMaximumLength() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoTitle("
				+ DEFAULT_LOCALE.toString() + ')');
		final String invalidTitle = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		importField.setStringValue(product, invalidTitle, importGuidHelper);
	}

	/**
	 * Test get import field of key words.
	 */
	@Test
	public void testGetImportFieldOfKeyWords() {
		final String value = AAA;

		Product product = createProduct();

		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE);
		ldf.setKeyWords(value);
		product.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoKeyWords("
				+ DEFAULT_LOCALE.toString() + ')');
		assertEquals(value, importField.getStringValue(product));

		final String newValue = BBB;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE).getKeyWords());
	}

	/**
	 * Test setting keywords with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingKeywordsWithTextExceedingMaximumLength() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoKeyWords("
				+ DEFAULT_LOCALE.toString() + ')');
		final String invalidKeywords = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		importField.setStringValue(product, invalidKeywords, importGuidHelper);
	}


	/**
	 * Test get import field of description.
	 */
	@Test
	public void testGetImportFieldOfDescription() {
		final String value = AAA;

		Product product = createProduct();

		LocaleDependantFields ldf = product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE);
		ldf.setDescription(value);
		product.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoDescription("
				+ DEFAULT_LOCALE.toString() + ')');
		assertEquals(value, importField.getStringValue(product));

		final String newValue = BBB;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, product.getLocaleDependantFieldsWithoutFallBack(DEFAULT_LOCALE).getDescription());
	}

	/**
	 * Test get import field of description error too long.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testGetImportFieldOfDescriptionErrorTooLong() {
		final ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "seoDescription("
				+ DEFAULT_LOCALE.toString() + ')');

		final String invalidDescriptionText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		importField.setStringValue(product, invalidDescriptionText, importGuidHelper);
	}

	/**
	 * Test get import field of sku code.
	 */
	@Test
	public void testGetImportFieldOfSkuCode() {
		final String value = AAA;

		ProductSku productSku = createProductSku();

		productSku.setSkuCode(value);

		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "skuCode");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = BBB;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, product.getDefaultSku().getSkuCode());
	}

	/**
	 * Test get import field of sku width.
	 */
	@Test
	public void testGetImportFieldOfSkuWidth() {
		final String value = BIGDECIMAL_STRING;

		ProductSku productSku = createProductSku();

		productSku.setWidth(new BigDecimal(BIGDECIMAL_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "width");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.getDefaultSku().getWidth()));
	}

	/**
	 * Test get import field of sku height.
	 */
	@Test
	public void testGetImportFieldOfSkuHeight() {
		final String value = BIGDECIMAL_STRING;

		ProductSku productSku = createProductSku();

		productSku.setHeight(new BigDecimal(BIGDECIMAL_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "height");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.getDefaultSku().getHeight()));
	}

	/**
	 * Test get import field of sku length.
	 */
	@Test
	public void testGetImportFieldOfSkuLength() {
		final String value = BIGDECIMAL_STRING;

		ProductSku productSku = createProductSku();

		productSku.setLength(new BigDecimal(BIGDECIMAL_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "length");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.getDefaultSku().getLength()));
	}

	/**
	 * Test get import field of sku weight.
	 */
	@Test
	public void testGetImportFieldOfSkuWeight() {
		final String value = BIGDECIMAL_STRING;

		ProductSku productSku = createProductSku();

		productSku.setWeight(new BigDecimal(BIGDECIMAL_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "weight");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.getDefaultSku().getWeight()));
	}

	/**
	 * Test get import field shippable.
	 */
	@Test
	public void testGetImportFieldShippable() {
		final String value = FALSE_STRING;

		ProductSku productSku = createProductSku();

		productSku.setShippable(Boolean.parseBoolean(value));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "shippable");
		assertEquals(value, importField.getStringValue(product));

		final String newValue = TRUE_STRING;
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.getDefaultSku().isShippable()));
	}

	/**
	 * Test get import field of brand.
	 */
	@Test
	public void testGetImportFieldOfBrand() {
		final String guid = AAA;
		final Brand brand = createBrand();
		brand.setGuid(guid);
		product.setBrand(brand);

		final String newGuid = BBB;
		final Brand newBrand = createBrand();
		newBrand.setGuid(newGuid);

		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).isBrandGuidExist(with(any(String.class)), with(aNull(String.class)));
				will(returnValue(true));

				allowing(importGuidHelper).findBrandByGuidAndCatalogGuid(with(any(String.class)), with(aNull(String.class)));
				will(returnValue(newBrand));
			}
		});

		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ "brandCode");
		importField.setCatalog(new CatalogImpl());

		assertTrue(importField.isRequired());
		assertEquals(guid, importField.getStringValue(product));

		importField.checkStringValue(product, newGuid, importGuidHelper);
		importField.setStringValue(product, newGuid, importGuidHelper);
		assertEquals(newGuid, importField.getStringValue(product));

		importField.setStringValue(product, null, importGuidHelper);
		assertNull(product.getBrand());
	}

	/**
	 * Test get import field of brand with non existent GUID.
	 */
	@Test(expected = EpInvalidGuidBindException.class)
	public void testGetImportFieldOfBrandWithNonExistGuid() {
		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).findBrandByGuidAndCatalogGuid(with(any(String.class)), with(aNull(String.class)));
				will(returnValue(null));
			}
		});

		CatalogImportField importField = (CatalogImportField) productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ "brandCode");
		importField.setCatalog(new CatalogImpl());

		importField.setStringValue(product, NON_EXISTING_GUID, importGuidHelper);
	}

	/**
	 * Test get import field of image.
	 */
	@Test
	public void testGetImportFieldOfImage() {
		final String image = AAA;
		product.setImage(image);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "image");
		assertFalse(importField.isRequired());

		assertEquals(image, importField.getStringValue(product));

		final String newImage = BBB;
		importField.setStringValue(product, newImage, importGuidHelper);
		assertEquals(newImage, product.getImage());

		importField.setStringValue(product, GlobalConstants.NULL_VALUE, importGuidHelper);
		assertNull(product.getImage());
	}

	/**
	 * Test setting image with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingImageWithTextExceedingMaxLength() {
		final String invalidImageText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "image");
		importField.setStringValue(product, invalidImageText, importGuidHelper);
	}

	/**
	 * Test get import field of sku attributes.
	 */
	@Test
	public void testGetImportFieldOfSkuAttributes() {
		ProductSku productSku = createProductSku();

		productSku.getAttributeValueGroup().setAttributeValueMap(productSkuAttributeValueMap);
		product.addOrUpdateSku(productSku);

		final Integer color = Integer.valueOf(3);
		productSku.getAttributeValueGroup().setAttributeValue(attributeColor, null, color);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_COLOR);
		assertEquals(color.toString(), importField.getStringValue(product));

		final String newColor = "5";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(product, newColor, importGuidHelper);
		assertEquals(newColor, productSku.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_PRODUCT_COLOR, null));
	}

	/**
	 * Test get import field of color with error.
	 */
	@Test(expected = EpIntBindException.class)
	public void testGetImportFieldOfColorWithError() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_COLOR);
		importField.setStringValue(product, INVALID_DECIMAL_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of sku option.
	 */
	@Test
	public void testGetImportFieldOfSkuOption() {
		ProductSku productSku = createProductSku();

		product.addOrUpdateSku(productSku);

		productSku.setSkuOptionValue(skuOption, valueCode1);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + SKU_OPTION_NAME);
		assertEquals(valueCode1, importField.getStringValue(product));

		importField.setStringValue(product, valueCode2, importGuidHelper);
		assertEquals(skuOptionValue2.getOptionValueKey(), productSku.getSkuOptionValue(skuOption).getOptionValueKey());
	}

	/**
	 * Test get import field of sku option with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfSkuOptionWithNull() {
		ImportField importField = productImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + SKU_OPTION_NAME);
		importField.setStringValue(product, null, importGuidHelper);
	}

	/**
	 * Test get import field of hidden.
	 */
	@Test
	public void testGetImportFieldOfHidden() {
		final String value = "false";

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "storeVisible");
		assertFalse(importField.isRequired());
		assertEquals(value, importField.getStringValue(product));

		final String newValue = "true";
		importField.setStringValue(product, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(product.isHidden()));
	}

	/**
	 * Test get import field sku file name.
	 */
	@Test
	public void testGetImportFieldSkuFileName() {
		ProductSku productSku = createProductSku();

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);
		digitalAsset.setFileName(AAA);
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "fileName");
		assertEquals(AAA, importField.getStringValue(product));

		importField.setStringValue(product, BBB, importGuidHelper);
		assertEquals(BBB, product.getDefaultSku().getDigitalAsset().getFileName());
	}

	/**
	 * Test setting filename with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingFilenameWithTextExceedingMaxLength() {
		final String invalidFilenameText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "fileName");
		importField.setStringValue(product, invalidFilenameText, importGuidHelper);
	}

	/**
	 * Test get import field sku expiry days.
	 */
	@Test
	public void testGetImportFieldSkuExpiryDays() {
		ProductSku productSku = createProductSku();

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);

		digitalAsset.setExpiryDays(Integer.parseInt(INT_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "expiryDays");
		assertEquals(INT_STRING, importField.getStringValue(product));

		importField.setStringValue(product, NEW_INT_STRING, importGuidHelper);
		assertEquals(Integer.parseInt(NEW_INT_STRING), product.getDefaultSku().getDigitalAsset().getExpiryDays());
	}

	/**
	 * Test get import field sku max download times.
	 */
	@Test
	public void testGetImportFieldSkuMaxDownloadTimes() {
		ProductSku productSku = createProductSku();

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);

		digitalAsset.setMaxDownloadTimes(Integer.parseInt(INT_STRING));
		product.addOrUpdateSku(productSku);

		ImportField importField = productImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "maxDownloadTimes");
		assertEquals(INT_STRING, importField.getStringValue(product));

		importField.setStringValue(product, NEW_INT_STRING, importGuidHelper);
		assertEquals(Integer.parseInt(NEW_INT_STRING), product.getDefaultSku().getDigitalAsset().getMaxDownloadTimes());
	}

	private void shouldReturnCategoryFromFindCategoryByGuidAndCatalogGuid(final Category category) {
		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).findCategoryByGuidAndCatalogGuid(with(any(String.class)),
						with(aNull(String.class)),
						with(any(boolean.class)),
						with(any(boolean.class)));
				will(returnValue(category));
			}
		});
	}

	private ProductSku createProductSku() {
		ProductSku productSku = new ProductSkuImpl();

		expectationsFactory.allowingBeanFactoryGetBean("jpaAdaptorSkuOptionValue", JpaAdaptorOfSkuOptionValueImpl.class);

		return productSku;
	}

	private Product createProduct() {
		Product product = new ProductImpl();
		product.initialize();

		return product;
	}

	private Brand createBrand() {
		Brand brand = new BrandImpl();

		brand.initialize();
		return brand;
	}

	private ProductTypeImpl createProductType() {
		ProductTypeImpl productType = new ProductTypeImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);

		productType.initialize();
		productType.setName(PRODUCT_TYPE_NAME);

		productType.setProductAttributeGroup(new AttributeGroupImpl());
		productType.getProductAttributeGroup().setAttributeGroupAttributes(productAttributes);

		productType.setSkuAttributeGroup(new AttributeGroupImpl());
		productType.getSkuAttributeGroup().setAttributeGroupAttributes(productSkuAttributes);

		productType.setProductAttributeGroupAttributes(productAttributes);
		productType.setSkuAttributeGroupAttributes(productSkuAttributes);

		// Add sku option to the product type
		productType.getSkuOptions().add(skuOption);
		return productType;
	}

	private ProductType createProductTypeForTestingDisplayName() {
		return new ProductTypeImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isWithMultipleSkus() {
				return true;
			}
		};
	}

	private ImportDataTypeProductImpl createImportDataTypeProduct() {
		ImportDataTypeProductImpl productImportType = new ImportDataTypeProductImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_SKU, createProductSku());

		return productImportType;
	}

	private SkuOption createSkuOption() {
		final SkuOption skuOption = new SkuOptionImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		skuOption.initialize();
		return skuOption;
	}

	private Category createCategory() {
		final Category category = new CategoryImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		category.initialize();
		category.setCatalog(getCatalog());
		return category;
	}

	private Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("irrelevant catalog code");
		}
		return masterCatalog;
	}

	private void setupSkuOption() {
		// Define a sku option
		skuOptionValue1 = new SkuOptionValueImpl();
		valueCode1 = "aaa";
		skuOptionValue1.setOptionValueKey(valueCode1);
		skuOptionValue2 = new SkuOptionValueImpl();
		valueCode2 = "bbb";
		skuOptionValue2.setOptionValueKey(valueCode2);

		skuOption = createSkuOption();
		skuOption.setOptionKey(SKU_OPTION_NAME);
		skuOption.addOptionValue(skuOptionValue1);
		skuOption.addOptionValue(skuOptionValue2);
	}

	private void setupAttributeImageSmall() {
		attributeImageSmall = createAttribute(AttributeType.IMAGE, ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL, true);
		addAttributeToAttributeGroupAttributes(attributeImageSmall, productAttributes);
		createProductAttributeValue(attributeImageSmall, AttributeType.IMAGE, null);
	}

	private void setupAttributeSize() {
		attributeSize = createAttribute(AttributeType.INTEGER, ATTRIBUTE_KEY_PRODUCT_SIZE, false);
		addAttributeToAttributeGroupAttributes(attributeSize, productAttributes);
		AttributeValue attributeValue = createProductAttributeValue(attributeSize, AttributeType.INTEGER, Integer.valueOf(1));
		productAttributeValueMap.put(ATTRIBUTE_KEY_PRODUCT_SIZE, attributeValue);
	}

	private void setupAttributeColor() {
		attributeColor = createAttribute(AttributeType.INTEGER, ATTRIBUTE_KEY_PRODUCT_COLOR, false);
		addAttributeToAttributeGroupAttributes(attributeColor, productSkuAttributes);
		AttributeValue attributeValue = createProductAttributeValue(attributeColor, AttributeType.INTEGER, Integer.valueOf(1));
		productSkuAttributeValueMap.put(ATTRIBUTE_KEY_PRODUCT_COLOR, attributeValue);
	}

	private Attribute createAttribute(final AttributeType attributueType, final String key, final boolean localeDependant) {
		Attribute result = new AttributeImpl();
		result = new AttributeImpl();
		result.setAttributeType(attributueType);
		result.setKey(key);
		result.setLocaleDependant(localeDependant);
		return result;
	}

	private void addAttributeToAttributeGroupAttributes(final Attribute attribute, final Set<AttributeGroupAttribute> attributeGroupAttributes) {
		final AttributeGroupAttribute attributeGroupAttribute = new AttributeGroupAttributeImpl();
		attributeGroupAttribute.setAttribute(attribute);
		attributeGroupAttributes.add(attributeGroupAttribute);
	}

	private AttributeValue createProductAttributeValue(final Attribute attribute, final AttributeType attributeType, final Object value) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attributeType);
		attributeValue.setValue(value);
		return attributeValue;
	}

}
