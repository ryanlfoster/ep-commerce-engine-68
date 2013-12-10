package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Test <code>ImportDataTypeProductSkuImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.ExcessiveImports" })
public class ImportDataTypeProductSkuImplTest {
	private static final String NEW_INT_STRING = "456";

	private static final String INT_STRING = "1";

	private static final String BIGDECIMAL_STRING = "12.56";

	private static final String BBB = "bbb";

	private static final String AAA = "aaa";

	private static final String CAD = "CAD";

	private static final String USD = "USD";

	private static final String ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL = "productImageSmall";

	private static final String ATTRIBUTE_KEY_PRODUCT_SIZE = "productSize";

	private static final String PRODUCT_TYPE_NAME = "A test product type";

	private static final String INVALID_DATE_STRING = "asdf";

	private static final String INVALID_DECIMAL_STRING = "kasdjf";

	private static final String SKU_OPTION_NAME = "skuOptionName";

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private ImportDataTypeProductSkuImpl productSkuImportType;

	private Map<String, AttributeValue> productSkuAttributeValueMap;

	private ProductSku productSku;

	private Set<AttributeGroupAttribute> productTypeSkuAttributes;

	private ProductTypeImpl productType;

	private ImportGuidHelper importGuidHelper;

	private Attribute attributeSize;

	private String valueCode1;

	private String valueCode2;

	private SkuOptionValue skuOptionValue2;

	private SkuOptionValue skuOptionValue1;

	private SkuOption skuOption;

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

		productSkuImportType = new ImportDataTypeProductSkuImpl();

		importGuidHelper = context.mock(ImportGuidHelper.class);

		setupSkuOption();

		productSkuAttributeValueMap = new HashMap<String, AttributeValue>();
		productTypeSkuAttributes = new HashSet<AttributeGroupAttribute>();
		setupAttributeImageSmall();
		setupAttributeSize();

		productSku = createProductSku();
		productSku.setProduct(new ProductImpl());

		productType = createProductType();
		productSkuImportType.init(productType);

		Collection<Currency> supportedCurrencies = new ArrayList<Currency>();
		supportedCurrencies.add(Currency.getInstance(USD));
		supportedCurrencies.add(Currency.getInstance(CAD));
		productSkuImportType.setSupportedCurrencies(supportedCurrencies);

		Collection<Locale> supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.US);
		supportedLocales.add(Locale.CANADA_FRENCH);
		productSkuImportType.setSupportedLocales(supportedLocales);
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
		productSkuImportType.init(null);
	}

	/**
	 * Test initialization with wrong type.
	 */
	@Test(expected = EpDomainException.class)
	public void testInitializationWithWrongType() {
		productSkuImportType.init(new Object());
	}

	/**
	 * Test get required import fields.
	 */
	@Test
	public void testGetRequiredImportFields() {
		List<ImportField> requiredImportFields = productSkuImportType.getRequiredImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertTrue(importField.isRequired());
		}
	}

	/**
	 * Test get optional import fields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<ImportField> requiredImportFields = productSkuImportType.getOptionalImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertFalse(importField.isRequired());
		}
	}

	/**
	 * Test get import field of start date.
	 */
	@Test
	public void testGetImportFieldOfStartDate() {
		final Date now = new Date();
		productSku.setStartDate(now);
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		assertFalse(importField.isRequired());
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(productSku));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(productSku, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(productSku.getStartDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(productSku.getStartDate(), DATE_FORMAT, Locale.getDefault()));

		// If a null value is given, the current date is set.
		importField.setStringValue(productSku, GlobalConstants.NULL_VALUE, importGuidHelper);
		assertNotNull(productSku.getStartDate());
	}

	/**
	 * Test get import field of start date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfStartDateWithError() {
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		importField.setStringValue(productSku, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of end date.
	 */
	@Test
	public void testGetImportFieldOfEndDate() {
		final Date now = new Date();
		productSku.setEndDate(now);
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(productSku));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(productSku, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(productSku.getEndDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(productSku.getEndDate(), DATE_FORMAT, Locale.getDefault()));

		importField.setStringValue(productSku, "null", importGuidHelper);
		assertNull(productSku.getEndDate());
	}

	/**
	 * Test get import field of end date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfEndDateWithError() {
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		importField.setStringValue(productSku, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get name.
	 */
	@Test
	public void testGetName() {
		final String name = ImportDataTypeProductSkuImpl.PREFIX_OF_IMPORT_DATA_TYPE_NAME + ImportDataType.SEPARATOR + PRODUCT_TYPE_NAME;
		assertEquals(name, productSkuImportType.getName());
	}

	/**
	 * Test get guid field name.
	 */
	@Test
	public void testGetGuidFieldName() {
		final String guid = AAA;
		productSku.setGuid(guid);
		assertEquals(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "skuCode", productSkuImportType.getGuidFieldName());
	}

	/**
	 * Test get import field of sku code.
	 */
	@Test
	public void testGetImportFieldOfSkuCode() {
		final String value = AAA;

		productSku.setSkuCode(value);

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "skuCode");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = BBB;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, productSku.getSkuCode());
	}

	/**
	 * Test get import field of sku width.
	 */
	@Test
	public void testGetImportFieldOfSkuWidth() {
		final String value = BIGDECIMAL_STRING;

		productSku.setWidth(new BigDecimal(BIGDECIMAL_STRING));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "width");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getWidth()));
	}

	/**
	 * Test get import field of sku height.
	 */
	@Test
	public void testGetImportFieldOfSkuHeight() {
		final String value = BIGDECIMAL_STRING;

		productSku.setHeight(new BigDecimal(BIGDECIMAL_STRING));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "height");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getHeight()));
	}

	/**
	 * Test get import field of sku length.
	 */
	@Test
	public void testGetImportFieldOfSkuLength() {
		final String value = BIGDECIMAL_STRING;

		productSku.setLength(new BigDecimal(BIGDECIMAL_STRING));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "length");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getLength()));
	}

	/**
	 * Test get import field of sku weight.
	 */
	@Test
	public void testGetImportFieldOfSkuWeight() {
		final String value = BIGDECIMAL_STRING;

		productSku.setWeight(new BigDecimal(BIGDECIMAL_STRING));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "weight");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getWeight()));
	}

	/**
	 * Test get import field of default product.
	 */
	@Test
	public void testGetImportFieldOfDefaultProduct() {
		final String guid = AAA;
		final Product product = createProduct();
		product.setGuid(guid);
		productSku.setProduct(product);

		final String newGuid = BBB;
		final Product newProduct = new ProductImpl();
		newProduct.setGuid(newGuid);

		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).findProductByGuid(with(any(String.class)),
						with(any(boolean.class)),
						with(any(boolean.class)),
						with(any(boolean.class)));
				will(returnValue(newProduct));
			}
		});

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "productCode");
		assertTrue(importField.isRequired());

		assertEquals(guid, importField.getStringValue(productSku));

		importField.setStringValue(productSku, newGuid, importGuidHelper);
		assertEquals(newGuid, importField.getStringValue(productSku));
	}

	/**
	 * Test get import field of shippable.
	 */
	@Test
	public void testGetImportFieldOfShippable() {
		final String value = "false";

		productSku.setShippable(Boolean.parseBoolean(value));
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "shippable");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = "true";
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.isShippable()));
	}

	/**
	 * Test get import field of size.
	 */
	@Test
	public void testGetImportFieldOfSize() {
		final Integer size = Integer.valueOf(3);
		productSku.getAttributeValueGroup().setAttributeValue(attributeSize, null, size);

		ImportField importField = productSkuImportType
				.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_SIZE);
		assertEquals(size.toString(), importField.getStringValue(productSku));

		final String newSize = "5";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(productSku, newSize, importGuidHelper);
		assertEquals(newSize, productSku.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_PRODUCT_SIZE, null));
	}

	/**
	 * Test get import field of size with error.
	 */
	@Test(expected = EpIntBindException.class)
	public void testGetImportFieldOfSizeWithError() {
		ImportField importField = productSkuImportType
				.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_PRODUCT_SIZE);

		importField.setStringValue(productSku, INVALID_DECIMAL_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of sku option.
	 */
	@Test
	public void testGetImportFieldOfSkuOption() {
		productSku.setSkuOptionValue(skuOption, valueCode1);

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + SKU_OPTION_NAME);
		assertEquals(valueCode1, importField.getStringValue(productSku));

		importField.setStringValue(productSku, valueCode2, importGuidHelper);
		assertEquals(skuOptionValue2.getOptionValueKey(), productSku.getSkuOptionValue(skuOption).getOptionValueKey());
	}

	/**
	 * Test get import field of sku option with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfSkuOptionWithNull() {
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + SKU_OPTION_NAME);
		importField.setStringValue(productSku, null, importGuidHelper);
	}

	/**
	 * Test get import field of image.
	 */
	@Test
	public void testGetImportFieldOfImage() {
		final String image = AAA;
		productSku.setImage(image);
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "image");
		assertFalse(importField.isRequired());

		assertEquals(image, importField.getStringValue(productSku));

		final String newImage = BBB;
		importField.setStringValue(productSku, newImage, importGuidHelper);
		assertEquals(newImage, productSku.getImage());
	}


	/**
	 * Test setting image with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingImageWithTextExceedingMaxLength() {
		final String invalidImageText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "image");
		importField.setStringValue(productSku, invalidImageText, importGuidHelper);
	}

	/**
	 * Test setting filename with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingFilenameWithTextExceedingMaxLength() {
		final String invalidFilenameText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "fileName");
		importField.setStringValue(productSku, invalidFilenameText, importGuidHelper);
	}

	/**
	 * Test get import field of file name.
	 */
	@Test
	public void testGetImportFieldOfFileName() {
		final String fileName = AAA;

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);
		digitalAsset.setFileName(fileName);

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "fileName");
		assertFalse(importField.isRequired());

		assertEquals(fileName, importField.getStringValue(productSku));

		final String newFileName = BBB;
		importField.setStringValue(productSku, newFileName, importGuidHelper);
		assertEquals(newFileName, productSku.getDigitalAsset().getFileName());
	}

	/**
	 * Test get import field of expiry days.
	 */
	@Test
	public void testGetImportFieldOfExpiryDays() {
		final String value = INT_STRING;

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);

		digitalAsset.setExpiryDays(Integer.parseInt(value));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "expiryDays");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getDigitalAsset().getExpiryDays()));
	}

	/**
	 * Test get import field of max download times.
	 */
	@Test
	public void testGetImportFieldOfMaxDownloadTimes() {
		final String value = INT_STRING;

		final DigitalAsset digitalAsset = new DigitalAssetImpl();
		productSku.setDigital(true);
		productSku.setDigitalAsset(digitalAsset);
		digitalAsset.setMaxDownloadTimes(Integer.parseInt(value));

		ImportField importField = productSkuImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "maxDownloadTimes");
		assertEquals(value, importField.getStringValue(productSku));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productSku, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(productSku.getDigitalAsset().getMaxDownloadTimes()));
	}

	private ProductSku createProductSku() {
		ProductSku productSku = new ProductSkuImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("jpaAdaptorSkuOptionValue", JpaAdaptorOfSkuOptionValueImpl.class);

		productSku.initialize();
		return productSku;
	}

	private Product createProduct() {
		Product product = new ProductImpl();
		product.initialize();
		return product;
	}

	private SkuOption createSkuOption() {
		final SkuOption skuOption = new SkuOptionImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		skuOption.initialize();
		return skuOption;
	}


	private ProductTypeImpl createProductType() {
		ProductTypeImpl productType = new ProductTypeImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		productType.initialize();
		productType.setName(PRODUCT_TYPE_NAME);

		productType.setSkuAttributeGroup(new AttributeGroupImpl());
		productType.getSkuAttributeGroup().setAttributeGroupAttributes(productTypeSkuAttributes);

		productType.getSkuOptions().add(skuOption);

		productType.setProductAttributeGroupAttributes(productTypeSkuAttributes);
		productType.setSkuAttributeGroupAttributes(productTypeSkuAttributes);

		return productType;
	}

	private void setupSkuOption() {
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
		Attribute attributeImageSmall = createAttribute(AttributeType.IMAGE, ATTRIBUTE_KEY_PRODUCT_IMAGE_SMALL, true);
		addAttributeToAttributeGroupAttributes(attributeImageSmall, productTypeSkuAttributes);
		createProductAttributeValue(attributeImageSmall, AttributeType.IMAGE, null);
	}

	private void setupAttributeSize() {
		attributeSize = createAttribute(AttributeType.INTEGER, ATTRIBUTE_KEY_PRODUCT_SIZE, false);
		addAttributeToAttributeGroupAttributes(attributeSize, productTypeSkuAttributes);
		AttributeValue attributeValue = createProductAttributeValue(attributeSize, AttributeType.INTEGER, Integer.valueOf(1));
		productSkuAttributeValueMap.put(ATTRIBUTE_KEY_PRODUCT_SIZE, attributeValue);
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
