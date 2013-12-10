package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * ReturnAndExchange dbTest.
 */
@Ignore
public class ReturnAndExchangeTest {
//
//	private static final Currency CURRENCY = Currency.getInstance("USD");
//
//	private static final BigDecimal RETURN_TOTAL = new BigDecimal("19.04");
//
//	private static final BigDecimal PRODUCT_PRICE = new BigDecimal("10.00");
//
//	private static final String USER_NAME = "User";
//
//	private CmUser cmUser;
//
//	private Product product;
//
//	private OrderService orderService;
//
//	private CheckoutService checkoutService;
//
//	private ShoppingCart shoppingCart;
//
//	private PaymentGatewayService paymentGatewayService;
//
//	/** The main object under test. */
//	private ReturnAndExchangeService exchangeService;
//
//	/** An anonymous customer, you can use them to make orders. */
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
//		tac.useDb(getClass().getName(), true);
//		scenario = (SimpleStoreScenario) tac.useScenario(SimpleStoreScenario.class);
//		elasticPath = tac.getElasticPath();
//		persisterFactory = tac.getPersistersFactory();
//		customerScenario = new CustomerScenario(scenario.getStore(), persisterFactory);
//		customerScenario.initialize();
//
//		paymentGatewayService = elasticPath.getBean(ContextIdNames.PAYMENT_GATEWAY_SERVICE);
//
//		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());
//
//		product = persisterFactory.getCatalogTestPersister().persistProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
//				scenario.getWarehouse(), PRODUCT_PRICE, Utils.uniqueCode("Canon PowerShot"), Utils.uniqueCode("Sku01"));
//
//		anonymousCustomer = persisterFactory.getStoreTestPersister().createDefaultCustomer(scenario.getStore());
//
//		shoppingCart = createShoppingCart();
//
//		shoppingCart.addCartItem(product.getDefaultSku(), 1);
//
//		checkoutService = (CheckoutService) elasticPath.getBean("checkoutService");
//
//		checkoutService.checkout(shoppingCart, getOrderPayment());
//
//		exchangeService = (ReturnAndExchangeService) elasticPath.getBean(ContextIdNames.ORDER_RETURN_SERVICE);
//
//		orderService = (OrderService) elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//		setupCmUser();
//
//		setupPropertyForReason();
//
//		/** persist default Canada tax jurisdiction. */
//		persisterFactory.getTaxTestPersister().persistDefaultTaxJurisdictions();
//	}
//
//	/**
//	 * setupPropertyForReason.
//	 */
//	private void setupPropertyForReason() {
//		final PropertyBased propertyBased = (PropertyBased) elasticPath.getBean(ContextIdNames.ORDER_RETURN_SKU_REASON);
//
//		Map<String, Properties> propertiesMap = new HashMap<String, Properties>();
//
//		Properties value = new Properties();
//		value.put("OrderReturnSkuReason_UnwantedGift", "Unwanted Gift");
//		value.put("OrderReturnSkuReason_IncorrectItem", "Incorrect Item");
//		value.put("OrderReturnSkuReason_Faulty", "Faulty");
//		propertiesMap.put("orderReturnSkuReason.properties", value);
//
//		propertyBased.setPropertiesMap(propertiesMap);
//	}
//
//	/**
//	 * setupCmUser.
//	 * 
//	 * @param cmUserService
//	 */
//	private void setupCmUser() {
//		CmUserService cmUserService = (CmUserService) elasticPath.getBean(ContextIdNames.CMUSER_SERVICE);
//
//		cmUser = cmUserService.findByUserName(USER_NAME);
//
//		if (cmUser == null) {
//			cmUser = new CmUserImpl();
//			cmUser.setUserName(USER_NAME);
//			cmUser.setCreationDate(new Date());
//			cmUser.setDefaultValues();
//			cmUser = cmUserService.update(cmUser);
//		}
//	}
//
//	private Order setupEventOriginator(final Order order) {
//		final EventOriginatorImpl eventOriginator = new EventOriginatorImpl();
//		eventOriginator.setType(EventOriginatorType.CMUSER);
//		eventOriginator.setCmUser(cmUser);
//		order.setModifiedBy(eventOriginator);
//		return order;
//	}
//
//	/**
//	 * testAddGetUpdate.
//	 */
//	public void testAddGetUpdate() {
//		OrderReturn orderReturn = createOrderReturn();
//		// orderReturn.setCreatedDate(new Date());
//
//		OrderReturn savedOrderReturn = exchangeService.add(orderReturn);
//
//		assertEquals(savedOrderReturn.getUidPk(), exchangeService.get(savedOrderReturn.getUidPk()).getUidPk());
//
//		savedOrderReturn.setCreatedDate(new Date());
//
//		exchangeService.update(savedOrderReturn);
//	}
//
//	/**
//	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.cancelReturnExchange()'.
//	 */
//	public void testCancelExcahnge() {
//		OrderReturn orderReturn = createOrderReturnForExchnage();
//
//		orderReturn.setReturnStatus(OrderReturnStatus.AWAITING_STOCK_RETURN);
//		OrderReturn result = exchangeService.cancelReturnExchange(orderReturn);
//
//		assertEquals(OrderReturnStatus.CANCELLED, result.getReturnStatus());
//	}
//
//	/**
//	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.completeReturn()'.
//	 */
//	public void testCompleteReturn() {
//		OrderReturn orderReturn = createOrderReturnForExchnage();
//
//		assertNull(orderReturn.getReturnPayment());
//
//		final OrderReturn result = exchangeService.completeReturn(orderReturn, ReturnExchangeType.MANUAL_RETURN);
//
//		orderReturn.setReturnPayment(result.getReturnPayment());
//
//		assertNotNull(orderReturn.getReturnPayment());
//		// RETURN_TOTAL = PRODUCT_PRICE + SHIPPING_COST which is $7 + taxes.
//		assertEquals(RETURN_TOTAL, orderReturn.getReturnPayment().getAmount());
//		assertEquals(OrderReturnStatus.COMPLETED, orderReturn.getReturnStatus());
//
//		// catch error
//		try {
//			exchangeService.completeReturn(result, ReturnExchangeType.PHYSICAL_RETURN_REQUIRED);
//			fail("EpServiceException must be thrown, because of unexpected type");
//		} catch (EpServiceException ex) {
//			// success
//			assertNotNull(ex);
//		}
//	}
//
//	/**
//	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.completeExchnageOrder()'.
//	 */
//	public void testCompleteExchnageOrder() {
//		OrderReturn returnOrder = createOrderReturnForExchnage();
//
//		setupExchangeOrder(returnOrder);
//
//		Order order = exchangeService.createExchangeOrder(returnOrder, true);
//
//		returnOrder.setExchangeOrder(orderService.get(order.getUidPk()));
//
//		exchangeService.completeExchange(returnOrder, ReturnExchangeType.CREATE_WO_PAYMENT, null);
//
//		assertEquals(OrderStatus.IN_PROGRESS, returnOrder.getExchangeOrder().getStatus());
//	}
//
//	/**
//	 * Test method for OrderReturnOutOfDateException exception.
//	 */
//	public void testReturnOutOfDateException() {
//		// TODO: the following should work, but a JPA exception is thrown need further investigation.
//		/**
//		 * OrderReturn returnOrder = createOrderReturnForExchnage(); returnOrder.setReturnComment("Hello, world!");
//		 * applyOriginator(returnOrder.getOrder()); returnOrder = exchangeService.editReturn(returnOrder); returnOrder =
//		 * exchangeService.get(returnOrder.getUidPk()); OrderReturn interceptedReturn = exchangeService.get(returnOrder.getUidPk());
//		 * interceptedReturn.setReturnComment("Hi, world!"); applyOriginator(interceptedReturn.getOrder()); interceptedReturn =
//		 * exchangeService.editReturn(interceptedReturn); try { returnOrder.setReturnComment("Hey, world!"); applyOriginator(returnOrder.getOrder());
//		 * exchangeService.editReturn(returnOrder); fail("OrderReturnOutOfDateException should be thrown"); } catch (OrderReturnOutOfDateException e) {
//		 * assertNotNull(e); } assertEquals("Hi, world!", exchangeService.get(returnOrder.getUidPk()).getReturnComment());
//		 */
//	}
//
//	Order getOrderFromShoppingCart() {
//		return setupEventOriginator(orderService.get(shoppingCart.getCompletedOrder().getUidPk()));
//	}
//
//	private OrderReturn createOrderReturnForExchnage() {
//		OrderReturn orderReturn = createOrderReturn();
//		return exchangeService.createReturn(orderReturn, ReturnExchangeType.PHYSICAL_RETURN_REQUIRED);
//	}
//
//	/**
//	 * Creates order for return.
//	 * 
//	 * @return OrderReturn
//	 */
//	private OrderReturn createOrderReturn() {
//		OrderShipment orderShipment = orderService.processReleaseShipment(getOrderFromShoppingCart().getAllShipments().get(0));
//
//		Order shippedOrder = setupEventOriginator(orderService.completeShipment(orderShipment.getShipmentNumber(), null, true, new Date(), false,
//				getEventOriginatorHelper().getCmUserOriginator(cmUser)));
//
//		OrderReturn orderReturn = (OrderReturn) elasticPath.getBean(ContextIdNames.ORDER_RETURN);
//
//		orderReturn.populateOrderReturn(shippedOrder, shippedOrder.getAllShipments().get(0), OrderReturnType.RETURN);
//
//		for (final OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
//			orderReturnSku.setQuantity(1);
//			orderReturnSku.setReturnReason("Faulty");
//		}
//
//		orderReturn.setCreatedByCmUser(cmUser);
//		orderReturn.setOrderReturnAddress(createOrderReturnAddress());
//		orderReturn.recalculateOrderReturn();
//		return orderReturn;
//	}
//
//	/**
//	 * Creates a new order return address into the data source.
//	 */
//	private OrderAddress createOrderReturnAddress() {
//		final OrderAddress address = getBillingAddress();
//
//		// TODO: think of service usage.
//		return (OrderAddress) tac.getTxTemplate().execute(new TransactionCallback() {
//
//			public Object doInTransaction(final TransactionStatus arg0) {
//				PersistenceEngine persistenceEngine = elasticPath.getBean(ContextIdNames.PERSISTENCE_ENGINE);
//				return persistenceEngine.saveOrMerge(address);
//			}
//		});
//	}
//
//	/**
//	 * @param exchange
//	 */
//	private void setupExchangeOrder(final OrderReturn exchange) {
//		List<CartItem> cartItemList = createCartItemList();
//
//		ShoppingCart shoppingCart = exchangeService.populateShoppingCart(exchange, cartItemList, scenario.getShippingServiceLevel(),
//				getBillingAddress());
//
//		CustomerSession custSession = (CustomerSession) elasticPath.getBean("customerSession");
//		custSession.setCurrency(Currency.getInstance("USD"));
//		custSession.setCustomer(anonymousCustomer);
//		shoppingCart.setCustomerSession(custSession);
//		shoppingCart.setLocale(Locale.ENGLISH);
//	}
//
//	private List<CartItem> createCartItemList() {
//		final List<CartItem> orderSkus = new ArrayList<CartItem>();
//		// Create mock cart items
//		final CartItem cartItem = (CartItem) elasticPath.getBean(ContextIdNames.SHOPPING_CART_ITEM);
//
//		// cartItem.setUidPk(Calendar.getInstance().getTimeInMillis());
//		final ProductSku productSkuImpl = product.getDefaultSku();
//		cartItem.setProductSku(productSkuImpl);
//		cartItem.setQuantity(1);
//		// cartItem.setProductSkuPrice(new ProductPriceImpl());
//		// any product sku price for the current cart item can be set up here.
//		cartItem.setProductSkuPrice(productSkuImpl.getCatalogSkuPrice(scenario.getCatalog(), CURRENCY));
//
//		orderSkus.add(cartItem);
//		return orderSkus;
//	}
//
//	// =================== UTILITY METHODS ========================= \\
//	/**
//	 * @return Set<PaymentGateway>
//	 */
//	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
//
//		final Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
//
//		gateways.add(persisterFactory.getStoreTestPersister().persistDefaultPaymentGateway());
//
//		PaymentGateway paymentgateway = new ExchangePaymentGatewayImpl();
//		paymentgateway.setName(Utils.uniqueCode("Exchange PaymentGateway"));
//		gateways.add(paymentGatewayService.saveOrUpdate(paymentgateway));
//		return gateways;
//	}
//
//	/**
//	 * @return OrderPayment
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
//	 * @return ShoppingCart
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
//		shoppingCart.setShippingCost(BigDecimal.ONE);
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
//	 * @return CustomerSession
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
//	private OrderAddress getBillingAddress() {
//		OrderAddress billingAddress = new OrderAddressImpl();
//		billingAddress.setFirstName("Billy");
//		billingAddress.setLastName("Bob");
//		billingAddress.setCountry("CA");
//		billingAddress.setStreet1("1295 Charleston Road");
//		billingAddress.setCity("Mountain View");
//		billingAddress.setSubCountry("BC");
//		billingAddress.setZipOrPostalCode("94043");
//		billingAddress.setGuid(Utils.uniqueCode("address"));
//
//		return billingAddress;
//	}
//
//	public EventOriginatorHelper getEventOriginatorHelper() {
//		return (EventOriginatorHelper) elasticPath.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
//	}
}
