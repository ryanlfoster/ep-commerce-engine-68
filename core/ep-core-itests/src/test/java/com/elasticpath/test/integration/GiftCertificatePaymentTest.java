/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * Test using Gift Certificates during the check out process to pay for orders.
 */
@Ignore
public class GiftCertificatePaymentTest {
//	private static final char[] GC_AMOUNT = "500".toCharArray();
//
//	private CheckoutService checkoutService;
//
//	private GiftCertificateTransactionService giftCertificateTransactionService;
//
//	private PaymentGateway failingPaymentGateway;
//
//	private OrderPayment templateOrderPayment;
//
//	protected Customer customer;
//
//	protected CustomerAddress address;
//
//	protected CustomerCreditCard creditCard;
//
//	protected CustomerSession customerSession;
//
//	private CustomerScenario customerScenario;
//
//	private ElasticPath elasticPath;
//
//	private SimpleStoreScenario scenario;
//
//	private TestApplicationContext tac;
//
//	private TestDataPersisterFactory persisterFactory;
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
//		giftCertificateTransactionService = (GiftCertificateTransactionService) elasticPath.getBean("giftCertificateTransactionService");
//
//		setUpPaymentGatewayAndProperties();
//		assertFalse(scenario.getStore().getPaymentGatewayMap().isEmpty());
//		assertFalse(scenario.getStore().getPaymentGateways().isEmpty());
//
//		final AttributeService attributeService = (AttributeService) this.elasticPath.getBean(ContextIdNames.ATTRIBUTE_SERVICE);
//		if (attributeService == null) {
//			throw new EpSystemException("Could not load attribute service");
//		}
//
//		this.elasticPath.setCustomerProfileAttributes(attributeService.getCustomerProfileAttributesMap());
//
//		Map<String, String> props = new HashMap<String, String>();
//		props.put("encryption.key", "keyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
//		props.put("email.image.url", "image_url");
//		props.put("cmclient.webinf.path", ".");
//
//		elasticPath.setProperties(props);
//
//		customer = customerScenario.getCustomer();
//		address = customerScenario.getAddress();
//		creditCard = customerScenario.getCreditCard();
//		customerSession = customerScenario.getCustomerSession();
//		templateOrderPayment = getOrderPayment();
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
//	public void testMultiplePaymentSourcesFailingCapture() {
//
//	}
//
//	public void testMultiplePaymentSourcesFailingAuth() {
//
//	}
//
//	/**
//	 * Tests a gift certificate payment. No credit card should be used.
//	 */
//	public void testGiftCertificateOnlyCheckoutHappyCase() {
//		templateOrderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
//		final ShoppingCart shoppingCart = createShoppingCartWithAppliedGiftCertificate();
//		addPhysicalItemToCart(shoppingCart);
//
//		GiftCertificateService giftCertificateService = (GiftCertificateService) elasticPath.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
//		GiftCertificate giftCertificate = shoppingCart.getAppliedGiftCertificates().iterator().next();
//		assertNotNull(giftCertificate);
//
//		final GiftCertificate freshGc = giftCertificateService.findByGiftCertificateCode(giftCertificate.getGiftCertificateCode());
//		assertNotNull(freshGc);
//
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
//		// one shipment should have been created
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals(1, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals(1, payments.size());
//		OrderPayment authPayment = payments.iterator().next();
//		// the authorization should be for the total amount
//		assertEquals(order.getTotal(), authPayment.getAmount());
//		assertEquals(OrderPaymentStatus.APPROVED, authPayment.getStatus());
//		assertEquals(OrderPayment.AUTHORIZATION_TRANSACTION, authPayment.getTransactionType());
//		assertEquals(PaymentType.GIFT_CERTIFICATE, authPayment.getPaymentMethod());
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
//		// check the GC referenced by the auth payment
//		assertNotNull(authPayment.getGiftCertificate());
//		assertNotNull(authPayment.getGiftCertificate().getGiftCertificateCode());
//
//		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
//				getEventOriginatorHelper().getSystemOriginator());
//
//		Set<OrderPayment> orderPayments = order.getOrderPayments();
//
//		// totally there should be two payments = 1 auth + 1 capture
//		assertEquals(2, orderPayments.size());
//
//		// get the capture payment
//		OrderPayment capturePayment = null;
//		for (OrderPayment currPayment : orderPayments) {
//			if (OrderPayment.CAPTURE_TRANSACTION.equals(currPayment.getTransactionType())) {
//				capturePayment = currPayment;
//			}
//		}
//		// no capture payment?!?!
//		assertNotNull(capturePayment);
//
//		// total amount of capure should be > 0 and equal to the authorization payment amount
//		assertTrue(capturePayment.getAmount().compareTo(BigDecimal.ZERO) > 0);
//		assertEquals(authPayment.getAmount(), capturePayment.getAmount());
//
//		// check the capture payment
//		assertEquals(PaymentType.GIFT_CERTIFICATE, capturePayment.getPaymentMethod());
//		assertEquals(OrderPaymentStatus.APPROVED, capturePayment.getStatus());
//	}
//
//	public void testSplitShipmentGiftCertAndCreditCardCheckoutHappyCase() {
//		templateOrderPayment.setPaymentMethod(PaymentType.CREDITCARD);
//		ShoppingCart shoppingCart = createShoppingCartWithAppliedGiftCertificate();
//		addPhysicalItemToCart(shoppingCart);
//		addElectronicItemToCart(shoppingCart);
//		GiftCertificate giftCertificate = shoppingCart.getAppliedGiftCertificates().iterator().next();
//		assertNotNull(giftCertificate);
//
//		// set failing payment gateway off
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//
//		// set the quantity of the product to be 15 so that the total is more than $100 (the GC value)
//		shoppingCart.getCartItems().iterator().next().setQuantity(150);
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		List<Order> ordersList = orderService.findOrderByCustomerEmail(customer.getEmail(), true);
//		assertEquals("Only one order should have been created by the checkout service.", 1, ordersList.size());
//		Order order = ordersList.iterator().next();
//
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals("Two shipments should have been created.", 2, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals("There should be 4 payments: 2 CreditCard auths, 1 GiftCert auth, 1 CreditCard capture", 4, payments.size());
//
//		Set<OrderPayment> creditCardAuths = new HashSet<OrderPayment>();
//		Set<OrderPayment> giftCertAuths = new HashSet<OrderPayment>();
//		Set<OrderPayment> creditCardCaptures = new HashSet<OrderPayment>();
//		Set<OrderPayment> giftCertCaptures = new HashSet<OrderPayment>();
//		BigDecimal authPaymentAmount = BigDecimal.ZERO;
//		for (OrderPayment payment : payments) {
//			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(payment.getTransactionType())) {
//				switch (payment.getPaymentMethod()) {
//				case CREDITCARD:
//					creditCardAuths.add(payment);
//					break;
//				case GIFT_CERTIFICATE:
//					giftCertAuths.add(payment);
//					break;
//				}
//				authPaymentAmount = authPaymentAmount.add(payment.getAmount());
//			} else if (OrderPayment.CAPTURE_TRANSACTION.equals(payment.getTransactionType())) {
//				switch (payment.getPaymentMethod()) {
//				case CREDITCARD:
//					creditCardCaptures.add(payment);
//					break;
//				case GIFT_CERTIFICATE:
//					giftCertCaptures.add(payment);
//					break;
//				}
//			}
//		}
//		assertEquals("There should be 2 CreditCard auths.", 2, creditCardAuths.size());
//		assertEquals("There should be one GiftCert auth.", 1, giftCertAuths.size());
//		assertEquals("There should be one CreditCard capture.", 1, creditCardCaptures.size());
//		assertTrue("There should be no GiftCert captures.", giftCertCaptures.isEmpty());
//		assertEquals("The sum of authorization amounts should be for the total of the order.", order.getTotal(), authPaymentAmount);
//		final OrderPayment giftCertAuth = giftCertAuths.iterator().next();
//		assertEquals("All the GiftCert amount should have been used for the auth.", new BigDecimal(GC_AMOUNT).setScale(2), giftCertAuth.getAmount());
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
//		payments = order.getOrderPayments();
//		assertEquals("There should now be 6 payments: 2 CreditCard auths, 1 GiftCert auth, 2 CreditCard captures, and 1 GiftCert capture.", 6,
//				payments.size());
//
//		creditCardAuths = new HashSet<OrderPayment>();
//		giftCertAuths = new HashSet<OrderPayment>();
//		creditCardCaptures = new HashSet<OrderPayment>();
//		giftCertCaptures = new HashSet<OrderPayment>();
//		BigDecimal totalCaptureAmount = BigDecimal.ZERO;
//		for (OrderPayment payment : payments) {
//			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(payment.getTransactionType())) {
//				switch (payment.getPaymentMethod()) {
//				case CREDITCARD:
//					creditCardAuths.add(payment);
//					break;
//				case GIFT_CERTIFICATE:
//					giftCertAuths.add(payment);
//					break;
//				}
//			} else if (OrderPayment.CAPTURE_TRANSACTION.equals(payment.getTransactionType())) {
//				switch (payment.getPaymentMethod()) {
//				case CREDITCARD:
//					creditCardCaptures.add(payment);
//					break;
//				case GIFT_CERTIFICATE:
//					giftCertCaptures.add(payment);
//					break;
//				}
//				totalCaptureAmount = totalCaptureAmount.add(payment.getAmount());
//			}
//		}
//		assertEquals("There should be 2 CreditCard captures.", 2, creditCardCaptures.size());
//		assertEquals("There should be 1 GiftCert capture.", 1, giftCertCaptures.size());
//		assertEquals("Total amount captured should be equal to the total order amount.", order.getTotal(), totalCaptureAmount);
//		OrderPayment giftCertificateCapturePayment = giftCertCaptures.iterator().next();
//		assertEquals("The captured amount on the GiftCert should equal the amount authorized on the GiftCert.", giftCertAuth.getAmount(),
//				giftCertificateCapturePayment.getAmount());
//
//		// check the capture payments
//		assertEquals("GiftCert capture should have been approved.", OrderPaymentStatus.APPROVED, giftCertificateCapturePayment.getStatus());
//		for (OrderPayment creditCardCapturePayment : creditCardCaptures) {
//			assertEquals("CreditCard capture should have been approved.", OrderPaymentStatus.APPROVED, creditCardCapturePayment.getStatus());
//		}
//
//		// check the gift certificate balance
//		GiftCertificateService giftCertificateService = (GiftCertificateService) elasticPath.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
//		final GiftCertificate freshGc = giftCertificateService.findByGiftCertificateCode(giftCertificate.getGiftCertificateCode());
//		assertNotNull("The GiftCert should still exist in the database.", freshGc);
//
//		assertEquals("The GiftCert's balance should be 0.", 0, BigDecimal.ZERO.compareTo(giftCertificateTransactionService.getBalance(freshGc)));
//		assertEquals("The GiftCert's reserved amount should be 0.", 0, BigDecimal.ZERO.compareTo(giftCertificateTransactionService
//				.getReservedAmount(freshGc)));
//	}
//
//	public void testOnePhysicalShipmentGiftCertAndCreditCardCheckoutHappyCase() {
//		templateOrderPayment.setPaymentMethod(PaymentType.CREDITCARD);
//
//		final ShoppingCart shoppingCart = createShoppingCartWithAppliedGiftCertificate();
//		addPhysicalItemToCart(shoppingCart);
//
//		GiftCertificate giftCertificate = shoppingCart.getAppliedGiftCertificates().iterator().next();
//		assertNotNull("GiftCert should have been applied to the cart.", giftCertificate);
//
//		// set failing payment gateway off
//		NullPaymentGatewayImpl.setFailOnCapture(false);
//		NullPaymentGatewayImpl.setFailOnPreAuthorize(false);
//
//		// set the quantity of the product to be 15 so that the total is more than $100 (the GC value)
//		shoppingCart.getCartItems().iterator().next().setQuantity(150);
//
//		// checkout
//		checkoutService.checkout(shoppingCart, templateOrderPayment);
//
//		// get order service
//		OrderService orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		// only one order should have been created by the checkout service
//		List<Order> ordersList = orderService.findOrderByCustomerEmail(customer.getEmail(), true);
//		assertEquals(1, ordersList.size());
//		Order order = ordersList.iterator().next();
//
//		List<OrderShipment> shipments = order.getAllShipments();
//		assertEquals("One shipment should have been created.", 1, shipments.size());
//
//		// check payments
//		Set<OrderPayment> payments = order.getOrderPayments();
//		assertEquals("There should be two payments: 1 GiftCert auth, and 1 CreditCard auth.", 2, payments.size());
//
//		OrderPayment creditCardAuth = null;
//		OrderPayment giftCertAuth = null;
//		BigDecimal authPaymentAmount = BigDecimal.ZERO;
//		for (OrderPayment authPayment : payments) {
//			authPaymentAmount = authPaymentAmount.add(authPayment.getAmount());
//			if (authPayment.getPaymentMethod() == PaymentType.CREDITCARD) {
//				creditCardAuth = authPayment;
//			} else if (authPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE) {
//				giftCertAuth = authPayment;
//			}
//		}
//		assertNotNull("There should be a CreditCard auth.", creditCardAuth);
//		assertNotNull("There should be a GiftCert auth.", giftCertAuth);
//		assertEquals("The sum of authorization amounts should equal the total order amount", order.getTotal(), authPaymentAmount);
//		assertEquals("All the gift cert amount should have been used for the auth.", 0, new BigDecimal(GC_AMOUNT)
//				.compareTo(giftCertAuth.getAmount()));
//
//		PhysicalOrderShipment shipment = order.getPhysicalShipments().iterator().next();
//
//		// set the appropriate status
//		shipment.setStatus(OrderShipmentStatus.PACKING);
//		// update order after setting the state of the shipment
//		order = orderService.update(order);
//
//		assertTrue("The shipment should be ready for funds capture now.", shipment.isReadyForFundsCapture());
//
//		order = orderService.completeShipment(shipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
//				getEventOriginatorHelper().getSystemOriginator());
//
//		Set<OrderPayment> orderPayments = order.getOrderPayments();
//
//		assertEquals("There should be 4 payments in total now: 2 auths, and 2 captures", 4, orderPayments.size());
//
//		// get the capture payment
//		OrderPayment creditCardCapturePayment = null;
//		OrderPayment giftCertificateCapturePayment = null;
//		for (OrderPayment currPayment : orderPayments) {
//			if (OrderPayment.CAPTURE_TRANSACTION.equals(currPayment.getTransactionType())
//					&& currPayment.getPaymentMethod() == PaymentType.CREDITCARD) {
//				creditCardCapturePayment = currPayment;
//			} else if (OrderPayment.CAPTURE_TRANSACTION.equals(currPayment.getTransactionType())
//					&& currPayment.getPaymentMethod() == PaymentType.GIFT_CERTIFICATE) {
//				giftCertificateCapturePayment = currPayment;
//			}
//		}
//		assertNotNull("There should be a capture on the CreditCard.", creditCardCapturePayment);
//		assertNotNull("There should be a capture on the GiftCert.", giftCertificateCapturePayment);
//		assertTrue("Total amount of CreditCard capture should be > 0.", creditCardCapturePayment.getAmount().compareTo(BigDecimal.ZERO) > 0);
//		assertEquals("CreditCard capture amount should be equal to the CreditCard auth amount.", creditCardAuth.getAmount(),
//				creditCardCapturePayment.getAmount());
//		assertTrue("Total amount of GiftCert capture should be > 0.", giftCertificateCapturePayment.getAmount().compareTo(BigDecimal.ZERO) > 0);
//		assertEquals("GiftCert capture amount should be equal to the GiftCert auth amount.", giftCertAuth.getAmount(), giftCertificateCapturePayment
//				.getAmount());
//		assertEquals("GiftCert capture should have been approved.", OrderPaymentStatus.APPROVED, giftCertificateCapturePayment.getStatus());
//		assertEquals("CreditCard capture should have been approved.", OrderPaymentStatus.APPROVED, creditCardCapturePayment.getStatus());
//
//		// check the gift certificate balance
//		GiftCertificateService giftCertificateService = (GiftCertificateService) elasticPath.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
//		final GiftCertificate freshGc = giftCertificateService.findByGiftCertificateCode(giftCertificate.getGiftCertificateCode());
//		assertNotNull("The GiftCert should still exist in the db.", freshGc);
//		assertEquals("The GiftCert balance should be 0.", 0, BigDecimal.ZERO.compareTo(giftCertificateTransactionService.getBalance(freshGc)));
//		assertEquals("The GiftCert reserved amount should be 0.", 0, BigDecimal.ZERO.compareTo(giftCertificateTransactionService
//				.getReservedAmount(freshGc)));
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
//		orderPayment.setEmail(customer.getEmail());
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
//	private ShoppingCart createShoppingCartWithAppliedGiftCertificate() {
//		ShoppingCart shoppingCart = createEmptyShoppingCart();
//		shoppingCart.applyGiftCertificate(persisterFactory.getStoreTestPersister().persistGiftCertificate(scenario.getStore(),
//				new BigDecimal(GC_AMOUNT)));
//		return shoppingCart;
//	}
//
//	private void setUpPaymentGatewayAndProperties() {
//		failingPaymentGateway = new NullPaymentGatewayImpl();
//		failingPaymentGateway.setName(Utils.uniqueCode("Failing NullPaymentGateway"));
//
//		Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>();
//		paymentGateways.add(failingPaymentGateway);
//		// add Gift certificate payment gateway
//		GiftCertificatePaymentGatewayImpl giftCertificatePaymentGatewayImpl = new GiftCertificatePaymentGatewayImpl();
//		giftCertificatePaymentGatewayImpl.setName(Utils.uniqueCode("GC Payment Gateway"));
//
//		paymentGateways.add(giftCertificatePaymentGatewayImpl);
//		scenario.getStore().setPaymentGateways(paymentGateways);
//		assertNotNull(scenario.getStore().getPaymentGatewayMap());
//	}
//
//	private void addElectronicItemToCart(final ShoppingCart shoppingCart) {
//		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//				scenario.getCategory(), scenario.getWarehouse());
//		ProductSku sku = product.getDefaultSku();
//		sku.setShippable(false);
//		sku.setDigital(true);
//
//		ProductService prodService = (ProductService) elasticPath.getBean(ContextIdNames.PRODUCT_SERVICE);
//		product = prodService.saveOrUpdate(product);
//
//		shoppingCart.addCartItem(product.getDefaultSku(), 5);
//	}
//
//	private void addPhysicalItemToCart(final ShoppingCart shoppingCart) {
//		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//				scenario.getCategory(), scenario.getWarehouse());
//		// save the pre-order enabled product
//		ProductService prodService = (ProductService) elasticPath.getBean(ContextIdNames.PRODUCT_SERVICE);
//		product = prodService.saveOrUpdate(product);
//
//		shoppingCart.addCartItem(product.getDefaultSku(), 5);
//	}
//
//	protected ShoppingCart createEmptyShoppingCart() {
//		ShoppingCart shoppingCart = (ShoppingCart) elasticPath.getBean(ContextIdNames.SHOPPING_CART);
//		shoppingCart.setDefaultValues();
//		shoppingCart.setBillingAddress(address);
//		shoppingCart.setShippingAddress(address);
//		shoppingCart.setCustomerSession(customerSession);
//		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario.getShippingServiceLevel()));
//		shoppingCart.setSelectedShippingServiceLevelUid(scenario.getShippingServiceLevel().getUidPk());
//		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
//		shoppingCart.setStore(scenario.getStore());
//		return shoppingCart;
//	}
//
//	public EventOriginatorHelper getEventOriginatorHelper() {
//		return (EventOriginatorHelper) elasticPath.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
//	}
}
