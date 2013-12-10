package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAuthentication;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestCustomerProfileFactory;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Test <code>ImportDataTypeCustomerImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class ImportDataTypeCustomerImplTest {
	private static final String TEST_GUID = "aaa";

	private static final String NON_ALPHANUMERIC = "This.Is.A.Test;";

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	// Required Import Fields
	private static final String IMPORT_FIELD_GUID = "guid";

	private static final String IMPORT_FIELD_USERID = "userId";

	// Optional Import Fields
	private static final String IMPORT_FIELD_PASSWORD = "password";

	private static final String IMPORT_FIELD_STATUS = "status";

	private static final String IMPORT_FIELD_CREATIONDATE = "creationDate";

	private static final String[] REQUIRED_FIELDS = new String[] { IMPORT_FIELD_GUID, IMPORT_FIELD_USERID, CustomerImpl.ATT_KEY_CP_ANONYMOUS_CUST,
			CustomerImpl.ATT_KEY_CP_EMAIL, CustomerImpl.ATT_KEY_CP_FIRST_NAME, CustomerImpl.ATT_KEY_CP_LAST_NAME,
			CustomerImpl.ATT_KEY_CP_HTML_EMAIL };

	private static final String[] OPTIONAL_FIELDS = new String[] { IMPORT_FIELD_STATUS, IMPORT_FIELD_PASSWORD, IMPORT_FIELD_CREATIONDATE,
			CustomerImpl.ATT_KEY_CP_COMPANY, CustomerImpl.ATT_KEY_CP_DOB, CustomerImpl.ATT_KEY_CP_FAX, CustomerImpl.ATT_KEY_CP_GENDER,
			CustomerImpl.ATT_KEY_CP_PHONE, CustomerImpl.ATT_KEY_CP_PREF_CURR, CustomerImpl.ATT_KEY_CP_PREF_LOCALE,
			CustomerImpl.ATT_KEY_CP_BE_NOTIFIED };

	private ImportDataTypeCustomerImpl customerImportType;

	private Customer customer;

	private ImportGuidHelper importGuidHelper;

	private String validDateString;

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

	private AttributeService attributeService;

	private ValidatorUtils validatorUtils;

	private CustomerAuthentication customerAuthentication;

	private BeanFactoryExpectationsFactory expectationsFactory;

	/** Test initialization. */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);

		attributeService = context.mock(AttributeService.class);
		validatorUtils = context.mock(ValidatorUtils.class);
		customerAuthentication = context.mock(CustomerAuthentication.class);

		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.VALIDATOR_UTILS, validatorUtils);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, utility);

		context.checking(new Expectations() {
			{
				allowing(attributeService).getCustomerProfileAttributesMap();
				will(returnValue(new TestCustomerProfileFactory().getProfile()));
			}
		});

		customerImportType = new ImportDataTypeCustomerImpl();

		importGuidHelper = context.mock(ImportGuidHelper.class);

		customerImportType.init(null);

		customer = createCustomer();

		validDateString = ConverterUtils.date2String(new Date(), DATE_FORMAT, Locale.getDefault());
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Assert that {@link ImportDataTypeCustomerImpl#init(Object) will be successful if given a null argument.
	 */
	@Test
	public void testSuccessfulInit() {
		customerImportType.init(null);
	}

	/**
	 * Assert that {@link ImportDataTypeCustomerImpl#init(Object) will fail if not given null argument.
	 */
	@Test(expected = EpDomainException.class)
	public void testUnsuccessfulInit() {
		customerImportType.init(new Object());
	}

	/**
	 * Assert the successful path on {@link ImportDataTypeCustomerImpl#getRequiredImportFields()}.
	 */
	@Test
	public void testGetRequiredImportFields() {
		List<String> requiredFields = Arrays.asList(REQUIRED_FIELDS);
		List<ImportField> retrievedRequiredImportFields = customerImportType.getRequiredImportFields();
		assertEquals(REQUIRED_FIELDS.length, retrievedRequiredImportFields.size());

		for (ImportField importField : retrievedRequiredImportFields) {
			assertTrue(importField.isRequired());
			assertTrue(requiredFields.contains(importField.getName()));
		}
	}

	/**
	 * Assert the successful path on {@link ImportDataTypeCustomerImpl#getOptionalImportFields()}.
	 */
	@Test
	public void testGetOptionalImportFields() {
		List<String> optionalFields = Arrays.asList(OPTIONAL_FIELDS);
		List<ImportField> retrievedOptionalImportFields = customerImportType.getOptionalImportFields();
		assertEquals(OPTIONAL_FIELDS.length, retrievedOptionalImportFields.size());

		for (ImportField importField : retrievedOptionalImportFields) {
			assertFalse(importField.isRequired());
			assertTrue(importField.getName() + " should not be an optional field", optionalFields.contains(importField.getName()));
		}
	}

	/**
	 * Assert the successful path on {@link ImportDataTypeCustomerImpl#getImportField()}.
	 */
	@Test
	public void testGetImportFieldOfCustomerProfiles() {

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE_VALUE, CustomerProfileValueImpl.class);

		final String firstName = "firstname";
		customer.getCustomerProfile().setStringProfileValue(CustomerImpl.ATT_KEY_CP_FIRST_NAME, firstName);

		ImportField importField = customerImportType.getImportField(ImportDataTypeProductImpl.PREFIX_OF_FIELD_NAME
				+ CustomerImpl.ATT_KEY_CP_FIRST_NAME);
		assertEquals(firstName, importField.getStringValue(customer));

		final String newFirstName = "firstname2";

		context.checking(new Expectations() {
			{
				allowing(validatorUtils).validateAttributeValue(with(any(AttributeValue.class)));
			}
		});

		importField.setStringValue(customer, newFirstName, importGuidHelper);
		assertEquals(newFirstName, customer.getCustomerProfile().getStringProfileValue(CustomerImpl.ATT_KEY_CP_FIRST_NAME));
	}

	/**
	 * Verify get import field of customer GUID.
	 */
	@Test
	public void testGetImportFieldOfCustomerGuid() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_GUID);
		assertNotNull(importField.getStringValue(customer));

		importField.setStringValue(customer, TEST_GUID, importGuidHelper);
		assertEquals(TEST_GUID, customer.getGuid());
	}

	/**
	 * Test get import field of customer guid will fail with null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfCustomerGuidWillFailWithNull() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_GUID);
		importField.setStringValue(customer, GlobalConstants.NULL_VALUE, importGuidHelper);
	}

	/**
	 * Test get import field of user id.
	 */
	@Test
	public void testGetImportFieldOfUserId() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_USERID);
		assertEquals(null, importField.getStringValue(customer));

		importField.setStringValue(customer, TEST_GUID, importGuidHelper);
		assertEquals(TEST_GUID, importField.getStringValue(customer));
	}

	/**
	 * Test get import field of user id fail null.
	 */
	@Test(expected = EpNonNullBindException.class)
	public void testGetImportFieldOfUserIdFailNull() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_USERID);
		importField.setStringValue(customer, GlobalConstants.NULL_VALUE, importGuidHelper);
	}

	/**
	 * Test get import field of password non null.
	 */
	@Test
	public void testGetImportFieldOfPasswordNonNull() {
		final String password = "eightcha";

		context.checking(new Expectations() {
			{
				oneOf(customerAuthentication).getClearTextPassword();
				will(returnValue(null));
				oneOf(customerAuthentication).getClearTextPassword();
				will(returnValue(password));

				oneOf(customerAuthentication).setClearTextPassword(password);
			}
		});

		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_PASSWORD);
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customer));

		importField.setStringValue(customer, password, importGuidHelper);
		assertEquals(IMPORT_FIELD_PASSWORD + " was not set or retrieved properly.", password, importField.getStringValue(customer));
	}

	/**
	 * Test get import field of password null.
	 */
	@Test
	public void testGetImportFieldOfPasswordNull() {
		context.checking(new Expectations() {
			{
				allowing(customerAuthentication).getClearTextPassword();
				will(returnValue(null));
			}
		});

		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_PASSWORD);
		assertEquals(GlobalConstants.NULL_VALUE, importField.getStringValue(customer));

		importField.setStringValue(customer, null, importGuidHelper);
		assertEquals(IMPORT_FIELD_PASSWORD + " was set to null but is not returning null.",
				GlobalConstants.NULL_VALUE,
				importField.getStringValue(customer));
	}

	/**
	 * Test get import field of password too short.
	 */
	@Test(expected = EpInvalidValueBindException.class)
	public void testGetImportFieldOfPasswordTooShort() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_PASSWORD);
		importField.setStringValue(customer, "sevench", importGuidHelper);
	}

	/**
	 * Test get import field of status enabled.
	 */
	@Test
	public void testGetImportFieldOfStatusEnabled() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		assertEquals(String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
		importField.setStringValue(customer, "1", importGuidHelper);
		assertEquals(IMPORT_FIELD_STATUS, String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
	}

	/**
	 * Test get import field of status disabled.
	 */
	@Test
	public void testGetImportFieldOfStatusDisabled() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		assertEquals(String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
		importField.setStringValue(customer, "2", importGuidHelper);
		assertEquals(IMPORT_FIELD_STATUS, String.valueOf(Customer.STATUS_DISABLED), importField.getStringValue(customer));
	}

	/**
	 * Test get import field of status pending.
	 */
	@Test
	public void testGetImportFieldOfStatusPending() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		assertEquals(String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
		importField.setStringValue(customer, "3", importGuidHelper);
		assertEquals(IMPORT_FIELD_STATUS, String.valueOf(Customer.STATUS_PENDING_APPROVAL), importField.getStringValue(customer));
	}

	/**
	 * Test get import field of status invalid.
	 */
	@Test(expected = EpInvalidValueBindException.class)
	public void testGetImportFieldOfStatusInvalid() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		assertEquals(String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
		importField.setStringValue(customer, "0", importGuidHelper);
	}

	/**
	 * Test get import field of status null.
	 */
	@Test
	public void testGetImportFieldOfStatusNull() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		assertEquals(String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
		assertEquals(IMPORT_FIELD_STATUS, String.valueOf(Customer.STATUS_ACTIVE), importField.getStringValue(customer));
	}

	/**
	 * Test get import field of status fail alphanumeric.
	 */
	@Test(expected = EpInvalidValueBindException.class)
	public void testGetImportFieldOfStatusFailAlphanumeric() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_STATUS);
		importField.setStringValue(customer, NON_ALPHANUMERIC, importGuidHelper);
	}

	/**
	 * Test get import field of creation date non null.
	 */
	@Test
	public void testGetImportFieldOfCreationDateNonNull() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_CREATIONDATE);

		// Check that the importField initially returns a value of today's date
		final Date creationDate = customer.getCreationDate();
		assertEquals(creationDate.toString(),
				ConverterUtils.string2Date(importField.getStringValue(customer), DATE_FORMAT, Locale.getDefault()).toString());

		importField.setStringValue(customer, validDateString, importGuidHelper);
		assertEquals(IMPORT_FIELD_CREATIONDATE + " was not set or retrieved properly.", validDateString, importField.getStringValue(customer));
	}

	/**
	 * Test get import field of creation date null.
	 */
	@Test
	public void testGetImportFieldOfCreationDateNull() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_CREATIONDATE);
		importField.setStringValue(customer, null, importGuidHelper);

		// Should be automatically assigned to creation date, but we don't have that to the millisecond
		final Date creationDate = customer.getCreationDate();
		assertEquals(creationDate.toString(),
				ConverterUtils.string2Date(importField.getStringValue(customer), DATE_FORMAT, Locale.getDefault()).toString());
	}

	/**
	 * Test get import field of creation date fail invalid date.
	 */
	@Test(expected = EpDateBindException.class)
	public void testGetImportFieldOfCreationDateFailInvalidDate() {
		ImportField importField = customerImportType.getImportField(ImportDataTypeCustomerImpl.PREFIX_OF_FIELD_NAME + IMPORT_FIELD_CREATIONDATE);
		importField.setStringValue(customer, NON_ALPHANUMERIC, importGuidHelper);
	}

	/**
	 * Test is entity import.
	 */
	@Test
	public void testIsEntityImport() {
		assertTrue(customerImportType.isEntityImport());
		assertFalse(customerImportType.isValueObjectImport());
	}

	/**
	 * Test create value object failure.
	 */
	@Test(expected = EpUnsupportedOperationException.class)
	public void testCreateValueObjectFailure() {
		customerImportType.createValueObject();
	}

	/**
	 * Test get import job runner bean name.
	 */
	@Test
	public void testGetImportJobRunnerBeanName() {
		assertEquals("importJobRunnerCustomer", customerImportType.getImportJobRunnerBeanName());
	}

	private Customer createCustomer() {
		final Customer customer = new CustomerImpl();

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, customerAuthentication);

		customer.setGuid((new RandomGuidImpl()).toString());
		customer.initialize();
		return customer;
	}
}
