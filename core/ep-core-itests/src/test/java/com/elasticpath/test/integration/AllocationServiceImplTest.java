/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import org.junit.Ignore;

/**
 * Tests the allocation service.
 */
@Ignore
public class AllocationServiceImplTest {

//	/**
//	 * 
//	 */
//	private static final int ORDERED_QTY = 2;
//
//	private ShoppingCart shoppingCart;
//
//	private Product product;
//
//	private InventoryService inventoryService;
//
//	private OrderService orderService;
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
//		ProductService productService = (ProductService) elasticPath.getBean(ContextIdNames.PRODUCT_SERVICE);
//
//		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
//				scenario.getWarehouse());
//		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
//		product = productService.saveOrUpdate(product);
//
//		inventoryService = (InventoryService) elasticPath.getBean("inventoryService");
//
//		PaymentGateway paymentGateway = new NullPaymentGatewayImpl();
//		paymentGateway.setName(Utils.uniqueCode("fakePaymentGateway"));
//		scenario.getStore().getPaymentGateways().add(paymentGateway);
//
//		customer = customerScenario.getCustomer();
//		address = customerScenario.getAddress();
//		creditCard = customerScenario.getCreditCard();
//		customerSession = customerScenario.getCustomerSession();
//
//		shoppingCart = createEmptyShoppingCart();
//
//		orderService = elasticPath.getBean(ContextIdNames.ORDER_SERVICE);
//
//	}
//
//	/**
//	 * Test inventory and inventory allocation levels: 1) After creating an order 2) After releasing the order shipment 3) After completing the order
//	 * Quantity on hand should only be reduced after completing the order.
//	 */
//	public void testAllocateQuantityForOrderShipment() {
//
//		CheckoutService checkoutService = (CheckoutService) elasticPath.getBean("checkoutService");
//
//		// Record the original inventory levels to check against later.
//		Inventory preOrderInventory = product.getDefaultSku().getInventory(scenario.getWarehouse().getUidPk());
//
//		// Checkout a sku
//		shoppingCart.addCartItem(product.getDefaultSku(), ORDERED_QTY);
//		OrderPayment orderPayment = new OrderPaymentImpl();
//		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
//		checkoutService.checkout(shoppingCart, orderPayment);
//
//		// Check the completed checkout is as expected
//		Order order = shoppingCart.getCompletedOrder();
//
//		List<PhysicalOrderShipment> physicalShipments = order.getPhysicalShipments();
//		assertEquals("Only one shipment has to be created", 1, physicalShipments.size());
//		PhysicalOrderShipment shipment = physicalShipments.iterator().next();
//
//		Set<OrderSku> shipmentOrderSkus = shipment.getShipmentOrderSkus();
//		assertEquals("Only one order sku should be in shipment", 1, shipmentOrderSkus.size());
//		OrderSku orderSku = shipmentOrderSkus.iterator().next();
//		assertEquals("Allocated order sku qty should be = ORDERED_QTY", ORDERED_QTY, orderSku.getAllocatedQuantity());
//		Inventory postOrderInventory = orderSku.getProductSku().getInventory(scenario.getWarehouse().getUidPk());
//		assertEquals("Inventory should have allocated qty = ORDERED_QTY", ORDERED_QTY, postOrderInventory.getAllocatedQuantity());
//		assertEquals("Quantity on hand should be unaffected", preOrderInventory.getQuantityOnHand(), postOrderInventory.getQuantityOnHand());
//
//		// Release the order for pick/packing and check the inventory
//		order = orderService.get(order.getUidPk());
//		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
//		shipment = (PhysicalOrderShipment) order.getAllShipments().iterator().next();
//		shipment = (PhysicalOrderShipment) orderService.processReleaseShipment(shipment);
//
//		orderSku = shipment.getShipmentOrderSkus().iterator().next();
//		Inventory pickPackingInventory = orderSku.getProductSku().getInventory(scenario.getWarehouse().getUidPk());
//		pickPackingInventory = inventoryService.get(pickPackingInventory.getUidPk());
//
//		assertEquals("Order sku allocated quantity should be = ORDERED_QTY", ORDERED_QTY, orderSku.getAllocatedQuantity());
//		assertEquals("Inventory allocated quantity should be ORDERED_QTY", 2, pickPackingInventory.getAllocatedQuantity());
//		assertEquals("Inventory on hand quantity should be the same - the same inventory is still in the warehouse", postOrderInventory
//				.getQuantityOnHand()
//				- ORDERED_QTY, pickPackingInventory.getAvailableQuantityInStock());
//
//		// Complete the order and check the inventory.
//		orderService.completeShipment(shipment.getShipmentNumber(), "my fake traking code", false, new Date(), false, getEventOriginatorHelper()
//				.getSystemOriginator());
//
//		Inventory postCompleteInventory = inventoryService.get(pickPackingInventory.getUidPk());
//
//		assertEquals("No inventory should now be allocated", 0, postCompleteInventory.getAllocatedQuantity());
//		assertEquals("Quantity on hand should be less the ORDERED_QTY", postOrderInventory.getQuantityOnHand() - ORDERED_QTY, postCompleteInventory
//				.getQuantityOnHand());
//
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
