/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * Tests for the CheckoutServiceImpl class.
 */
public class CheckoutServiceImplTest extends DbTestCase {

	@Autowired private ShoppingItemDtoFactory shoppingItemDtoFactory;
	@Autowired private CartDirector cartDirector;
	@Autowired private CheckoutService checkoutService;
	@Autowired private CustomerService customerService;
	@Autowired private ProductService productService;
	@Autowired private OrderService orderService;
	protected Customer customer;
	protected CustomerAddress address;
	protected CustomerCreditCard creditCard;
	protected CustomerSession customerSession;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception on error.
	 */
	@Before
	public void setUp() throws Exception {
//		((OrderServiceImpl)orderService).setEmailService(new FakeEmailServiceImpl());
		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());

		CustomerAddress customerAddress = persisterFactory.getStoreTestPersister().createCustomerAddress("Testington", "Testy",
				"123 Testing Circle", null, "Testville", "USA", "AL", "11111", "1234567890");
		customer = persisterFactory.getStoreTestPersister().persistCustomer(scenario.getStore(), Utils.uniqueCode("test") + "@elasticpath.com",
				persisterFactory.getStoreTestPersister().createCustomerCreditCard(customerAddress), customerAddress);
		address = customer.getAddresses().get(0);
		creditCard = customer.getCreditCards().get(0);
		customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
	}

	// FIXME: Failing test
	/**
	 * Integration test to check the general successful ordering of a single physical good with payment through the test cyber source payment
	 * gateway. General Steps: 1. Make Order 2. Pay 3. Check for success 4. Release shipment 5. Ensure capture is successful
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testCybersourceHappyCase() {
		// construct and save new shopping cart
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		addToCart(shoppingCart, product.getDefaultSku(), 1);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		checkoutService.checkout(shoppingCart, templateOrderPayment);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.list();
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();

		// only one shipment should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// only one payment should exist
		Set<OrderPayment> orderPayments = order.getOrderPayments();
		assertEquals(1, orderPayments.size());

		OrderPayment authPayment = orderPayments.iterator().next();

		// check authorization payment
		assertEquals(PaymentType.CREDITCARD, authPayment.getPaymentMethod());
		assertEquals(OrderPaymentStatus.APPROVED, authPayment.getStatus());
		assertEquals(OrderPayment.AUTHORIZATION_TRANSACTION, authPayment.getTransactionType());

		OrderShipment shipment = shipments.iterator().next();

		// there should be auth code
		assertNotNull(authPayment.getAuthorizationCode());

		// check if shopping cart is empty
		assertEquals(0, shoppingCart.getNumItems());

		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, shipment.getShipmentStatus());

		// set the appropriate status
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		// update order after setting the state of the shipment
		order = orderService.update(order);

		// release shipment
		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
				getEventOriginatorHelper().getSystemOriginator());

		orderPayments = order.getOrderPayments();

		// totally there should be two payments = 1 auth + 1 capture
		assertEquals(2, orderPayments.size());

		// get the capture payment
		OrderPayment capturePayment = null;
		for (OrderPayment currPayment : orderPayments) {
			if (OrderPayment.CAPTURE_TRANSACTION.equals(currPayment.getTransactionType())) {
				capturePayment = currPayment;
			}
		}
		// no capture payment?!?!
		assertNotNull(capturePayment);

		// total amount of capure should be > 0 and equal to the authorization payment amount
		assertTrue(capturePayment.getAmount().compareTo(BigDecimal.ZERO) > 0);
		assertEquals(authPayment.getAmount(), capturePayment.getAmount());

		// check the capture payment
		assertEquals(templateOrderPayment.getCardHolderName(), capturePayment.getCardHolderName());
		assertEquals(PaymentType.CREDITCARD, capturePayment.getPaymentMethod());
		assertEquals(OrderPaymentStatus.APPROVED, capturePayment.getStatus());
	}

	//FIXME: Failing test
	/**
	 * 1. Add digital product and physical product to the shopping cart 2. Checkout. Check payments and shipments. 3. Release the physical shipment
	 * 4. Check payments Payments should be 2 auths and 1 capture initially when creating the order. After releasing the shipment there has to be
	 * created one more capture on the previously created auth.
	 */
	@Ignore
	@DirtiesDatabase
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	@Test
	public void testSplitShipmentCybersourceHappyCase() {
		// construct and save new shopping cart
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());

		// ------------ create Physical product and add it to cart
		Product physicalProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		addToCart(shoppingCart, physicalProduct.getDefaultSku(), 1);

		// ------------ create Electronic product and add it to cart
		Product electronicProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		electronicProduct.getDefaultSku().setShippable(false);

		DigitalAsset digAsset = getBeanFactory().getBean(ContextIdNames.DIGITAL_ASSET);

		digAsset.setExpiryDays(1);
		digAsset.setFileName("file name");
		digAsset.setMaxDownloadTimes(1);

		electronicProduct.getDefaultSku().setDigitalAsset(digAsset);
		electronicProduct = productService.saveOrUpdate(electronicProduct);

		addToCart(shoppingCart, electronicProduct.getDefaultSku(), 2);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		checkoutService.checkout(shoppingCart, templateOrderPayment);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getShopper().getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(2, shipments.size());

		// one electronic
		assertEquals(1, order.getElectronicShipments().size());
		ElectronicOrderShipment electronicShipment = order.getElectronicShipments().iterator().next();
		assertEquals(OrderShipmentStatus.SHIPPED, electronicShipment.getShipmentStatus());

		// and one physical
		assertEquals(1, order.getPhysicalShipments().size());
		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, phShipment.getShipmentStatus());

		// three payments should exist: 2 auths and 1 capture (for electronic shipment)
		Set<OrderPayment> orderPayments = order.getOrderPayments();
		assertEquals(3, orderPayments.size());

		for (OrderPayment payment : orderPayments) {
			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(payment.getTransactionType())) {
				// check authorization payment
				assertEquals(PaymentType.CREDITCARD, payment.getPaymentMethod());
				assertEquals(OrderPaymentStatus.APPROVED, payment.getStatus());
				// there should be auth code
				assertNotNull(payment.getAuthorizationCode());
			}
		}
		PhysicalOrderShipment physicalShipment = order.getPhysicalShipments().iterator().next();

		// check if shopping cart is empty
		assertEquals(0, shoppingCart.getNumItems());

		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, physicalShipment.getShipmentStatus());

		// set the appropriate status
		physicalShipment.setStatus(OrderShipmentStatus.SHIPPED);
		// update order after setting the state of the shipment
		order = orderService.update(order);

		// release shipment
		order = orderService.completeShipment(physicalShipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
				getEventOriginatorHelper().getSystemOriginator());

		orderPayments = order.getOrderPayments();

		// totally there should be two payments = 2 auth + 2 capture
		assertEquals(4, orderPayments.size());

		int failedTransactions = 0;
		int captureTransactions = 0;
		BigDecimal capturedAmount = BigDecimal.ZERO;
		BigDecimal authorizedAmount = BigDecimal.ZERO;
		for (OrderPayment currPayment : orderPayments) {
			if (currPayment.getStatus() == OrderPaymentStatus.FAILED) {
				failedTransactions++;
			}
			if (currPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
				captureTransactions++;
				capturedAmount = capturedAmount.add(currPayment.getAmount());
			} else if (currPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
				authorizedAmount = authorizedAmount.add(currPayment.getAmount());
			}
		}
		assertEquals(0, failedTransactions);
		assertEquals(2, captureTransactions);
		assertEquals(order.getTotal(), capturedAmount);
		assertEquals(order.getTotal(), authorizedAmount);
	}

	// FIXME: Failing test
	/**
	 * Test Scenario: 1. Create shopping cart: 1 pre order product, 1 in stock 2. Checkout 3. Check payments 4. Release shipment 5. Check payments
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testPreAndBackOrderCheckoutAndCompleteShipmentCyberSource() {
		// construct and save new shopping cart
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());

		// ------------ create a Pre-order product and add it to cart
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
		// save the pre-order enabled product
		product = productService.saveOrUpdate(product);

		addToCart(shoppingCart, product.getDefaultSku(), 5);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		checkoutService.checkout(shoppingCart, templateOrderPayment);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getShopper().getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();

		// one shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		// the authorization should be for 1 dollar only
		assertEquals(0, BigDecimal.ONE.compareTo(authPayment.getAmount()));

		PhysicalOrderShipment shipment = order.getPhysicalShipments().iterator().next();

		// set the appropriate status
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		// update order after setting the state of the shipment
		order = orderService.update(order);

		assertTrue(shipment.isReadyForFundsCapture());

		// sleep for two seconds to let some time pass before we create second auth
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
				getEventOriginatorHelper().getSystemOriginator());

		payments = order.getOrderPayments();
		assertEquals("There should be a total of 4 payments: 1 auth for 1$, 1 auth reversal, 1 auth for the real amount, 1 capture.", 4, payments
				.size());

		for (OrderPayment currPayment : payments) {
			assertEquals(PaymentType.CREDITCARD, currPayment.getPaymentMethod());
			if (currPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
				assertEquals(order.getTotal(), currPayment.getAmount());
			} else if (currPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)
					&& currPayment.getAmount().compareTo(BigDecimal.ONE) > 0) {
				assertEquals(order.getTotal(), currPayment.getAmount());
			}
		}

		assertEquals(1, order.getAllShipments().size());
		shipment = order.getPhysicalShipments().iterator().next();

		assertEquals(OrderShipmentStatus.SHIPPED, shipment.getShipmentStatus());

		assertEquals(OrderStatus.COMPLETED, order.getStatus());

	}

	private void addToCart(ShoppingCart shoppingCart, ProductSku productSku, int quantity) {
		ShoppingItemDto dto = shoppingItemDtoFactory.createDto(productSku.getSkuCode(), quantity);
		cartDirector.addItemToCart(shoppingCart, dto);
		cartDirector.saveShoppingCart(shoppingCart);
	}

	/**
	 * Test the shipping address confirmation step of the checkout process. This is somewhat similar to what happens in
	 * ShippingAddressFormControllerImpl
	 */
	@DirtiesDatabase
	@Test
	public void testCheckoutShippingAddressStep() {
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());

		// Update the shopping cart
		shoppingCart.setShippingAddress(address);

		assertNotNull("no shipping service is provided for the selected address", shoppingCart.getShippingServiceLevelList());
		assertTrue("no shipping service is provided for the selected address", shoppingCart.getShippingServiceLevelList().size() > 0);

		// Set the billing address to the shipping address by default
		if (shoppingCart.getBillingAddress() == null) {
			if (customer.getPreferredBillingAddress() == null) {
				shoppingCart.setBillingAddress(address);
				customer.setPreferredBillingAddress(address);
			} else {
				shoppingCart.setBillingAddress(customer.getPreferredBillingAddress());
			}
		}

		// Update taxes and totals
		checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);
		assertNotNull("There should be a tax calculation result", shoppingCart.getTaxCalculationResult());

		// Update the customer
		customer.setPreferredShippingAddress(address);
		if (customer.isPersisted()) {
			customerService.verifyCustomer(customer);
			try {
				customerService.update(customer);
			} catch (Exception e) {
				fail("Should not have got an exception updating the customer " + e.getMessage());
			}
		}

	}

	// FIXME: Failing test
	/**
	 * Test the billing and review step of the checkout process. This is somewhat similar to what happens in BillingAndReviewFormControllerImpl
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testCheckoutBillingWithExistingCreditCard() {
		final CustomerCreditCard selectedCreditCard = creditCard;
		if (scenario.getStore().isCreditCardCvv2Enabled()) {
			// clean the cvv2, customer need to input it each time.
			selectedCreditCard.setSecurityCode("");
		} else {
			// Set valid code to avoid validation error when cvv2 is not in use.
			selectedCreditCard.setSecurityCode("000");
		}

		selectedCreditCard.setDefaultCard(true);
		customer.updateCreditCard(selectedCreditCard);

		final OrderPayment orderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, selectedCreditCard);

		final ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ProductSku productSku1 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();
		ProductSku productSku2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();
		ProductSku productSku3 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();
		ProductSku productSku4 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();
		ProductSku productSku5 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();

		addToCart(shoppingCart, productSku1, 1);
		addToCart(shoppingCart, productSku2, 1);
		addToCart(shoppingCart, productSku3, 2);
		addToCart(shoppingCart, productSku4, 1);
		addToCart(shoppingCart, productSku5, 4);

		assertEquals("There should be 5 cart items", 5, shoppingCart.getCartItems().size());
		assertEquals("There should be a total quantity 9 items in the cart", 9, shoppingCart.getNumItems());

		checkoutService.checkout(shoppingCart, orderPayment);

		Order order = shoppingCart.getCompletedOrder();
		assertNotNull("The shopping cart should contain a completed order", order);
		assertTrue("The order should contain shipments", order.getPhysicalShipments().size() > 0);
		for (PhysicalOrderShipment phShipment : order.getPhysicalShipments()) {
			assertTrue("The shipment address should be persistedt", phShipment.getShipmentAddress().isPersisted());
			assertNotNull("The shipment should have taxes", phShipment.getShipmentTaxes());
			for (OrderTaxValue taxValue : phShipment.getShipmentTaxes()) {
				assertTrue("The order tax value should be persisted", taxValue.isPersisted());
			}
			assertNotNull("The shipment should have skus", phShipment.getShipmentOrderSkus());
			assertEquals("The shipment should have all 5 skus ordered", 5, phShipment.getShipmentOrderSkus().size());
			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
				assertTrue("The order sku should be persisted", orderSku.isPersisted());
			}
		}

	}

	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
		final String[][] properties = { { "merchantID", "ekkontest" }, { "logMaximumSize", "10" }, { "sendToProduction", "false" },
				{ "logDirectory", "log" }, { "targetAPIVersion", "1.24" }, { "keysDirectory", "resources/cybersource" }, { "enableLog", "false" } };

		final Properties propertiesMap = new Properties();

		for (final String[] keyValuePair : properties) {
			propertiesMap.put(keyValuePair[0], keyValuePair[1]);
		}

		Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();

		// only one payment gateway of type CREDITCARD has to be active at a time
		// otherwise an authorization can be performed with the NullPaymentGateway and captured with Cybersource
		// this leads to erroneous results
		// gateways.add(persisterFactory.getStoreTestPersister().persistDefaultPaymentGateway());

		gateways.add(persisterFactory.getStoreTestPersister().persistCyberSourcePaymentGateway(propertiesMap));

		return gateways;
	}

	/**
	 * Returns an instance of <code>EventOriginatorHelper</code>.
	 *
	 * @return A new instance of <code>EventOriginatorHelper</code>.
	 */
	public EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}
}
