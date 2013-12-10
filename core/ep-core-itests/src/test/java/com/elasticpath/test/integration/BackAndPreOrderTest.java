/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * TODO
 */
@Ignore
public class BackAndPreOrderTest {

//	private CheckoutService checkoutService;
//
//	private Product product;
//
//	private ShoppingCart shoppingCart;
//
//	/** An anonymous customer, you can use them to make orders. */
//	protected Customer anonymousCustomer;
//
//	private ElasticPath elasticPath;
//
//	private SimpleStoreScenario scenario;
//
//	private TestApplicationContext tac;

//	/**
//	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
//	 */
//	@Override
//	public void setUp() throws Exception {
//		super.setUp();
//		tac = TestApplicationContext.getInstance();
//		tac.useDb(getClass().getName());
//		scenario = (SimpleStoreScenario) tac.useScenario(SimpleStoreScenario.class);
//		elasticPath = tac.getElasticPath();
//
//		// Retrieve services we need for this test.
//		checkoutService = (CheckoutService) elasticPath.getBean("checkoutService");
//
//		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());
//
//		product = tac.getPersistersFactory().getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//				scenario.getCategory(), scenario.getWarehouse());
//		// save the pre-order enabled product
//		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
//		ProductService productService = elasticPath.getBean(ContextIdNames.PRODUCT_SERVICE);
//		product = productService.saveOrUpdate(product);
//		anonymousCustomer = tac.getPersistersFactory().getStoreTestPersister().createDefaultCustomer(scenario.getStore());
//		shoppingCart = createShoppingCart();
//		shoppingCart.addCartItem(product.getDefaultSku(), 5);
//	}
//
//	@Override
//	public void tearDown() throws Exception {
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//	}
//
//	// ============================ TESTS ========================= \\
//
//	/**
//	 * Test Scenario: 1. Create shopping cart: 1 pre order product, 1 in stock 2. Checkout 3. Check payments 4. Release shipment 5. Check payments
//	 */
//	public void testPreAndBackOrderCheckoutAndCompleteShipment() {
//		// make new order payment
//		OrderPayment templateOrderPayment = getOrderPayment();
//
//		// setup the payment gateway props
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// only one order should have been created by the checkout service
//		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals(1, ordersList.size());
//		Order order = ordersList.iterator().next();
//
//		// two shipments should have been created
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals(1, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals(1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		// the authorization should be for 1 dollar only
//		assertEquals(0, BigDecimal.ONE.compareTo(authPayment.getAmount()));
//
//		PhysicalOrderShipment shipment = order.getPhysicalShipments().iterator().next();
//
//		// set the appropriate status
//		shipment.setStatus(OrderShipmentStatus.PACKING);
//		// update order after setting the state of the shipment
//		order = orderService.update(order);
//
//		assertTrue(shipment.isReadyForFundsCapture());
//
//		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
//				getEventOriginatorHelper().getSystemOriginator());
//
//		assertEquals(OrderStatus.COMPLETED, order.getStatus());
//
//		assertEquals(1, order.getAllShipments().size());
//		shipment = order.getPhysicalShipments().iterator().next();
//
//		assertEquals(OrderShipmentStatus.SHIPPED, shipment.getShipmentStatus());
//
//		payments = order.getOrderPayments();
//		assertEquals("There should be 4 payments: 1 auth for $1, 1 reverse auth, 1 auth for the real amount, and 1 capture for the real amount.", 4,
//				payments.size());
//
//		OrderPayment capturePayment = null;
//		OrderPayment authSecondPayment = null;
//		for (OrderPayment currPayment : payments) {
//			assertEquals(PaymentType.CREDITCARD, currPayment.getPaymentMethod());
//			if (currPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
//				assertEquals(order.getTotal(), currPayment.getAmount());
//				capturePayment = currPayment;
//			} else if (currPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)
//					&& currPayment.getAmount().compareTo(BigDecimal.ONE) > 0) {
//				assertEquals(order.getTotal(), currPayment.getAmount());
//				authSecondPayment = currPayment;
//			}
//
//		}
//		assertNotNull(capturePayment);
//		assertNotNull(authSecondPayment);
//
//	}
//
//	/**
//	 * Test Scenario: 1. Create shopping cart: 1 pre-ordered product, 1 in stock 2. Checkout 3. Check payments 4. Try to release shipment. Capture
//	 * will throw an exception which will lead to shipment to stay in the old state and an email to be sent to the store administrator. 5. Check
//	 * payments. There should be 1 failed.
//	 */
//	public void testFailingCapturePaymentOnCompleteShipmentInPreOrderState() {
//		// make new order payment
//		OrderPayment templateOrderPayment = getOrderPayment();
//
//		// setup the payment gateway props
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// only one order should have been created by the checkout service
//		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals(1, ordersList.size());
//		Order order = ordersList.iterator().next();
//
//		// two shipments should have been created
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals(1, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals(1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		// the authorization should be for 1 dollar only
//		assertEquals(0, BigDecimal.ONE.compareTo(authPayment.getAmount()));
//
//		PhysicalOrderShipment shipment = order.getPhysicalShipments().iterator().next();
//
//		// set the appropriate status
//		shipment.setStatus(OrderShipmentStatus.PACKING);
//		// update order after setting the state of the shipment
//		order = orderService.update(order);
//
//		assertTrue(shipment.isReadyForFundsCapture());
//
//		// setup the payment gateway props
//		NullPaymentGatewayImpl.setFailOnCapture(true);
//		// save the old order status
//		OrderStatus oldOrderStatus = order.getStatus();
//
//		try {
//			order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
//					getEventOriginatorHelper().getSystemOriginator());
//			fail("capture payment should throw exception and we should never get to here.");
//		} catch (CompleteShipmentFailedException e) {
//			// skip exception
//		}
//
//		order = orderService.get(order.getUidPk());
//
//		assertEquals(oldOrderStatus, order.getStatus());
//
//		assertEquals(1, order.getAllShipments().size());
//		shipment = order.getPhysicalShipments().iterator().next();
//
//		assertEquals(OrderShipmentStatus.PACKING, shipment.getShipmentStatus());
//
//		payments = order.getOrderPayments();
//		assertEquals(
//				"There should be 4 payments: 1 auth for $1, 1 reverse auth, 1 auth for the real amount, and 1 failed capture for the real amount.",
//				4, payments.size());
//
//		int failedCapturePayments = 0;
//		for (OrderPayment currPayment : payments) {
//			assertEquals(PaymentType.CREDITCARD, currPayment.getPaymentMethod());
//			if (currPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION) && currPayment.getStatus() == OrderPaymentStatus.FAILED) {
//				failedCapturePayments++;
//				assertEquals(order.getTotal(), currPayment.getAmount());
//			} else if (currPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)
//					&& currPayment.getAmount().compareTo(BigDecimal.ONE) > 0) {
//				assertEquals(order.getTotal(), currPayment.getAmount());
//			}
//		}
//		assertEquals(1, failedCapturePayments);
//
//		// TODO check if an email has been sent to the store administrator
//	}
//
//	/**
//	 * Test Scenario: 1. Create shopping cart: 1 pre-ordered product, 1 in stock 2. Checkout 3. Check payments 4. Try to release shipment. Auth
//	 * transaction will throw an exception which will lead to shipment to stay in the old state and an email to be sent to the store administrator.
//	 * 5. Check payments. There should be 1 failed auth.
//	 */
//	public void testFailingAuthPaymentOnCompleteShipmentInPreOrderState() {
//		// make new order payment
//		OrderPayment templateOrderPayment = getOrderPayment();
//
//		// setup the payment gateway props
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// only one order should have been created by the checkout service
//		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals(1, ordersList.size());
//		Order order = ordersList.iterator().next();
//
//		// two shipments should have been created
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals(1, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals(1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		// the authorization should be for 1 dollar only
//		assertEquals(0, BigDecimal.ONE.compareTo(authPayment.getAmount()));
//
//		PhysicalOrderShipment shipment = order.getPhysicalShipments().iterator().next();
//
//		// set the appropriate status
//		shipment.setStatus(OrderShipmentStatus.PACKING);
//		// update order after setting the state of the shipment
//		order = orderService.update(order);
//
//		assertTrue(shipment.isReadyForFundsCapture());
//
//		// setup the payment gateway props
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(true);
//		// save the old order status
//		OrderStatus oldOrderStatus = order.getStatus();
//
//		try {
//			order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
//					getEventOriginatorHelper().getSystemOriginator());
//			fail("Attempting to complete the shipment should have resulted in an exception since the payment auth should have failed.");
//		} catch (CompleteShipmentFailedException e) {
//			// exception expected, fetch the order again to get any new payments that may have been logged to it
//			order = orderService.get(order.getUidPk());
//		}
//
//		assertEquals(oldOrderStatus, order.getStatus());
//
//		assertEquals(1, order.getAllShipments().size());
//		shipment = order.getPhysicalShipments().iterator().next();
//
//		assertEquals(OrderShipmentStatus.PACKING, shipment.getShipmentStatus());
//
//		payments = order.getOrderPayments();
//		assertEquals("There should be 2 payments: 1 auth for $1, and 1 failed auth for the real amount.", 2, payments.size());
//
//		int failedCapturePayments = 0;
//		int failedAuthPayments = 0;
//		for (OrderPayment currPayment : payments) {
//			assertEquals(PaymentType.CREDITCARD, currPayment.getPaymentMethod());
//			if (currPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION) && currPayment.getStatus() == OrderPaymentStatus.FAILED) {
//				failedCapturePayments++;
//				assertEquals(order.getTotal(), currPayment.getAmount());
//			} else if (currPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)
//					&& currPayment.getAmount().compareTo(BigDecimal.ONE) > 0 && currPayment.getStatus() == OrderPaymentStatus.FAILED) {
//				failedAuthPayments++;
//				assertEquals(order.getTotal(), currPayment.getAmount());
//			}
//		}
//		assertEquals(0, failedCapturePayments);
//		assertEquals(1, failedAuthPayments);
//
//		// TODO check if an email has been sent to the store administrator
//	}
//
//	// =================== UTILITY METHODS ========================= \\
//
//	/**
//	 * @return
//	 */
//	private OrderPayment getOrderPayment() {
//		OrderPayment orderPayment = (OrderPayment) elasticPath.getBean(ContextIdNames.ORDER_PAYMENT);
//		orderPayment.setCardHolderName("test test");
//		orderPayment.setCardType("001");
//		orderPayment.setCreatedDate(new Date());
//		orderPayment.setCurrencyCode("USD");
//		orderPayment.setEmail(anonymousCustomer.getEmail());
//		orderPayment.setExpiryMonth("09");
//		orderPayment.setExpiryYear("10");
//		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
//		orderPayment.setCvv2Code("1111");
//		orderPayment.setUnencryptedCardNumber("4111111111111111");
//		return orderPayment;
//	}
//
//	/**
//	 * @return
//	 */
//	private ShoppingCart createShoppingCart() {
//		ShoppingCart shoppingCart = (ShoppingCart) elasticPath.getBean(ContextIdNames.SHOPPING_CART);
//		shoppingCart.setDefaultValues();
//
//		shoppingCart.setCustomerSession(getCustomerSession());
//
//		shoppingCart.setBillingAddress(getBillingAddress());
//		shoppingCart.setShippingAddress(getBillingAddress());
//
//		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario.getShippingServiceLevel()));
//		shoppingCart.setSelectedShippingServiceLevelUid(scenario.getShippingServiceLevel().getUidPk());
//		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
//		shoppingCart.setStore(scenario.getStore());
//
//		ShoppingCartService shoppingCartService = (ShoppingCartService) elasticPath.getBean(ContextIdNames.SHOPPING_CART_SERVICE);
//
//		shoppingCartService.update(shoppingCart);
//
//		return shoppingCart;
//	}
//
//	/**
//	 * @return
//	 */
//	private CustomerSession getCustomerSession() {
//		CustomerSession session = (CustomerSession) elasticPath.getBean(ContextIdNames.CUSTOMER_SESSION);
//		session.setCreationDate(new Date());
//		session.setCurrency(Currency.getInstance(Locale.US));
//		session.setLastAccessedDate(new Date());
//		session.setGuid("" + System.currentTimeMillis());
//		session.setLocale(Locale.US);
//		session.setCustomer(anonymousCustomer);
//
//		return session;
//	}
//
//	/**
//	 * Initializes a mock billing address.
//	 * 
//	 * @return the Address
//	 */
//	private Address getBillingAddress() {
//		Address billingAddress = new OrderAddressImpl();
//		billingAddress.setFirstName("Billy");
//		billingAddress.setLastName("Bob");
//		billingAddress.setCountry("US");
//		billingAddress.setStreet1("1295 Charleston Road");
//		billingAddress.setCity("Mountain View");
//		billingAddress.setSubCountry("CA");
//		billingAddress.setZipOrPostalCode("94043");
//		billingAddress.setGuid(Utils.uniqueCode("address"));
//
//		return billingAddress;
//	}
//
//	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
//		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
//		gateways.add(TestApplicationContext.getInstance().getPersistersFactory().getStoreTestPersister().persistDefaultPaymentGateway());
//		return gateways;
//	}
//
//	public EventOriginatorHelper getEventOriginatorHelper() {
//		return (EventOriginatorHelper) elasticPath.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
//	}
}
