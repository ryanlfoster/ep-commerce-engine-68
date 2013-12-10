package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.test.builders.CustomerCreditCardBuilder;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;
/**
 * An integration test for the CustomerServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class CustomerServiceImplTest extends BasicSpringContextTest {

    private static final String TEST_EMAIL_1 = "test@elasticpath.com";

	private static final String TEST_EMAIL_2 = "test2@elasticpath.com";
	
	private static final String PASSWORD = "password";
    public static final String TOKEN_DISPLAY_VALUE = "**** **** **** 1234";
    public static final String TOKEN_GATEWAY_GUID = "abc";

    @Autowired
	@Qualifier("customerService")
	private CustomerService service;

	private SimpleStoreScenario scenario;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	/**
	 * Testing the ability to create a new customer with the same email (userId) as an existing (anonymous) customer. The service call should NOT
	 * throw a UserIdExistException since anonymous customers should not be included in the duplicate id check.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateCustomerWithSameUserIdAsAnonymousCustomer() {
		createPersistedAnonymousCustomer(TEST_EMAIL_1, scenario.getStore());

		try {
			createPersistedCustomer(TEST_EMAIL_1, scenario.getStore());
		} catch (UserIdExistException uidee) {
			fail("Should be able to add a new customer with the same userId as an existing anonymous customer.");
		}
	}

	/**
	 * Testing the ability to update a customer's email address to the same email (userId) as an existing (anonymous) customer. The service call
	 * should NOT throw a UserIdExistException since anonymous customers should not be included in the duplicate id check.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateCustomerWithSameUserIdAsAnonymousCustomer() {
		createPersistedAnonymousCustomer(TEST_EMAIL_1, scenario.getStore());

		try {
			Customer customer = createPersistedCustomer(TEST_EMAIL_2, scenario.getStore());
			customer.setEmail(TEST_EMAIL_1);
			service.update(customer);
		} catch (UserIdExistException uidee) {
			fail("Should be able to add a new customer with the same userId as an existing anonymous customer.");
		}
	}
	
	/**
	 * Test that a customer can be updated when in User ID Mode 2.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateCustomerWithGeneratedUserId() {
		changeUserIdMode(WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE);
		
		Customer customer = createPersistedCustomer(TEST_EMAIL_2, scenario.getStore());
		//Assert the correct customer has been created.
		assertEquals(TEST_EMAIL_2, customer.getEmail());
		
		customer.setEmail(TEST_EMAIL_1);
		service.update(customer);
		
		assertEquals(TEST_EMAIL_1, customer.getEmail());
	}

	/**
	 * Testing the ability to create a new customer with the same email (userId) as an existing customer. The service call should throw a
	 * UserIdExistException as this is not allowed.
	 */
	@DirtiesDatabase
	@Test
	public void testAttemptCreateCustomerWithSameUserIdAsExistingCustomer() {
		createPersistedCustomer(TEST_EMAIL_1, scenario.getStore());

		try {
			createPersistedCustomer(TEST_EMAIL_1, scenario.getStore());
		} catch (UserIdExistException uidee) {
			return;
		}
		fail("Should not be able to add a new customer with the same userId as an existing customer.");
	}

	/**
	 * Testing the ability to update a customer's email address to the same email (userId) as an existing customer. The service call should throw a
	 * UserIdExistException since this is not allowed.
	 */
	@DirtiesDatabase
	@Test
	public void testAttemptUpdateCustomerWithSameUserIdAsExistingCustomer() {
		createPersistedCustomer(TEST_EMAIL_1, scenario.getStore());

		try {
			Customer customerB = createPersistedCustomer(TEST_EMAIL_2, scenario.getStore());
			customerB.setEmail(TEST_EMAIL_1);
			service.update(customerB);
		} catch (UserIdExistException uidee) {
			return;
		}
		fail("Should not be able to add a new customer with the same userId as an existing customer.");
	}

	/**
	 * Test persisting a customer and then loading it back from the DB
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetCustomer() {
		Customer customer = createCustomer();
		service.add(customer);
		assertFalse("UidPk should not be zero", customer.getUidPk() == 0);
		assertTrue("Customer should be persistent", customer.isPersisted());
		Customer retrievedCustomer = service.get(customer.getUidPk());
		assertEquals("Loaded customer should have correct UidPk", customer.getUidPk(), retrievedCustomer.getUidPk());
	}

	/**
	 * Test persisting a customer and then updating the firstname (tests value saved to customerprofile map).
	 */
	@DirtiesDatabase
	@Test
	public void testAddThenUpdateCustomerProfileValue() {
		final String firstName = "First Name";

		Customer customer = createCustomer();
		service.add(customer);
		assertFalse("UidPk should not be zero", customer.getUidPk() == 0);
		assertTrue("Customer should be persistent", customer.isPersisted());

		customer.setFirstName(firstName);
		service.update(customer);

		Customer retrievedCustomer = service.get(customer.getUidPk());
		assertEquals("Loaded customer should have correct UidPk", customer.getUidPk(), retrievedCustomer.getUidPk());
		assertEquals("Loaded customer should have saved Firstname", firstName, retrievedCustomer.getFirstName());
	}

	/**
	 * Test adding an address to a customer.
	 */
	@DirtiesDatabase
	@Test
	public void testAddCustomerAddress() {
		// Persist a customer
		Customer customer = createCustomer();
		service.add(customer);

		// Add an address to the customer
		final CustomerAddress customerAddress = createAddress();
		customer.addAddress(customerAddress);
		assertEquals("The customer should now have an address", customer.getAddresses().size(), 1);
		assertEquals("The customer address should match the one we created", customer.getAddresses().get(0), customerAddress);
		String guid = customer.getAddresses().get(0).getGuid();
		assertNotNull("The address should have a guid", guid);

		// Update the customer
		Customer updatedCustomer = service.update(customer);
		assertEquals("The returned object should have one address", updatedCustomer.getAddresses().size(), 1);
		CustomerAddress updatedAddress = updatedCustomer.getAddresses().get(0);
		assertTrue("The customer's address should now be persistent", updatedAddress.isPersisted());

		// Load the address back from the customer object
		CustomerAddress retrievedAddress = updatedCustomer.getAddressByUid(updatedAddress.getUidPk());
		assertNotNull("The address should be found in the database", retrievedAddress);
		assertEquals("The GUID of the retrieved address should match that of the updated object", updatedAddress.getGuid(), retrievedAddress
				.getGuid());
		assertEquals("The address we got back keyed by guid should match", updatedAddress, retrievedAddress);
		
		// Also try using the service to get the updated customer address.
		assertNotNull(service.getCustomerAddress(updatedAddress.getUidPk()));
	}

	/**
	 * Test that editing a saved address and updating works correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testEditCustomerAddress() {
		// Persist a customer
		Customer customer = createCustomer();
		service.add(customer);
		assertEquals("The customer should have a group", customer.getCustomerGroups().size(), 1);

		// Add an address to the customer and save to the DB
		customer.addAddress(createAddress());
		Customer updatedCustomer = service.update(customer);
		assertEquals("The customer should still have a group", updatedCustomer.getCustomerGroups().size(), 1);

		// Make a change to the address and save to the DB
		CustomerAddress address = updatedCustomer.getAddresses().get(0);
		long addressUid = address.getUidPk();
		String uniqueName = Utils.uniqueCode("name");
		address.setFirstName(uniqueName);
		Customer updatedTwiceCustomer = service.update(updatedCustomer);
		CustomerAddress updatedAddress = updatedTwiceCustomer.getAddressByUid(addressUid);
		assertEquals("The updated address should have the same ID", addressUid, updatedAddress.getUidPk());
		assertEquals("The updated address should have the updated first name", uniqueName, updatedAddress.getFirstName());

		// Change a field to the same value and save again
		String lastName = new String(updatedAddress.getLastName());
		updatedAddress.setLastName(lastName);
		assertEquals("Name should not have changed", lastName, updatedAddress.getLastName());
		assertTrue("These objects should be equal", address.equals(updatedAddress));
		Customer updatedThriceCustomer = service.update(updatedTwiceCustomer);
		CustomerAddress updatedTwiceAddress = updatedThriceCustomer.getAddressByUid(addressUid);
		assertEquals("The address of the updated customer should still have the same ID", addressUid, updatedTwiceAddress.getUidPk());
		assertEquals("The address of the updated customer should still have the updated first name", uniqueName, updatedTwiceAddress.getFirstName());

		// Now reload customer and resave
		Customer reloadedCustomer = service.get(customer.getUidPk());
		Customer updatedReloadedCustomer = service.update(reloadedCustomer);
		assertNotNull("We should have got the customer back", updatedReloadedCustomer);
	}
	
	/**
	 * Test that a Customer's address can be created or updated while at the same time
	 * having the option to change the default billing or shipping address.
	 */
	@DirtiesDatabase
	@Test
	public void testAddOrUpdateCustomerAddress() {
		Customer customer = null;
		CustomerAddress address = null;
		
		// Assert that we can create an address and not make it the default.
		customer = service.add(createCustomer());
		customer = service.addOrUpdateAddress(customer, createAddress());
		assertEquals("The customer should only have 1 address.", 1, customer.getAddresses().size());
		assertNull("The customer should not have a preferred billing address.",  customer.getPreferredBillingAddress());
		assertNull("The customer should not have a preferred shipping address.", customer.getPreferredShippingAddress());
		
		// Assert that we can update an address and not make it the default.
		address = customer.getAddresses().get(0);
		address.setFirstName("a");
		customer = service.addOrUpdateAddress(customer, address);
		assertEquals("The customer should only have 1 address.", 1, customer.getAddresses().size());
		assertNull("The customer should not have a preferred billing address.",  customer.getPreferredBillingAddress());
		assertNull("The customer should not have a preferred shipping address.", customer.getPreferredShippingAddress());
		assertEquals("The persisted billing address should equal the in-memory billing address.", address, customer.getAddresses().get(0));
		
		// Assert that we can make an existing address the default billing address.
		address = customer.getAddresses().get(0);
		customer = service.addOrUpdateCustomerBillingAddress(customer, address);
		assertEquals("The customer should only have 1 address.", 1, customer.getAddresses().size());
		assertEquals("The customer should have a preferred billing address.", address, customer.getPreferredBillingAddress());
		assertNull("The customer should not have a preferred shipping address.", customer.getPreferredShippingAddress());
		assertEquals("The persisted billing address should equal the in-memory billing address.", address, customer.getAddresses().get(0));
	}
	
	/**
	 * Test adding a credit card to a customer.
	 */
	@DirtiesDatabase
	@Test
	public void testAddCustomerCreditCard() {
		// Persist a customer
		Customer customer = createCustomer();
		service.add(customer);

		// Add a credit card
		final CustomerCreditCard creditCard = createCreditCard();
		customer.addCreditCard(creditCard);
		assertEquals("The customer should now have a credit card", customer.getCreditCards().size(), 1);
		assertEquals("The customer card should match the one we created", customer.getCreditCards().get(0), creditCard);
		assertNotNull("The card should have a guid", customer.getCreditCards().get(0).getGuid());

		// Update the customer
		Customer updatedCustomer = service.update(customer);
		assertEquals("The returned object should have one credit card", updatedCustomer.getCreditCards().size(), 1);
		CustomerCreditCard updatedCard = updatedCustomer.getCreditCards().get(0);
		assertTrue("The customer's credit card should now be persistent", updatedCard.isPersisted());

		// Load the credit card back from the customer object
		CustomerCreditCard retrievedCard = updatedCustomer.getCreditCardByUid(updatedCard.getUidPk());
		assertNotNull("The credit card should be found in the database", retrievedCard);
		assertEquals("The GUID of the retrieved card should match that of the updated object", updatedCard.getGuid(), retrievedCard.getGuid());
		assertEquals("The card we got back keyed by guid should match", updatedCard, retrievedCard);
	}

	/**
	 * Test that editing a saved credit card and updating works correctly.
	 */
	@DirtiesDatabase
	@Test
	public void testEditCustomerCreditCard() {
		// Persist a customer
		Customer customer = createCustomer();
		service.add(customer);

		// Add a credit card to the customer and save to the DB
		customer.addCreditCard(createCreditCard());
		Customer updatedCustomer = service.update(customer);
		assertTrue("The credit card should be persistent", updatedCustomer.getCreditCards().get(0).isPersisted());

		// Make a change to the credit card and save to the DB
		CustomerCreditCard creditCard = updatedCustomer.getCreditCards().get(0);
		long cardUid = creditCard.getUidPk();
		String uniqueName = Utils.uniqueCode("name");
		creditCard.setCardHolderName(uniqueName);
		Customer updatedTwiceCustomer = service.update(updatedCustomer);
		CustomerCreditCard updatedCard = updatedTwiceCustomer.getCreditCardByUid(cardUid);
		assertEquals("The updated credit card should have the same ID", cardUid, updatedCard.getUidPk());
		assertEquals("The updated credit card should have the updated name", uniqueName, updatedCard.getCardHolderName());

		// Change a field to the same value and save again
		String expiryMonth = updatedCard.getExpiryMonth();
		updatedCard.setExpiryMonth(expiryMonth);
		assertEquals("Expiry month should not have changed", expiryMonth, updatedCard.getExpiryMonth());
		assertTrue("These objects should be equal", expiryMonth.equals(updatedCard.getExpiryMonth()));
		Customer updatedThriceCustomer = service.update(updatedTwiceCustomer);
		CustomerCreditCard updatedTwiceCard = updatedThriceCustomer.getCreditCardByUid(cardUid);
		assertEquals("The credit card of the updated customer should still have the same ID", cardUid, updatedTwiceCard.getUidPk());
		assertEquals("The credit card of the updated customer should still have the updated expiry month", expiryMonth, updatedTwiceCard
				.getExpiryMonth());

		// Now reload customer and resave
		Customer reloadedCustomer = service.get(customer.getUidPk());
		Customer updatedReloadedCustomer = service.update(reloadedCustomer);
		assertNotNull("We should have got the customer back", updatedReloadedCustomer);
	}

	/**
	 * Test that changing nothing other than the "default card" flag works.
	 */
	@DirtiesDatabase
	@Test
	public void testChangingDefaultCard() {
		// Persist a customer with 2 credit cards
		Customer customer = createCustomer();
		CustomerCreditCard creditCard1 = createCreditCard();
		CustomerCreditCard creditCard2 = createCreditCard();
		creditCard1.setDefaultCard(true);
		customer.addCreditCard(creditCard1);
		customer.addCreditCard(creditCard2);
		service.add(customer);

		assertEquals("Customer should have 2 credit cards", 2, customer.getCreditCards().size());
		long firstCardUid = 0L;
		long secondCardUid = 0L;
		for (CustomerCreditCard card : customer.getCreditCards()) {
			if (card.isDefaultCard()) {
				assertEquals("Default should be card 1", creditCard1.getCardHolderName(), card.getCardHolderName());
				firstCardUid = card.getUidPk();
			} else {
				assertFalse("Other card should not be default ", creditCard2.isDefaultCard());
				secondCardUid = card.getUidPk();
			}
		}
		assertFalse("First card Uid should have been found", firstCardUid == 0);
		assertFalse("Second card Uid should have been found", secondCardUid == 0);

		// Now change second card to default
		CustomerCreditCard secondCard = customer.getCreditCardByUid(secondCardUid);
		secondCard.setDefaultCard(true);
		customer.updateCreditCard(secondCard);
		assertTrue("Customer's second card should be the default", customer.getCreditCardByUid(secondCardUid).isDefaultCard());
		assertFalse("Customer's first card should no longer be the default", customer.getCreditCardByUid(firstCardUid).isDefaultCard());

		// Update the customer object
		Customer updatedCustomer = service.update(customer);
		assertTrue("Updated customer's second card should be the default", updatedCustomer.getCreditCardByUid(secondCardUid).isDefaultCard());
		assertFalse("Updated customer's first card should no longer be the default", updatedCustomer.getCreditCardByUid(firstCardUid)
				.isDefaultCard());

	}
	
	/**
	 * Verifies that {@link CustomerService#getCustomerLastModifiedDate(String) will return <code>null</code> if the GUID is invalid.
	 */
	@DirtiesDatabase
	@Test
	public void testGetLastModifiedDateInvalidGuid() {
		Date dateFromService = service.getCustomerLastModifiedDate("INVALID_GUID"); 
		assertNull("The method should return null when the GUID is invalid.", dateFromService);
	}



	
	/**
	 * Tests {@link CustomerService#getCustomerLastModifiedDate(String).
	 */
	@DirtiesDatabase
	@Test
	public void testGetLastModifiedDate() {
		Customer customer = createPersistedAnonymousCustomer(TEST_EMAIL_1, scenario.getStore());
		Date dateFromCustomer = customer.getLastModifiedDate();
		Date dateFromService = service.getCustomerLastModifiedDate(customer.getGuid());
		assertEquals("The date loaded from the object should be equal to the date retrieved by the service.", dateFromCustomer, dateFromService);
	}

	/**
	 * Test that a salt is persisted when a customer password is set.
	 */
	@DirtiesDatabase
	@Test
	public void testPasswordSalt() {
		Customer customer = createCustomer();
		customer.setClearTextPassword(PASSWORD);
		customer = service.update(customer);
		
		Customer loadedCustomer = service.load(customer.getUidPk());
		
		assertNotNull("There should be a salt value", loadedCustomer.getCustomerAuthentication().getSalt());
		assertFalse("The salt should not be empty", loadedCustomer.getCustomerAuthentication().getSalt().isEmpty());
	}
	
	/**
	 * Test that the same clear text password generates a unique encoded password each time (due to random salting).
	 */
	@DirtiesDatabase
	@Test
	public void testEncodedPasswordUniqueness() {
		Customer customer = createCustomer();
		customer.setClearTextPassword(PASSWORD);
		customer = service.update(customer);
		
		String password = customer.getPassword();
		assertNotNull("The customer should have a password set", password);
		assertFalse("The password should not be empty", password.isEmpty());
		
		customer.setClearTextPassword(PASSWORD);
		customer = service.update(customer);

		assertFalse("The encoded password should be different", password.equals(customer.getPassword()));
	}

	@Test
	@DirtiesDatabase
	public void ensureCascadeDeletionOfTokensOnCustomerDelete() {
		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(TOKEN_DISPLAY_VALUE)
				.withGatewayGuid(TOKEN_GATEWAY_GUID);

		PaymentToken firstToken = tokenBuilder
				.withValue("first-token-value").build();
		PaymentToken secondToken = tokenBuilder
				.withValue("second-token-value").build();
		PaymentToken thirdToken = tokenBuilder
				.withValue("third-token-value").build();

		final Customer customer = createCustomer();
		List<PaymentMethod> customerPaymentMethods = Arrays.<PaymentMethod>asList(firstToken, secondToken);
		customer.getPaymentMethods().addAll(customerPaymentMethods);
		customer.getPaymentMethods().setDefault(thirdToken);
		service.add(customer);

		service.remove(customer);
		for (PaymentMethod token : customer.getPaymentMethods().all()) {
			long uidPk = ((PaymentToken) token).getUidPk();
			assertNull(String.format("Token with uidpk {%s} should have been cascade deleted", uidPk),
					persistenceEngine.get(PaymentTokenImpl.class, uidPk));
		}
		assertNull(String.format("Token with uidpk {%s} should have been cascade deleted", thirdToken.getUidPk()),
				persistenceEngine.get(PaymentTokenImpl.class, thirdToken.getUidPk()));
	}

	@Test
	@DirtiesDatabase
	public void ensureCustomerSavesWithPopulatedTokenFields() {
		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue(TOKEN_DISPLAY_VALUE)
				.withGatewayGuid(TOKEN_GATEWAY_GUID);

		PaymentToken firstToken = tokenBuilder
				.withValue("first-token-value").build();
		PaymentToken secondToken = tokenBuilder
				.withValue("second-token-value").build();


		final Customer customer = createCustomer();
		List<PaymentMethod> customerPaymentMethods = Arrays.<PaymentMethod>asList(firstToken, secondToken);
		customer.getPaymentMethods().addAll(customerPaymentMethods);
		customer.getPaymentMethods().setDefault(secondToken);
		service.add(customer);

		Customer retrievedCustomer = service.get(customer.getUidPk());
		CustomerPaymentMethods retrievedPaymentTokens = retrievedCustomer.getPaymentMethods();

		assertEquals("Two payment tokens should have been saved on the customer", 2, retrievedPaymentTokens.all().size());
		for (PaymentMethod token : retrievedPaymentTokens.all()) {
			assertNotNull("Tokens referenced by the customer should exist in the database",
					persistenceEngine.get(PaymentTokenImpl.class, ((PaymentToken) token).getUidPk()));
		}
		assertNotNull("Default payment token should be persisted", customer.getPaymentMethods().getDefault());
	}

	@Test
	@DirtiesDatabase
	public void ensureCustomerSavesWithEmptyTokenFields() {
		final Customer customer = createCustomer();
		service.add(customer);

		Customer retrievedCustomer = service.get(customer.getUidPk());
		CustomerPaymentMethods retrievedCustomerCustomerPaymentMethods = retrievedCustomer.getPaymentMethods();
		assertEquals("No payment tokens should have been saved on the customer", 0, retrievedCustomerCustomerPaymentMethods.all().size());
		assertNull("No default payment token should have been saved on the customer", customer.getPaymentMethods().getDefault());
	}

    @Test(expected= InvalidDataAccessApiUsageException.class)
    @DirtiesDatabase
    public void ensureCustomerSavedWithTokenWithNullDisplayValueThrowsAnException() {
        PaymentToken tokenWithNoDisplayValue = new PaymentTokenImpl.TokenBuilder()
                .withGatewayGuid(TOKEN_GATEWAY_GUID)
                .withValue("token-value")
                .build();

        Customer customer = createCustomer();
        customer.getPaymentMethods().addAll(Arrays.<PaymentMethod>asList(tokenWithNoDisplayValue));

        service.add(customer);
    }

    @Test(expected= InvalidDataAccessApiUsageException.class)
    @DirtiesDatabase
    public void ensureCustomerSavedWithTokenWithNullTokenValueThrowsAnException() {
        PaymentToken tokenWithNoValue = new PaymentTokenImpl.TokenBuilder()
                .withGatewayGuid(TOKEN_GATEWAY_GUID)
                .withDisplayValue(TOKEN_DISPLAY_VALUE)
                .build();

        Customer customer = createCustomer();
        customer.getPaymentMethods().addAll(Arrays.<PaymentMethod>asList(tokenWithNoValue));

        service.add(customer);
    }

	//====================================================================================================================
	// Setup methods
	//====================================================================================================================
	
	private Customer createPersistedAnonymousCustomer(final String emailAddress, final Store store) {
		final Customer anonymousCustomer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		anonymousCustomer.setEmail(emailAddress);
		anonymousCustomer.setStoreCode(store.getCode());
		anonymousCustomer.setAnonymous(true);

		return service.add(anonymousCustomer);
	}

	private Customer createPersistedCustomer(final String emailAddress, final Store store) {
		final Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		customer.setEmail(emailAddress);
		customer.setStoreCode(store.getCode());
		customer.setAnonymous(false);

		return service.add(customer);
	}

	private CustomerAddress createAddress() {
		final CustomerAddress customerAddress = beanFactory.getBean("customerAddress");
		customerAddress.setFirstName("Test");
		customerAddress.setLastName("Test");
		customerAddress.setCountry(Locale.getDefault().getDisplayCountry());
		customerAddress.setStreet1("123 Testing St");
		customerAddress.setCity("Vancouver");
		customerAddress.setZipOrPostalCode("V5Y1N3");
		return customerAddress;
	}

	private Customer createCustomer() {
		final Customer customer = beanFactory.getBean("customer");
		customer.setUserId(Utils.uniqueCode("id"));
		customer.setCreationDate(new Date());
		customer.setLastEditDate(new Date());
		customer.setStatus(Customer.STATUS_ACTIVE);
		customer.setAnonymous(false);
		customer.setGuid((new RandomGuidImpl()).toString());
		customer.setEmail("test" + Math.round(Math.random() * 1000) + "@elasticpath.com");
		customer.setStoreCode(scenario.getStore().getCode());
		return customer;
	}

	private CustomerCreditCard createCreditCard() {
		return new CustomerCreditCardBuilder(beanFactory)
				.withCardNumber("4111111111111111")
				.withExpiryMonth("01")
				.withExpiryYear("2008")
				.withCardHolderName(Utils.uniqueCode("test user"))
				.withCardType("VISA").build();
	}
	
	private void changeUserIdMode(final int userIdMode) {
		SettingsService settingsService = beanFactory.getBean(ContextIdNames.SETTINGS_SERVICE);
		SettingDefinition userIdSetting = settingsService.getSettingDefinition("COMMERCE/SYSTEM/userIdMode");
		userIdSetting.setDefaultValue(String.valueOf(userIdMode));
		settingsService.updateSettingDefinition(userIdSetting);
	}
	
	protected <T> T doInTransaction(final TransactionCallback<T> callBack) {
		return getTac().getTxTemplate().execute(callBack);
	}
}
