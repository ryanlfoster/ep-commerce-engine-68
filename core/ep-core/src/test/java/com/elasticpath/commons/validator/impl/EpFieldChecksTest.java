/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.validator.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;
import com.elasticpath.settings.impl.SettingsServiceImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>EpFieldChecks</code>.  This class needs to mock a concrete class so it
 * has some duplicate code from AbstractJMock1EPTestCase in it because we can't do
 * multiple inheritance with the cglib version of MockObjectTestCase.
 */
public class EpFieldChecksTest {

	private static final String EXPIRY_MONTH = "expiryMonth";

	private static final String EXPIRY_YEAR = "expiryYear";

	private static final String QUANTITY = "quantity";

	private static final String CUSTOMER = "customer";

	private static final String ADDRESS = "address";

	private static final String PHONE_NUMBER = "phoneNumber";

	private static final String SECURITY_CHECK = "securityCheck";

	private static final String REQUIRED_WHEN = "requiredWhen";

	private static final String EMAIL = "email";

	private static final String PASSWORD = "password";

	private static final String PASSWORD_VALUE = "123456";

	private static final String NO_LEADING_TRAILING_WHITESPACE = "noLeadingTrailingWhiteSpaces";

	private static final String SUBCOUNTRY_REQUIRED = "SubCountryRequired";

	private static final String CANADA_KEY = "CA";

	private static final String CHINA_KEY = "CN";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TimeService mockTimeService;

	private ValidatorAction mockValidatorAction;

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory bfef;

	@Mock
	private Geography geography;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of any error phoneNumberField.addVar("pattern", "^([0-9]|\\s|-|\\)|\\(|\\+)*$", "");
	 */
	@Before
	public void setUp() throws Exception {
		this.setupBeanFactory();

		final CustomerService mockCustomerService = context.mock(CustomerService.class);
		context.checking(new Expectations() {
			{
				allowing(mockCustomerService).getUserIdMode();
				will(returnValue(1));
			}
		});
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, mockCustomerService);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});

		this.mockValidatorAction = new ValidatorAction();
		EpFieldChecks.setGeography(getGeography());
	}

	@After
	public void tearDown() {
		bfef.close();
	}

	/**
	 * Setup BeanFactory mocking.
	 */
	protected void setupBeanFactory() {
		beanFactory = context.mock(BeanFactory.class);
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);

		bfef.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		final UtilityImpl utility = new UtilityImpl();

		bfef.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, ProductAttributeValueImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE_VALUE, CustomerProfileValueImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.GEOGRAPHY, getGeography());
		bfef.allowingBeanFactoryGetBean(ContextIdNames.PASSWORD_GENERATOR, new ShaPasswordEncoder());
		bfef.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, utility);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.SETTINGS_SERVICE, new SettingsServiceImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				SettingValue settingValue = new SettingValueImpl();
				settingValue.setValue("1");
				return settingValue;
			}
		});
	}

	private Geography getGeography() {
		context.checking(new Expectations() {
			{
				allowing(geography).getSubCountryCodes(CANADA_KEY);
				will(returnValue(Collections.singleton("BC")));

				allowing(geography).getSubCountryCodes(CHINA_KEY);
				will(returnValue(Collections.emptySet()));
			}
		});

		return geography;
	}

	// TODO: Fix these two test cases.
	// /**
	// * Test method for 'com.elasticpath.commons.validator.impl.EpFieldCheck.validateTwoFields(bean, va, field, errors)'.
	// *
	// * Used to make sure two fields in the bean are of the same vlaue. Should be tested on the clearPassword and confirmClearTextPassword
	// * fields on Customer, but it is involves the passwordEncoding
	// * not is core package
	// */
	// public void testValidateTwoFields() {
	// final String emailStr = EMAIL_VALUE;
	// final String confirmPassword = EMAIL_VALUE;

	// final Customer customer = context.mock(Customer.class);
	// PasswordEncoder mockPasswordEncoder = context.mock(PasswordEncoder.class);
	// customer.setPasswordEncoder((PasswordEncoder mockPasswordEncoder);
	// customer.setClearTextPassword(passwordStr);
	// customer.setConfirmClearTextPassword(confirmPassword);
	//
	// final Field passwordField = new Field();
	// passwordField.setDepends(REQUIRED);
	// passwordField.setProperty(PASSWORD);
	// final Arg arg0 = new Arg();
	// arg0.setKey("address.firstName");
	// passwordField.addArg(arg0);
	// passwordField.addVar("secondProperty", "confirmClearTextPassword", "");
	//
	// final Errors errors = new BindException(customer, CUSTOMER);
	// assertTrue(EpFieldChecks.validateTwoFields(customer, this.mockValidatorAction, passwordField,
	// errors));
	// }
	//
	// /**
	// * Test method for 'com.elasticpath.commons.validator.impl.EpFieldCheck.validateTwoFields(bean, va, field, errors)'.
	// */
	// public void testValidateTwoFieldsFalse() {
	// final String passwordStr = PASSWORD_VALUE;
	// final String confirmPassword = "654321";
	//
	// final Customer customer = new CustomerImpl();
	// customer.setClearTextPassword(passwordStr);
	// customer.setConfirmClearTextPassword(confirmPassword);
	//
	// final Field passwordField = new Field();
	// passwordField.setDepends(REQUIRED);
	// passwordField.setProperty(PASSWORD);
	// final Arg arg0 = new Arg();
	// arg0.setKey("account.password");
	// passwordField.addArg(arg0);
	// passwordField.addVar("secondProperty", "confirmPassword", "");
	//
	// final Errors errors = new BindException(customer, CUSTOMER);
	// atLeast(1).of(mockValidatorAction).getName();
	//	will(returnValue(CUSTOMER));

	// oneOf(mockValidatorAction).getMsg();
	//	will(returnValue("error.twofields"));
	//
	//
	// assertFalse(EpFieldChecks.validateTwoFields(customer, this.mockValidatorAction, passwordField,
	// errors));
	// }

	/**
	 * Test method for 'com.elasticpath.commons.validator.impl.EpFieldCheck.validateRequiredWhen(bean, va, field, errors)'.
	 */
	@Test
	public void testValidateRequiredWhen() {
		final String passwordStr = PASSWORD_VALUE;

		final Customer customer = new CustomerImpl();
		customer.initialize();
		customer.setPassword(passwordStr);

		final Field passwordField = new Field();
		passwordField.setDepends(REQUIRED_WHEN);
		passwordField.setProperty(PASSWORD);
		final Arg arg0 = new Arg();
		arg0.setKey("account.password");
		passwordField.addArg(arg0);
		passwordField.addVar(REQUIRED_WHEN, "anonymous == false", "");

		final Errors errors = new BindException(customer, CUSTOMER);
		assertTrue(EpFieldChecks.validateRequiredWhen(customer, this.mockValidatorAction, passwordField, errors));
	}

	/**
	 * Test method for 'com.elasticpath.commons.validator.impl.EpFieldCheck.validateRequiredWhen(bean, va, field, errors)'.
	 */
	@Test
	public void testValidateRequiredWhenFalse() {
		final Customer customer = new CustomerImpl();
		customer.initialize();
		customer.setPassword("");

		final Field passwordField = new Field();
		passwordField.setDepends(REQUIRED_WHEN);
		passwordField.setProperty(PASSWORD);
		final Arg arg0 = new Arg();
		arg0.setKey("account.password");
		passwordField.addArg(arg0);
		passwordField.addVar(REQUIRED_WHEN, "anonymous == false", "");

		final Errors errors = new BindException(customer, CUSTOMER);
		mockValidatorAction.setName(CUSTOMER);
		mockValidatorAction.setMsg("error.requiredWhen");
		assertFalse(EpFieldChecks.validateRequiredWhen(customer, this.mockValidatorAction, passwordField, errors));
	}

	/**
	 * Test the security check validation.
	 */
	@Test
	public void testValidateSecurityCheck() {
		final String[] badStrings = { "<", ">", " sdklfjs sdkfj > kdfs", "><", " sfsdf<SDfsd" };
		final String[] goodStrings = { "+ (123) 456-7891", "billy bob", "" };

		final Field firstNameField = new Field();
		firstNameField.setDepends(SECURITY_CHECK);
		firstNameField.setProperty("firstName");

		final CustomerAddressImpl address = new CustomerAddressImpl();
		address.initialize();
		mockValidatorAction.setName("address");

		for (int i = 0; i < badStrings.length; i++) {
			address.setFirstName(badStrings[i]);
			final Errors errors = new BindException(address, CUSTOMER);
			mockValidatorAction.setMsg("errors.whitespace");
			assertFalse(EpFieldChecks.validateSecurityCheck(address, this.mockValidatorAction,
					firstNameField, errors));
		}

		for (int i = 0; i < goodStrings.length; i++) {
			address.setFirstName(goodStrings[i]);
			final Errors errors = new BindException(address, CUSTOMER);
			assertTrue(EpFieldChecks.validateSecurityCheck(address, this.mockValidatorAction,
					firstNameField, errors));
		}

	}

	/**
	 * Test phone number validation with a valid phone number.
	 */
	@Test
	public void testValidatePhoneNumberTrue() {
		String correctPhoneNumber = "+ (123) 456-7891";

		final Address address = new CustomerAddressImpl();
		address.setPhoneNumber(correctPhoneNumber);

		final Field phoneNumberField = new Field();
		phoneNumberField.setDepends(PHONE_NUMBER);
		phoneNumberField.setProperty(PHONE_NUMBER);

		final Arg arg0 = new Arg();
		arg0.setKey("address.phoneNumber");
		phoneNumberField.addArg(arg0);
		phoneNumberField.addVar("pattern", "^([0-9]|\\s|-|\\)|\\(|\\+)*$", "");
		phoneNumberField.addVar("minlength", "10", "");

		final Errors errors = new BindException(address, ADDRESS);

		assertTrue(EpFieldChecks.validatePhoneNumber(address, this.mockValidatorAction, phoneNumberField, errors));
	}

	/**
	 * Test phone number validation with a blank phone number when it is not required.
	 */
	@Test
	public void testValidatePhoneNumberTrueWhenBlank() {
		String correctPhoneNumber = "";

		final Address address = new CustomerAddressImpl();
		address.setPhoneNumber(correctPhoneNumber);

		final Field phoneNumberField = new Field();
		phoneNumberField.setDepends(PHONE_NUMBER);
		phoneNumberField.setProperty(PHONE_NUMBER);

		final Arg arg0 = new Arg();
		arg0.setKey("address.phoneNumber");
		phoneNumberField.addArg(arg0);
		phoneNumberField.addVar("pattern", "^([0-9]|\\s|-|\\)|\\(|\\+)*$", "");
		phoneNumberField.addVar("minlength", "10", "");

		final Errors errors = new BindException(address, ADDRESS);

		assertTrue(EpFieldChecks.validatePhoneNumber(address, this.mockValidatorAction, phoneNumberField, errors));
	}

	/**
	 * Test phone number validation with an invalid phone number.
	 */
	@Test
	public void testValidatePhoneNumberFalse() {
		final String badPhoneNumber = "k+ (123) 456-7891";

		final Address address = new CustomerAddressImpl();
		address.setPhoneNumber(badPhoneNumber);

		final Field phoneNumberField = new Field();
		phoneNumberField.setDepends(PHONE_NUMBER);
		phoneNumberField.setProperty(PHONE_NUMBER);

		final Arg arg0 = new Arg();
		arg0.setKey("address.phoneNumber");
		phoneNumberField.addArg(arg0);
		phoneNumberField.addVar("pattern", "^([0-9]|\\s|-|\\)|\\(|\\+)*$", "");
		phoneNumberField.addVar("minlength", "10", "");

		final Errors errors = new BindException(address, ADDRESS);
		mockValidatorAction.setName(ADDRESS);
		mockValidatorAction.setMsg("errors.phoneNumber");

		assertFalse(EpFieldChecks.validatePhoneNumber(address, this.mockValidatorAction, phoneNumberField, errors));
	}

	/**
	 * Test email validation.
	 */
	@Test
	public void testValidateEmail() {
		final String[] invalidEmail = { "Wenjie Liu@elasticpath.com", ".wenjie.liu.@elasticpath.com", "wenjie.liu.@elasticpath.com",
				"wenjie.liu@ep'.com" };
		final String[] validEmail = { "\"Wenjie Liu\"@elasticpath.com", "!#$%&'*+-/=?^_`{|}~@elasticpath.com", "wenjie.liu@elasticpath.com"	};

		final Field emailField = new Field();
		emailField.setDepends(EMAIL);
		emailField.setProperty(EMAIL);
		mockValidatorAction.setName(CUSTOMER);

		for (int i = 0; i < invalidEmail.length; i++) {
			final Customer customer = generateCustomerStub(invalidEmail[i]);
			final Errors errors = new BindException(customer, CUSTOMER);
			mockValidatorAction.setMsg("errors.email");
			assertFalse(EpFieldChecks.validateEmail(customer, this.mockValidatorAction, emailField, errors));
		}

		for (int i = 0; i < validEmail.length; i++) {
			final Customer customer = generateCustomerStub(validEmail[i]);
			final Errors errors = new BindException(customer, CUSTOMER);
			assertTrue(EpFieldChecks.validateEmail(customer, this.mockValidatorAction, emailField, errors));
		}
	}

	/**
	 * Test email validation.
	 */
	@Test
	public void testValidateDelimitedEmailsInvalid() {
		final String invalidEmails = "Wenjie Liu@elasticpath.com,.wenjie.liu.@elasticpath.com";

		final Field emailField = new Field();
		emailField.setDepends(EMAIL);
		emailField.setProperty(EMAIL);
		mockValidatorAction.setName(CUSTOMER);
		final Customer customer = new CustomerImpl() {
			private static final long serialVersionUID = 5933607082476113883L;

			@Override
			public String getEmail() {
				return invalidEmails;
			}
		};

		final Errors errors = new BindException(customer, CUSTOMER);
		mockValidatorAction.setMsg("errors.email");
		assertFalse(EpFieldChecks.validateEmail(customer, this.mockValidatorAction, emailField, errors));
	}

	/**
	 * Test email validation.
	 */
	@Test
	public void testValidateDelimitedEmailsValid() {
		final String validEmails = "\"Wenjie Liu\"@elasticpath.com, !#$%&'*+-/=?^_`{|}~@elasticpath.com,wenjie.liu@elasticpath.com,"
			+ "wenjie.liu@test.com";

		final Field emailField = new Field();
		emailField.setDepends(EMAIL);
		emailField.setProperty(EMAIL);

		final Customer customer = generateCustomerStub(validEmails);

		final Errors errors = new BindException(customer, CUSTOMER);
		assertTrue(EpFieldChecks.validateDelimitedEmails(customer, this.mockValidatorAction, emailField, errors));
	}

	/**
	 * Test leading/trailing whitespaces validation.
	 */
	@Test
	public void testValidateNoLeadingTrailingWhiteSpaces() {
		final String[] badStrings = { "  wLeadingSpace", "wTrailingSpace  " };
		final String[] goodStrings = { "noWhiteSpaces", "middle spaces" };

		final Field emailField = new Field();
		emailField.setDepends(NO_LEADING_TRAILING_WHITESPACE);
		emailField.setProperty(EMAIL);
		mockValidatorAction.setName(CUSTOMER);

		for (int i = 0; i < badStrings.length; i++) {
			final Customer customer = generateCustomerStub(badStrings[i]);
			final Errors errors = new BindException(customer, CUSTOMER);
			mockValidatorAction.setMsg("errors.whitespace");
			assertFalse(EpFieldChecks.validateNoLeadingTrailingWhiteSpaces(customer, this.mockValidatorAction, emailField,
					errors));
		}

		for (int i = 0; i < goodStrings.length; i++) {
			final Customer customer = generateCustomerStub(goodStrings[i]);
			final Errors errors = new BindException(customer, CUSTOMER);
			assertTrue(EpFieldChecks.validateNoLeadingTrailingWhiteSpaces(customer, this.mockValidatorAction, emailField,
					errors));
		}
	}

	/**
	 * Test quantity validation with valid quantity.
	 */
	@Test
	public void testValidateQuantityTrue() {
		ShoppingItem cartItem = new ShoppingItemImpl();

		final Field quantityField = new Field();
		quantityField.setDepends("epCartQuantity");
		quantityField.setProperty(QUANTITY);

		final Arg arg0 = new Arg();
		arg0.setKey(QUANTITY);
		quantityField.addArg(arg0);

		final Errors errors = new BindException(cartItem, "cartItem");

		cartItem.setPrice(2, null);

		assertTrue(EpFieldChecks.validateEpCartQuantity(cartItem, this.mockValidatorAction, quantityField, errors));
	}

	/**
	 * Tests that the credit card validation works properly with no condition for eligibility.
	 */
	@Test
	public void testValidateCreditCardNoCondition() {
		new EpFieldChecks().setTimeService(mockTimeService);
		final Field field = new Field();
		field.setProperty(EXPIRY_MONTH);

		final Arg arg0 = new Arg();
		arg0.setKey(EXPIRY_MONTH);
		field.addArg(arg0);

		field.addVar("expiryYearProperty", EXPIRY_YEAR, null);

		CustomerCreditCard creditCard = new CustomerCreditCardImpl();
		creditCard.setExpiryYear("3000");
		creditCard.setExpiryMonth("12");
		final Errors errors = new BindException(creditCard, "cartItem");

		assertTrue(EpFieldChecks.validateCreditCardExpiration(creditCard, this.mockValidatorAction, field, errors));
	}

	/**
	 * Tests that the credit card validation validates in case the condition
	 * is 'true'. The credit card has an expired date.
	 */
	@Test
	public void testValidateCreditCardWithPassingCondition() {
		new EpFieldChecks().setTimeService(mockTimeService);
		final Field field = new Field();
		field.setProperty(EXPIRY_MONTH);

		final Arg arg0 = new Arg();
		arg0.setKey(EXPIRY_MONTH);
		field.addArg(arg0);

		field.addVar("expiryYearProperty", EXPIRY_YEAR, null);
		// add variable for the condition
		field.addVar("validateExpirationWhen", "cardType == testCardType", null);

		CustomerCreditCard creditCard = new CustomerCreditCardImpl();
		creditCard.setCardType("testCardType");
		creditCard.setExpiryMonth("01");
		creditCard.setExpiryYear("1990");
		final Errors errors = new BindException(creditCard, "creditCard");
		mockValidatorAction.setName("validatorName");
		mockValidatorAction.setMsg("message");
		assertFalse(EpFieldChecks.validateCreditCardExpiration(creditCard, this.mockValidatorAction, field, errors));
	}

	/**
	 * Tests that the credit card validation will be skipped in case
	 * the condition it has applied is not fulfilled.
	 */
	@Test
	public void testValidateCreditCardWithFailingCondition() {
		new EpFieldChecks().setTimeService(mockTimeService);
		final Field field = new Field();
		field.setProperty(EXPIRY_MONTH);

		final Arg arg0 = new Arg();
		arg0.setKey(EXPIRY_MONTH);
		field.addArg(arg0);

		field.addVar("expiryYearProperty", EXPIRY_YEAR, null);
		// add variable for the condition
		field.addVar("validateExpirationWhen", "cardType == otherTestCardType", null);

		CustomerCreditCard creditCard = new CustomerCreditCardImpl();
		creditCard.setCardType("testCardType");
		creditCard.setExpiryMonth("01");
		creditCard.setExpiryYear("1990");
		final Errors errors = new BindException(creditCard, "creditCard");
		mockValidatorAction.setName("validatorName");
		mockValidatorAction.setMsg("message");
		assertTrue(EpFieldChecks.validateCreditCardExpiration(creditCard, this.mockValidatorAction, field, errors));
	}

	/**
	 * Test method for 'com.elasticpath.commons.validator.impl.EpFieldCheck.validateSubCountryRequired(bean, va, field, errors)'.
	 */
	@Test
	public void testValidatesubcountryrequired() {
		final Address address = new CustomerAddressImpl();
		address.setCountry(CANADA_KEY);

		final Field subCountryField = new Field();
		subCountryField.setDepends(SUBCOUNTRY_REQUIRED);
		subCountryField.setProperty("subCountry");
		final Arg arg0 = new Arg();
		arg0.setKey("globals.subcountry");
		subCountryField.addArg(arg0);
		subCountryField.addVar("countryProperty", "country", "");

		final Errors errors = new BindException(address, "customerAddressImpl");
		mockValidatorAction.setName(ADDRESS);
		mockValidatorAction.setMsg("error.requiredWhen");
		assertFalse(EpFieldChecks.validateSubCountryRequired(address, this.mockValidatorAction, subCountryField, errors));
		address.setSubCountry("BC");
		assertTrue(EpFieldChecks.validateSubCountryRequired(address, this.mockValidatorAction, subCountryField, errors));

		address.setCountry(CHINA_KEY);
		assertTrue(EpFieldChecks.validateSubCountryRequired(address, this.mockValidatorAction, subCountryField, errors));
	}

	private Customer generateCustomerStub(final String email) {
		final Customer customer = new CustomerImpl() {
			private static final long serialVersionUID = -5890876227739295403L;

			@Override
			public String getEmail() {
				return email;
			}
		};
		return customer;
	}
}
