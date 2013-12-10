/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.impl.DefaultTaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test cases for <code>OrderImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class OrderImplTest extends AbstractEPTestCase {

	private static final String TEST_ORDER_NUMBER1 = "TestOrderNumber1";

	private static final Currency CURRENCY = Currency.getInstance("USD");

	private static final long TEST_UIDPK = 10000L;
	
	private static final String TAX_CODE = "tax_code";
	private static final String FOO = "foo";
	private static final String BAR = "bar";

	private Store store;
	private StoreService storeService;

	/**
	 * Prepare for the tests.
	 *
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		store = createStore();

		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);

		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(store.getCode()); will(returnValue(store));
		} });
	}
	
	/**
	 * Test that the order subtotal is the sum of the order's shipments subtotals.
	 */
	@Test
	public void testSubtotalSum() {
		final BigDecimal shipmentSubtotal = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotal();
				will(returnValue(shipmentSubtotal));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotal();
				will(returnValue(shipmentSubtotal));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<OrderShipment>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = -5548083387876584259L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
		};
		assertEquals("Order subtotal should be the sum of its shipment subtotals, and the scale should be 2.", 
				new BigDecimal("20").setScale(2), order.getSubtotal());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		Order order = createTestOrder();
		Date testDate = new Date();
		order.setCreatedDate(testDate);
		assertEquals(testDate, order.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetSetLastModifiedDate() {
		Order order = createTestOrder();
		Date testDate = new Date();
		order.setLastModifiedDate(testDate);
		assertEquals(testDate, order.getLastModifiedDate());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLastModifiedBy()'.
	 */
	@Test
	public void testGetSetLastModifiedBy() {
		Order order = createTestOrder();
		EventOriginator originator = new EventOriginatorImpl();
		order.setModifiedBy(originator);
		assertEquals(originator, order.getModifiedBy());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getIpAddress()'.
	 */
	@Test
	public void testGetSetIpAddress() {
		Order order = createTestOrder();
		String testString = "TestString";
		order.setIpAddress(testString);
		assertEquals(testString, order.getIpAddress());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCustomer()'.
	 */
	@Test
	public void testGetSetCustomer() {
		Order order = createTestOrder();
		Customer customer = new CustomerImpl();
		order.setCustomer(customer);
		assertEquals(customer, order.getCustomer());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderBillingAddress()'.
	 */
	@Test
	public void testGetSetOrderBillingAddress() {
		Order order = createTestOrder();
		OrderAddress orderAddress = new OrderAddressImpl();
		order.setBillingAddress(orderAddress);
		assertEquals(orderAddress, order.getBillingAddress());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderShipments()'.
	 */
	@Test
	public void testGetSetOrderShipments() {
		OrderImpl order = createTestOrder();
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		List<OrderShipment> shipmentSet = new ArrayList<OrderShipment>();
		shipmentSet.add(orderShipment);
		order.setShipments(shipmentSet);
		assertEquals(shipmentSet, order.getAllShipments());

		int numShipments = order.getAllShipments().size();
		OrderShipment anotherOrderShipment = new PhysicalOrderShipmentImpl();
		order.addShipment(anotherOrderShipment);
		assertEquals(numShipments + 1, order.getAllShipments().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderShipments()'.
	 */
	@Test
	public void testGetSetOrderPayments() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		Set<OrderPayment> paymentSet = new HashSet<OrderPayment>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		assertEquals(paymentSet, order.getOrderPayments());

		int numPayments = order.getOrderPayments().size();
		OrderPayment anotherOrderPayment = new OrderPaymentImpl();
		order.addOrderPayment(anotherOrderPayment);
		assertEquals(numPayments + 1, order.getOrderPayments().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getOrderEvents()'.
	 */
	@Test
	public void testGetSetOrderEvents() {
		OrderImpl order = createTestOrder();
		Set<OrderEvent> orderNotesSet = new HashSet<OrderEvent>();
		order.setOrderEvents(orderNotesSet);
		assertEquals(orderNotesSet, order.getOrderEvents());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getLocale()'.
	 */
	@Test
	public void testGetSetLocale() {
		Order order = createTestOrder();
		Locale testLocale = Locale.CANADA;
		order.setLocale(testLocale);
		assertEquals(testLocale, order.getLocale());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderImpl.getCurrency()'.
	 */
	@Test
	public void testGetSetCurrency() {
		Order order = createTestOrder();
		order.setCurrency(CURRENCY);
		assertEquals(CURRENCY, order.getCurrency());
	}

	/**
	 * Test that the before tax subtotal is the sum of the before tax subtotal
	 * of all the OrderShipments, and that the scale of the returned Money object
	 * is 2.
	 */
	@Test
	public void testBeforeTaxSubtotalSum() {
		final BigDecimal beforeTaxSubtotal = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotalBeforeTax();
				will(returnValue(beforeTaxSubtotal));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotalBeforeTax();
				will(returnValue(beforeTaxSubtotal));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<OrderShipment>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = 3957500176048089715L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
			@Override
			public Currency getCurrency() {
				return CURRENCY;
			}
		};
		assertEquals("Order beforeTaxSubtotal should be the sum of its shipment beforeTaxSubtotals, and the scale should be 2.",
				new BigDecimal("20").setScale(2), order.getBeforeTaxSubtotalMoney().getAmountUnscaled());
		assertEquals(CURRENCY, order.getBeforeTaxSubtotalMoney().getCurrency());
	}

	/**
	 * Test that the subtotal discount is the sum of the subtotal discounts
	 * of all the OrderShipments, and that the scale of the returned Money object
	 * is 2.
	 */
	@Test
	public void testSubtotalDiscountSum() {
		final BigDecimal subtotalDiscount = BigDecimal.TEN;
		final OrderShipment mockOrderShipment = context.mock(OrderShipment.class);
		final OrderShipment mockOrderShipment2 = context.mock(OrderShipment.class, "second order shipment");
		context.checking(new Expectations() {
			{
				allowing(mockOrderShipment).getSubtotalDiscount();
				will(returnValue(subtotalDiscount));

				allowing(mockOrderShipment).getCreatedDate();
				will(returnValue(new Date()));

				allowing(mockOrderShipment2).getSubtotalDiscount();
				will(returnValue(subtotalDiscount));

				allowing(mockOrderShipment2).getCreatedDate();
				will(returnValue(new Date()));
			}
		});
		final List<OrderShipment> shipments = new ArrayList<OrderShipment>();
		shipments.add(mockOrderShipment);
		shipments.add(mockOrderShipment2);
		OrderImpl order = new OrderImpl() {
			private static final long serialVersionUID = -4789276059268183318L;

			@Override
			public List<OrderShipment> getShipments() {
				return shipments;
			}
			@Override
			public Currency getCurrency() {
				return CURRENCY;
			}
		};
		assertEquals("Order subtotal discount should be the sum of its shipment subtotal discounts, and the scale should be 2.",
				new BigDecimal("20").setScale(2), order.getSubtotalDiscountMoney().getAmountUnscaled());
		assertEquals(CURRENCY, order.getSubtotalDiscountMoney().getCurrency());
	}

	/** Test for getOrderShippingCostMoney(). */
	@Test
	public void testGetOrderShippingCostMoney() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCurrencyCode(CURRENCY.getCurrencyCode());
		Set<OrderPayment> paymentSet = new HashSet<OrderPayment>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);

		assertEquals(BigDecimal.ZERO.setScale(2), order.getTotalShippingCostMoney().getAmount());

		PhysicalOrderShipment orderShipment1 = new PhysicalOrderShipmentImpl();
		orderShipment1.setShippingCost(new BigDecimal("50.00"));
		order.addShipment(orderShipment1);

		PhysicalOrderShipment orderShipment2 = new PhysicalOrderShipmentImpl();
		orderShipment2.setShippingCost(new BigDecimal("25.50"));
		order.addShipment(orderShipment2);

		assertEquals(new BigDecimal("75.50"), order.getTotalShippingCostMoney().getAmount());

	}

	/** Test for getBeforeTaxShippingCostMoney(). */
	@Test
	public void testGetBeforeTaxShippingCostMoney() {
		Order order = createTestOrder();
		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCurrencyCode(CURRENCY.getCurrencyCode());
		Set<OrderPayment> paymentSet = new HashSet<OrderPayment>();
		paymentSet.add(orderPayment);
		order.setOrderPayments(paymentSet);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);

		assertEquals(BigDecimal.ZERO.setScale(2), order.getTotalShippingCostMoney().getAmount());

		PhysicalOrderShipment orderShipment1 = new PhysicalOrderShipmentImpl();
		orderShipment1.setShippingCost(new BigDecimal("45.00"));
		order.addShipment(orderShipment1);

		PhysicalOrderShipment orderShipment2 = new PhysicalOrderShipmentImpl();
		orderShipment2.setShippingCost(new BigDecimal("22.50"));
		order.addShipment(orderShipment2);

		assertEquals(new BigDecimal("67.50"), order.getBeforeTaxTotalShippingCostMoney().getAmount());

	}

	/** test for OrderImpl method. */
	@Test
	public void testGetShippingAddress() {
		Order order = createTestOrder();
		assertNull(order.getShippingAddress());

		OrderAddress testAddress = new OrderAddressImpl();
		PhysicalOrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		orderShipment.setShipmentAddress(testAddress);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(orderShipment);

		assertSame(testAddress, order.getShippingAddress());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.getReturns()'.
	 */
	@Test
	public void testGetReturns() {
		OrderImpl order = createTestOrder();
		Set<OrderReturn> returns = new HashSet<OrderReturn>();
		order.setReturns(returns);
		assertEquals(returns, order.getReturns());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.addReturn(OrderReturn)'.
	 */
	@Test
	public void testOrderReturn() {
		Order order = createTestOrder();
		OrderReturn orderReturn = new OrderReturnImpl();
		order.addReturn(orderReturn);
		assertEquals(1, order.getReturns().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.adOrderNote(OrderNote)'.
	 */
	@Test
	public void testOrderNote() {
		Order order = createTestOrder();
		OrderEvent orderEvent = new OrderEventImpl();
		order.addOrderEvent(orderEvent);
		assertEquals(1, order.getOrderEvents().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.getOrderNumber'.
	 */
	@Test
	public void testGetSetOrderNumber() {
		Order order = createTestOrder();
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		assertEquals(TEST_ORDER_NUMBER1, order.getOrderNumber());
		assertEquals(TEST_ORDER_NUMBER1, order.getGuid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.order.impl.OrderImpl.setGuid'.
	 */
	@SuppressWarnings({ "PMD.EmptyCatchBlock" })
	@Test
	public void testSetGuidShouldThrowException() {
		Order order = createTestOrder();
		try {
			order.setGuid(TEST_ORDER_NUMBER1);
			fail();
		} catch (Exception e) {
			// exception was expected
		}
	}
	
	/**
	 * Test order status changes due to its shipment status changes.
	 */
	@Test
	public void testOrderStatusChange() {
		Order order = createTestOrder();
		final OrderShipment shipment = new PhysicalOrderShipmentImpl();
		shipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		final OrderShipment shipment2 = new PhysicalOrderShipmentImpl();
		shipment2.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(shipment);
		order.addShipment(shipment2);
		assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
		
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		assertEquals(OrderStatus.PARTIALLY_SHIPPED, order.getStatus());
		
		shipment2.setStatus(OrderShipmentStatus.SHIPPED);
		assertEquals(OrderStatus.COMPLETED, order.getStatus());
	}
	
	/**
	 * Test that an order with digital goods cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWithDigitalGoods() {
		Order order = createTestOrder();
		final OrderShipment shipment = new ElectronicOrderShipmentImpl();
		shipment.setStatus(OrderShipmentStatus.RELEASED);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(shipment);
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		assertFalse(order.isCancellable());
	}
	
	/**
	 * Test that a completed order cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWhenComplete() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.COMPLETED);
		assertFalse(order.isCancellable());
	}
	
	/**
	 * Test that a partially completed order cannot be cancelled.
	 */
	@Test
	public void testOrderNotCancellableWhenPartiallyShipped() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
		assertFalse(order.isCancellable());
	}
	
	/**
	 * Test that an order awaiting exchange can be cancelled.
	 */
	@Test
	public void testOrderCancellableWhenAwaitingExchange() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.AWAITING_EXCHANGE);
		assertFalse(order.isCancellable());
	}
	
	/**
	 * Test that an order that has been cancelled cannot be cancelled again.
	 */
	@Test
	public void testOrderNotCancellableWhenCancelled() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CANCELLED);
		assertFalse(order.isCancellable());
	}
	
	/**
	 * Test that an order currently in progress can be cancelled.
	 */
	@Test
	public void testOrderCancellableWhenInProgress() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.IN_PROGRESS);
		assertTrue(order.isCancellable());
	}
	
//	/**
//	 * Test that an order pending review can be cancelled.
//	 */
//	public void testOrderCancellableWhenPendingReview() {
//		OrderImpl order = (OrderImpl) createTestOrder();
//		order.setStatus(OrderStatus.PENDING_REVIEW);
//		assertTrue(order.isCancellable());
//	}
	
	/**
	 * Test that an order with digital goods can be put on hold.
	 */
	@Test
	public void testOrderHoldableWithDigitalGoods() {
		Order order = createTestOrder();
		final OrderShipment shipment = new ElectronicOrderShipmentImpl();
		shipment.setStatus(OrderShipmentStatus.RELEASED);
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.addShipment(shipment);
		assertTrue(order.isHoldable());
	}
	
	/**
	 * Test that a completed order cannot be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenComplete() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.COMPLETED);
		assertFalse(order.isHoldable());
	}
	
	/**
	 * Test that a partially completed order can be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenPartiallyShipped() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
		assertFalse(order.isHoldable());
	}
	
	/**
	 * Test that an order awaiting exchange can be put on hold.
	 */
	@Test
	public void testOrderHoldableWhenAwaitingExchange() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.AWAITING_EXCHANGE);
		assertFalse(order.isHoldable());
	}
	
	/**
	 * Test that an order that has been cancelled cannot be put on hold.
	 */
	@Test
	public void testOrderNotHoldableWhenCancelled() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.CANCELLED);
		assertFalse(order.isHoldable());
	}
	
	/**
	 * Test that an order currently in progress can be put on hold.
	 */
	@Test
	public void testOrderHoldableWhenInProgress() {
		OrderImpl order = createTestOrder();
		order.setStatus(OrderStatus.IN_PROGRESS);
		assertTrue(order.isHoldable());
	}
	
//	/**
//	 * Test that an order pending review can be put on hold.
//	 */
//	public void testOrderHoldablePendingReview() {
//		OrderImpl order = (OrderImpl) createTestOrder();
//		order.setStatus(OrderStatus.PENDING_REVIEW);
//		assertTrue(order.isHoldable());
//	}
	
	/**
	 * Test that a shipment can be added to an order and gets a valid shipment number.
	 */
	@Test
	public void testPersistedOrderAddShipment() {
		Order order = createTestOrder();
		assertTrue("Order should be persistent", order.isPersisted());
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		try {
			order.addShipment(orderShipment);
		} catch (OrderNotPersistedException ex) {
			fail("Persisted order should not have a problem adding a shipment");
		}
		assertNotNull("Shipment number should not be null", orderShipment.getShipmentNumber());
		assertTrue("Shipment number should not be empty", orderShipment.getShipmentNumber().length() > 0);
		assertTrue("Shipment number should contain order number", orderShipment.getShipmentNumber().contains(order.getOrderNumber()));
	}

	/**
	 * Test that an attempt to add a shipment to a transient order throws an exception.
	 */
	@Test
	public void testTransientOrderAddShipment() {
		Order order = createTestOrder();
		order.setUidPk(0);
		assertFalse("Order should not be persistent", order.isPersisted());
		OrderShipment orderShipment = new PhysicalOrderShipmentImpl();
		try {
			order.addShipment(orderShipment);
			fail("Attempting to add a shipment to a transient order should result in an exception");
		} catch (OrderNotPersistedException expected) {
			assertNotNull("Exception was expected", expected);
		}
	}

	@Test
	public void testOrderDataLazyInstantiation() {
		Order order = createTestOrder();

		assertNull("Getter of non-existent order data key should return null", order.getFieldValue(FOO));
		assertEquals("Map getter should return empty map", Collections.emptyMap(), order.getFieldValues());
	}

	@Test
	public void testOrderData() {
		Order order = createTestOrder();
		order.setFieldValue(FOO, BAR);

		assertEquals("Getter/Setters should work as expected", BAR, order.getFieldValue(FOO));
		assertEquals("Map getter should also work", Collections.singletonMap(FOO, BAR), order.getFieldValues());
	}

	/**
	 * Implementation of Order with auto-recalculation enabled by default for testing purposes.
	 */
	class TestOrderImpl extends OrderImpl implements Order {
		
		private static final long serialVersionUID = -9036493321034387004L;

		/**
		 * Override constructor to enable auto-recalculation.
		 * Uses exclusive tax calculation mode.
		 */
		public TestOrderImpl() {
			this(false);
		}

		/**
		 * Override constructor to enable auto-recalculation.
		 * @param inclusiveTax if tax calculation mode is inclusive
		 */
		public TestOrderImpl(final boolean inclusiveTax) {
			super();
			enableRecalculation();
			setTaxCalculationService(new DefaultTaxCalculationServiceImpl() {
				@Override
				public TaxCalculationResult calculateTaxes(final String storeCode, final Address address, final Currency currency,
						final Money shippingCost, final Collection< ? extends ShoppingItem> lineItems, final Money preTaxDiscount) {
					DefaultTaxCalculationServiceImpl taxCalculationService = new DefaultTaxCalculationServiceImpl() {
						@Override
						protected TaxJurisdiction findTaxJurisdiction(final Store store, final Address address) {
							TaxJurisdictionImpl taxJurisdiction = new TaxJurisdictionImpl();
							if (inclusiveTax) {
								taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE);
							} else {
								taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
							}
							TaxCategoryImpl taxCategory = new TaxCategoryImpl() {
								private static final long serialVersionUID = -4130964090306658630L;

								@Override
								public String getDisplayName(final Locale locale) {
									return "tax_category_name";
								}
							};
							taxCategory.setName("tax_cat_name");
							TaxRegionImpl taxRegion = new TaxRegionImpl();
							taxRegion.setRegionName("BC");
							Map<String, TaxValue> taxValuesMap = new HashMap<String, TaxValue>();
							TaxValue value = new TaxValueImpl();
							TaxCodeImpl taxCode = new TaxCodeImpl();
							taxCode.setCode(TAX_CODE);
							taxCode.setGuid(TAX_CODE);
							value.setTaxCode(taxCode);

							value.setTaxValue(BigDecimal.TEN); // 10% tax
							taxValuesMap.put(TAX_CODE, value);
							
							taxRegion.setTaxValuesMap(taxValuesMap);
							taxCategory.addTaxRegion(taxRegion);
							taxJurisdiction.addTaxCategory(taxCategory);
							return taxJurisdiction;
						}
					};
					taxCalculationService.setStoreService(storeService);
					
					CustomerAddressImpl address2 = new CustomerAddressImpl();
					address2.setCountry("CANADA");
					address2.setSubCountry("BC");
					return taxCalculationService.calculateTaxes(storeCode, address2, currency, shippingCost, lineItems, preTaxDiscount);
				}
			});
		}
	}

	/**
	 * Implementation of PhysicalOrderShipment with auto-recalculation enabled by default for testing purposes.
	 */
	class TestPhysicalOrderShipmentImpl extends PhysicalOrderShipmentImpl implements PhysicalOrderShipment {
		private static final long serialVersionUID = -5938308330575188600L;

		/**
		 * Override default constructor to enable auto-recalculation.
		 */
		public TestPhysicalOrderShipmentImpl() {
			super();
			enableRecalculation();
		}
	}

	/**
	 * Implementation of PhysicalOrderShipment with auto-recalculation enabled by default for testing purposes.
	 */
	class TestElectronicOrderShipmentImpl extends ElectronicOrderShipmentImpl implements ElectronicOrderShipment {
		private static final long serialVersionUID = -1540246999040879265L;

		/**
		 * Override default constructor to enable auto-recalculation.
		 */
		public TestElectronicOrderShipmentImpl() {
			super();
			enableRecalculation();
		}
	}
	
	private OrderImpl createTestOrder() {
		return createTestOrder(false);
	}
	
	/**
	 * Create an order for testing.
	 * 
	 * @param inclusive tax calculation mode if true
	 * @return the test order
	 */
	private OrderImpl createTestOrder(final boolean inclusive) {
		OrderImpl order = new TestOrderImpl(inclusive);
		order.initialize();
		order.setUidPk(TEST_UIDPK);
		order.setOrderNumber(TEST_ORDER_NUMBER1);
		order.setCurrency(CURRENCY);
		order.setStoreCode(store.getCode());
		return order;
	}

	private Store createStore() {
		Set <TaxCode> taxCodes = new HashSet <TaxCode>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(TAX_CODE));

		final Store store = new StoreImpl();
		store.setCode("storeCode");
		store.setTaxCodes(taxCodes);

		return store;
	}
	
	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}
}
