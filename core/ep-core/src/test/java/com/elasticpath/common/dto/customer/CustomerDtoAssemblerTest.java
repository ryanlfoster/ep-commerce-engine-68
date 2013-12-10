/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.common.dto.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elasticpath.common.dto.AddressDTO;
import com.elasticpath.common.dto.assembler.customer.BuiltinFilters;
import com.elasticpath.common.dto.assembler.customer.CreditCardFilter;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerAuthentication;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerProfileImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.CustomerAddressBuilder;
import com.elasticpath.test.factory.CustomerBuilder;
import com.elasticpath.test.factory.CustomerCreditCardBuilder;
import com.elasticpath.test.factory.CustomerDTOBuilder;

/**
 * Test {@link CustomerDtoAssembler} functionality.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.TooManyMethods" })
public class CustomerDtoAssemblerTest {

	private static final String EXPECTED_DTO_SHOULD_EQUAL_ACTUAL = "The assembled customer DTO should be equal to the expected customer DTO.";

	private static final String EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL =
			"The assembled customer domain object should be equal to the expected customer domain object.";

	private static final String CREDIT_CARD_TYPE_VISA = "VISA";

	private static final String CREDIT_CARD_GUID = "CREDIT_CARD_GUID";

	private static final String ENCRYPTED_CARD = "ENCRYPTED_CARD";

	private static final String CUSTOMER_GROUP_GUID = "CUSTOMER_GROUP_GUID";

	private static final String FILTERED_CREDIT_CARD_NUMBER_VISA = "4111111111111111";

	private static final String FILTERED_CREDIT_CARD_NUMBER_MASTERCARD = "5500000000000004";

	private static final String SALT = "SALT";

	private static final String COMPANY_NAME = "COMPANY";

	private static final char GENDER = 'M';

	private static final String EMAIL_ADDRESS = "customer@elasticpath.com";

	private static final String USER_ID = "USER_ID";

	private static final String STORE_CODE = "STORE_CODE";

	private static final String PASSWORD = "PASSWORD";

	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	private static final int ISSUE_NUMBER = 123;

	private static final Currency PREFERRED_CURRENCY = Currency.getInstance("CAD");

	private static final Locale PREFERRED_LOCALE = Locale.CANADA_FRENCH;

	private static final String FAX_NUMBER = "604-555-5555";

	private static final String PHONE_NUMBER = "604-555-1234";

	private static final String LAST_NAME = "LAST";

	private static final String FIRST_NAME = "FIRST";

	private static final Date DATE_OF_BIRTH = new Date();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private CustomerDtoAssembler customerDtoAssembler;

	private CustomerGroupService customerGroupService;

	private Date creationDate;

	private Date lastEditDate;

	private CustomerAddress address;

	private CustomerCreditCard creditCard;

	private CustomerGroup customerGroup;

	private AttributeService attributeService;

	private final Utility utility = new UtilityImpl() {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getDefaultDateFormatPattern() {
			return "EEE MMM dd HH:mm:ss z yyyy";
		}
	};

	private CreditCardEncrypter creditCardEncrypter;

	private Map<String, Attribute> customerProfileAttributes;

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		attributeService = context.mock(AttributeService.class);
		creditCardEncrypter = context.mock(CreditCardEncrypter.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE_VALUE, CustomerProfileValueImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.UTILITY, utility);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_ADDRESS, CustomerAddressImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_CREDIT_CARD, CustomerCreditCardImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, creditCardEncrypter);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_PROFILE, CustomerProfileImpl.class);

		customerDtoAssembler = new CustomerDtoAssembler();
		customerDtoAssembler.setBeanFactory(beanFactory);
		customerDtoAssembler.setCardFilter(BuiltinFilters.STATIC);

		customerGroupService = context.mock(CustomerGroupService.class);
		customerDtoAssembler.setCustomerGroupService(customerGroupService);

		creationDate = new Date();
		lastEditDate = creationDate;
		address = createAddress();
		creditCard = createCreditCard(CREDIT_CARD_GUID, CREDIT_CARD_TYPE_VISA, true);
		customerGroup = createCustomerGroup();

		customerProfileAttributes = createCustomerProfileAttributes();

		shouldGetCustomerProfileAttributesMap();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test customer DTO assembly from domain object.
	 */
	@Test
	public void testCustomerAssembleDtoFromDomainObject() {
		CustomerDTO expectedCustomerDto = createCustomerDto();

		Customer customer = createCustomer();
		CustomerDTO customerDto = new CustomerDTO();

		customerDtoAssembler.assembleDto(customer, customerDto);

		assertReflectionEquals(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL, expectedCustomerDto, customerDto, ReflectionComparatorMode.LENIENT_ORDER);
	}

	/**
	 * Test customer DTO assembly does not set preferred billing address when null on the domain object being translated.
	 */
	@Test
	public void testCustomerAssembleDtoDoesNotSetPreferredBillingAddressWhenNull() {
		CustomerDTO expectedCustomerDto = createCustomerDto();
		expectedCustomerDto.setPreferredBillingAddressGuid(null);

		Customer customer = createCustomer();
		customer.setPreferredBillingAddress(null);

		CustomerDTO customerDto = new CustomerDTO();

		customerDtoAssembler.assembleDto(customer, customerDto);

		assertReflectionEquals(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL, expectedCustomerDto, customerDto, ReflectionComparatorMode.LENIENT_ORDER);
		assertNull("The preferred billing address guid should be null.", customerDto.getPreferredBillingAddressGuid());
	}

	/**
	 * Test customer DTO assembly does not set preferred shipping address when null on the domain object being translated.
	 */
	@Test
	public void testCustomerAssembleDtoDoesNotSetPreferredShippingingAddressWhenNull() {
		CustomerDTO expectedCustomerDto = createCustomerDto();
		expectedCustomerDto.setPreferredShippingAddressGuid(null);

		Customer customer = createCustomer();
		customer.setPreferredShippingAddress(null);

		CustomerDTO customerDto = new CustomerDTO();

		customerDtoAssembler.assembleDto(customer, customerDto);

		assertReflectionEquals(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL, expectedCustomerDto, customerDto, ReflectionComparatorMode.LENIENT_ORDER);
		assertNull("The preferred shipping address guid should be null.", customerDto.getPreferredShippingAddressGuid());
	}

	/**
	 * Test customer DTO assembly does not use credit card when card type not populated with a recognized card type on the domain object being
	 * translated.
	 */
	@Test
	public void testCustomerAssembleDtoDoesNotUseCreditCardWhenCardTypeNotPopulatedWithRecognizedCardType() {
		CustomerDTO expectedCustomerDto = createCustomerDto();
		expectedCustomerDto.setCreditCards(Collections.<CreditCardDTO> emptyList());

		Customer customer = createCustomer();
		customer.getCreditCards().get(0).setCardType("UNKNOWN_CARD_TYPE");

		CustomerDTO customerDto = new CustomerDTO();

		customerDtoAssembler.assembleDto(customer, customerDto);

		assertReflectionEquals(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL, expectedCustomerDto, customerDto, ReflectionComparatorMode.LENIENT_ORDER);
		assertEquals("The credit card list should be empty.", Collections.<CreditCardDTO> emptyList(), customerDto.getCreditCards());
	}

	/**
	 * Test customer domain assembly from DTO.
	 */
	@Test
	public void testCustomerDomainAssemblyFromDto() {
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldFindCustomerGroupByGuid();

		Customer expectedCustomer = createCustomer();

		CustomerDTO customerDto = createCustomerDto();
		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);

		assertReflectionEquals(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL, expectedCustomer, customer, ReflectionComparatorMode.LENIENT_DATES);
	}

	/**
	 * Test customer assemble domain does not create duplicate addresses when assembling the domain object.
	 */
	@Test
	public void testCustomerAssembleDomainDoesNotCreateDuplicateAddressesWhenAssemblingDomainObject() {
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldFindCustomerGroupByGuid();

		Customer expectedCustomer = createCustomer();

		CustomerDTO customerDto = createCustomerDto();
		customerDto.getAddresses().add(createAddressDto(address));

		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);

		assertReflectionEquals(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL, expectedCustomer, customer, ReflectionComparatorMode.LENIENT_DATES);
		assertEquals("The domain object should have no duplicate addresses", 1, customer.getAddresses().size());
	}

	/**
	 * The test customer assemble domain does not create duplicate credit cards when assembling the domain object.
	 */
	@Test
	public void testCustomerAssembleDomainDoesNotCreateDuplicateCreditCardsWhenAssemblingDomainObject() {
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldFindCustomerGroupByGuid();

		Customer expectedCustomer = createCustomer();

		CustomerDTO customerDto = createCustomerDto();
		customerDto.getCreditCards().add(createCreditCardDto(creditCard));

		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);

		assertReflectionEquals(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL, expectedCustomer, customer, ReflectionComparatorMode.LENIENT_DATES);
		assertEquals("The domain object should have no duplicate credit cards.", 1, customer.getCreditCards().size());
	}

	/**
	 * Test customer assemble domain creates multiple credit cards when assembling domain object.
	 */
	@Test
	public void testCustomerAssembleDomainCreatesMultipleCreditCardsWhenAssemblingDomainObject() {
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_MASTERCARD);
		shouldFindCustomerGroupByGuid();

		CustomerCreditCard alternateCreditCard = createCreditCard("ALTERNATE_CARD_GUID", "MASTERCARD", false);

		Customer expectedCustomer = createCustomer();
		expectedCustomer.addCreditCard(alternateCreditCard);

		CustomerDTO customerDto = createCustomerDto();
		customerDto.getCreditCards().add(createCreditCardDto(alternateCreditCard));

		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);

		assertReflectionEquals(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL, expectedCustomer, customer, ReflectionComparatorMode.LENIENT_DATES);
		assertEquals("The domain object should have two credit cards.", 2, customer.getCreditCards().size());
	}

	/**
	 * Test customer assemble domain fails when the customer DTO has a credit card where the card number is not populated.
	 */
	@Test(expected = EpInvalidValueBindException.class)
	public void testCustomerAssembleDomainFailsWhenDtoHasACreditCardWhereCardNumberIsNotPopulated() {
		CustomerDTO customerDto = createCustomerDto();
		customerDto.getCreditCards().get(0).setCardNumber("");

		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);
	}

	/**
	 * Test customer assemble domain does not create duplicate customer groups when assembling the domain object.
	 */
	@Test
	public void testCustomerAssembleDomainDoesNotCreateDuplicateCustomerGroupsWhenAssemblingDomainObject() {
		shouldEncryptCreditCard(FILTERED_CREDIT_CARD_NUMBER_VISA);
		shouldFindCustomerGroupByGuid();
		shouldFindCustomerGroupByGuid();

		Customer expectedCustomer = createCustomer();

		CustomerDTO customerDto = createCustomerDto();
		customerDto.getGroups().add(CUSTOMER_GROUP_GUID);

		Customer customer = new CustomerImpl();

		customerDtoAssembler.assembleDomain(customerDto, customer);

		assertReflectionEquals(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL, expectedCustomer, customer, ReflectionComparatorMode.LENIENT_DATES);
		assertEquals("The domain object should have no duplicate customer groups", 1, customer.getCustomerGroups().size());
	}

	/**
	 * Test customer dto assembler uses emptying filter if no card filter set.
	 */
	@Test
	public void testCustomerDtotAssemblerUsesEmptyingFilterIfNoCardFilterSet() {
		Customer customer = createCustomer();
		CustomerDTO customerDto = new CustomerDTO();

		customerDtoAssembler.setCardFilter(null);
		customerDtoAssembler.assembleDto(customer, customerDto);

		assertEquals("The card filter should use the EMPTYING built in filter if none set.",
				BuiltinFilters.EMPTYING,
				customerDtoAssembler.getCardFilter());
	}

	private void shouldGetCustomerProfileAttributesMap() {
		context.checking(new Expectations() {
			{
				allowing(attributeService).getCustomerProfileAttributesMap();
				will(returnValue(customerProfileAttributes));
			}
		});
	}

	private void shouldEncryptCreditCard(final String creditCard) {
		context.checking(new Expectations() {
			{
				oneOf(creditCardEncrypter).encrypt(creditCard);
				will(returnValue(ENCRYPTED_CARD));
			}
		});
	}

	private void shouldFindCustomerGroupByGuid() {
		context.checking(new Expectations() {
			{
				oneOf(customerGroupService).findByGuid(CUSTOMER_GROUP_GUID);
				will(returnValue(customerGroup));
			}
		});
	}

	private Customer createCustomer() {
		return CustomerBuilder.newCustomer()
			.withGuid(CUSTOMER_GUID)
			.withCreationDate(creationDate)
			.withLastEditDate(lastEditDate)
			.withAddedAddress(address)
			.withPreferredBillingAddress(address)
			.withPreferredShippingAddress(address)
			.withCustomerAuthentication(createCustomerAuthentication())
			.withPassword(PASSWORD)
			.withStatus(Customer.STATUS_ACTIVE)
			.withStoreCode(STORE_CODE)
			.withUserId(USER_ID)
			.withAddedCreditCard(createCreditCard(CREDIT_CARD_GUID, CREDIT_CARD_TYPE_VISA, true))
			.withAddedCustomerGroup(customerGroup)
			.withCustomerProfile(createCustomerProfile())
			.withEmail(EMAIL_ADDRESS)
			.withPreferredLocale(PREFERRED_LOCALE)
			.withPreferredCurrency(PREFERRED_CURRENCY)
			.withFirstName(FIRST_NAME)
			.withLastName(LAST_NAME)
			.withAnonymous(false)
			.withDateOfBirth(DATE_OF_BIRTH)
			.withPhoneNumber(PHONE_NUMBER)
			.withGender(GENDER)
			.withCompany(COMPANY_NAME)
			.withToBeNotified(true)
			.withHtmlEmailPreferred(true)
			.withFaxNumber(FAX_NUMBER)
			.build();
	}

	private CustomerAuthentication createCustomerAuthentication() {
		CustomerAuthentication customerAuthentication = new CustomerAuthenticationImpl();

		customerAuthentication.setSalt(SALT);

		return customerAuthentication;
	}

	private CustomerProfile createCustomerProfile() {
		CustomerProfile customerProfile = new CustomerProfileImpl();

		customerProfile.setProfileValueBeanId(ContextIdNames.CUSTOMER_PROFILE_VALUE);
		customerProfile.setProfileValueMap(new HashMap<String, AttributeValue>());

		return customerProfile;
	}

	private CustomerGroup createCustomerGroup() {
		CustomerGroup customerGroup = new CustomerGroupImpl();

		customerGroup.setGuid(CUSTOMER_GROUP_GUID);
		customerGroup.setName("CUSTOMER_GROUP_NAME");

		return customerGroup;
	}

	private Map<String, Attribute> createCustomerProfileAttributes() {
		final Map<String, Attribute> customerProfileAttributes = new LinkedHashMap<String, Attribute>();

		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_FIRST_NAME, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_LAST_NAME, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_EMAIL, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_PREF_LOCALE, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_PREF_CURR, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_ANONYMOUS_CUST, AttributeType.BOOLEAN);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_PHONE, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_FAX, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_GENDER, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_COMPANY, AttributeType.SHORT_TEXT);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_DOB, AttributeType.DATE);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_HTML_EMAIL, AttributeType.BOOLEAN);
		addProfileAttribute(customerProfileAttributes, CustomerImpl.ATT_KEY_CP_BE_NOTIFIED, AttributeType.BOOLEAN);

		return customerProfileAttributes;
	}

	private void addProfileAttribute(final Map<String, Attribute> customerProfileAttributes,
			final String key,
			final AttributeType attributeType) {
		Attribute attribute = new AttributeImpl();
		attribute.initialize();
		attribute.setAttributeType(attributeType);
		attribute.setKey(key);
		customerProfileAttributes.put(key, attribute);
	}

	private CustomerCreditCard createCreditCard(final String cardGuid, final String cardType, final Boolean defaultCard) {
		return CustomerCreditCardBuilder.newCustomerCreditCard()
				.withGuid(cardGuid)
				.withBillingAddress(address)
				.withCardHolderName(FIRST_NAME + " " + LAST_NAME)
				.withCardType(cardType)
				.withCardNumber(ENCRYPTED_CARD)
				.withDefaultCard(defaultCard)
				.withExpiryMonth("12")
				.withExpiryYear("2020")
				.withIssueNumber(ISSUE_NUMBER)
				.withStartMonth("01")
				.withStartYear("2010")
				.build();
	}

	private CustomerAddress createAddress() {
		return CustomerAddressBuilder.newCustomerAddress()
			    .withGuid("CUSTOMER_ADDRESS_GUID")
				.withFirstName(FIRST_NAME)
				.withLastName(LAST_NAME)
				.withStreet1("STREET 1")
				.withStreet2("STREET 2")
				.withCity("CITY")
				.withSubCountry("SUBCOUNTRY")
				.withCountry("COUNTRY")
				.withZipOrPostalCode("ZIPCODE")
				.withCommercialAddress(false)
				.withOrganization(COMPANY_NAME)
				.withFaxNumber(FAX_NUMBER)
				.withPhoneNumber(PHONE_NUMBER)
				.build();
	}

	private CustomerDTO createCustomerDto() {
		return CustomerDTOBuilder
				.newCustomerDTO()
				.withGuid(CUSTOMER_GUID)
				.withCreationDate(creationDate)
				.withLastEditDate(lastEditDate)
				.withAddedAddress(createAddressDto(address))
				.withPreferredBillingAddressGuid(address.getGuid())
				.withPreferredShippingAddressGuid(address.getGuid())
				.withPassword(PASSWORD)
				.withSalt(SALT)
				.withStatus(Customer.STATUS_ACTIVE)
				.withStoreCode(STORE_CODE)
				.withUserId(USER_ID)
				.withAddedCreditCard(createCreditCardDto(creditCard))
				.withAddedGroup(customerGroup.getGuid())
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_FIRST_NAME, AttributeType.SHORT_TEXT.toString(), FIRST_NAME))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_LAST_NAME, AttributeType.SHORT_TEXT.toString(), LAST_NAME))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_EMAIL, AttributeType.SHORT_TEXT.toString(), EMAIL_ADDRESS))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_PREF_LOCALE,
						AttributeType.SHORT_TEXT.toString(),
						PREFERRED_LOCALE.toString()))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_PREF_CURR,
						AttributeType.SHORT_TEXT.toString(),
						PREFERRED_CURRENCY.toString()))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_ANONYMOUS_CUST, AttributeType.BOOLEAN.toString(), "false"))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_PHONE, AttributeType.SHORT_TEXT.toString(), PHONE_NUMBER))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_FAX, AttributeType.SHORT_TEXT.toString(), FAX_NUMBER))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_GENDER,
						AttributeType.SHORT_TEXT.toString(),
						String.valueOf(GENDER)))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_COMPANY, AttributeType.SHORT_TEXT.toString(), COMPANY_NAME))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_DOB,
						AttributeType.DATE.toString(),
						DATE_OF_BIRTH.toString()))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_HTML_EMAIL, AttributeType.BOOLEAN.toString(), "true"))
				.withAddedProfileValue(createAttributeValueDto(CustomerImpl.ATT_KEY_CP_BE_NOTIFIED, AttributeType.BOOLEAN.toString(), "true"))
				.build();
	}

	private CreditCardDTO createCreditCardDto(final CustomerCreditCard creditCard) {
		CreditCardFilter cardFilter = customerDtoAssembler.getCardFilter();
		return cardFilter.filter(creditCard);
	}

	private AddressDTO createAddressDto(final CustomerAddress address) {
		AddressDTO addressDto = new AddressDTO();

		addressDto.setGuid(address.getGuid());
		addressDto.setFirstName(address.getFirstName());
		addressDto.setLastName(address.getLastName());
		addressDto.setStreet1(address.getStreet1());
		addressDto.setStreet2(address.getStreet2());
		addressDto.setCity(address.getCity());
		addressDto.setSubCountry(address.getSubCountry());
		addressDto.setCountry(address.getCountry());
		addressDto.setZipOrPostalCode(address.getZipOrPostalCode());
		addressDto.setCommercialAddress(address.isCommercialAddress());
		addressDto.setOrganization(address.getOrganization());
		addressDto.setPhoneNumber(address.getPhoneNumber());
		addressDto.setFaxNumber(address.getFaxNumber());

		return addressDto;
	}

	private AttributeValueDTO createAttributeValueDto(final String key, final String type, final String value) {
		AttributeValueDTO attributeValueDTO = new AttributeValueDTO();

		attributeValueDTO.setKey(key);
		attributeValueDTO.setType(type);
		attributeValueDTO.setValue(value);

		return attributeValueDTO;
	}

}
