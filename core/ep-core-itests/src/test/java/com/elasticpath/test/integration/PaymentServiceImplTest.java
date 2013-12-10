/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * TODO
 */
@Ignore
public class PaymentServiceImplTest {

//	private CheckoutService checkoutService;
//
//	private static final String GATEWAY_CYBERSOURCE = "CyberSourceGateway";
//
//	private static final String GATEWAY_NULL = "nullGateway";
//
//	private Product product;
//
//	protected Customer anonymousCustomer;
//
//	private ElasticPath elasticPath;
//
//	private SimpleStoreScenario scenario;
//
//	private CustomerScenario customerScenario;
//
//	private TestApplicationContext tac;
//
//	private TestDataPersisterFactory persisterFactory;
//
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
//		persisterFactory = tac.getPersistersFactory();
//		customerScenario = new CustomerScenario(scenario.getStore(), persisterFactory);
//		customerScenario.initialize();
//
//		checkoutService = (CheckoutService) elasticPath.getBean("checkoutService");
//
//		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
//				scenario.getWarehouse());
//
//		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties(GATEWAY_NULL));
//
//		assertTrue(scenario.getStore().getPaymentGatewayMap().size() > 0);
//
//		anonymousCustomer = persisterFactory.getStoreTestPersister().createDefaultCustomer(scenario.getStore());
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
//	public void testAdjustPayments() {
//		// construct and save new shopping cart
//		ShoppingCart shoppingCart = createShoppingCart();
//
//		// make new order payment
//		OrderPayment templateOrderPayment = newOrderPayment(anonymousCustomer.getEmail());
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// get order service
//		PaymentService paymentService = (PaymentService) elasticPath.getBean(ContextIdNames.PAYMENT_SERVICE);
//
//		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals("only one order should have been created by the checkout service", 1, ordersList.size());
//		Order order = ordersList.iterator().next();
//		assertFalse("Payment gateway list for order should not be empty", order.getStore().getPaymentGatewayMap().isEmpty());
//
//		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
//		assertNotNull("Order should have a modified by value", order.getModifiedBy());
//
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals("one shipment should have been created", 1, shipments.size());
//
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals("There should be one payment", 1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		assertEquals("Auth payment should be for the full amount of the order", order.getTotal(), authPayment.getAmount());
//
//		OrderShipment shipment = shipments.get(0);
//		assertNotNull("Order should have a billing address for the shipment", shipment.getOrder().getBillingAddress());
//
//		// call adjust payments. no change should be performed in terms of payments
//		paymentService.adjustShipmentPayment(shipment);
//
//		ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals("There should still only be one order", 1, ordersList.size());
//		order = ordersList.iterator().next();
//		payments = order.getOrderPayments();
//		assertEquals("There should still only be one payment", 1, payments.size());
//		assertFalse("Payment gateway list for order should still not be empty", order.getStore().getPaymentGatewayMap().isEmpty());
//
//		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
//
//		OrderSku newProductOrderSku = getNewProductOrderSku();
//		BigDecimal previousTotal = phShipment.getTotal();
//		phShipment.addShipmentOrderSku(newProductOrderSku);
//		assertTrue("the previous total amount should be less than the new one", previousTotal.compareTo(phShipment.getTotal()) < 0);
//
//		templateOrderPayment.setAmount(phShipment.getTotal());
//
//		assertEquals("shipment status should be inventory assigned", OrderShipmentStatus.INVENTORY_ASSIGNED, phShipment.getShipmentStatus());
//		assertEquals("shipment type should be physical", ShipmentType.PHYSICAL, phShipment.getType());
//		assertSame("shipment should reference the correct order", phShipment.getOrder(), order);
//		assertNotNull("Order should have a billing address for the shipment", phShipment.getOrder().getBillingAddress());
//		assertEquals("Payment method should be credit card", PaymentType.CREDITCARD, templateOrderPayment.getPaymentMethod());
//		assertFalse("Payment gateway list for shipment should not be empty", phShipment.getOrder().getStore().getPaymentGatewayMap().isEmpty());
//		assertNotNull("Shipment should have a payment gateway", phShipment.getOrder().getStore().getPaymentGatewayMap().get(
//				templateOrderPayment.getPaymentMethod()));
//
//		PaymentResult result = paymentService.adjustShipmentPayment(phShipment, templateOrderPayment);
//
//		assertEquals("two payments should have been conducted against the payment gateway", 2, result.getProcessedPayments().size());
//		assertEquals("Payment result should be OK", PaymentResult.CODE_OK, result.getResultCode());
//		assertEquals("There should be 3 payments: 1 auth + 1 reverse auth + 1 new auth (new amount)", 3, order.getOrderPayments().size());
//
//		OrderPayment reverseAuth = null;
//		OrderPayment newAuth = null;
//		OrderPayment oldAuth = null;
//		for (OrderPayment payment : order.getOrderPayments()) {
//			if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
//				assertNull("There should only be one reverse auth", reverseAuth);
//				reverseAuth = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
//				if (newAuth == null) {
//					newAuth = payment;
//				} else if (payment.getCreatedDate().compareTo(newAuth.getCreatedDate()) > 0) {
//					oldAuth = newAuth;
//					newAuth = payment;
//				} else {
//					oldAuth = payment;
//				}
//			}
//		}
//		assertNotNull("There should have been a reverse auth", reverseAuth);
//		assertNotNull("There should have been a new auth created", newAuth);
//		assertNotNull("The old auth should still exist", oldAuth);
//		assertEquals("Reverse auth should be for the original amount", oldAuth.getAmount(), reverseAuth.getAmount());
//		assertTrue("Amount reversed should be less than new auth amount", reverseAuth.getAmount().compareTo(newAuth.getAmount()) < 0);
//		assertEquals("New auth amount should be for the total of the shipment", phShipment.getTotal(), newAuth.getAmount());
//		assertEquals("New auth status should be approved", OrderPaymentStatus.APPROVED, newAuth.getStatus());
//
//		// add again product sku
//		OrderSku newProductOrderSku2 = getNewProductOrderSku();
//		newProductOrderSku2.setQuantity(20);
//		phShipment.addShipmentOrderSku(newProductOrderSku2);
//
//		// call adjust again...
//		templateOrderPayment.setAmount(phShipment.getTotal());
//
//		assertEquals("shipment type should be physical", ShipmentType.PHYSICAL, phShipment.getType());
//
//		PaymentResult result2 = paymentService.adjustShipmentPayment(phShipment, templateOrderPayment);
//
//		assertEquals("two payments should have been conducted against the payment gateway", 2, result2.getProcessedPayments().size());
//		assertEquals("payment result should be OK", PaymentResult.CODE_OK, result2.getResultCode());
//
//		assertEquals("There should be 5 payments : 2 old auths + 2 reverse auths + 1 new auth (new amount)", 5, order.getOrderPayments().size());
//
//		OrderPayment lastAuth = paymentService.getAllActiveAutorizationPayments(phShipment).iterator().next();
//		assertEquals("Auth should have been for the physical shipment total", phShipment.getTotal(), lastAuth.getAmount());
//		assertEquals("Auth should have been approved", OrderPaymentStatus.APPROVED, lastAuth.getStatus());
//
//	}
//
//	// =================== UTILITY METHODS ========================= \\
//
//	private PurchaseHistorySearchCriteria createPurchaseHistorySearchCriteria(final String userId, final String storeCode, final Date fromDate,
//			final Date toDate) {
//		PurchaseHistorySearchCriteria criteria = new PurchaseHistorySearchCriteriaImpl();
//		criteria.setUserId(userId);
//		criteria.setStoreCode(storeCode);
//		criteria.setFromDate(fromDate); // from 4 minutes ago
//		criteria.setToDate(toDate);
//		return criteria;
//	}
//
//	/**
//	 * @return
//	 */
//	private OrderSku getNewProductOrderSku() {
//
//		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
//				scenario.getWarehouse());
//
//		final OrderSku orderSku = (OrderSku) elasticPath.getBean(ContextIdNames.ORDER_SKU);
//
//		final ProductSku productSku = product.getDefaultSku();
//
//		orderSku.setProductUid(product.getUidPk());
//		orderSku.setListPrice(BigDecimal.ONE);
//		orderSku.setUnitPrice(BigDecimal.ONE);
//		final Date now = new Date();
//		orderSku.setCreatedDate(now);
//		orderSku.setLastModifiedDate(now);
//		orderSku.setQuantity(3);
//		orderSku.setSkuCode(productSku.getSkuCode());
//		orderSku.setProductSku(productSku);
//		orderSku.setDigitalAsset(productSku.getDigitalAsset());
//		orderSku.setTaxCode(product.getTaxCode().getCode());
//		orderSku.setTax(BigDecimal.ONE);
//		orderSku.setAllocatedQuantity(3);
//
//		if (productSku.getImage() != null) {
//			orderSku.setImage(productSku.getImage());
//		}
//
//		orderSku.setDisplayName("product_name2");
//
//		return orderSku;
//	}
//
//	/**
//	 * @return
//	 */
//	private OrderPayment newOrderPayment(final String customerEmail) {
//		OrderPayment orderPayment = (OrderPayment) elasticPath.getBean(ContextIdNames.ORDER_PAYMENT);
//		orderPayment.setCardHolderName("test test");
//		orderPayment.setCardType("001");
//		orderPayment.setCreatedDate(new Date());
//		orderPayment.setCurrencyCode("USD");
//		orderPayment.setEmail(customerEmail);
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
//		shoppingCart.addCartItem(product.getDefaultSku(), 1);
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
//		Address billingAddress = new CustomerAddressImpl();
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
//	private Set<PaymentGateway> setUpPaymentGatewayAndProperties(final String gatewayName) {
//
//		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
//
//		if (GATEWAY_CYBERSOURCE.equals(gatewayName)) {
//
//			final String[][] properties = { { "merchantID", "ekkontest" }, { "logMaximumSize", "10" }, { "sendToProduction", "false" },
//					{ "logDirectory", "log" }, { "targetAPIVersion", "1.24" }, { "keysDirectory", "resources/cybersource" },
//					{ "enableLog", "false" } };
//
//			final Properties propertiesMap = new Properties();
//
//			for (final String[] keyValuePair : properties) {
//				propertiesMap.put(keyValuePair[0], keyValuePair[1]);
//			}
//
//			gateways.add(persisterFactory.getStoreTestPersister().persistCyberSourcePaymentGateway(propertiesMap));
//		} else if (GATEWAY_NULL.equals(gatewayName)) {
//			gateways.add(persisterFactory.getStoreTestPersister().persistDefaultPaymentGateway());
//		}
//		return gateways;
//	}
//
//	public void testRefundPayment() {
//		// construct and save new shopping cart
//		ShoppingCart shoppingCart = createShoppingCart();
//
//		// make new order payment
//		OrderPayment templateOrderPayment = newOrderPayment(anonymousCustomer.getEmail());
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// get order service
//		PaymentService paymentService = (PaymentService) elasticPath.getBean(ContextIdNames.PAYMENT_SERVICE);
//
//		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals("only one order should have been created by the checkout service", 1, ordersList.size());
//		Order order = ordersList.iterator().next();
//		assertFalse("Payment gateway list for order should not be empty", order.getStore().getPaymentGatewayMap().isEmpty());
//
//		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
//		assertNotNull("Order should have a modified by value", order.getModifiedBy());
//
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals("one shipment should have been created", 1, shipments.size());
//
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals("There should be one payment", 1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		assertEquals("Auth payment should be for the full amount of the order", order.getTotal(), authPayment.getAmount());
//
//		OrderShipment shipment = shipments.get(0);
//		assertNotNull("Order should have a billing address for the shipment", shipment.getOrder().getBillingAddress());
//
//		OrderShipment phShipment = order.getAllShipments().iterator().next();
//		assertEquals("shipment status should be inventory assigned", OrderShipmentStatus.INVENTORY_ASSIGNED, phShipment.getShipmentStatus());
//		assertEquals("shipment type should be physical", ShipmentType.PHYSICAL, phShipment.getType());
//		assertSame("shipment should reference the correct order", phShipment.getOrder(), order);
//		assertNotNull("Order should have a billing address for the shipment", phShipment.getOrder().getBillingAddress());
//		assertEquals("Payment method should be credit card", PaymentType.CREDITCARD, templateOrderPayment.getPaymentMethod());
//		assertFalse("Payment gateway list for shipment should not be empty", phShipment.getOrder().getStore().getPaymentGatewayMap().isEmpty());
//		assertNotNull("Shipment should have a payment gateway", phShipment.getOrder().getStore().getPaymentGatewayMap().get(
//				templateOrderPayment.getPaymentMethod()));
//
//		phShipment = orderService.processReleaseShipment(phShipment);
//
//		assertEquals("shipment status should be RELEASED", OrderShipmentStatus.RELEASED, phShipment.getShipmentStatus());
//		assertEquals("shipment type should be physical", ShipmentType.PHYSICAL, phShipment.getType());
//
//		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackingcode"), true, new Date(), false,
//				getEventOriginatorHelper().getSystemOriginator());
//
//		OrderPayment capture = null;
//		OrderPayment auth = null;
//		for (OrderPayment payment : order.getOrderPayments()) {
//			if (payment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
//				assertNull("There should only be one capture", capture);
//				capture = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
//				assertNull("There should only be one auth", auth);
//				auth = payment;
//			}
//		}
//
//		assertNotNull(capture);
//		order = orderService.refundOrderPayment(order.getUidPk(), BigDecimal.TEN);
//
//		ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals("There should still only be one order", 1, ordersList.size());
//		order = ordersList.iterator().next();
//		payments = order.getOrderPayments();
//		assertEquals("There should be three payments", 3, payments.size());
//		assertFalse("Payment gateway list for order should still not be empty", order.getStore().getPaymentGatewayMap().isEmpty());
//
//		capture = null;
//		auth = null;
//		OrderPayment credit = null;
//		for (OrderPayment payment : order.getOrderPayments()) {
//			if (payment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
//				assertNull("There should only be one capture", capture);
//				capture = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
//				assertNull("There should only be one auth", auth);
//				auth = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)) {
//				assertNull("There should only be one credit", credit);
//				credit = payment;
//			}
//		}
//
//		assertNotNull(capture);
//		assertNotNull(auth);
//		assertNotNull(credit);
//
//		assertEquals(0, credit.getAmount().compareTo(BigDecimal.TEN));
//		assertEquals(0, capture.getAmount().compareTo(auth.getAmount()));
//
//		assertEquals(OrderPaymentStatus.APPROVED, credit.getStatus());
//
//		OrderPayment refundPayment = newOrderPayment(order.getCustomer().getEmail());
//
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// refund one's more with stand-alone
//		order = orderService.refundOrderPayment(order.getUidPk(), null, refundPayment, BigDecimal.ONE);
//
//		ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getCustomerSession().getCustomer().getGuid(), true);
//		assertEquals("There should still only be one order", 1, ordersList.size());
//		order = ordersList.iterator().next();
//		payments = order.getOrderPayments();
//		assertEquals("There should be three payments", 4, payments.size());
//		assertFalse("Payment gateway list for order should still not be empty", order.getStore().getPaymentGatewayMap().isEmpty());
//
//		capture = null;
//		auth = null;
//		boolean[] creditPayment = new boolean[2];
//		for (OrderPayment payment : order.getOrderPayments()) {
//			if (payment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)) {
//				assertNull("There should only be one capture", capture);
//				capture = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
//				assertNull("There should only be one auth", auth);
//				auth = payment;
//			} else if (payment.getTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)) {
//				if (!creditPayment[0]) {
//					creditPayment[0] = payment.getAmount().compareTo(BigDecimal.ONE) == 0;
//				}
//				if (!creditPayment[1]) {
//					creditPayment[1] = payment.getAmount().compareTo(BigDecimal.TEN) == 0;
//				}
//			}
//		}
//
//		assertNotNull(capture);
//		assertNotNull(auth);
//		assertTrue(creditPayment[0]);
//		assertTrue(creditPayment[1]);
//
//		assertEquals(0, capture.getAmount().compareTo(auth.getAmount()));
//
//		assertEquals(OrderPaymentStatus.APPROVED, credit.getStatus());
//
//	}
//
//	public EventOriginatorHelper getEventOriginatorHelper() {
//		return (EventOriginatorHelper) elasticPath.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
//	}
}
