/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShoppingCartFactoryForTestApplication;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.payment.gateway.impl.NullPaymentGatewayPluginImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.impl.OrderFactoryImpl;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * Test {@link OrderFactoryImpl} functionality.
 */
public class OrderFactoryImplTest extends DbTestCase {

	@Autowired private CheckoutService checkoutService;

	@Autowired private ShopperService shopperService;

	@Autowired private CartDirector cartDirector;

	@Autowired private ShoppingItemDtoFactory shoppingItemDtoFactory;

	/** An anonymous customer, you can use them to make orders. */
	private Customer anonymousCustomer;

	@Autowired private CartOrderService cartOrderService;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 *
	 * @throws Exception when exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());
		anonymousCustomer = persisterFactory.getStoreTestPersister().createDefaultCustomer(scenario.getStore());

		// Reset the payment gateway for each test.
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
	}

	@After
	public void tearDown() throws Exception {
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
	}

	/**
	 * Integration test to check that the order factory populates the cart order GUID in a created order that has been placed in a FAILED state.
	 */
	@DirtiesDatabase
	@Test
	public void testOrderFactoryPopulatesCartOrderGuidOnOrderFailure() {
		final ShoppingCart shoppingCart = createAnonymousShoppingCartWithScenarioStore();
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));

		CartOrder cartOrder = getBeanFactory().getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(shoppingCart.getGuid());
		cartOrderService.saveOrUpdate(cartOrder);

		// make new order payment
		final OrderPayment templateOrderPayment = getOrderPayment();

		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(true);

		CheckoutResults checkoutResult = checkoutService.checkout(shoppingCart, templateOrderPayment, false);
		assertTrue("order should fail", checkoutResult.isOrderFailed());

		Order order = checkoutResult.getOrder();
		assertNotNull("There should have been an order on the checkout results", order);
		assertTrue("The order should be persistent", order.isPersisted());
		assertEquals("The order cart order GUID should be set to the corresponding cart order GUID", cartOrder.getGuid(), order.getCartOrderGuid());
	}

	/**
	 * Integration test to check that the order factory populates the cart order GUID in a created order that was successful.
	 */
	@DirtiesDatabase
	@Test
	public void testOrderFactoryPopulatesCartOrderGuidOnOrderSuccess() {
		final ShoppingCart shoppingCart = createAnonymousShoppingCartWithScenarioStore();
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));

		CartOrder cartOrder = getBeanFactory().getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(shoppingCart.getGuid());
		cartOrderService.saveOrUpdate(cartOrder);

		// make new order payment
		final OrderPayment templateOrderPayment = getOrderPayment();

		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);

		CheckoutResults checkoutResult = checkoutService.checkout(shoppingCart, templateOrderPayment, false);
		assertFalse("The order should not have failed", checkoutResult.isOrderFailed());

		Order order = checkoutResult.getOrder();
		assertNotNull("There should have been an order on the checkout results", order);
		assertTrue("The order should be persistent", order.isPersisted());
		assertEquals("The order cart order GUID should be set to the corresponding cart order GUID", cartOrder.getGuid(), order.getCartOrderGuid());
	}

	/**
	 * Creates the anonymous shopping cart with scenario store.
	 *
	 * @return the shopping cart
	 */
	private ShoppingCart createAnonymousShoppingCartWithScenarioStore() {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.getShopperMemento().setStoreCode(scenario.getStore().getCode());
		shopperService.save(shopper);

		final CustomerSession customerSession = getCustomerSession(shopper);

		final ShoppingCart shoppingCart = TestShoppingCartFactoryForTestApplication.getInstance().createNewCartWithMemento(shopper,
				scenario.getStore());

		// FIXME: Remove once shoppingCart does not delegate back to CustomerSession.
		shoppingCart.setCustomerSession(customerSession);

		shoppingCart.initialize();
		shoppingCart.setBillingAddress(getBillingAddress());
		shoppingCart.setShippingAddress(getBillingAddress());
		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario.getShippingServiceLevel()));
		shoppingCart.setSelectedShippingServiceLevelUid(scenario.getShippingServiceLevel().getUidPk());
		shoppingCart.setCurrency(Currency.getInstance(Locale.US));

		final ShoppingCartService shoppingCartService = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART_SERVICE);
		shoppingCartService.saveOrUpdate(shoppingCart);
		return shoppingCart;
	}

	/**
	 * Gets the customer session.
	 *
	 * @param shopper the shopper
	 * @return the customer session
	 */
	private CustomerSession getCustomerSession(final Shopper shopper) {
		final CustomerSession session = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		session.setCreationDate(new Date());
		session.setCurrency(Currency.getInstance(Locale.US));
		session.setLastAccessedDate(new Date());
		session.setGuid("" + System.currentTimeMillis());
		session.setLocale(Locale.US);
		session.getShopper().setCustomer(anonymousCustomer);
		return session;
	}

	/**
	 * Gets the order payment.
	 *
	 * @return the order payment
	 */
	private OrderPayment getOrderPayment() {
		final OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCardHolderName("test test");
		orderPayment.setCardType("001");
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail(anonymousCustomer.getEmail());
		orderPayment.setExpiryMonth("09");
		orderPayment.setExpiryYear("10");
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCvv2Code("1111");
		orderPayment.setUnencryptedCardNumber("4111111111111111");
		return orderPayment;
	}

	/**
	 * Initializes a mock billing address.
	 *
	 * @return the Address
	 */
	private Address getBillingAddress() {
		final Address billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("Billy");
		billingAddress.setLastName("Bob");
		billingAddress.setCountry("CA");
		billingAddress.setStreet1("1295 Charleston Road");
		billingAddress.setCity("Vancouver");
		billingAddress.setSubCountry("BC");
		billingAddress.setZipOrPostalCode("94043");
		billingAddress.setGuid(Utils.uniqueCode("address"));
		return billingAddress;
	}

	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
		gateways.add(persisterFactory.getStoreTestPersister().persistDefaultPaymentGateway());
		return gateways;
	}

	private Product createPhysicalProduct() {
		Product physicalProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(),
				scenario.getWarehouse());
		return physicalProduct;
	}

	public EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}
}
