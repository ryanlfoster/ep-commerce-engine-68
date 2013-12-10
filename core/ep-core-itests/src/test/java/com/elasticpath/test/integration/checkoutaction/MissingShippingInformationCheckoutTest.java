/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.test.integration.checkoutaction;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShoppingCartFactoryForTestApplication;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.payment.gateway.impl.NullPaymentGatewayPluginImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.actions.impl.InvalidShippingServiceLevelException;
import com.elasticpath.service.shoppingcart.actions.impl.MissingShippingAddressException;
import com.elasticpath.service.shoppingcart.actions.impl.MissingShippingServiceLevelException;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Test failing payments on checkout.
 */
public class MissingShippingInformationCheckoutTest extends BasicSpringContextTest {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private ShopperService shopperService;
	
	@Autowired
	private CartDirector cartDirector;
	
	@Autowired
	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	private SimpleStoreScenario scenario;

	private ShoppingCart shoppingCart;
	
	protected Customer anonymousCustomer;
	
	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception when exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());
		anonymousCustomer = getTac().getPersistersFactory().getStoreTestPersister().createDefaultCustomer(scenario.getStore());
		shoppingCart = createEmptyShoppingCartWithScenarioStore();
		
		// Reset the payment gateway for each test.
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
	}

	/**
	 * Integration test to check the happy path for check out of a valid cart with physical goods.
	 */
	@DirtiesDatabase
	@Test
	public void checkoutValidCartWithPhysicalGoods() {
		setupValidShippingServiceOnCart(shoppingCart);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		CheckoutResults checkoutResult = checkoutService.checkout(shoppingCart, getOrderPayment(), false);
		assertFalse("Order should succeed.", checkoutResult.isOrderFailed());
	}
	
	/**
	 * Integration test to check the happy path for check out of a valid cart with non-physical goods.
	 */
	@Ignore("FIXME: Checkout with electronic shipments will fail intermittently due to @PrePersist and @PostPersist doing too much")
	@DirtiesDatabase
	@Test
	public void checkoutValidCartWithNonPhysicalGoods() {
		setupValidShippingServiceOnCart(shoppingCart);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createNonShippableProduct(), 1));
		CheckoutResults checkoutResult = checkoutService.checkout(shoppingCart, getOrderPayment(), false);
		assertFalse("Order should succeed.", checkoutResult.isOrderFailed());
	}
	
	/**
	 * Checkout cart with missing shipping address.
	 */
	@DirtiesDatabase
	@Test(expected = MissingShippingAddressException.class)
	public void checkoutCartWithMissingShippingAddress() {
		setupValidShippingServiceOnCart(shoppingCart);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		shoppingCart.setShippingAddress(null);
		checkoutService.checkout(shoppingCart, getOrderPayment(), true);
	}
	
	/**
	 * Checkout cart with missing shipping service level.
	 */
	@DirtiesDatabase
	@Test(expected = MissingShippingServiceLevelException.class)
	public void checkoutCartWithMissingShippingServiceLevel() {
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		checkoutService.checkout(shoppingCart, getOrderPayment(), true);
	}
	
	/**
	 * Checkout cart with invalid shipping service level.
	 * Note: there are checks to validate the selected {@link ShippingServiceLevel} against the list of levels set on the cart.
	 * This test works around that by changing the list to one that doesn't include cart's selected service level.
	 */
	@DirtiesDatabase
	@Test(expected = InvalidShippingServiceLevelException.class)
	public void checkoutCartWithInvalidShippingServiceLevel() {
		setupInvalidShippingServiceLevelOnCart(shoppingCart);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		checkoutService.checkout(shoppingCart, getOrderPayment(), true);
	}
	
	private ShoppingCart createEmptyShoppingCartWithScenarioStore() {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopperService.save(shopper);
		
		final CustomerSession customerSession = getCustomerSessionForShopper(shopper);
		
		final ShoppingCart shoppingCart = TestShoppingCartFactoryForTestApplication.getInstance().createNewCartWithMemento(
				shopper, scenario.getStore());
		
		// FIXME: Remove once shoppingCart does not delegate back to CustomerSession.
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setBillingAddress(getAddress());
		shoppingCart.setShippingAddress(getAddress());
		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
		return shoppingCart;
	}
	
	private void setupValidShippingServiceOnCart(final ShoppingCart shoppingCart) {
		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario.getShippingServiceLevel()));
		shoppingCart.setSelectedShippingServiceLevelUid(scenario.getShippingServiceLevel().getUidPk());
	}

	private void setupInvalidShippingServiceLevelOnCart(final ShoppingCart shoppingCart) {
		shoppingCart.setShippingServiceLevelList(Arrays.asList(getInvalidShippingServiceLevel()));
	}

	private ShippingServiceLevel getInvalidShippingServiceLevel() {
		getTac().getPersistersFactory().getStoreTestPersister().persistShippingRegion("INVALID_SHIPPING_REGION_NAME", "INVALID_REGION");
		ShippingServiceLevel invalidShippingServiceLevel = 
			getTac().getPersistersFactory().getStoreTestPersister().persistShippingServiceLevelFixedPriceCalcMethod(scenario.getStore(), 
																									"INVALID_SHIPPING_REGION_NAME",
																									"2 Business Days (Canada Post - Regular Parcel)", 
																									"INVALID", 
																									"5.00");
		return invalidShippingServiceLevel;
	}

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
	
	private CustomerSession getCustomerSessionForShopper(final Shopper shopper) {
		final CustomerSession session = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		session.setCreationDate(new Date());
		session.setCurrency(Currency.getInstance(Locale.US));
		session.setLastAccessedDate(new Date());
		session.setGuid("" + System.currentTimeMillis());
		session.setLocale(Locale.US);
		session.getShopper().setCustomer(anonymousCustomer);
		return session;
	}

	private Address getAddress() {
		final Address address = getBeanFactory().getBean(ContextIdNames.CUSTOMER_ADDRESS);
		address.setFirstName("Billy");
		address.setLastName("Bob");
		address.setCountry("CA");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Vancouver");
		address.setSubCountry("BC");
		address.setZipOrPostalCode("V5N1T8");
		address.setGuid(Utils.uniqueCode("address"));
		return address;
	}

	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
		gateways.add(getTac().getPersistersFactory().getStoreTestPersister().persistDefaultPaymentGateway());
		return gateways;
	}

	private Product createPhysicalProduct() {
		Product physicalProduct = getTac().getPersistersFactory().getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		return physicalProduct;
	}
	
	private Product createNonShippableProduct() {
		Product digitalProduct = createPhysicalProduct();
		digitalProduct.getDefaultSku().setShippable(false);

		ProductService prodService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		digitalProduct = prodService.saveOrUpdate(digitalProduct);
		return digitalProduct;
	}
}
