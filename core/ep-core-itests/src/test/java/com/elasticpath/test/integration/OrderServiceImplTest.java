/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.InventoryAuditImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.PurchaseHistorySearchCriteria;
import com.elasticpath.domain.order.impl.AbstractOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.order.impl.PurchaseHistorySearchCriteriaImpl;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryCommand;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration test for OrderService.
 */
@SuppressWarnings( {"PMD.TooManyStaticImports", "deprecation"} )
public class OrderServiceImplTest extends BasicSpringContextTest {

	private static final String INVALID_GUID = "BAD GUID";

	private static final String NON_EXISTENT_CART_ORDER_GUID = "NON_EXISTENT_CART_ORDER_GUID";

	private static final String DIFFERENT_CART_ORDER_GUID = "DIFFERENT_CART_ORDER_GUID";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private PaymentService paymentService;

	private Store store;

	private Product product;

	private Customer customer;

	private CustomerAddress address;

	private CustomerCreditCard creditCard;

	private CustomerSession customerSession;

	private SimpleStoreScenario scenario;

	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private OrderService orderService;

	private static final int MAX_RESULTS = 10;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private InventoryFacade inventoryFacade;

	private Shopper shopper;

	private ShoppingCart shoppingCart;

	@Autowired
	private TimeService timeService;

	private List<ReversibleCheckoutAction> reversibleCheckoutActions;
	private final ReversibleCheckoutAction failingAction = new ReversibleCheckoutAction() {
		
		@Override
		public void execute(CheckoutActionContext context) throws EpSystemException {
			throw new EpSystemException("Causing previous ReversibleCheckoutActions to roll back.");
			
		}
		
		@Override
		public void rollback(CheckoutActionContext context)
				throws EpSystemException {
			//do nothing
		}
	};
	
	@Autowired
	private ProductInventoryManagementService productInventoryManagementService;

	@Autowired
	private ProductService productService;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		persisterFactory = getTac().getPersistersFactory();
		store = scenario.getStore();
		product = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).get(0);

		customer = persisterFactory.getStoreTestPersister().createDefaultCustomer(store);
		address = persisterFactory.getStoreTestPersister().createCustomerAddress("Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC",
				"V6J5G4", "891312345007");
		customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		shopper = customerSession.getShopper();
		creditCard = persisterFactory.getStoreTestPersister().createCustomerCreditCard(address);
		reversibleCheckoutActions = getBeanFactory().getBean("reversibleActions");
	}

	// ============================ TESTS ========================= \\

	/**
	 * Make sure that the failing action is removed after each test so that it doesn't break the suite.
	 */
	@After
	public void cleanUpFailingReversibleActionIfExists() {
		reversibleCheckoutActions.remove(failingAction);
	}
	/**
	 * Test order inventory.
	 */
	@DirtiesDatabase
	@Test
	@SuppressWarnings("deprecation")
	public void testOrderInventory() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());

		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);


		assertNotNull("Shopping cart should contain items", shoppingCart.getCartItems());
		assertEquals("There should be 1 item in the shopping cart", 1, shoppingCart.getCartItems().size());
		ProductSku productSku = shoppingCart.getCartItems().get(0).getProductSku();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals("There should be 1 item in the order", 1, ordersList.size());
		Order order = ordersList.iterator().next();

		assertNotNull("We should be able to find the warehouse for this order", order.getStore().getWarehouses().get(0));
		long warehouseUidPk = order.getStore().getWarehouses().get(0).getUidPk();
		assertEquals("Order warehouse should be same as the original product", warehouseUidPk, store.getWarehouses().get(0).getUidPk());
		assertEquals("There should be 1 sku in the order", 1, order.getRootShoppingItems().size());
		ShoppingItem orderSku = order.getRootShoppingItems().iterator().next();
		assertEquals("The order sku guid should match the original sku guid", productSku.getGuid(), orderSku.getProductSku().getGuid());

		InventoryDto orderSkuInventory = productInventoryManagementService.getInventory(orderSku.getProductSku().getSkuCode(), scenario.getWarehouse().getUidPk());

		assertNotNull("There should be inventory for the order sku in the store's warehouse", orderSkuInventory);
	}

	/**
	 * Test that if the checkout process fails, checkout actions get reversed correctly and the order is saved with the failed status.
	 */
	@DirtiesDatabase
	@Test(expected = EpSystemException.class)
	public void testFailOrderOnReversingCheckoutProcess() {
		reversibleCheckoutActions.add(failingAction);
		
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());

		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);


		assertNotNull("Shopping cart should contain items", shoppingCart.getCartItems());
		assertEquals("There should be 1 item in the shopping cart", 1, shoppingCart.getCartItems().size());
		ProductSku productSku = shoppingCart.getCartItems().get(0).getProductSku();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals("There should be 1 item in the order", 1, ordersList.size());
		Order order = ordersList.iterator().next();
		
		assertEquals("The order should be in the failed state after checkout reversal", order.getStatus(), OrderStatus.FAILED);
		
		assertEquals("There should be 1 sku in the order", 1, order.getRootShoppingItems().size());
		ShoppingItem orderSku = order.getRootShoppingItems().iterator().next();
		assertEquals("The order sku guid should match the original sku guid", productSku.getGuid(), orderSku.getProductSku().getGuid());
	}

	
	/**
	 * Test cancelling an order.
	 */
	@DirtiesDatabase
	@Test
	public void testCancelOrder() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);

		assertTrue(order.isCancellable());
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		order = orderService.cancelOrder(order);

		assertEquals(OrderStatus.CANCELLED, order.getStatus());

		// 1 auth + 1 reverse auth payments should have been conducted
		assertEquals(2, order.getOrderPayments().size());

		OrderPayment reverseAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
				assertNull(reverseAuth);
				reverseAuth = payment;
			}
		}
		assertNotNull(reverseAuth);
		assertEquals(OrderPaymentStatus.APPROVED, reverseAuth.getStatus());

	}

	/**
	 * Test canceling a shipment.
	 */
	@DirtiesDatabase
	@Test
	public void testCancelShipment() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();

		// one shipment should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		assertTrue(phShipment.isCancellable());

		phShipment = orderService.cancelOrderShipment(phShipment);

		assertEquals(OrderShipmentStatus.CANCELLED, phShipment.getShipmentStatus());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());

		// 1 auth + 1 reverse auth payments should have been conducted
		assertEquals(2, order.getOrderPayments().size());

		OrderPayment reverseAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
				assertNull(reverseAuth);
				reverseAuth = payment;
			}
		}
		assertNotNull(reverseAuth);
		assertEquals(OrderPaymentStatus.APPROVED, reverseAuth.getStatus());
	}

	/**
	 * Test order status is completed when order has one non-shippable shipment and one canceled physical shipment.
	 *
	 * TODO - Investigate intermittent failure of this test (NullPointerException at ElectronicOrderShipmentImpl.java:89)
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testOrderStatusCompletedWithMultipleShipments() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		Product electronicProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		electronicProduct.getDefaultSku().setShippable(false);
		electronicProduct = productService.saveOrUpdate(electronicProduct);

		ShoppingItemDto electronicDto = new ShoppingItemDto(electronicProduct.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, electronicDto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(2, shipments.size());

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		phShipment = orderService.cancelOrderShipment(phShipment);

		ElectronicOrderShipment elecShipment = order.getElectronicShipments().iterator().next();
		assertEquals(OrderShipmentStatus.SHIPPED, elecShipment.getShipmentStatus());

		assertEquals(OrderShipmentStatus.CANCELLED, phShipment.getShipmentStatus());
		assertEquals(OrderStatus.COMPLETED, order.getStatus());
	}

	/**
	 * Test augmenting the shipment total.
	 */
	@DirtiesDatabase
	@Test
	public void testAugmentShipmentTotal() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals("only one order should have been created by the checkout service", 1, ordersList.size());
		Order order = ordersList.iterator().next();

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		assertNotNull("Order should have a modified by value", order.getModifiedBy());

		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals("one shipment should have been created", 1, shipments.size());

		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals("there should be one payment", 1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals("payment total should be for full amount of order", order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();

		OrderSku newProductOrderSku = getNewProductOrderSku();
		BigDecimal previousTotal = phShipment.getTotal();
		phShipment.addShipmentOrderSku(newProductOrderSku);
		assertTrue("the previous total amount should be less than the new one", previousTotal.compareTo(phShipment.getTotal()) < 0);

		PaymentResult paymentResult = paymentService.adjustShipmentPayment(phShipment);

		for (OrderPayment proccessedPayment : paymentResult.getProcessedPayments()) {
			order.addOrderPayment(proccessedPayment);
		}

		order = orderService.update(order);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		phShipment = order.getPhysicalShipments().iterator().next();

		assertEquals("The shipment status should be inventory assigned", OrderShipmentStatus.INVENTORY_ASSIGNED, phShipment.getShipmentStatus());
		assertEquals("The shipment type should be pysical", ShipmentType.PHYSICAL, phShipment.getOrderShipmentType());

		for (OrderSku sku : phShipment.getShipmentOrderSkus()) {
			assertSame("Skus in the shipment should know what shipment they are for", phShipment, sku.getShipment());
		}
		phShipment = (PhysicalOrderShipment) orderService.processReleaseShipment(phShipment);

		final int expectedPayments = 3;
		assertEquals("there should be 3 payments : 1 auth + 1 reverse auth + 1 new auth (new amount)", expectedPayments,
				order.getOrderPayments().size());

		OrderPayment reverseAuth = null;
		OrderPayment newAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
				assertNull("There should only be a single reverse auth", reverseAuth);
				reverseAuth = payment;
			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
				if (newAuth == null) {
					newAuth = payment;
				} else if (payment.getCreatedDate().compareTo(newAuth.getCreatedDate()) > 0) {
					newAuth = payment;
				}
			}
		}
		assertNotNull("There should have been a reverse auth", reverseAuth);
		assertNotNull("There should be a new auth", newAuth);

		assertEquals("Auth should have been for the new shipment total", phShipment.getTotal(), newAuth.getAmount());
		assertEquals("New auth status should be approved", OrderPaymentStatus.APPROVED, newAuth.getStatus());

	}

	/**
	 * Test creating a new shipment.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateNewShipment() {
		// construct new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals("Only one order should have been created by the checkout service", 1, ordersList.size());
		Order order = ordersList.iterator().next();

		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals("one shipment should have been created", 1, shipments.size());

		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals("there should be one payment", 1, payments.size());

		OrderPayment authPayment = payments.iterator().next();
		BigDecimal originalTotal = order.getTotal();
		assertEquals("Payment should be for the full amount of the order", originalTotal.doubleValue(), authPayment.getAmount().doubleValue(), 0);

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();

		PhysicalOrderShipmentImpl newPhysicalShipment = new PhysicalOrderShipmentImpl();
		newPhysicalShipment.setCreatedDate(new Date());
		newPhysicalShipment.setLastModifiedDate(new Date());
		newPhysicalShipment.setOrder(order);
		newPhysicalShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		newPhysicalShipment.initialize();

		OrderSku newProductOrderSku = getNewProductOrderSku();
		newPhysicalShipment.addShipmentOrderSku(newProductOrderSku);
		newPhysicalShipment.setShippingServiceLevelGuid(scenario.getShippingServiceLevel().getGuid());
		newPhysicalShipment.setShippingCost(BigDecimal.ONE);

		order.addShipment(newPhysicalShipment);
		assertTrue("the new shipment total should be > 0", BigDecimal.ZERO.compareTo(newPhysicalShipment.getTotal()) < 0);
		assertEquals("the order should now have 2 physical shipments", 2, order.getPhysicalShipments().size());

		templateOrderPayment.setAmount(newPhysicalShipment.getTotal());

		OrderPayment lastPayment = paymentService.getAllActiveAutorizationPayments(phShipment).iterator().next();
		assertNotNull("There should be a payment for the original shipment", lastPayment);
		assertSame("Order shipment for last payment should be the original shipment", phShipment, lastPayment.getOrderShipment());
		assertEquals("payment method should be credit card", PaymentType.CREDITCARD, lastPayment.getPaymentMethod());
		assertEquals("Order shipment should have a total of the original order", originalTotal, phShipment.getTotal());

		phShipment.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());

		PaymentResult paymentResult = paymentService.initializeNewShipmentPayment(newPhysicalShipment, templateOrderPayment);

		for (OrderPayment proccessedPayment : paymentResult.getProcessedPayments()) {
			order.addOrderPayment(proccessedPayment);
		}

		order = orderService.update(order);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		phShipment = order.getPhysicalShipments().iterator().next();
		orderService.processReleaseShipment(phShipment);

		assertEquals("There should be 2 payments: 1 auth + 1 new auth (new amount)", 2, order.getOrderPayments().size());

		OrderPayment newAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(OrderPayment.REVERSE_AUTHORIZATION)) {
				fail("There should not be any reverse auths");
			} else if (payment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
				if (newAuth == null) {
					newAuth = payment;
				} else if (payment.getCreatedDate().compareTo(newAuth.getCreatedDate()) > 0) {
					newAuth = payment;
				}
			}
		}
		assertNotNull("There should have been a new auth", newAuth);

		assertEquals("new auth total should be for physical shipment", newPhysicalShipment.getTotal(), newAuth.getAmount());
		assertEquals("new auth status should be approved", OrderPaymentStatus.APPROVED, newAuth.getStatus());
	}

	/**
	 * Tests that you can find products codes for skus that have been purchased by a user, given a start and end date.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductCodesPurchasedByUserAndFromDate() {
		// construct and save new shopping cart
		Order order = createOrder();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// Make a list of productCodes that were in the shopping cart
		List<String> shoppingCartProductCodes = new ArrayList<String>();
		for (ShoppingItem orderSku : shipments.get(0).getShipmentOrderSkus()) {
			shoppingCartProductCodes.add(orderSku.getProductSku().getProduct().getCode());
		}

		// Make sure we get that list back
		final int fourMinutesInMillis = 240000;
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(new Date(System.currentTimeMillis() - fourMinutesInMillis), // from 4 minutes ago
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);

		for (String code : shoppingCartProductCodes) {
			assertTrue("ProductCode " + code + " was not found in list of purchased codes", purchasedProductCodes.contains(code));
		}
	}

	/**
	 * Tests that while searching for the products bought by a user given a start and end date, failed orders are excluded.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductCodesPurchasedByUserAndFromDateExcludingFailedOrders() {
		// construct and save new shopping cart
		Order order = createOrder();
		order.failOrder();
		order = orderService.update(order);

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// Make a list of productCodes that were in the shopping cart
		List<String> orderProductCodes = new ArrayList<String>();
		for (OrderSku orderSku : shipments.get(0).getShipmentOrderSkus()) {
			orderProductCodes.add(orderSku.getProductSku().getProduct().getCode());
		}

		// Make sure we get that list back
		final int fourMinutesInMillis = 240000;
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(new Date(System.currentTimeMillis() - fourMinutesInMillis), // from 4 minutes ago
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);
		assertTrue("The product codes for a failed order should not be retrieved.", purchasedProductCodes.isEmpty());
	}

	/**
	 * Tests that you can find products codes for skus that have been purchased by a user.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductCodesPurchasedByUser() {
		// construct and save new shopping cart
		Order order = createOrder();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// Make a list of productCodes that were in the shopping cart
		List<String> orderProductCodes = new ArrayList<String>();
		for (OrderSku orderSku : shipments.get(0).getShipmentOrderSkus()) {
			orderProductCodes.add(orderSku.getProductSku().getProduct().getCode());
		}

		// Make sure we get that list back
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(null, //no from date
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);

		for (String code : orderProductCodes) {
			assertTrue("ProductCode " + code + " was not found in list of purchased codes", purchasedProductCodes.contains(code));
		}
	}

	/**
	 * Tests that while searching for the products bought by a user, failed orders are excluded.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductCodesPurchasedByUserExcludeFailedOrder() {
		// construct and save new shopping cart
		Order order = createOrder();
		order.failOrder();
		order = orderService.update(order);

		// Make sure we get that list back
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(null, //no from date
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);
		assertTrue("The product codes for a failed order should not be retrieved.", purchasedProductCodes.isEmpty());
	}

	/**
	 * Test that findOrderByCustomerGuid finds regular orders but excludes failed orders.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderByCustomerGuid() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> orderByCustomerGuid = orderService.findOrderByCustomerGuid(customerGuid, true);
		assertEquals("There should be 1 order found", 1, orderByCustomerGuid.size());
		assertEquals("The order should be the one we created", order, orderByCustomerGuid.get(0));
		List<Order> orderByOtherCustomer = orderService.findOrderByCustomerGuid(INVALID_GUID, true);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());

		order.failOrder();
		order = orderService.update(order);

		orderByCustomerGuid = orderService.findOrderByCustomerGuid(customerGuid, true);
		assertTrue("There should be no orders found", orderByCustomerGuid.isEmpty());
	}

	/**
	 * Test that findOrderByCustomerGuidAndStoreCode finds regular orders but excludes failed orders.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderByCustomerGuidAndStoreCode() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> orderByCustomerGuid = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), true);
		assertEquals("There should be 1 order found", 1, orderByCustomerGuid.size());
		assertEquals("The order should be the one we created", order, orderByCustomerGuid.get(0));
		List<Order> orderByOtherCustomer = orderService.findOrdersByCustomerGuidAndStoreCode(INVALID_GUID, store.getCode(), true);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());

		order.failOrder();
		order = orderService.update(order);

		orderByCustomerGuid = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), true);
		assertTrue("There should be no orders found", orderByCustomerGuid.isEmpty());
	}

	/**
	 * Test find order by customer guid and store code without full info.
	 */
	@DirtiesDatabase
	@Test
	@SuppressWarnings("deprecation")
	public void testFindOrderByCustomerGuidAndStoreCodeWithoutFullInfo() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> retrievedOrders = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), false);
		assertEquals("There should be 1 order found", 1, retrievedOrders.size());

		Order retrievedOrder = retrievedOrders.get(0);
		assertEquals("The order number should be the one we created", order.getOrderNumber(), retrievedOrder.getOrderNumber());
		assertEquals("The order store name should be the one we created", order.getStore().getName(), retrievedOrder.getStore().getName());
		assertEquals("The order store url should be the one we created", order.getStore().getUrl(), retrievedOrder.getStore().getUrl());
		assertEquals("The order total should be the one we created", order.getTotal(), retrievedOrder.getTotal());
		assertEquals("The order created date should be the one we created", order.getCreatedDate(), retrievedOrder.getCreatedDate());
		assertEquals("The order status should be the one we created", order.getStatus(), retrievedOrder.getStatus());
		assertNull("Billing address field shouldn't be retrieved", retrievedOrder.getBillingAddress());
		assertNull("Applied rules field shouldn't be retrieved", retrievedOrder.getAppliedRules());

		List<Order> orderByOtherCustomer = orderService.findOrdersByCustomerGuidAndStoreCode(INVALID_GUID, store.getCode(), false);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());
	}

	private Order createOrder() {
		shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();
		return order;
	}

	/**
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testHoldAndReleaseHoldOnOrder() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertEquals(1, shipments.size());

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertEquals(1, payments.size());
		OrderPayment authPayment = payments.iterator().next();
		assertEquals(order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);

		assertTrue(order.isHoldable());
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		order = orderService.holdOrder(order);

		assertEquals(OrderStatus.ONHOLD, order.getStatus());
		order = orderService.get(order.getUidPk());
		assertEquals(OrderStatus.ONHOLD, order.getStatus());

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		order = orderService.releaseHoldOnOrder(order);

		assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
		order = orderService.get(order.getUidPk());
		assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
	}

	/**
	 * Tests release shipment with product which is always available.
	 */
	@DirtiesDatabase
	@Test
	public void testReleaseOrderShipmentProductAlwaysAvailable() {
		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
				scenario.getWarehouse());
		product.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		product = productService.saveOrUpdate(product);

		final int qty5 = 5;
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(
				product.getDefaultSku().getSkuCode(),
				scenario.getWarehouse().getUidPk());
		int removeFromStock = -95; //we need to set qty on hand to 5, so need to adjust it from default 100 to 5
		InventoryCommand adjustInventoryCommand = inventoryFacade.getInventoryCommandFactory()
		.getAdjustInventoryCommand(inventoryDto.getInventoryKey(), removeFromStock);
		inventoryFacade.executeInventoryCommand(adjustInventoryCommand);

		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, dto);

		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);

		// checkout
		checkoutService.checkout(shoppingCart, orderPayment, true);

		inventoryDto = productInventoryManagementService.getInventory(
				inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		// check allocation status
		assertEquals(qty5, inventoryDto.getAvailableQuantityInStock());
		assertEquals(0, inventoryDto.getAllocatedQuantity());

		Order order = shoppingCart.getCompletedOrder();

		List<PhysicalOrderShipment> physicalShipments = order.getPhysicalShipments();
		assertEquals("Only one shipment should exist", 1, physicalShipments.size());
		PhysicalOrderShipment shipment = physicalShipments.iterator().next();

		Set<OrderSku> shipmentOrderSkus = shipment.getShipmentOrderSkus();
		assertEquals("One product was checked out only", 1, shipmentOrderSkus.size());
		OrderSku orderSku = shipmentOrderSkus.iterator().next();
		assertEquals("Quantity of two products was checked out", 2, orderSku.getAllocatedQuantity());
		inventoryDto = productInventoryManagementService.getInventory(
				orderSku.getSkuCode(),
				scenario.getWarehouse().getUidPk());

		// check allocation status
		assertEquals("Two allocated quantity on the inventory should not have changed", 0, inventoryDto.getAllocatedQuantity());

		order = orderService.get(order.getUidPk());

		assertEquals("Order state should be inventory assigned", OrderShipmentStatus.INVENTORY_ASSIGNED, order.getAllShipments().iterator().next()
				.getShipmentStatus());

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		shipment = (PhysicalOrderShipment) order.getAllShipments().iterator().next();
		shipment = (PhysicalOrderShipment) orderService.processReleaseShipment(shipment);

		assertEquals("After order release state should be RELEASED", OrderShipmentStatus.RELEASED, shipment.getShipmentStatus());

		orderSku = shipment.getShipmentOrderSkus().iterator().next();

		assertEquals("The allocated order sku quantity must have been increased by 2", 2, orderSku.getAllocatedQuantity());

		assertEquals("The number of orders awaiting shipment should be 1",
				new Long(1), orderService.getAwaitingShipmentsCount(scenario.getWarehouse()));
	}

	/**
	 * Tests release shipment with product which is available only when in stock.
	 */
	@DirtiesDatabase
	@Test
	public void testReleaseOrderShipmentProductInStock() {
		releaseOrderShipmentWithProductAvialability(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
	}

	/**
	 * Tests release of an order shipment. Scenario: 1. Create shopping cart with one product 2. Checkout the shopping cart 3.
	 *
	 * @param availabilityCriteria availability criteria
	 */
	public void releaseOrderShipmentWithProductAvialability(final AvailabilityCriteria availabilityCriteria) {
		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
				scenario.getWarehouse());
		product.setAvailabilityCriteria(availabilityCriteria);
		product = productService.saveOrUpdate(product);

		final int qty5 = 5;
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(
				product.getDefaultSku().getSkuCode(),
				scenario.getWarehouse().getUidPk());
		productInventoryManagementService.saveOrUpdate(inventoryDto);

		InventoryAudit inventoryAudit = buildInventoryAudit(InventoryEventType.STOCK_ADJUSTMENT, (qty5 - inventoryDto.getQuantityOnHand()));
		productInventoryManagementService.processInventoryUpdate(inventoryDto, inventoryAudit);
		inventoryDto = productInventoryManagementService.getInventory(inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		assertNotNull(inventoryDto);
		assertEquals(qty5, inventoryDto.getQuantityOnHand());
		assertEquals(availabilityCriteria, product.getAvailabilityCriteria());

		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, dto);

		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);

		// checkout
		checkoutService.checkout(shoppingCart, orderPayment, true);

		inventoryDto = productInventoryManagementService.getInventory(
				inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		// check allocation status
		assertEquals(qty5 - 2, inventoryDto.getAvailableQuantityInStock());
		assertEquals(2, inventoryDto.getAllocatedQuantity());

		Order order = shoppingCart.getCompletedOrder();

		List<PhysicalOrderShipment> physicalShipments = order.getPhysicalShipments();
		assertEquals(1, physicalShipments.size());
		PhysicalOrderShipment shipment = physicalShipments.iterator().next();

		Set<OrderSku> shipmentOrderSkus = shipment.getShipmentOrderSkus();
		assertEquals("One product was checked out only", 1, shipmentOrderSkus.size());
		OrderSku orderSku = shipmentOrderSkus.iterator().next();
		assertEquals("Quantity of two products was checked out", 2, orderSku.getAllocatedQuantity());
		inventoryDto = productInventoryManagementService.getInventory(
				orderSku.getSkuCode(),
				scenario.getWarehouse().getUidPk());

		// check allocation status
		assertEquals("Two allocated quantities expected", 2, inventoryDto.getAllocatedQuantity());

		order = orderService.get(order.getUidPk());

		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, order.getAllShipments().iterator().next().getShipmentStatus());

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		shipment = (PhysicalOrderShipment) order.getAllShipments().iterator().next();

		shipment = (PhysicalOrderShipment) orderService.processReleaseShipment(shipment);

		assertEquals(OrderShipmentStatus.RELEASED, shipment.getShipmentStatus());

		orderSku = shipment.getShipmentOrderSkus().iterator().next();
		InventoryDto inventory2 = productInventoryManagementService.getInventory(
				orderSku.getSkuCode(),
				scenario.getWarehouse().getUidPk());
		inventory2 = productInventoryManagementService.getInventory(
				inventory2.getSkuCode(), inventory2.getWarehouseUid());

		assertEquals(2, orderSku.getAllocatedQuantity());
		assertEquals(2, inventory2.getAllocatedQuantity());

		inventory2 = productInventoryManagementService.getInventory(
				inventory2.getSkuCode(), inventory2.getWarehouseUid());

		// The allocated quantity and quantity on hand should not be changed in orderService.processReleaseShipment,
		// we only change them in orderService.completeShipment.
		assertEquals(2, inventory2.getAllocatedQuantity());
		assertEquals(inventoryDto.getQuantityOnHand(), inventory2.getQuantityOnHand());
	}

	private InventoryAudit buildInventoryAudit(final InventoryEventType inventoryEventType, final int quantity) {
		InventoryAudit inventoryAudit = new InventoryAuditImpl();
		inventoryAudit.setEventType(inventoryEventType);
		inventoryAudit.setQuantity(quantity);
		return inventoryAudit;
	}

	// =================== UTILITY METHODS ========================= \\

	private PurchaseHistorySearchCriteria createPurchaseHistorySearchCriteria(final Date fromDate, final Date toDate) {
		PurchaseHistorySearchCriteria criteria = new PurchaseHistorySearchCriteriaImpl();
		criteria.setUserId(shopper.getCustomer().getUserId());
		criteria.setStoreCode(store.getCode());
		criteria.setFromDate(fromDate); // from 4 minutes ago
		criteria.setToDate(toDate);
		return criteria;
	}

	/**
	 * @return
	 */
	private OrderSku getNewProductOrderSku() {
		Product newProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final OrderSku orderSku = getBeanFactory().getBean(ContextIdNames.ORDER_SKU);

		final ProductSku productSku = newProduct.getDefaultSku();

		final Price price = getBeanFactory().getBean(ContextIdNames.PRICE);
		final Money amount = MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance("USD"));
		price.setListPrice(amount);
		orderSku.setPrice(1, price);
		orderSku.setUnitPrice(BigDecimal.ONE);
		final Date now = new Date();
		orderSku.setCreatedDate(now);
		final int qty3 = 3;
		orderSku.setQuantity(qty3);
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setProductSku(productSku);
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setTaxCode(newProduct.getTaxCode().getCode());
		orderSku.setTax(BigDecimal.ONE);
		orderSku.setAllocatedQuantity(qty3);

		if (productSku.getImage() != null) {
			orderSku.setImage(productSku.getImage());
		}

		orderSku.setDisplayName("product_name2");
		return orderSku;
	}

	/**
	 * @param shoppingCart
	 */
	private void assertCustomerEmailEqualsShopperCustomerEmail(final Shopper shopper) {
		assertEquals(customer.getEmail(), shopper.getCustomer().getEmail());
	}


	/**
	 * getEventOriginatorHelper.
	 * @return EventOriginatorHelper
	 */
	public EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testFindBySearchCriteriaStoreCode() {
		Order order = persistOrder(product);
		OrderSearchCriteria criteria = getBeanFactory().getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<String>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertEquals("One order should have been found", 1, results.size());
		assertEquals("The search result should be the expected order", order, results.get(0));
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testFindBySearchCriteriaStoreCodeWithExcludedOrderStatusSetToFailedOrder() {
		createAndPersistFailedOrder(product);

		OrderSearchCriteria criteria = getBeanFactory().getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		criteria.setExcludedOrderStatus(OrderStatus.FAILED);
		Set<String> storeCodes = new HashSet<String>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertEquals("No order should have been found", 0, results.size());
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testFindBySearchCriteriaStoreCodeWithoutExcludedOrderStatusSetToFailedOrder() {
		Order order = createAndPersistFailedOrder(product);

		OrderSearchCriteria criteria = getBeanFactory().getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<String>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertEquals("One order should have been found", 1, results.size());
		assertEquals("The search result should be the expected order", order, results.get(0));
	}

	private Order createAndPersistFailedOrder(final Product product) {
		Order order = persistOrder(product);
		order.failOrder();
		return orderService.update(order);
	}

	/**
	 * Test that getting the count by criteria with a store code works as expected.
	 */
	@DirtiesDatabase
	@Test
	public void testGetCountBySearchCriteriaStoreCode() {
		persistOrder(product);
		OrderSearchCriteria criteria = getBeanFactory().getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<String>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		long count = orderService.getOrderCountBySearchCriteria(criteria);
		assertEquals("One order should have been found", 1, count);
	}

	/**
	 * Test persistence of the Applied Rule with the connected coupon.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderByState() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, creditCard);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);
		checkoutService.checkout(shoppingCart, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals(1, ordersList.size());
		final Order order = ordersList.get(0);

		try {
			List<Order> returnValues = orderService.findOrderByStatus(order.getStatus(), OrderPaymentStatus.APPROVED,
					OrderShipmentStatus.INVENTORY_ASSIGNED);
			assertEquals("The number of orders returned:", 1, returnValues.size());
		} catch(Exception e) {
			fail("Error should not occur " + e.getMessage());
		}

	}

	/**
	 * Test that an order can have failed status and be found by such.
	 */
	@DirtiesDatabase
	@Test
	public void testFindFailedOrder() {
		Order order = persistOrder(product);
		List<Order> failedOrders = orderService.findOrderByStatus(OrderStatus.FAILED, null, null);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		order.failOrder();
		Order updatedOrder = orderService.update(order);

		failedOrders = orderService.findOrderByStatus(OrderStatus.FAILED, null, null);
		assertEquals("There should be one order returned", 1, failedOrders.size());

		Order failedOrder = failedOrders.get(0);
		assertEquals("The failed order should be the we updated", updatedOrder, failedOrder);
		assertEquals("Order status should be failed", OrderStatus.FAILED, failedOrder.getStatus());

		assertTrue("The failed order needs to have shipments.", failedOrder.getAllShipments().size() > 0);
		for (OrderShipment failedOrderShipment : failedOrder.getAllShipments()) {
			assertEquals("OrderShipment status should be failed", OrderShipmentStatus.FAILED_ORDER, failedOrderShipment.getShipmentStatus());
		}
	}



	/**
	 * Test that an order can have failed status and be found by such.
	 */
	@DirtiesDatabase
	@Test
	public void testFindFailedOrderUids() {
		Order order = persistOrder(product);

		final long ninetyDaysInMillis = 90L * 24 * 60 * 60 * 1000;
		Date now = timeService.getCurrentTime();
		Date past = new Date(now.getTime() - ninetyDaysInMillis);

		final int maxResults = 10;
		List<Long> failedOrders = orderService.getFailedOrderUids(past, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		order.failOrder();
		order = orderService.update(order);

		failedOrders = orderService.getFailedOrderUids(past, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertEquals("There should be one order returned", 1, failedOrders.size());

		Long failedOrderUid = failedOrders.get(0);
		assertEquals("The failed order should be the we updated", order.getUidPk(), failedOrderUid.longValue());
	}

	private Order persistOrder(final Product product) {
		Order order = persisterFactory.getOrderTestPersister().createOrderWithSkus(scenario.getStore(), product.getDefaultSku());
		assertEquals("Order status should be in progress", OrderStatus.IN_PROGRESS, order.getStatus());
		return order;
	}

	/**
	 * Tests {@link OrderService#getFailedOrderUids(Date, int)} to ensure it does not return more results than it is supposed to be returning.
	 */
	@DirtiesDatabase
	@Test
	public void testFindFailedOrderUidsMaxResults() {
		final int maxResults = 5;
		final int numOrders = 10;
		for (int i = 0; i < numOrders; ++i) {
			createAndPersistFailedOrder(product);
		}
		Date now = timeService.getCurrentTime();
		List<Long> failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertEquals("The method should return exactly maxResults results", maxResults, failedOrders.size());
	}

	/**
	 * Tests {@link OrderService#deleteOrders(List)} to ensure it cleans all the associations as well.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteOrder() {
		Order order = persistOrder(product);
		assertExpectedPersistedInstances(1, OrderImpl.class);
		assertExpectedPersistedInstances(1, OrderSkuImpl.class);
		assertExpectedPersistedInstances(1, OrderPaymentImpl.class);
		assertExpectedPersistedInstances(1, AbstractOrderShipmentImpl.class);
		assertExpectedPersistedInstances(1, OrderEventImpl.class);
		assertExpectedPersistedInstances(2, OrderAddressImpl.class);

		orderService.deleteOrders(Arrays.asList(order.getUidPk()));

		assertExpectedPersistedInstances(0, OrderImpl.class);
		assertExpectedPersistedInstances(0, OrderSkuImpl.class);
		assertExpectedPersistedInstances(0, OrderPaymentImpl.class);
		assertExpectedPersistedInstances(0, AbstractOrderShipmentImpl.class);
		assertExpectedPersistedInstances(0, OrderEventImpl.class);
		assertExpectedPersistedInstances(0, OrderAddressImpl.class);
	}

	private long getCount(final Class<?> entityClass) {
		PersistenceEngine persistenceEngine = getBeanFactory().getBean(ContextIdNames.PERSISTENCE_ENGINE);
		String entityName = entityClass.getSimpleName();
		String query = "SELECT COUNT(o.uidPk) FROM " + entityName + " o";
		List<Long> retrieve = persistenceEngine.retrieve(query);
		return retrieve.get(0);
	}

	private void assertExpectedPersistedInstances(final long count, final Class<?> entityClass) {
		assertEquals("The number of persisted instances should be as expected", count, getCount(entityClass));
	}

	/**
	 * Test finding the latest order GUID by cart order GUID when no cart orders exist.
	 */
	@DirtiesDatabase
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenNoCartOrdersExist() {
		// create an order with no cart order GUID assigned
		persistOrder(product);
		String result = orderService.findLatestOrderGuidByCartOrderGuid(NON_EXISTENT_CART_ORDER_GUID);
		assertNull("There should be no order guid returned.", result);
	}

	/**
	 * Test finding latest order GUID by cart order GUID when many orders exist for a cart order.
	 */
	@DirtiesDatabase
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenManyOrdersExistForACartOrder() {
		Order firstOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		Order secondOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		Order thirdOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);

		String result = orderService.findLatestOrderGuidByCartOrderGuid(CART_ORDER_GUID);
		assertFalse("The returned order guid should not equal the first order created.", firstOrder.getGuid().equals(result));
		assertFalse("The returned order guid should not equal the second order created.", secondOrder.getGuid().equals(result));
		assertEquals("The order guid should be the same as the last order created.", thirdOrder.getGuid(), result);
	}

	/**
	 * Test finding an order by cart order GUID will not return an order that does not match the cart order GUID.
	 */
	@DirtiesDatabase
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenManyOrdersExistForACartOrderButDoesNotMatchCartOrderGuidArgument() {
		createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		String result = orderService.findLatestOrderGuidByCartOrderGuid(DIFFERENT_CART_ORDER_GUID);
		assertNull("There should be no order guid returned.", result);
	}


	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderNumbersByCustomerGuid() {
		String customerGuid = customer.getGuid();
		String storeCode = store.getCode();

		List<String> orderNumbersBefore = orderService.findOrderNumbersByCustomerGuid(storeCode, customerGuid);
		assertTrue("No orders should be found as none is created yet.", orderNumbersBefore.isEmpty());

		Order order = createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid(storeCode, customerGuid);
		assertEquals("There should be exactly one order found.", 1, orderNumbers.size());
		assertTrue("The order number should match the one we just created.", orderNumbers.contains(order.getOrderNumber()));
	}

	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderNumbersByCustomerGuidInvalidCustomerGuid() {
		String storeCode = store.getCode();
		createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid(storeCode, "INVALID CUSTOMER GUID");
		assertTrue("No order should be found, since the customer guid does not match the GUID of the order's owner",
				orderNumbers.isEmpty());
	}

	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@DirtiesDatabase
	@Test
	public void testFindOrderNumbersByCustomerGuidInvalidStoreCode() {
		String customerGuid = customer.getGuid();
		createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid("INVALID_STORE_CODE", customerGuid);
		assertTrue("No order should be found, since the store code does not match that of the order.",
				orderNumbers.isEmpty());
	}

	private Order createOrderWithCartOrderGuid(final Product product, final String cartOrderGuid) {
		Order order = persistOrder(product);
		order.setCartOrderGuid(cartOrderGuid);
		return orderService.update(order);
	}

}
