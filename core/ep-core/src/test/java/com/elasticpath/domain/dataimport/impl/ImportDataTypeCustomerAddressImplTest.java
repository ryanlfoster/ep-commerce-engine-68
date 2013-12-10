package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBooleanBindException;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>ImportDataTypeCustomerImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class ImportDataTypeCustomerAddressImplTest extends AbstractEPTestCase {
	private static final String EP_BIND_EXCEPTION_EXPECTED = "EpBindExceptionFieldValue expected.";

	private ImportDataTypeCustomerAddressImpl customerAddressImportType;

	private CustomerAddress customerAddressImpl;

	private ImportGuidHelper mockImportGuidHelper;

	private ImportGuidHelper importGuidHelper;

	private static final String AAA = "aaa";

	private static final String NON_ALPHANUMERIC = "This.Is.A.Test;";

	private static final String TRUE = "true";
	private static final String FALSE = "false";

	//Required Import Fields
	private static final String IF_GUID = "guid";
	private static final String IF_CUSTOMERGUID = "customerGuid";
	//Optional Import Fields
	private static final String IF_FIRSTNAME = "firstName";
	private static final String IF_LASTNAME = "lastName";
	private static final String IF_PHONENUMBER = "phoneNumber";
	private static final String IF_FAXNUMBER = "faxNumber";
	private static final String IF_STREET1 = "street1";
	private static final String IF_STREET2 = "street2";
	private static final String IF_CITY = "city";
	private static final String IF_SUBCOUNTRY = "subCountry";
	private static final String IF_ZIPPOSTALCODE = "zipOrPostalCode";
	private static final String IF_COUNTRY = "country";
	private static final String IF_COMMERCIALFLAG = "commercialAddress";


	private static final String[] REQUIRED_FIELDS = new String[] { IF_GUID, IF_CUSTOMERGUID };

	private static final String[] OPTIONAL_FIELDS =
		new String[] { IF_FIRSTNAME, IF_LASTNAME, IF_PHONENUMBER, IF_FAXNUMBER, IF_STREET1, IF_STREET2,
		IF_CITY, IF_SUBCOUNTRY, IF_ZIPPOSTALCODE, IF_COUNTRY, IF_COMMERCIALFLAG };


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.UTILITY, UtilityImpl.class);

		this.customerAddressImportType = new ImportDataTypeCustomerAddressImpl();

		// Setup ImportService.
		setupImportService();

		customerAddressImportType.init(null);

		customerAddressImpl = getCustomerAddress();

	}

	private void setupImportService() {
		this.mockImportGuidHelper = context.mock(ImportGuidHelper.class);
		this.importGuidHelper = this.mockImportGuidHelper;
	}

	/**
	 * Test method init().
	 */
	@Test
	public void testInitError() {
		this.customerAddressImportType.init(null);

		try {
			this.customerAddressImportType.init(new Object());
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
		List<String> requiredFields = Arrays.asList(REQUIRED_FIELDS);
		List<ImportField> retrievedRequiredImportFields = this.customerAddressImportType.getRequiredImportFields();
		assertEquals(REQUIRED_FIELDS.length, retrievedRequiredImportFields.size());

		for (ImportField importField : retrievedRequiredImportFields) {
			assertTrue(importField.isRequired());
			assertTrue(importField.getName() + " is not in the list of required fields", requiredFields.contains(importField.getName()));
		}
	}

	/**
	 * Test method getOptionalImportFields.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<String> optionalFields = Arrays.asList(OPTIONAL_FIELDS);
		List<ImportField> retrievedOptionalImportFields = this.customerAddressImportType.getOptionalImportFields();
		assertEquals(OPTIONAL_FIELDS.length, retrievedOptionalImportFields.size());

		for (ImportField importField : retrievedOptionalImportFields) {
			assertFalse(importField.isRequired());
			assertTrue(importField.getName() + " should not be an optional field", optionalFields.contains(importField.getName()));
		}
	}

	//
	// TEST INDIVIDUAL IMPORT FIELDS - REQUIRED FIELDS
	//

	// guid

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCustomerAddressGuid() {
		//Make sure the import field has a guid to begin with
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IF_GUID);
		assertNotNull(importField.getStringValue(customerAddressImpl));

		//Make sure the import field guid can be set
		importField.setStringValue(customerAddressImpl, AAA, importGuidHelper);
		assertEquals(AAA, customerAddressImpl.getGuid());
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCustomerAddressGuidFailNull() {
		this.testRequiredFieldNull(IF_GUID);
	}

	// CustomerGuid

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCustomerGuid() {
		//Mock the customer query so we get what appears to be a valid guid for the customer
		context.checking(new Expectations() {
			{
				allowing(mockImportGuidHelper).isCustomerGuidExist(with(any(String.class)));
				will(returnValue(Boolean.TRUE));
			}
		});
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + IF_GUID);
		//if no exceptions then assume it succeeded
		importField.setStringValue(customerAddressImpl, AAA, importGuidHelper);
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCustomerGuidFailNull() {
		this.testRequiredFieldNull(IF_GUID);
	}

	//
	// TEST INDIVIDUAL IMPORT FIELDS - OPTIONAL FIELDS
	//

	//FirstName

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfFirstNameNonNull() {
		this.testOptionalImportFieldNonNull(IF_FIRSTNAME, "John");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfFirstNameNull() {
		this.testOptionalImportFieldNull(IF_FIRSTNAME);
	}

//	/**
//	 * Test method getImportField.
//	 */
//	public void testGetImportFieldOfFirstNameFailAlphanumeric() {
//		this.testAlphanumericFail(IF_FIRSTNAME);
//	}

	//LastName

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfLastNameNonNull() {
		this.testOptionalImportFieldNonNull(IF_LASTNAME, "Smith");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfLastNameNull() {
		this.testOptionalImportFieldNull(IF_LASTNAME);
	}

//	/**
//	 * Test method getImportField.
//	 */
//	public void testGetImportFieldOfLastNameFailAlphanumeric() {
//		this.testAlphanumericFail(IF_LASTNAME);
//	}

	//phoneNumber

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfPhoneNumberNonNull() {
		this.testOptionalImportFieldNonNull(IF_PHONENUMBER, "6045555555");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfPhoneNumberNull() {
		this.testOptionalImportFieldNull(IF_PHONENUMBER);
	}

	//faxNumber

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfFaxNumberNonNull() {
		this.testOptionalImportFieldNonNull(IF_FAXNUMBER, "6045555555");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfFaxNumberNull() {
		this.testOptionalImportFieldNull(IF_FAXNUMBER);
	}

	//street1

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStreet1NonNull() {
		this.testOptionalImportFieldNonNull(IF_STREET1, "Elastic Path Drive");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStreet1Null() {
		this.testOptionalImportFieldNull(IF_STREET1);
	}

	//street2

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStreet2NonNull() {
		this.testOptionalImportFieldNonNull(IF_STREET2, "Elastic path Drive");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfStreet2Null() {
		this.testOptionalImportFieldNull(IF_STREET2);
	}

	//city

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCityNonNull() {
		this.testOptionalImportFieldNonNull(IF_CITY, "My City");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCityNull() {
		this.testOptionalImportFieldNull(IF_CITY);
	}

	//subCountry

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfSubCountryNonNull() {
		setupGeography();

		ImportField importField = this.customerAddressImportType.getImportField(
				ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + IF_SUBCOUNTRY);
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customerAddressImpl));
		importField.setStringValue(customerAddressImpl, "BC", importGuidHelper);
		assertEquals(IF_SUBCOUNTRY, "BC", importField.getStringValue(customerAddressImpl));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfSubCountryNull() {
		this.testOptionalImportFieldNull(IF_SUBCOUNTRY);
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfSubCountryFailAlphanumeric() {
		this.testAlphanumericFail(IF_SUBCOUNTRY);
	}

	//zipOrPostalCode

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfZipOrPostalCodeNonNull() {
		this.testOptionalImportFieldNonNull(IF_ZIPPOSTALCODE, "V2N3W4");
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfZipOrPostalCodeNull() {
		this.testOptionalImportFieldNull(IF_ZIPPOSTALCODE);
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfZipOrPostalCodeFailAlphanumeric() {
		this.testAlphanumericFail(IF_ZIPPOSTALCODE);
	}

	//country

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCountryNonNull() {
		setupGeography();

		ImportField importField = this.customerAddressImportType.getImportField(
				ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + IF_COUNTRY);
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customerAddressImpl));
		importField.setStringValue(customerAddressImpl, "US", importGuidHelper);
		assertEquals(IF_SUBCOUNTRY, "US", importField.getStringValue(customerAddressImpl));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCountryNull() {
		this.testOptionalImportFieldNull(IF_COUNTRY);
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCountryFailAlphanumeric() {
		this.testAlphanumericFail(IF_COUNTRY);
	}

	//country

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCommercialAddressNonNull() {
		ImportField importField = this.customerAddressImportType.getImportField(
				ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + IF_COMMERCIALFLAG);
		//Check that the importField initially returns a value of false
		assertEquals(FALSE, importField.getStringValue(customerAddressImpl));
		//Set the importField's String parameter to null and verify that it doesn't complain, leaves it at default
		importField.setStringValue(customerAddressImpl, TRUE, importGuidHelper);
		assertEquals(IF_COMMERCIALFLAG,
				TRUE, importField.getStringValue(customerAddressImpl));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCommercialAddressNull() {
		ImportField importField = this.customerAddressImportType.getImportField(
				ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + IF_COMMERCIALFLAG);
		//Check that the importField initially returns a value of false
		assertEquals(FALSE, importField.getStringValue(customerAddressImpl));
		//Set the importField's String parameter to null and verify that it doesn't complain, leaves it at default
		importField.setStringValue(customerAddressImpl, null, importGuidHelper);
		assertEquals(IF_COMMERCIALFLAG,
				FALSE, importField.getStringValue(customerAddressImpl));
	}

	/**
	 * Test method getImportField.
	 */
	@Test
	public void testGetImportFieldOfCommercialAddressAlphanumeric() {
		try {
			this.testAlphanumericFail(IF_COMMERCIALFLAG);
			fail("expected boolean bind exception here - invalid boolean type.");
		} catch (EpBooleanBindException e) {
			assertNotNull(e);
		}
	}

	//
	// TEST REMAINING METHODS
	//

	/**
	 * Test method isEntityImport().
	 */
	@Test
	public void testIsEntityImport() {
		assertFalse(this.customerAddressImportType.isEntityImport());
		assertTrue(this.customerAddressImportType.isValueObjectImport());
	}

	/**
	 * Test method createValueObject().
	 */
	@Test
	public void testCreateValueObject() {
		stubGetBean(ContextIdNames.CUSTOMER_ADDRESS, CustomerAddressImpl.class);
		CustomerAddress customerAddress = (CustomerAddress) this.customerAddressImportType.createValueObject();
		assertNotNull(customerAddress);
	}

	/**
	 * Test method getImportJobRunnerBeanName().
	 */
	@Test
	public void testGetImportJobRunnerBeanName() {
		assertSame("importJobRunnerCustomer", customerAddressImportType.getImportJobRunnerBeanName());
	}

	/**
	 * Tests an optional ImportField for success when provided with a null value.
	 * @param fieldName
	 */
	private void testOptionalImportFieldNull(final String fieldName) {
		//get the import field object
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + fieldName);
		//Check that the importField initially returns a value of "null"
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customerAddressImpl));
		//Set the importField's String parameter to null and verify that it doesn't complain
		importField.setStringValue(customerAddressImpl, null, importGuidHelper);
		assertEquals(fieldName + " was set to null but is not returning null.",
				GlobalConstants.NULL_VALUE, importField.getStringValue(customerAddressImpl));
	}
	/**
	 * Tests an optional ImportField for success when provided with a valid String.
	 * @param fieldName
	 */
	private void testOptionalImportFieldNonNull(final String fieldName, final String argument) {
		//get the import field object
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + fieldName);
		//Check that the importField initially returns a value of "null"
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customerAddressImpl));
		//Set the importField's String parameter and verify that it's returns the value that's been set
		importField.setStringValue(customerAddressImpl, argument, importGuidHelper);
		assertEquals(fieldName + " was not set or retrieved properly.", argument, importField.getStringValue(customerAddressImpl));
	}

	/**
	 * Tests a required ImportField for failure when provided with a null string.
	 * @param fieldName
	 */
	private void testRequiredFieldNull(final String fieldName) {
		//get the appropriate import field object
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + fieldName);
		//Set the importField's userId to null and verify that it complains
		try {
			importField.setStringValue(customerAddressImpl, GlobalConstants.NULL_VALUE, importGuidHelper);
			fail("EpNonNullBindException expected");
		} catch (EpNonNullBindException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Tests a required ImportField for failure when provided with a non-alphanumeric-only string.
	 * @param fieldName
	 */
	private void testAlphanumericFail(final String fieldName) {
		// get the appropriate import field object
		ImportField importField = this.customerAddressImportType.getImportField(ImportDataTypeCustomerAddressImpl.PREFIX_OF_FIELD_NAME + fieldName);
		//Set the importField's userId to non-alphanumeric string and verify that it complains
		try {
			importField.setStringValue(customerAddressImpl, NON_ALPHANUMERIC, importGuidHelper);
			fail(EP_BIND_EXCEPTION_EXPECTED);
		} catch (EpInvalidValueBindException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Returns a new <code>CustomerAddress</code> instance.
	 *
	 * @return a new <code>CustomerAddress</code> instance.
	 */
	protected CustomerAddress getCustomerAddress() {
		final CustomerAddressImpl customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid((new RandomGuidImpl()).toString());
		customerAddress.initialize();
		return customerAddress;
	}

	private void setupGeography() {
		final Map<String, String> countryMap = new HashMap<String, String>();
		countryMap.put("US", "United States");

		final Map<String, String> subCountryMap = new HashMap<String, String>();
		subCountryMap.put("BC", "BritishColumbia");

		final Geography geographyMock = context.mock(Geography.class);
		context.checking(new Expectations() {
			{
				allowing(geographyMock).getCountryCodes();
				will(returnValue(countryMap.keySet()));

				allowing(geographyMock).getSubCountryCodes("US");
				will(returnValue(subCountryMap.keySet()));
			}
		});
		stubGetBean(ContextIdNames.GEOGRAPHY, geographyMock);
	}
}
