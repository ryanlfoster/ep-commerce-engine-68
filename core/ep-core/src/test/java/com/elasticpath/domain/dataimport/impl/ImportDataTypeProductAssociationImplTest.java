/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Locale;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.exception.EpInvalidGuidBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>ImportDataTypeProductAssociationImpl</code>.
 */
public class ImportDataTypeProductAssociationImplTest extends AbstractEPTestCase {

	private static final String NEW_INT_STRING = "456";

	private static final String INT_STRING = "1";

	private static final String EP_BIND_EXCEPTION_EXPECTED = "EpBindExceptionFieldValue expected.";

	private static final String EP_BIND_EXCEPTION_NON_NULL_EXPECTED = "EpBindExceptionNonNull expected.";

	private static final String BBB = "bbb";

	private static final String AAA = "aaa";

	private static final String NON_EXISTING_GUID = "NON_EXISTING_GUID";

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	private ImportDataTypeProductAssociationImpl productAssociationImportType;

	private ProductAssociation productAssociation;

	private ImportGuidHelper mockImportGuidHelper;

	private static final String INVALID_DATE_STRING = "asdf";

	private final Utility utility = new UtilityImpl() {
		private static final long serialVersionUID = -7987860231219963904L;

		/**
		 * Override for test.
		 * @return pattern
		 */
		@Override
		protected String getDefaultDateFormatPattern() {
			return DATE_FORMAT;
		}
	};



	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.PRODUCT_ASSOCIATION, ProductAssociationImpl.class);
		stubGetBean(ContextIdNames.UTILITY, utility);

		this.productAssociationImportType = new ImportDataTypeProductAssociationImpl();

		productAssociationImportType.init(null);

		// Setup ImportService.
		setupImportService();

		setupProductAssociation();
	}

	private void setupProductAssociation() {
		this.productAssociation = getBeanFactory().getBean(ContextIdNames.PRODUCT_ASSOCIATION);
	}

	private void setupImportService() {
		this.mockImportGuidHelper = context.mock(ImportGuidHelper.class);

	}

	/**
	 * Test method init().
	 */
	@Test
	public void testInitError() {
		this.productAssociationImportType.init(null);

		try {
			this.productAssociationImportType.init(new Object());
			fail("EpDomainException expected.");
		} catch (final EpDomainException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method getRequiredImportFields.
	 */
	@Test
	public void testGetRequiredImportFields() {
		for (ImportField importField : this.productAssociationImportType.getRequiredImportFields()) {
			assertTrue(importField.isRequired());
		}
	}

	/**
	 * Test method getOptionalImportFields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		for (ImportField importField : this.productAssociationImportType.getOptionalImportFields()) {
			assertFalse(importField.isRequired());
		}
	}

	/**
	 * Test setting the source product code succeeds.
	 */
	@Test
	public void testGetImportFieldOfSourceProduct() {

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "sourceProductCode");
		assertTrue(importField.isRequired());

		// No error if the given guid is a valid one.
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).isProductGuidExist("validGuid");
				will(returnValue(true));
			}
		});
		final Product newProduct = new ProductImpl();
		final String newGuid = BBB;
		newProduct.setGuid(newGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findProductByGuid(
						with(any(String.class)), with(any(boolean.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(newProduct));
			}
		});

		importField.setStringValue(productAssociation, "validGuid", mockImportGuidHelper);

		// Exception expected if the given guid is an invalid one.
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).isProductGuidExist("invalidGuid");
				will(returnValue(false));
			}
		});
		try {
			importField.setStringValue(productAssociation, "invalidGuid", mockImportGuidHelper);
			fail("EpInvalidGuidBindException expected.");
		} catch (EpInvalidGuidBindException e) {
			// succeed!
			assertNotNull(e);
		}

		// The getStringValue() method is not supported.
		try {
			importField.getStringValue(productAssociation);
			fail("EpUnsupportedOperationException expected.");
		} catch (EpUnsupportedOperationException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test setting the source product code to null fails.
	 */
	@Test
	public void testGetImportFieldOfSourceProductWithError() {
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "sourceProductCode");

		try {
			importField.setStringValue(productAssociation, null, mockImportGuidHelper);
			fail(EP_BIND_EXCEPTION_NON_NULL_EXPECTED);
		} catch (EpNonNullBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test setting the target product code succeeds.
	 */
	@Test
	public void testGetImportFieldOfTargetProduct() {
		final String guid = AAA;
		final Product product = new ProductImpl();
		product.setGuid(guid);
		productAssociation.setTargetProduct(product);

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "targetProductCode");
		assertTrue(importField.isRequired());

		assertEquals(guid, importField.getStringValue(productAssociation));

		final String newGuid = BBB;
		final Product newProduct = new ProductImpl();
		newProduct.setGuid(newGuid);
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findProductByGuid(
						with(any(String.class)), with(any(boolean.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(newProduct));
			}
		});
		importField.setStringValue(productAssociation, newGuid, mockImportGuidHelper);
		assertEquals(newGuid, importField.getStringValue(productAssociation));
	}

	/**
	 * Test settings the target product code to null fails.
	 */
	@Test
	public void testGetImportFieldOfTargetProductWithError() {
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "targetProductCode");

		try {
			importField.setStringValue(productAssociation, null, mockImportGuidHelper);
			fail(EP_BIND_EXCEPTION_NON_NULL_EXPECTED);
		} catch (EpNonNullBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).findProductByGuid(
						with(any(String.class)), with(any(boolean.class)), with(any(boolean.class)), with(any(boolean.class)));
				will(returnValue(null));
			}
		});
			importField.setStringValue(productAssociation, NON_EXISTING_GUID, mockImportGuidHelper);
			fail("EpBindExceptionInvalidGuid expected.");
		} catch (EpInvalidGuidBindException e) {
			// succeed!
			assertNotNull(e);
		}

	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStartDate() {
		final Date now = new Date();
		productAssociation.setStartDate(now);
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "enableDate");
		assertFalse(importField.isRequired());
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(productAssociation));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(productAssociation, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), mockImportGuidHelper);
		assertFalse(now.equals(productAssociation.getStartDate()));
		assertEquals(afterNow.toString(), productAssociation.getStartDate().toString());

		// Start date can be set to null, which will use the current time instead.
		importField.setStringValue(productAssociation, null, mockImportGuidHelper);
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStartDateWithError() {
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "enableDate");
		try {
			importField.setStringValue(productAssociation, INVALID_DATE_STRING, mockImportGuidHelper);
			fail(EP_BIND_EXCEPTION_EXPECTED);
		} catch (EpDateBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfEndDate() {
		final Date now = new Date();
		productAssociation.setEndDate(now);
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "disableDate");
		assertFalse(importField.isRequired());
		assertEquals(ConverterUtils.date2String(now, DATE_FORMAT, Locale.getDefault()), importField.getStringValue(productAssociation));

		final Date afterNow = new Date();
		final int timeFrame = 99000;
		afterNow.setTime(now.getTime() + timeFrame);

		importField.setStringValue(productAssociation, ConverterUtils.date2String(afterNow, DATE_FORMAT, Locale.getDefault()), mockImportGuidHelper);
		assertFalse(now.equals(productAssociation.getEndDate()));
		assertEquals(afterNow.toString(), productAssociation.getEndDate().toString());

		// set it to null
		importField.setStringValue(productAssociation, "null", mockImportGuidHelper);
		assertNull(productAssociation.getEndDate());
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfEndDateWithError() {
		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME
				+ "disableDate");
		try {
			importField.setStringValue(productAssociation, INVALID_DATE_STRING, mockImportGuidHelper);
			fail(EP_BIND_EXCEPTION_EXPECTED);
		} catch (EpDateBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method getName.
	 */
	@Test
	public void testGetName() {
		assertEquals(ImportDataTypeProductAssociationImpl.PREFIX_OF_IMPORT_DATA_TYPE_NAME, this.productAssociationImportType.getName());
	}

	/**
	 * Test method getGuidFieldName.
	 */
	@Test
	public void testGetGuidFieldName() {
		assertEquals(ImportDataTypeProductAssociationImpl.PREFIX_OF_FIELD_NAME + "sourceProductCode", this.productAssociationImportType
				.getGuidFieldName());
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfSourceProductDependant() {
		final String value = "false";

		this.productAssociation.setSourceProductDependent(Boolean.parseBoolean(value));

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME
				+ "sourceProductDependant");
		assertFalse(importField.isRequired());
		assertEquals(value, importField.getStringValue(productAssociation));

		final String newValue = "true";
		importField.setStringValue(productAssociation, newValue, mockImportGuidHelper);
		assertEquals(newValue, String.valueOf(productAssociation.isSourceProductDependent()));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfDefaultQuantity() {
		final String value = INT_STRING;

		this.productAssociation.setDefaultQuantity(Integer.parseInt(value));

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME
				+ "defaultQuantity");
		assertFalse(importField.isRequired());
		assertEquals(value, importField.getStringValue(productAssociation));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productAssociation, newValue, mockImportGuidHelper);
		assertEquals(newValue, String.valueOf(productAssociation.getDefaultQuantity()));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfOrdering() {
		final String value = INT_STRING;

		this.productAssociation.setOrdering(Integer.parseInt(value));

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME + "ordering");
		assertFalse(importField.isRequired());
		assertEquals(value, importField.getStringValue(productAssociation));

		final String newValue = NEW_INT_STRING;
		importField.setStringValue(productAssociation, newValue, mockImportGuidHelper);
		assertEquals(newValue, String.valueOf(productAssociation.getOrdering()));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfAssociationType() {
		final String value = String.valueOf(ProductAssociation.ACCESSORY);

		this.productAssociation.setAssociationType(Integer.parseInt(value));

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME
				+ "associationType");
		assertTrue(importField.isRequired());
		assertEquals(value, importField.getStringValue(productAssociation));

		final String newValue = String.valueOf(ProductAssociation.CROSS_SELL);
		importField.setStringValue(productAssociation, newValue, mockImportGuidHelper);
		assertEquals(newValue, String.valueOf(productAssociation.getAssociationType()));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfAssociationTypeWithError() {
		final String value = String.valueOf(ProductAssociation.ACCESSORY);
		this.productAssociation.setAssociationType(Integer.parseInt(value));

		ImportField importField = this.productAssociationImportType.getImportField(ImportDataTypeProductSkuImpl.PREFIX_OF_FIELD_NAME
				+ "associationType");

		try {
			final String newValue = String.valueOf(Integer.MAX_VALUE);
			importField.setStringValue(productAssociation, newValue, mockImportGuidHelper);
			fail("EpInvalidValueBindException expected.");
		} catch (EpInvalidValueBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method createValueObject().
	 */
	@Test
	public void testCreateValueObject() {
		try {
			this.productAssociationImportType.createValueObject();
			fail("createValueObject is not supported because ProductAssociation is not a value object");
		} catch (UnsupportedOperationException ex) {
			assertNotNull(ex);
		}
	}

	/**
	 * Test that calling saveOrUpdate() is not supported.
	 */
	@Test
	public void testSaveOrUpdateValueObject() {
		try {
			this.productAssociationImportType.saveOrUpdate(null, null);
			fail("saveOrUpdate is not supported because ProductAssociation is not a value object");
		} catch (UnsupportedOperationException ex) {
			assertNotNull(ex);
		}
	}

	/**
	 * Test that calling clearValueObjects() is not supported because ProductAssociation has no value objects.
	 */
	@Test
	public void testClearValueObjects() {
		try {
			this.productAssociationImportType.clearValueObjects(null);
			fail("clearValueObjects is not supported because ProductAssociation has no value objects");
		} catch (UnsupportedOperationException ex) {
			assertNotNull(ex);
		}
	}
}
