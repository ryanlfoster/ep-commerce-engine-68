package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
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
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
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
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryLocaleDependantFieldsImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleFallbackPolicyFactory;
import com.elasticpath.domain.dataimport.CatalogImportField;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Test <code>ImportDataTypeCategoryImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.ExcessiveImports"  })
public class ImportDataTypeCategoryImplTest {

	private static final String CATALOG_CODE = "CATALOG_CODE";

	private static final String CATEGORY_CODE = "categoryCode";

	private static final String NULL = "null";

	private static final String GUID_ONE = "aaa";

	private static final String GUID_TWO = "bbb";

	private static final String NON_EXISTING_GUID = "NON_EXISTING_GUID";

	private static final String ATTRIBUTE_KEY_CATEGORY_IMAGE_SMALL = "categoryImageSmall";

	private static final String ATTRIBUTE_KEY_CATEGORY_SIZE = "categorySize";

	private static final String CATEGORY_TYPE_NAME = "A test category type";

	private static final String INVALID_DATE_STRING = "asdf";

	private static final String INVALID_DECIMAL_STRING = "kasdjf";

	private static final String INVALID_INTEGER_STRING = "asdf";

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private ImportDataTypeCategoryImpl categoryImportType;

	private Map<String, AttributeValue> attributeValueMap;

	private Category category;

	private Set<AttributeGroupAttribute> categoryTypeAttributes;

	private CategoryTypeImpl categoryType;

	private ImportGuidHelper importGuidHelper;

	private Attribute attributeSize;

	private Attribute attributeImageSmall;

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

		categoryImportType = new ImportDataTypeCategoryImpl();

		importGuidHelper = context.mock(ImportGuidHelper.class);
		
		CatalogLocaleFallbackPolicyFactory localePolicyFactory = new CatalogLocaleFallbackPolicyFactory();
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, localePolicyFactory);

		Collection<Locale> supportedLocales = new ArrayList<Locale>();
		supportedLocales.add(Locale.getDefault());

		attributeValueMap = new HashMap<String, AttributeValue>();
		categoryTypeAttributes = new HashSet<AttributeGroupAttribute>();
		setupAttributeImageSmall();
		setupAttributeSize();

		category = createCategory();
		category.getAttributeValueGroup().setAttributeValueMap(attributeValueMap);

		categoryType = new CategoryTypeImpl();
		categoryType.setName(CATEGORY_TYPE_NAME);
		categoryType.setAttributeGroup(new AttributeGroupImpl());
		categoryType.getAttributeGroup().setAttributeGroupAttributes(categoryTypeAttributes);
		categoryType.setCategoryAttributeGroupAttributes(categoryTypeAttributes);

		categoryImportType.init(categoryType);
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
		categoryImportType.init(null);
	}

	/**
	 * Test initialization with wrong type.
	 */
	@Test(expected = EpDomainException.class)
	public void testInitializationWithWrongType() {
		categoryImportType.init(new Object());
	}

	/**
	 * Test method getRequiredImportFields.
	 */
	@Test
	public void testGetRequiredImportFields() {
		List<ImportField> requiredImportFields = categoryImportType.getRequiredImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertTrue(importField.isRequired());
		}
	}

	/**
	 * Test method getOptionalImportFields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<ImportField> requiredImportFields = categoryImportType.getOptionalImportFields();
		for (final ImportField importField : requiredImportFields) {
			assertFalse(importField.isRequired());
		}
	}

	/**
	 * Test get import field does not exist.
	 */
	@Test
	public void testGetImportFieldDoesNotExist() {
		try {
			categoryImportType.getImportField("NO_EXIST");
			fail("Where's my exception.");
		} catch (EpDomainException expected) {
			assertTrue(expected.getMessage().indexOf("doesn't exist") > -1);
		}
	}

	/**
	 * Test get import field of GUID.
	 */
	@Test
	public void testGetImportFieldOfGuid() {
		final String guid = GUID_ONE;
		category.setGuid(guid);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + CATEGORY_CODE);
		assertEquals(guid, importField.getStringValue(category));

		final String newGuid = GUID_TWO;
		importField.setStringValue(category, newGuid, importGuidHelper);
		assertEquals(newGuid, category.getGuid());
	}

	/**
	 * Test get import field of GUID with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfGuidWithNull() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + CATEGORY_CODE);
		importField.setStringValue(category, null, importGuidHelper);
	}

	/**
	 * Test get import field of GUID with invalid GUID.
	 */
	@Test(expected = EpInvalidGuidBindException.class)
	public void testGetImportFieldOfGuidWithInvalidGuid() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + CATEGORY_CODE);
		importField.setStringValue(category, "asdf-123", importGuidHelper);
	}

	/**
	 * Test get import field of parent category.
	 */
	@Test
	public void testGetImportFieldOfParentCategory() {
		final Category newCategory = createCategory();
		final String newGuid = GUID_TWO;
		newCategory.setGuid(newGuid);

		shouldReturnCategoryFromFindCategoryByGuidAndCatalogGuid(newCategory);
		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).isCategoryGuidExist(with(any(String.class)), with(any(String.class)));
				will(returnValue(true));
			}
		});

		final String guid = GUID_ONE;
		final Category category = createCategory();
		category.setGuid(guid);
		category.setParent(category);

		CatalogImportField importField = (CatalogImportField) categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME
				+ "parentCategoryCode");
		assertTrue(importField.isRequired());
		importField.setCatalog(new CatalogImpl());

		assertEquals(guid, importField.getStringValue(category));

		importField.setStringValue(category, newGuid, importGuidHelper);
		assertEquals(newGuid, importField.getStringValue(category));

		importField.setStringValue(category, null, importGuidHelper);
		importField.checkStringValue(category, null, importGuidHelper);
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(category));
	}

	/**
	 * Test get import field of default category with error.
	 */
	@Test(expected = EpInvalidGuidBindException.class)
	public void testGetImportFieldOfDefaultCategoryWithError() {
		shouldReturnCategoryFromFindCategoryByGuidAndCatalogGuid(null);

		CatalogImportField importField = (CatalogImportField) categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME
				+ "parentCategoryCode");
		importField.setCatalog(new CatalogImpl());

		importField.setStringValue(category, NON_EXISTING_GUID, importGuidHelper);
	}

	/**
	 * Test get import field of start date.
	 */
	@Test
	public void testGetImportFieldOfStartDate() {
		final Date now = new Date();
		category.setStartDate(now);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		assertFalse(importField.isRequired());
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(category));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(category, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(category.getStartDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(category.getStartDate(), DATE_FORMAT, Locale.getDefault()));

		importField.setStringValue(category, GlobalConstants.NULL_VALUE, importGuidHelper);
		assertNotNull(category.getStartDate());
	}

	/**
	 * Test get import field of start date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfStartDateWithError() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "enableDate");
		importField.setStringValue(category, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of end date.
	 */
	@Test
	public void testGetImportFieldOfEndDate() {
		final Date now = new Date();
		category.setEndDate(now);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(category));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(category, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), importGuidHelper);
		assertFalse(now.equals(category.getEndDate()));
		assertEquals(ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()),
				ConverterUtils.date2String(category.getEndDate(), DATE_FORMAT, Locale.getDefault()));

		importField.setStringValue(category, NULL, importGuidHelper);
		assertNull(category.getEndDate());
	}

	/**
	 * Test get import field of end date with error.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfEndDateWithError() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "disableDate");
		importField.setStringValue(category, INVALID_DATE_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of ordering.
	 */
	@Test
	public void testGetImportFieldOfOrdering() {
		final int intValue = 3;
		category.setOrdering(intValue);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "ordering");
		assertEquals(String.valueOf(intValue), importField.getStringValue(category));

		final int newIntValue = Integer.MAX_VALUE;

		importField.setStringValue(category, String.valueOf(newIntValue), importGuidHelper);
		assertFalse(intValue == category.getOrdering());
		assertEquals(newIntValue, category.getOrdering());

		importField.setStringValue(category, NULL, importGuidHelper);
		assertEquals(newIntValue, category.getOrdering());
	}

	/**
	 * Test get import field of ordering with error.
	 */
	@Test(expected = EpIntBindException.class)
	public void testGetImportFieldOfOrderingWithError() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "ordering");
		importField.setStringValue(category, INVALID_INTEGER_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of display name.
	 */
	@Test
	public void testGetImportFieldOfDisplayName() {
		final String value = GUID_ONE;
		final Locale expectedLocale = Locale.getDefault();

		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(expectedLocale);
		ldf.setDisplayName(value);
		category.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "displayName("
				+ Locale.getDefault().toString() + ')');
		assertEquals("Import Fields should be able to read from LocaleDependantFields",
				value, importField.getStringValue(category));

		final String newValue = GUID_TWO;
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals("Import Fields should be able to mutate LocaleDependantFields",
				newValue, category.getLocaleDependantFieldsWithoutFallBack(expectedLocale).getDisplayName());
	}

	/**
	 * Test get import field of display name error too long.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testGetImportFieldOfDisplayNameErrorTooLong() {
		final ImportField importField = categoryImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "displayName("
				+ Locale.getDefault().toString() + ')');

		final StringBuffer sbf = new StringBuffer();
		for (int i = 1; i <= GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1; i++) {
			sbf.append('A');
		}

		importField.setStringValue(category, sbf.toString(), importGuidHelper);
	}

	/**
	 * Test get import field of URL.
	 */
	@Test
	public void testGetImportFieldOfUrl() {
		final String value = GUID_ONE;
		final Locale expectedLocale = Locale.getDefault();

		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(expectedLocale);
		ldf.setUrl(value);
		category.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoUrl("
				+ Locale.getDefault().toString() + ')');
		assertEquals("CategoryImportFields should be able to get url from ldf", value, importField.getStringValue(category));

		final String newValue = GUID_TWO;
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals("CategoryImportFields should be able to set url to ldf",
				newValue, category.getLocaleDependantFieldsWithoutFallBack(expectedLocale).getUrl());
	}

	/**
	 * Test setting url with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingUrlWithTestExceedingMaxLength() {
		final Locale expectedLocale = Locale.getDefault();
		final String invalidDescriptionText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoUrl("
				+ expectedLocale + ')');
		importField.setStringValue(category, invalidDescriptionText, importGuidHelper);
	}

	/**
	 * Test get import field of title.
	 */
	@Test
	public void testGetImportFieldOfTitle() {
		final String value = GUID_ONE;
		final Locale expectedLocale = Locale.getDefault();

		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(expectedLocale);
		ldf.setTitle(value);
		category.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoTitle("
				+ Locale.getDefault().toString() + ')');
		assertEquals("Category Import Fields should be able to read title from ldf",
				value, importField.getStringValue(category));

		final String newValue = GUID_TWO;
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals("Category Import Fields should be able to mutate title in ldf",
				newValue, category.getLocaleDependantFieldsWithoutFallBack(expectedLocale).getTitle());
	}

	/**
	 * Test setting title with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingTitleWithTestExceedingMaxLength() {
		final Locale expectedLocale = Locale.getDefault();
		final String invalidDescriptionText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoTitle("
				+ expectedLocale + ')');
		importField.setStringValue(category, invalidDescriptionText, importGuidHelper);
	}

	/**
	 * Test get import field of key words.
	 */
	@Test
	public void testGetImportFieldOfKeyWords() {
		final String value = GUID_ONE;
		final Locale expectedLocale = Locale.getDefault();

		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(expectedLocale);
		ldf.setKeyWords(value);
		category.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoKeyWords("
				+ Locale.getDefault().toString() + ')');
		assertEquals("Category Import Fields should be able to read keyWords from ldf",
				value, importField.getStringValue(category));

		final String newValue = GUID_TWO;
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals("Category Import Fields should be able to set keyWords to ldf",
				newValue, category.getLocaleDependantFieldsWithoutFallBack(expectedLocale).getKeyWords());
	}

	/**
	 * Test setting keywords with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingKeywordsWithTestExceedingMaxLength() {
		final Locale expectedLocale = Locale.getDefault();
		final String invalidDescriptionText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoKeyWords("
				+ expectedLocale + ')');
		importField.setStringValue(category, invalidDescriptionText, importGuidHelper);
	}

	/**
	 * Test get import field of description.
	 */
	@Test
	public void testGetImportFieldOfDescription() {
		final String value = GUID_ONE;
		final Locale expectedLocale = Locale.getDefault();

		LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(expectedLocale);
		ldf.setDescription(value);
		category.addOrUpdateLocaleDependantFields(ldf);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoDescription("
				+ Locale.getDefault().toString() + ')');
		assertEquals("Category ImportField should be able to get description from ldf",
				value, importField.getStringValue(category));

		final String newValue = GUID_TWO;
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals("Category ImportField should be able to set description on ldf",
				newValue, category.getLocaleDependantFieldsWithoutFallBack(expectedLocale).getDescription());
	}

	/**
	 * Test setting description with text exceeding maximum length.
	 */
	@Test(expected = EpTooLongBindException.class)
	public void testSettingDescriptionWithTestExceedingMaxLength() {
		final Locale expectedLocale = Locale.getDefault();
		final String invalidDescriptionText = StringUtils.repeat("A", GlobalConstants.SHORT_TEXT_MAX_LENGTH + 1);
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + "seoDescription("
				+ expectedLocale + ')');
		importField.setStringValue(category, invalidDescriptionText, importGuidHelper);
	}

	/**
	 * Test get import field of size.
	 */
	@Test
	public void testGetImportFieldOfSize() {
		final Integer size = Integer.valueOf(3);

		category.getAttributeValueGroup().setAttributeValue(attributeSize, null, size);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_CATEGORY_SIZE);
		assertEquals(size.toString(), importField.getStringValue(category));

		final String newSize = "5";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(category, newSize, importGuidHelper);
		assertEquals(newSize, category.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_CATEGORY_SIZE, null));
	}

	/**
	 * Test get import field of size with error.
	 */
	@Test(expected = EpIntBindException.class)
	public void testGetImportFieldOfSizeWithError() {
		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + ATTRIBUTE_KEY_CATEGORY_SIZE);
		importField.setStringValue(category, INVALID_DECIMAL_STRING, importGuidHelper);
	}

	/**
	 * Test get import field of small image.
	 */
	@Test
	public void testGetImportFieldOfSmallImage() {
		final String imageSmall = "aaa.jpg";
		final Locale expectedLocale = Locale.getDefault();

		category.getAttributeValueGroup().setAttributeValue(attributeImageSmall, expectedLocale, imageSmall);

		ImportField importField = categoryImportType.getImportField(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME
				+ ATTRIBUTE_KEY_CATEGORY_IMAGE_SMALL + '(' + expectedLocale.toString() + ')');
		assertEquals(imageSmall, importField.getStringValue(category));

		final String newImageSmall = "bbb.jpg";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(category, newImageSmall, importGuidHelper);
		assertEquals(newImageSmall, category.getAttributeValueGroup().getStringAttributeValue(ATTRIBUTE_KEY_CATEGORY_IMAGE_SMALL, expectedLocale));
	}

	/**
	 * Test get name.
	 */
	@Test
	public void testGetName() {
		assertEquals(ImportDataTypeCategoryImpl.PREFIX_OF_IMPORT_DATA_TYPE_NAME + ImportDataType.SEPARATOR + CATEGORY_TYPE_NAME,
				categoryImportType.getName());
	}

	/**
	 * Test get GUID field name.
	 */
	@Test
	public void testGetGuidFieldName() {
		final String guid = GUID_ONE;
		category.setGuid(guid);
		assertEquals(ImportDataTypeCategoryImpl.PREFIX_OF_FIELD_NAME + CATEGORY_CODE, categoryImportType.getGuidFieldName());
	}

	/**
	 * Test get import field of hidden.
	 */
	@Test
	public void testGetImportFieldOfHidden() {
		final String value = "false";

		ImportField importField = categoryImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME + "storeVisible");
		assertFalse(importField.isRequired());
		assertEquals(value, importField.getStringValue(category));

		final String newValue = "true";
		importField.setStringValue(category, newValue, importGuidHelper);
		assertEquals(newValue, String.valueOf(category.isHidden()));
	}

	private void shouldReturnCategoryFromFindCategoryByGuidAndCatalogGuid(final Category category) {
		context.checking(new Expectations() {
			{
				allowing(importGuidHelper).findCategoryByGuidAndCatalogGuid(with(any(String.class)),
						with((String) null),
						with(any(boolean.class)),
						with(any(boolean.class)));
				will(returnValue(category));
			}
		});
	}

	private Category createCategory() {
		final Category category = new CategoryImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		category.initialize();
		Catalog catalog = new CatalogImpl();
		catalog.setCode(CATALOG_CODE);
		category.setCatalog(catalog);
		return category;
	}

	private void setupAttributeImageSmall() {
		attributeImageSmall = createAttribute(AttributeType.IMAGE, ATTRIBUTE_KEY_CATEGORY_IMAGE_SMALL, true);
		addAttributeToAttributeGroupAttributes(attributeImageSmall, categoryTypeAttributes);
		createProductAttributeValue(attributeImageSmall, AttributeType.IMAGE, null);
	}

	private void setupAttributeSize() {
		attributeSize = createAttribute(AttributeType.INTEGER, ATTRIBUTE_KEY_CATEGORY_SIZE, false);
		addAttributeToAttributeGroupAttributes(attributeSize, categoryTypeAttributes);
		AttributeValue attributeValue = createProductAttributeValue(attributeSize, AttributeType.INTEGER, Integer.valueOf(1));
		attributeValueMap.put(ATTRIBUTE_KEY_CATEGORY_SIZE, attributeValue);
	}

	private AttributeValue createProductAttributeValue(final Attribute attribute, final AttributeType attributeType, final Object value) {
		AttributeValue attributeValue = new ProductAttributeValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attributeType);
		attributeValue.setValue(value);
		return attributeValue;
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
		final AttributeGroupAttribute categoryTypeAttribute = new AttributeGroupAttributeImpl();
		categoryTypeAttribute.setAttribute(attribute);
		attributeGroupAttributes.add(categoryTypeAttribute);
	}
}
