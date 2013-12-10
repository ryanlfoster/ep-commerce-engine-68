/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * Tests for the CheckoutServiceImpl class with promotions using the RuleEngine.
 */
@Ignore
public class CheckoutWithPromotionsTest {

//	private CheckoutService checkoutService;
//
//	private RuleService ruleService;
//
//	private RuleSetService ruleSetService;
//
//	private RecompilingRuleEngine ruleEngine;
//
//	protected Customer customer;
//
//	protected CustomerAddress address;
//
//	protected CustomerCreditCard creditCard;
//
//	protected CustomerSession customerSession;
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
//	/**
//	 * Get a reference to TestApplicationContext for use within the test. Setup
//	 * scenarios.
//	 */
//	@Override
//	public void setUp() throws Exception {
//		super.setUp();
//		tac = TestApplicationContext.getInstance();
//		tac.useDb(getClass().getName());
//		scenario = (SimpleStoreScenario) tac
//				.useScenario(SimpleStoreScenario.class);
//		elasticPath = tac.getElasticPath();
//		persisterFactory = tac.getPersistersFactory();
//		customerScenario = new CustomerScenario(scenario.getStore(),
//				persisterFactory);
//		customerScenario.initialize();
//
//		checkoutService = (CheckoutService) elasticPath
//				.getBean("checkoutService");
//		ruleService = (RuleService) elasticPath
//				.getBean(ContextIdNames.RULE_SERVICE);
//		ruleSetService = (RuleSetService) elasticPath
//				.getBean(ContextIdNames.RULE_SET_SERVICE);
//		ruleEngine = (RecompilingRuleEngine) elasticPath
//				.getBean("epRuleEngine");
//		scenario.getStore().setPaymentGateways(
//				setUpPaymentGatewayAndProperties());
//
//		final Rule freeCaseWithCameraRule = (Rule) elasticPath
//				.getBean(ContextIdNames.PROMOTION_RULE);
//		freeCaseWithCameraRule.setRuleSet(ruleSetService
//				.findByScenarioId(RuleScenarios.CART_SCENARIO));
//		freeCaseWithCameraRule.setEnabled(true);
//		freeCaseWithCameraRule.setStore(scenario.getStore());
//		freeCaseWithCameraRule.setName("Free Case With Digital Camera");
//		freeCaseWithCameraRule.setStartDate(new Date());
//		CmUserService cmUserService = (CmUserService) elasticPath
//				.getBean(ContextIdNames.CMUSER_SERVICE);
//		freeCaseWithCameraRule.setCmUser(cmUserService.findByUserName("admin"));
//		freeCaseWithCameraRule.addEligibility((RuleEligibility) elasticPath
//				.getBean(ContextIdNames.EVERYONE_ELIGIBILITY));
//
//		final RuleCondition oneDigitalCameraCondition = (RuleCondition) elasticPath
//				.getBean(ContextIdNames.CART_CONTAINS_CATEGORY_COND);
//		for (RuleParameter ruleParameter : oneDigitalCameraCondition
//				.getParameters()) {
//			if (ruleParameter.getKey().equals(RuleParameter.NUM_ITEMS_KEY)) {
//				ruleParameter.setValue("1");
//			} else if (ruleParameter.getKey().equals(
//					RuleParameter.NUM_ITEMS_QUANTIFIER_KEY)) {
//				ruleParameter.setValue("at least");
//			} else if (ruleParameter.getKey().equals(
//					RuleParameter.CATEGORY_CODE_KEY)) {
//				ruleParameter.setValue("" + scenario.getCategory().getUidPk());
//			}
//			oneDigitalCameraCondition.addParameter(ruleParameter);
//		}
//		// freeCaseWithCameraRule.addCondition(oneDigitalCameraCondition);
//
//		final RuleAction freeCameraCaseAction = ((RuleAction) elasticPath
//				.getBean(ContextIdNames.CART_CATEGORY_PERCENT_ACTION));
//		for (RuleParameter ruleParameter : freeCameraCaseAction.getParameters()) {
//			if (ruleParameter.getKey().equals(RuleParameter.NUM_ITEMS_KEY)) {
//				ruleParameter.setValue("1");
//			} else if (ruleParameter.getKey().equals(
//					RuleParameter.DISCOUNT_PERCENT_KEY)) {
//				ruleParameter.setValue("100");
//			} else if (ruleParameter.getKey().equals(
//					RuleParameter.CATEGORY_CODE_KEY)) {
//				ruleParameter.setValue("" + scenario.getCategory().getUidPk());
//			}
//			freeCameraCaseAction.addParameter(ruleParameter);
//		}
//		freeCaseWithCameraRule.addAction(freeCameraCaseAction);
//
//		ruleService.add(freeCaseWithCameraRule);
//		ruleEngine.recompileRuleBase();
//
//		customer = customerScenario.getCustomer();
//		address = customerScenario.getAddress();
//		creditCard = customerScenario.getCreditCard();
//		customerSession = customerScenario.getCustomerSession();
//	}
//
//	// ============================ TESTS ========================= \\
//
//	/**
//	 * Test that a free item promotion gets applied to the cart and that the
//	 * order calculated values such as the total are identical to those before
//	 * checkout in the shopping cart.
//	 */
//	public void testCheckoutWithFreeItemPromotion() {
//		final CustomerCreditCard selectedCreditCard = creditCard;
//		if (scenario.getStore().isCreditCardCvv2Enabled()) {
//			// clean the cvv2, customer need to input it each time.
//			selectedCreditCard.setSecurityCode("");
//		} else {
//			// Set valid code to avoid validation error when cvv2 is not in use.
//			selectedCreditCard.setSecurityCode("000");
//		}
//
//		selectedCreditCard.setDefaultCard(true);
//		customer.updateCreditCard(selectedCreditCard);
//
//		final OrderPayment orderPayment = getOrderPayment();
//		orderPayment.useCreditCard(selectedCreditCard);
//
//		final ShoppingCart shoppingCart = createEmptyShoppingCart();
//		ProductSku productSku1 = persisterFactory.getCatalogTestPersister()
//				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//						scenario.getCategory(), scenario.getWarehouse())
//				.getDefaultSku();
//		ProductSku productSku2 = persisterFactory.getCatalogTestPersister()
//				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//						scenario.getCategory(), scenario.getWarehouse())
//				.getDefaultSku();
//		ProductSku productSku3 = persisterFactory.getCatalogTestPersister()
//				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//						scenario.getCategory(), scenario.getWarehouse())
//				.getDefaultSku();
//		ProductSku productSku4 = persisterFactory.getCatalogTestPersister()
//				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//						scenario.getCategory(), scenario.getWarehouse())
//				.getDefaultSku();
//		ProductSku productSku5 = persisterFactory.getCatalogTestPersister()
//				.createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
//						scenario.getCategory(), scenario.getWarehouse())
//				.getDefaultSku();
//
//		shoppingCart.addCartItem(productSku1, 1);
//		shoppingCart.addCartItem(productSku2, 1);
//		shoppingCart.addCartItem(productSku3, 2);
//		shoppingCart.addCartItem(productSku4, 1);
//		shoppingCart.addCartItem(productSku5, 4);
//
//		assertEquals("There should be 5 cart items", 5, shoppingCart
//				.getCartItems().size());
//		assertEquals("There should be a total quantity 9 items in the cart", 9,
//				shoppingCart.getNumItems());
//
//		assertEquals("The ShoppingCart should have one rule applied to it.", 1,
//				shoppingCart.getAppliedRules().size());
//
//		final BigDecimal shoppingCartTotal = shoppingCart.getTotal();
//
//		checkoutService.checkout(shoppingCart, orderPayment);
//
//		Order order = shoppingCart.getCompletedOrder();
//		assertNotNull("The shopping cart should contain a completed order",
//				order);
//
//		assertEquals(
//				"The order total should be the same as the shopping cart total before checkout.",
//				shoppingCartTotal, order.getTotal());
//
//		assertTrue("The order should contain shipments", order
//				.getPhysicalShipments().size() > 0);
//		for (PhysicalOrderShipment phShipment : order.getPhysicalShipments()) {
//			for (OrderSku orderSku : phShipment.getShipmentOrderSkus()) {
//				assertTrue("The order sku should be persistent", orderSku
//						.isPersisted());
//			}
//		}
//	}
//
//	// =================== UTILITY METHODS ========================= \\
//
//	private OrderPayment getOrderPayment() {
//		OrderPayment orderPayment = (OrderPayment) elasticPath
//				.getBean(ContextIdNames.ORDER_PAYMENT);
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
//	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
//		Set<PaymentGateway> gateways = new HashSet<PaymentGateway>();
//		gateways.add(persisterFactory.getStoreTestPersister()
//				.persistDefaultPaymentGateway());
//		return gateways;
//	}
//
//	protected ShoppingCart createEmptyShoppingCart() {
//		ShoppingCart shoppingCart = (ShoppingCart) elasticPath
//				.getBean(ContextIdNames.SHOPPING_CART);
//		shoppingCart.setDefaultValues();
//		shoppingCart.setBillingAddress(address);
//		shoppingCart.setShippingAddress(address);
//		shoppingCart.setCustomerSession(customerSession);
//		shoppingCart.setShippingServiceLevelList(Arrays.asList(scenario
//				.getShippingServiceLevel()));
//		shoppingCart.setSelectedShippingServiceLevelUid(scenario
//				.getShippingServiceLevel().getUidPk());
//		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
//		shoppingCart.setStore(scenario.getStore());
//		return shoppingCart;
//	}
}
