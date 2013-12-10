/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.test.integration.cartorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.CustomerBuilder;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * Cart order service integration test.
 */
public class CartOrderServiceImplTest extends BasicSpringContextTest {

	private static final String INVALID_SHIPPING_SERVICE_LEVEL_GUID = "INVALID_SHIPPING_SERVICE_LEVEL_GUID";

	private static final String INVALID_ADDRESS_GUID = "INVALID_ADDRESS_GUID";

	private static final String INVALID_CARTORDER_GUID = "an invalid guid";

	private static final String CART_GUID = "CART-1";

	private static final String CARTORDER_GUID = "CARTORDER-1";

	private static final String CUSTOMER_BILLING_ADDRESS_GUID = "CUSTOMERADDRESS-1";

	private static final String CUSTOMER_SHIPPING_ADDRESS_GUID = "CUSTOMERBILLINGADDRESS-1";

	private static String DEFAULT_COUNTRY_CODE;

	private static String DEFAULT_SUBCOUNTRY_CODE;

	private static String DEFAULT_SHIPPING_SERVICE_LEVEL_GUID;

	private ShoppingCartSimpleStoreScenario scenario;


	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private CustomerAddressDao addressDao;

	@Autowired
	private CartOrderService cartOrderService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private CustomerBuilder customerBuilder;
	
	private Store store;

	private ShoppingCart cart;

	private Customer customer;

	private ProductSku shippableProductSku;

	/**
	 * A setup for the integration test.
	 */
	@Before
	public void initialize() {
		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);

		ShippingRegion defaultShippingRegion = scenario.getShippingRegion();
		Map<String, Region> regionMap = defaultShippingRegion.getRegionMap();
		Region region = regionMap.values().iterator().next();
		DEFAULT_COUNTRY_CODE = region.getCountryCode();
		DEFAULT_SUBCOUNTRY_CODE = region.getSubCountryCodeList().get(0);
		DEFAULT_SHIPPING_SERVICE_LEVEL_GUID = scenario.getShippingServiceLevel().getGuid();

		store = scenario.getStore();

		createAndPersistAddressWithGuid(CUSTOMER_BILLING_ADDRESS_GUID);
		createAndPersistAddressWithGuid(CUSTOMER_SHIPPING_ADDRESS_GUID);
		cart = configureAndPersistCart();
		shippableProductSku = scenario.getShippableProducts().get(0).getDefaultSku();
	}

	/**
	 * Ensure valid address and level are not sanitized.
	 */
	@DirtiesDatabase
	@Test
	public void ensureValidAddressAndLevelAreNotSanitized() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(CUSTOMER_SHIPPING_ADDRESS_GUID, DEFAULT_SHIPPING_SERVICE_LEVEL_GUID);

		CartOrder sanitizedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());
		
		assertEquals("Address guid is incorrect.", CUSTOMER_SHIPPING_ADDRESS_GUID, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid is incorrect.", DEFAULT_SHIPPING_SERVICE_LEVEL_GUID, 
				sanitizedCartOrder.getShippingServiceLevelGuid());
	}
	
	/**
	 * Ensure correct sanitation for null shipping info.
	 */
	@DirtiesDatabase
	@Test
	public void ensureCorrectSanitationForNullShippingInfo() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(null, null);

		CartOrder sanitizedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());
		
		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid should be null.", null, sanitizedCartOrder.getShippingServiceLevelGuid());
	}
	
	/**
	 * Ensure correct sanitation for invalid shipping info.
	 */
	@DirtiesDatabase
	@Test
	public void ensureCorrectSanitationForInvalidShippingInfo() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(INVALID_ADDRESS_GUID, INVALID_SHIPPING_SERVICE_LEVEL_GUID);

		CartOrder sanitizedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());
		
		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid should be null.", null, sanitizedCartOrder.getShippingServiceLevelGuid());
	}
	
	/**
	 * Ensure correct sanitation for invalid address.
	 */
	@DirtiesDatabase
	@Test
	public void ensureCorrectSanitationForInvalidAddress() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(INVALID_ADDRESS_GUID, DEFAULT_SHIPPING_SERVICE_LEVEL_GUID);

		CartOrder sanitizedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());
		
		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid should be null.", null, sanitizedCartOrder.getShippingServiceLevelGuid());
	}
		
	/**
	 * Ensure correct sanitation for invalid shipping service level.
	 */
	@DirtiesDatabase
	@Test
	public void ensureCorrectSanitationForInvalidShippingServiceLevel() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(CUSTOMER_SHIPPING_ADDRESS_GUID,
				INVALID_SHIPPING_SERVICE_LEVEL_GUID);

		CartOrder sanitizedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());
		
		assertEquals("Address guid is incorrect.", CUSTOMER_SHIPPING_ADDRESS_GUID, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid should be null.", null, sanitizedCartOrder.getShippingServiceLevelGuid());
	}

	/**
	 * Tests that all fields of cart order are persisted.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistenceOfCartOrderFields() {
		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setBillingAddressGuid(CUSTOMER_BILLING_ADDRESS_GUID);
		cartOrder.setShippingAddressGuid(CUSTOMER_SHIPPING_ADDRESS_GUID);
		cartOrder.setShippingServiceLevelGuid(DEFAULT_SHIPPING_SERVICE_LEVEL_GUID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		PaymentToken token = getPersistedToken();
		cartOrder.setPaymentMethod(token);

		cartOrderService.saveOrUpdate(cartOrder);

		CartOrder retrievedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());

		assertEquals("Customer billing address guid was not persisted", CUSTOMER_BILLING_ADDRESS_GUID, retrievedCartOrder.getBillingAddressGuid());
		assertEquals("Customer shipping address guid was not persisted", CUSTOMER_SHIPPING_ADDRESS_GUID, retrievedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping service level guid was not persisted", DEFAULT_SHIPPING_SERVICE_LEVEL_GUID, retrievedCartOrder.getShippingServiceLevelGuid());
		assertEquals("Shopping cart guid was not persisted", CART_GUID, retrievedCartOrder.getShoppingCartGuid());
		assertEquals("Payment method was not persisted", token, retrievedCartOrder.getPaymentMethod());
		assertEquals("The persisted cart order was not identical to expected cart order", cartOrder, retrievedCartOrder);
	}

	@DirtiesDatabase
	@Test
	public void testRetrieveBillingAddress() {
		CartOrder cartOrder = createAndSaveCartOrder();
		Address retrievedBillingAddress = cartOrderService.getBillingAddress(cartOrder);
		assertNotNull(retrievedBillingAddress);
		assertEquals(CUSTOMER_BILLING_ADDRESS_GUID, retrievedBillingAddress.getGuid());
	}

	@DirtiesDatabase
	@Test
	public void testRetrieveShippingAddress() {
		CartOrder cartOrder = createAndSaveCartOrder();
		Address retrievedShippingAddress = cartOrderService.getShippingAddress(cartOrder);
		assertNotNull(retrievedShippingAddress);
		assertEquals(CUSTOMER_SHIPPING_ADDRESS_GUID, retrievedShippingAddress.getGuid());
	}

	/**
	 * Assert that a CartOrder which is updated will also update its ShoppingCart's Last Modified Date.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateCartAlsoUpdatesShoppingCartLastModifiedDate() {
		createAndSaveCartOrder();
		ShoppingCart cart = loadShoppingCart();
		Date initialDate = cart.getLastModifiedDate();

		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		cartOrder.setBillingAddressGuid("1234"); // This means it will be saved to the DB.
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		cart = loadShoppingCart();
		Date updatedDate = cart.getLastModifiedDate();
		assertTrue(initialDate.before(updatedDate));
	}

	/**
	 * Assert that a CartOrder can be removed and its address and shopping cart are not removed.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveCartOrder() {
		CartOrder cartOrder = createAndSaveCartOrder();

		CartOrder retrievedCartOrder = cartOrderService.findByGuid(cartOrder.getGuid());

		cartOrderService.remove(retrievedCartOrder);
		assertNull(cartOrderService.findByGuid(CARTORDER_GUID));

		assertNotNull(loadShoppingCart());
		PaymentTokenImpl paymentMethod = (PaymentTokenImpl) cartOrder.getPaymentMethod();
		assertNotNull("Payment method should not have been cascade deleted", persistenceEngine.get(PaymentTokenImpl.class, paymentMethod.getUidPk()));
		assertNotNull("Billing address should not have been deleted", addressDao.findByGuid(CUSTOMER_BILLING_ADDRESS_GUID));
		assertNotNull("Shipping address should not have been deleted", addressDao.findByGuid(CUSTOMER_SHIPPING_ADDRESS_GUID));
	}

	/**
	 * Assert that an Address can be removed and the CartOrder and ShoppingCart remain.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveAddress() {
		createAndSaveCartOrder();
		addressDao.remove(addressDao.findByGuid(CUSTOMER_BILLING_ADDRESS_GUID));
		assertNull(addressDao.findByGuid(CUSTOMER_BILLING_ADDRESS_GUID));
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		assertNotNull(cartOrder);
		assertNotNull(cartOrder.getShoppingCartGuid());
		assertNull(cartOrderService.getBillingAddress(cartOrder));
	}

	/**
	 * Assert that a CartOrder can have its address removed and updated.
	 */
	@DirtiesDatabase
	@Test
	public void testNullAddress() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		cartOrder.setBillingAddressGuid(null);
		cartOrderService.saveOrUpdate(cartOrder);

		cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		assertNotNull(cartOrder);
		assertNull(cartOrder.getBillingAddressGuid());
		assertNotNull(addressDao.findByGuid(CUSTOMER_BILLING_ADDRESS_GUID));
	}

	/**
	 * Assert that a CartOrder will throw an exception if its ShoppingCart GUID is set to null.
	 */
	@DirtiesDatabase
	@Test(expected = IllegalArgumentException.class)
	public void testNullShoppingCart() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		cartOrder.setShoppingCartGuid(null);
	}

	/**
	 * Asserts that a removed cart order will not exist.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveCartOrderByShoppingCartGuid() {
		createAndSaveCartOrder();
		ShoppingCart shoppingCart = loadShoppingCart();
		assertNotNull(shoppingCart);
		cartOrderService.removeIfExistsByShoppingCart(shoppingCart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);
		assertNull(cartOrder);
	}

	/**
	 * Asserts that the {@link CartOrderService#removeIfExistsByShoppingCart(ShoppingCart)} will not fail on a non-existing cart order.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveNonExistingCartOrderByShoppingCartGuid() {
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);
		assertNull(cartOrder);
		cartOrderService.removeIfExistsByShoppingCart(cart);
	}


	/**
	 * Test create if not exists with default billing address and credit card.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateIfNotExistsWithDefaultBillingAddressAndCreditCard() {
		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);

		assertEquals("Customer preferred billing address guid and cart order billing address guid should be equal.",
				customer.getPreferredBillingAddress().getGuid(), cartOrder.getBillingAddressGuid());
		assertEquals("Customer credit card and cart order payment method should be equal.",
				customer.getCreditCards().get(0), cartOrder.getPaymentMethod());
	}

	/**
	 * Test create if not exists with default billing address.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateIfNotExistsWithDefaultBillingAddress() {
		customer.setCreditCards(new ArrayList<CustomerCreditCard>());
		customer = customerService.update(customer);
		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);

		assertEquals("Customer preferred billing address guid and cart order billing address guid should be equal.",
				customer.getPreferredBillingAddress().getGuid(), cartOrder.getBillingAddressGuid());
		assertNull("card order payment method should be null.", cartOrder.getPaymentMethod());
	}

	/**
	 * Test clearing customer credit cards.
	 */
	@DirtiesDatabase
	@Test
	public void testClearingCustomerCreditCards() {
		List<Long> creditCardUidsBeforeRemoval = getCreditCardUids();

		customer.setCreditCards(new ArrayList<CustomerCreditCard>());
		customer = customerService.update(customer);
		assertEquals("No credit cards should be saved on updated Customer.", 0, customer.getCreditCards().size());
		customer = customerService.get(customer.getUidPk());
		assertEquals("No credit cards should be saved on persisted Customer.", 0, customer.getCreditCards().size());

		List<CustomerCreditCard> orphanedCreditCards = findCreditCardsByUids(creditCardUidsBeforeRemoval);
		assertTrue("deleted credit cards should not be orphaned", orphanedCreditCards.isEmpty());
	}

	private List<Long> getCreditCardUids() {
		List<CustomerCreditCard> creditCards = customer.getCreditCards();
		List<Long> creditCardUids = new ArrayList<Long>();
		for (CustomerCreditCard creditCard : creditCards) {
			creditCardUids.add(creditCard.getUidPk());
		}
		return creditCardUids;
	}

	private List<CustomerCreditCard> findCreditCardsByUids(final List<Long> creditCardUidsBeforeRemoval) {
		String selectCreditCardsByUidPks = "SELECT cc FROM CustomerCreditCardImpl cc WHERE cc.uidPk in (:list)";
		return persistenceEngine.retrieve(selectCreditCardsByUidPks, creditCardUidsBeforeRemoval);
	}


	/**
	 * Assert that the {@link CartOrderService#createIfNotExists(cartGuid)} will create a CartOrder with default payment method GUID, and the billing
	 * address GUID is null because the Customer has no default billing address.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateIfNotExistsWithDefaultCreditCard() {
		customer.setPreferredBillingAddress(null);
		customer = customerService.update(customer);

		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);

		assertNull("card order billing address guid should be null.", cartOrder.getBillingAddressGuid());
		assertEquals("Customer credit card payment method and cart order payment method should be equal.",
				customer.getCreditCards().get(0), cartOrder.getPaymentMethod());
	}

	/**
	 * Assert that the {@link CartOrderService#createIfNotExists(cartGuid)} will create a CartOrder without payment method GUID and the billing
	 * address GUID because the Customer has no such info.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateIfNotExistsWithNoDefaults() {
		customer.setPreferredBillingAddress(null);
		customer.setCreditCards(new ArrayList<CustomerCreditCard>());
		customer = customerService.update(customer);

		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);

		assertNull("Cart order billing address guid should be null.", cartOrder.getBillingAddressGuid());
		assertNull("Cart order payment method should be null.", cartOrder.getPaymentMethod());
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for the main flow.
	 */
	@DirtiesDatabase
	@Test
	public void testFindGuidsByCustomerGuid() {
		List<String> guidsBefore = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), customer.getGuid());
		assertTrue("No cart order GUID should be found, as there are no cart orders yet.", guidsBefore.isEmpty());

		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), customer.getGuid());
		assertEquals("Only one cart order should be found.", 1, guids.size());
		assertTrue("The cart order GUID should match that of the newly created cart order.", guids.contains(cartOrder.getGuid()));
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for an invalid customer GUID.
	 */
	@DirtiesDatabase
	@Test
	public void testFindGuidsByCustomerGuidInvalidGuid() {
		cartOrderService.createIfNotExists(CART_GUID);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), "INVALID_GUID");
		assertTrue("No cart order guids should be found, as the customer GUID does not match.", guids.isEmpty());
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for an invalid store code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindGuidsByCustomerGuidInvalidStoreCode() {
		cartOrderService.createIfNotExists(CART_GUID);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid("BAD STORE CODE", customer.getGuid());
		assertTrue("No cart order guids should be found, as the store code is invalid.", guids.isEmpty());
	}

	/**
	 * Assert that a ShoppingCart can be removed and the CartOrder is returned as null but the Address stays. The CartOrder may still be in the DB...
	 */
	@DirtiesDatabase
	@Test
	public void testGetCartOrderStoreCode() {
		createAndSaveCartOrder();
		String storeCode = cartOrderService.getStoreCodeForCartOrder(CARTORDER_GUID);
		assertEquals("The store code from service should match the one from scenario.", store.getCode(), storeCode);
	}

	/**
	 * Tests getting the last modified date of a CartOrder and checks it against the one from the shopping cart.
	 */
	@DirtiesDatabase
	@Test
	public void testLastModifiedDate() {
		createAndSaveCartOrder();
		ShoppingCart shoppingCart = loadShoppingCart();
		Date lastModifiedDateFromDB = shoppingCart.getLastModifiedDate();

		Date cartOrderLastModifiedDate = cartOrderService.getCartOrderLastModifiedDate(CARTORDER_GUID);
		assertEquals("Last modified date of the CartOrder should be equal to the one in the database.",
				lastModifiedDateFromDB,
				cartOrderLastModifiedDate);
	}

	/**
	 * Test populating transient fields on shopping cart.
	 */
	@DirtiesDatabase
	@Test
	public void testPopulatingTransientFieldsOnShoppingCart() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		ShoppingCart populatedShoppingCart = cartOrderService.populateShoppingCartTransientFields(cart, cartOrder);
		ShippingServiceLevel expectedShippingServiceLevel = scenario.getShippingServiceLevel();
		assertEquals(expectedShippingServiceLevel, populatedShoppingCart.getSelectedShippingServiceLevel());
		List<ShippingServiceLevel> populatedShippingServiceLevels = populatedShoppingCart.getShippingServiceLevelList();
		assertTrue(populatedShippingServiceLevels.contains(expectedShippingServiceLevel));
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	/**
	 * Test populating transient fields on shopping cart with null shipping address.
	 */
	@DirtiesDatabase
	@Test
	public void testPopulatingTransientFieldsOnShoppingCartWithNullShippingAddress() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		cartOrder.setShippingAddressGuid("non-existent");
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		ShoppingCart populatedShoppingCart = cartOrderService.populateShoppingCartTransientFields(cart, cartOrder);
		assertNull("Shopping cart selected service level should be empty.", populatedShoppingCart.getSelectedShippingServiceLevel());
		List<ShippingServiceLevel> populatedShippingServiceLevels = populatedShoppingCart.getShippingServiceLevelList();
		assertTrue("Shopping cart service levels should be empty.", populatedShippingServiceLevels.isEmpty());
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	/**
	 * Test populating transient fields on shopping cart with non-existent shipping service level.
	 */
	@DirtiesDatabase
	@Test
	public void testPopulatingTransientFieldsOnShoppingCartWithNonExistentShippingServiceLevel() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByGuid(CARTORDER_GUID);
		cartOrder.setShippingServiceLevelGuid("non-existent");
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		ShoppingCart populatedShoppingCart = cartOrderService.populateShoppingCartTransientFields(cart, cartOrder);
		assertNull("Shopping cart selected service level should be empty.", populatedShoppingCart.getSelectedShippingServiceLevel());
		List<ShippingServiceLevel> populatedShippingServiceLevels = populatedShoppingCart.getShippingServiceLevelList();
		assertTrue("Shopping cart service levels should be empty.", populatedShippingServiceLevels.isEmpty());
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	/**
	 * Verifies that getting the last modified date of non-existing CartOrder returns <code>null</code>.
	 */
	@DirtiesDatabase
	@Test
	public void testLastModifiedDateInvalidGuid() {
		Date cartOrderLastModifiedDate = cartOrderService.getCartOrderLastModifiedDate(INVALID_CARTORDER_GUID);
		assertNull("The last modified date should be null, iff the cart order is not found.", cartOrderLastModifiedDate);
	}

	private ShoppingCart configureAndPersistCart() {
		ShopperService shopperService = getBean(ContextIdNames.SHOPPER_SERVICE);
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		StoreTestPersister storeTestPersister = getTac().getPersistersFactory().getStoreTestPersister();
		customer = storeTestPersister.createDefaultCustomer(store);
		shopper.setCustomer(customer);
		shopper.setStoreCode(store.getCode());
		shopper = shopperService.save(shopper);

		// TODO: Handrolling customer session is probably not a good idea
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		final ShoppingCartImpl shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(store);
		shoppingCart.getShoppingCartMemento().setGuid(CART_GUID);
		shoppingCart.setCustomerSession(customerSession);
		shopper.setCurrentShoppingCart(shoppingCart);
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	private CartOrder createAndSaveCartOrder() {
		
		CartOrder cartOrder = new CartOrderImpl();
		cartOrder.setGuid(CARTORDER_GUID);
		cartOrder.setBillingAddressGuid(CUSTOMER_BILLING_ADDRESS_GUID);
		cartOrder.setShippingAddressGuid(CUSTOMER_SHIPPING_ADDRESS_GUID);
		cartOrder.setShippingServiceLevelGuid(DEFAULT_SHIPPING_SERVICE_LEVEL_GUID);
		cartOrder.setShoppingCartGuid(CART_GUID);

		cartOrder.setPaymentMethod(getPersistedToken());

		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		assertTrue(cartOrder.isPersisted());
		return cartOrder;
	}

	private PaymentToken getPersistedToken() {

		PaymentToken token = new PaymentTokenImpl.TokenBuilder()
			.withDisplayValue("**** **** **** 1234")
			.withGatewayGuid("1234")
			.withValue("token-value")
			.build();
		
		Customer customer = customerBuilder.withStoreCode(store.getCode())
				.withTokens(token)
				.build();
		
		customer = customerService.add(customer);
		
		token = (PaymentToken) customer.getPaymentMethods().all().iterator().next();
		return token;
	}

	private Address createAndPersistAddressWithGuid(final String guid) {
		Address address = new CustomerAddressImpl();
		address.setGuid(guid);
		address.setCountry(DEFAULT_COUNTRY_CODE);
		address.setSubCountry(DEFAULT_SUBCOUNTRY_CODE);
		return addressDao.saveOrUpdate(address);
	}
	
	private ShoppingCart loadShoppingCart() {
		return shoppingCartService.findByGuid(CART_GUID);
	}
	
    private void shouldHavePhysicalGoodInCart() {
        final ShoppingItemDto dto = new ShoppingItemDto(shippableProductSku.getSkuCode(), 1);
		cartDirector.addItemToCart(cart, dto);
		cart = shoppingCartService.saveOrUpdate(cart);
    }
    
	private CartOrder shouldCreateAndPersistCardOrderWithAddressAndLevelGuids(final String shippingAddressGuid, final String shippingServiceLevelGuid) {
		cartOrderService.createIfNotExists(CART_GUID);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(CART_GUID);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		cartOrder.setShippingServiceLevelGuid(shippingServiceLevelGuid);

		cartOrderService.saveOrUpdate(cartOrder);
		return cartOrder;
	}
	
	private <T> T getBean(final String name) {
		return getBeanFactory().getBean(name);
	}

}
 