/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.order.impl;

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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.event.impl.OrderEventCreditCardDetailsFormatter;
import com.elasticpath.domain.event.impl.OrderEventGiftCertificateDetailsFormatter;
import com.elasticpath.domain.event.impl.OrderEventHelperImpl;
import com.elasticpath.domain.event.impl.OrderEventPaymentTokenDetailsFormatter;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.impl.AbstractOrderShipmentImpl;
import com.elasticpath.domain.order.impl.AdvancedOrderSearchCriteriaImpl;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.persistence.support.impl.OrderCriterionImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.FetchMode;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.payment.PaymentServiceException;
import com.elasticpath.service.payment.impl.PaymentResultImpl;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test <code>OrderServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength" })
public class OrderServiceImplTest extends AbstractEPServiceTestCase {

	private static final String GUID_VALUE = "guidValue";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final int FIRST_RESULT = 0;

	private static final int MAX_RESULTS = 100;

	private OrderServiceImpl orderServiceImpl;

	private static final String ORDER_NUMBER = "order_1";

	private static final long MINUTE_IN_MILLIS = 60000;

	private static final String EMAIL_VALUE = "support@elasticpath.ca";

	private OrderCriterion orderCriterion;
	private Store store;
	private StoreImpl store2;
	private Order order, order2;
	
	private TimeService mockTimeService;
	private final PaymentService mockPaymentService = context.mock(PaymentService.class);
	private final StoreService mockStoreService = context.mock(StoreService.class);


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		orderServiceImpl = new OrderServiceImpl();
		orderServiceImpl.setPersistenceEngine(getPersistenceEngine());
		orderServiceImpl.setFetchPlanHelper(getFetchPlanHelper());
		context.checking(new Expectations() {
			{
				allowing(getMockFetchPlanHelper()).configureLoadTuner(with(aNull(LoadTuner.class)));
				allowing(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		orderCriterion = new OrderCriterionImpl();
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);

		stubGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, EventOriginatorHelperImpl.class);

		OrderEventHelperImpl orderEventHelper = new OrderEventHelperImpl();
		orderEventHelper.setTimeService(new OrderReturnAndExchangeServiceTest.DummyTimeService());
		orderEventHelper.setBeanFactory(getBeanFactory());
		orderEventHelper.setMoneyFormatter(new StandardMoneyFormatter());
		orderEventHelper.setFormatterMap(createOrderEventHelperFormatterMap());
		
		
		stubGetBean(ContextIdNames.ORDER_EVENT_HELPER, orderEventHelper);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		orderServiceImpl.setPaymentService(mockPaymentService);
		orderServiceImpl.setStoreService(mockStoreService);
		orderServiceImpl.setTimeService(mockTimeService);
		
		stubGetBean(ContextIdNames.ORDER_SERVICE, orderServiceImpl);

		// tax calc service set-up
		final TaxCalculationService taxService = context.mock(TaxCalculationService.class);
		final TaxCalculationResultImpl taxCalculationResultImpl = new TaxCalculationResultImpl();
		taxCalculationResultImpl.setTaxInclusive(true);
		Money money = MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(Locale.US));
		taxCalculationResultImpl.setBeforeTaxShippingCost(money);
		taxCalculationResultImpl.setDefaultCurrency(Currency.getInstance(Locale.CANADA));
		context.checking(new Expectations() {
			{
				allowing(taxService).calculateTaxes(
						with(any(String.class)),
						with(any(Address.class)),
						with(any(Currency.class)),
						with(any(Money.class)),
						with(any(Collection.class)),
						with(any(Money.class)));
				will(returnValue(taxCalculationResultImpl));
			}
		});
		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, taxService);
		stubGetBean(ContextIdNames.ORDER_SERVICE, orderServiceImpl);
	
		store = new StoreImpl();
		store.setCode("store");

		store2 = new StoreImpl();
		store2.setCode("store2");

		context.checking(new Expectations() { {
			allowing(mockStoreService).findStoreWithCode(store.getCode()); will(returnValue(store));
			allowing(mockStoreService).findStoreWithCode(store2.getCode()); will(returnValue(store2));
		} });

		order = new OrderImpl();
		order.setUidPk(1L);
		order.setStoreCode(store.getCode());

		order2 = new OrderImpl();
		order2.setUidPk(1L);
		order2.setStoreCode(store.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		orderServiceImpl.setPersistenceEngine(null);
		try {
			orderServiceImpl.add(new OrderImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(orderServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.add(Order)'.
	 */
	@Test
	public void testAdd() {
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(order)));
			}
		});
		Order added = orderServiceImpl.add(order);
		assertEquals("Store should have been set", store, added.getStore());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findOrder'.
	 */
	@Test
	public void testFindOrder() {
		// expectations
		try {
			orderServiceImpl.findOrder(null, null, true);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});
		final List<Order> resultList1 = orderServiceImpl.findOrder("orderNumber", ORDER_NUMBER, true);
		assertEquals(Collections.singletonList(order), resultList1);
		assertEquals("Store should be populated via the store code", store, order.getStore());

		final List<Order> resultList2 = orderServiceImpl.findOrder("orderNumber", ORDER_NUMBER, false);
		assertEquals(Collections.singletonList(order), resultList2);
		assertEquals("Store should be populated via the store code", store, order.getStore());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findOrderByCustomerCriteria'.
	 */
	@Test
	public void testFindOrderByCustomerEmail() {
		boolean isExactMatch = true;

		// expectations
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});
		assertEquals(Collections.singletonList(order), orderServiceImpl.findOrderByCustomerEmail(EMAIL_VALUE, isExactMatch));
		assertStoreIsPopulated(store, order);

		// expectations
		isExactMatch = false;
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(order2)));
			}
		});
		assertEquals(Collections.singletonList(order2), orderServiceImpl.findOrderByCustomerEmail(EMAIL_VALUE, isExactMatch));
		assertStoreIsPopulated(store, order2);
	}

	@Test
	public void testFindOrderByStatus() {
		// expectations
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});
		assertEquals(Collections.singletonList(order), orderServiceImpl.findOrderByStatus(
				OrderStatus.CANCELLED, OrderPaymentStatus.PENDING, OrderShipmentStatus.INVENTORY_ASSIGNED));
		assertStoreIsPopulated(store, order);
	}

	@Test
	public void testFindOrderByGiftCertificateCode() {
		final boolean isExactMatch = true;
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});

		assertEquals(Collections.singletonList(order), orderServiceImpl.findOrderByGiftCertificateCode("GC1", isExactMatch));
		assertStoreIsPopulated(store, order);
	}
	
	@Test
	public void testAwaitingShipmentsCount() {
		final List<Long> orders = new ArrayList<Long>();
		orders.add(new Long(2));

		Warehouse warehouse = new WarehouseImpl();
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with("COUNT_PHYSICAL_SHIPMENTS_BY_STATUS_AND_WAREHOUSE"), with(any(Object[].class)));
				will(returnValue(orders));
			}
		});
		final Long result = orderServiceImpl.getAwaitingShipmentsCount(warehouse);
		assertEquals(result, new Long(2));
	}

	@Test
	public void testFindOrderByCustomerGuid() {
		boolean isExactMatch = true;

		// expectations
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		stubGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		stubGetBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA, customerSearchCriteria);
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieve(
						with(any(String.class)), with(any(Object[].class)), with(any(int.class)), with(any(int.class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});

		assertEquals("The orders should come from the retrieve call",
				Collections.singletonList(order), orderServiceImpl.findOrderByCustomerGuid(GUID_VALUE, isExactMatch));
		assertStoreIsPopulated(store, order);
		assertEquals("The customer search criteria should include the guid", GUID_VALUE, customerSearchCriteria.getGuid());
		assertEquals("The order criteria should include the customer criteria", customerSearchCriteria,
				orderSearchCriteria.getCustomerSearchCriteria());
		assertEquals("The order criteria should exclude failed orders", OrderStatus.FAILED, orderSearchCriteria.getExcludedOrderStatus());
		assertTrue("The customer search criteria should disable fuzzy match", customerSearchCriteria.isFuzzySearchDisabled());

		// expectations
		isExactMatch = false;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(int.class)), with(any(int.class)));
				will(returnValue(Collections.singletonList(order)));
			}
		});

		assertEquals("The orders should come from the retrieve call",
				Collections.singletonList(order), orderServiceImpl.findOrderByCustomerGuid(GUID_VALUE, isExactMatch));
		assertFalse("The customer search criteria should enable fuzzy match", customerSearchCriteria.isFuzzySearchDisabled());
	}

	@Test
	public void testFindOrderByCustomerGuidAndStoreCode() {
		// expectations
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		stubGetBean(ContextIdNames.ORDER_SEARCH_CRITERIA, orderSearchCriteria);
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
					will(returnValue(Collections.singletonList(order)));
			}
		});

		final boolean retrieveFullInfo = true;
		assertEquals("The orders should come from the retrieve call",
				Collections.singletonList(order),
				orderServiceImpl.findOrdersByCustomerGuidAndStoreCode(GUID_VALUE, store.getCode(), retrieveFullInfo));
		assertStoreIsPopulated(store, order);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findOrderAdvanced'.
	 */
	@Test
	public void testFindOrderAdvanced() {
		final List<Order> orderList = Collections.singletonList(order);

		final AdvancedOrderSearchCriteria orderSearchCriteria = new AdvancedOrderSearchCriteriaImpl();
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieve(
						orderCriterion.getAdvancedOrderCriteria(orderSearchCriteria), FIRST_RESULT, MAX_RESULTS);
				will(returnValue(orderList));
			}
		});
		assertEquals(orderList, orderServiceImpl.findOrderAdvanced(orderSearchCriteria, MAX_RESULTS));
		assertStoreIsPopulated(store, order);

		Calendar fromDate = new GregorianCalendar(Calendar.YEAR, Calendar.AUGUST, Calendar.DAY_OF_MONTH);
		final Date orderFromDate = fromDate.getTime();
		Calendar toDate = new GregorianCalendar(Calendar.YEAR, Calendar.OCTOBER, Calendar.DAY_OF_MONTH);
		final Date orderToDate = toDate.getTime();
		orderSearchCriteria.setOrderFromDate(orderFromDate);
		orderSearchCriteria.setOrderToDate(orderToDate);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieve(
						with(orderCriterion.getAdvancedOrderCriteria(orderSearchCriteria)),
						with(any(Object[].class)),
						with(FIRST_RESULT),
						with(MAX_RESULTS));
				will(returnValue(orderList));
			}
		});
		assertSame(orderList, orderServiceImpl.findOrderAdvanced(orderSearchCriteria, MAX_RESULTS));

	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findByCreatedDate'.
	 */
	@Test
	public void testFindByCreatedDate() {
		final Date date = new Date();
		final List<Order> orders = Collections.singletonList(order);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("ORDER_SELECT_BY_CREATED_DATE", date);
				will(returnValue(orders));
			}
		});
		assertEquals(orders, orderServiceImpl.findByCreatedDate(date));
		assertStoreIsPopulated(store, order);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.list'.
	 */
	@Test
	public void testList() {
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("ORDER_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(order, order2)));
			}
		});
		assertEquals(Arrays.asList(order, order2), orderServiceImpl.list());
		assertStoreIsPopulated(store, order2);
	}

	@Test
	public void testFindOrdersBySearchCriteria() {
		stubGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockFetchPlanHelper()).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTunerImpl.class)));
				allowing(getMockFetchPlanHelper()).setFetchMode(FetchMode.JOIN);
				allowing(getMockFetchPlanHelper()).clearFetchPlan();

				allowing(getMockPersistenceEngine()).retrieve(with(any(String.class)), with(any(Integer.class)), with(any(Integer.class)));
					will(returnValue(Arrays.asList(order, order2)));
			}
		});
		assertEquals(Arrays.asList(order, order2), orderServiceImpl.findOrdersBySearchCriteria(new OrderSearchCriteria(), 1, 1));
		assertStoreIsPopulated(store, order2);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.get'.
	 */
	@Test
	public void testGet() {
		stubGetBean(ContextIdNames.ABSTRACT_ORDER_SHIPMENT, AbstractOrderShipmentImpl.class);
		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).get(OrderImpl.class, order.getUidPk());
				will(returnValue(order));

				allowing(getMockFetchPlanHelper()).addField(with(any(Class.class)), with(any(String.class)));
			}
		});

		final Order loadedOrder = orderServiceImpl.get(order.getUidPk());
		assertSame(order, loadedOrder);
		assertStoreIsPopulated(store, order);
	}

	@Test
	public void testGetOrderDetail() {
		stubGetBean(ContextIdNames.ABSTRACT_ORDER_SHIPMENT, AbstractOrderShipmentImpl.class);
		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).get(OrderImpl.class, order.getUidPk());
				will(returnValue(order));

				allowing(getMockFetchPlanHelper()).addField(with(any(Class.class)), with(any(String.class)));
			}
		});

		final Order loadedOrder = orderServiceImpl.getOrderDetail(order.getUidPk());
		assertSame(order, loadedOrder);
		assertStoreIsPopulated(store, order);
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.get'.
	 */
	@Test
	public void testGetObject() {
		stubGetBean(ContextIdNames.ABSTRACT_ORDER_SHIPMENT, AbstractOrderShipmentImpl.class);
		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).initialize(with(any(Object.class)));

				allowing(getMockPersistenceEngine()).get(OrderImpl.class, order.getUidPk());
				will(returnValue(order));

				allowing(getMockFetchPlanHelper()).addField(with(any(Class.class)), with(any(String.class)));
			}
		});

		final Order loadedOrder = orderServiceImpl.get(order.getUidPk());
		assertSame(order, loadedOrder);
		assertStoreIsPopulated(store, order);
	}

	private OrderSku createOrderSku(final String guid, final long uid, final int qty) {
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setGuid(guid);
		final long skuUid1 = uid;
		orderSku.setUidPk(skuUid1);
		final int skuQty1 = qty;
		orderSku.setPrice(skuQty1, null);
		return orderSku;
	}

	private OrderReturnSku createOrderReturnSku(final String guid, final OrderSku orderSku, final int qtyReturned) {
		OrderReturnSku orderReturnSku = new OrderReturnSkuImpl();
		orderReturnSku.setGuid(guid);
		orderReturnSku.setOrderSku(orderSku);
		orderReturnSku.setQuantity(qtyReturned);
		return orderReturnSku;
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.getOrderSkuReturnQtyMap(orderUid)'.
	 */
	@Test
	public void testGetOrderSkuReturnQtyMap() {
		//Create an orderSku for 2 items
		final long skuUid1 = 111L;
		final int skuQty1 = 2;
		OrderSku orderSku1 = createOrderSku("OrderSku1", skuUid1, skuQty1);
		//Create a return for 1 of the first 2 items
		final int returnSku1Qty1 = 1;
		OrderReturnSku orderReturnSku1 = createOrderReturnSku("OrderReturnSku1", orderSku1, returnSku1Qty1);
		OrderReturn orderReturn1 = new OrderReturnImpl();
		orderReturn1.setRmaCode("rmaCode1");
		Set<OrderReturnSku> orderReturnSkus1 = new HashSet<OrderReturnSku>();
		orderReturnSkus1.add(orderReturnSku1);
		orderReturn1.setOrderReturnSkus(orderReturnSkus1);

		//Create a return for the second of the first OrderSku's 2 items
		final int returnSku1Qty2 = 1;
		OrderReturnSku orderReturnSku2 = createOrderReturnSku("OrderReturnSku2", orderSku1, returnSku1Qty2);

		//Create a second orderSku for 3 items
		final long skuUid2 = 222L;
		final int skuQty2 = 3;
		OrderSku orderSku2 = createOrderSku("orderSku2", skuUid2, skuQty2);
		//Create a return for 1 of the second orderSku's 3 items
		final int returnSku2Qty1 = 1;
		OrderReturnSku orderReturnSku3 = createOrderReturnSku("orderReturnSku3", orderSku2, returnSku2Qty1);

		//At this point the first OrderSku's 2 items should both be returned, and one of the second OrderSku's 3 items should be returned.
		//Add the OrderReturnSkus to an OrderReturn
		Set<OrderReturnSku> orderReturnSkus2 = new HashSet<OrderReturnSku>();
		orderReturnSkus2.add(orderReturnSku2);
		orderReturnSkus2.add(orderReturnSku3);
		OrderReturn orderReturn2 = new OrderReturnImpl();
		orderReturn2.setRmaCode("rmaCode2");
		orderReturn2.setOrderReturnSkus(orderReturnSkus2);

		final Order mockOrder = context.mock(Order.class);
		final long uid = 1234L;
		context.checking(new Expectations() {
			{
				allowing(mockOrder).getUidPk();
				will(returnValue(uid));
			}
		});
		final Set<OrderReturn> orderReturns = new HashSet<OrderReturn>();
		orderReturns.add(orderReturn1);
		orderReturns.add(orderReturn2);
		context.checking(new Expectations() {
			{
				allowing(mockOrder).getReturns();
				will(returnValue(orderReturns));
			}
		});
		final Order order = mockOrder;
		OrderServiceImpl service = new OrderServiceImpl() {
			@Override
			public Order get(final long orderUid) {
				return order;
			}

		};

		Map<Long, Integer> orderSkuReturnedMap = service.getOrderSkuReturnQtyMap(uid);
		assertEquals("Two items should be returned from the first OrderSku.",
				returnSku1Qty1 + returnSku1Qty2, (orderSkuReturnedMap.get(new Long(skuUid1))).intValue());
		assertEquals("One item should be returned from the second OrderSku.",
				returnSku2Qty1, (orderSkuReturnedMap.get(new Long(skuUid2))).intValue());
	}

	/**
	 * Test method for 'com.elasticpath.service.OrderServiceImpl.findByUids(orderUids)'.
	 */
	@Test
	public void testFindByUids() {
		final List<Long> orderUids = Arrays.asList(1L, 2L);

		// expectations
		final List<Order> orders = Arrays.asList(order, order2);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQueryWithList(
						with("ORDER_BY_UIDS"), with("list"), with(orderUids), with(any(Object[].class)));
				will(returnValue(orders));
			}
		});
		assertEquals(orders, this.orderServiceImpl.findByUids(orderUids));
		assertStoreIsPopulated(store, order);

		// Should return an empty list if no product uid is given.
		List<Order> result = this.orderServiceImpl.findByUids(new ArrayList<Long>());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 4841309847005158665L;

			@Override
			public Money getBeforeTaxShippingCost() {
				return MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(Locale.US));
			}

			@Override
			public Money getBeforeTaxSubTotal() {
				return MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.US));
			}

		};
		result.setDefaultCurrency(Currency.getInstance(Locale.US));

		PhysicalOrderShipmentImpl physicalOrderShipmentImpl = new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = 7907342381488460247L;

			@Override
			protected TaxCalculationResult calculateTaxes() {
				return result;
			}

			@Override
			public String getShipmentNumber() {
				return "1-1";
			}

			@Override
			public long getUidPk() {
				return 2;
			}
		};

		return physicalOrderShipmentImpl;

	}

	/**
	 * Test that when a capture fails while attempting to complete a shipment we deal with it nicely.
	 */
	@Test
	public void testCompleteShipmentCaptureFails() {

		stubGetBean(ContextIdNames.EVENT_ORIGINATOR, EventOriginatorImpl.class);

		final String orderNumber = "1";
		final String shipmentNumber = "1-1";
		final String trackingCode = "code";
		final boolean captureFund = true;
		final boolean sendConfEmail = false;
		final List<OrderShipment> shipmentList = new ArrayList<OrderShipment>();
		PhysicalOrderShipmentImpl physicalOrderShipmentImpl = this.getMockPhysicalOrderShipment();

		shipmentList.add(physicalOrderShipmentImpl);

		List<Order> orderList = new ArrayList<Order>();
		Order order = new OrderImpl() {
			private static final long serialVersionUID = -2159834083423268104L;

			@Override
			public String getOrderNumber() {
				return orderNumber;
			}

		};
		order.setUidPk(1);
		orderList.add(order);
		order.addShipment(physicalOrderShipmentImpl);
		context.checking(new Expectations() {
			{

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("PHYSICAL_SHIPMENT_BY_SHIPMENT_NUMBER"), with(any(Object[].class)));
				will(returnValue(shipmentList));

				allowing(getMockPersistenceEngine()).merge(with(any(Persistable.class)));
				allowing(getMockPersistenceEngine()).save(with(any(Persistable.class)));
			}
		});

		final PaymentResult paymentResult = new PaymentResultImpl();
		paymentResult.setResultCode(PaymentResult.CODE_FAILED);
		final OrderPayment orderPayment = new OrderPaymentImpl();
		paymentResult.addProcessedPayment(orderPayment);
		context.checking(new Expectations() {
			{
				allowing(mockPaymentService).processShipmentPayment(with(any(OrderShipment.class)));
				will(returnValue(paymentResult));

				allowing(mockPaymentService).adjustShipmentPayment(with(any(OrderShipment.class)));
				will(throwException(new PaymentServiceException("expected")));

				oneOf(mockPaymentService).rollBackPayments(Collections.singletonList(orderPayment));
			}
		});

		try {
			orderServiceImpl.completeShipment(shipmentNumber, trackingCode, captureFund, null,
					sendConfEmail, getEventOriginatorHelper().getSystemOriginator());
			fail("Expected an exception to be thrown since the payment capture didn't succeed.");
		} catch (CompleteShipmentFailedException e) {
			assertNotNull(e);
		}
	}

	/**
	 * test the quartz job method to update shipment status.
	 */
	@Test
	public void testUpdateOrderShipmentStatus() {

		stubGetBean(ContextIdNames.EVENT_ORIGINATOR, EventOriginatorImpl.class);
		stubGetBean(ContextIdNames.ABSTRACT_ORDER_SHIPMENT, AbstractOrderShipmentImpl.class);
		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);
		stubGetBean(ContextIdNames.ORDER_EVENT, OrderEventImpl.class);

		final int pickDelayOrder1 = 4;
		final int pickDelayOrder2 = 8;
		Date now = new Date(System.currentTimeMillis());

		final Order order = new OrderImpl();
		order.setUidPk(1);
		Warehouse warehouse1 = new WarehouseImpl();
		warehouse1.setPickDelay(pickDelayOrder1);
		store.setWarehouses(Arrays.asList(warehouse1));
		order.setStoreCode(store.getCode());

		final Order secondOrder = new OrderImpl();
		secondOrder.setUidPk(2);
		Warehouse warehouse2 = new WarehouseImpl();
		warehouse2.setPickDelay(pickDelayOrder2);

		store2.setWarehouses(Arrays.asList(warehouse2));
		secondOrder.setStoreCode(store2.getCode());
		
		final OrderShipment order2Shipment = this.getMockPhysicalOrderShipment();
		order2Shipment.setCreatedDate(now);
		order2Shipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		// set created date an hour earlier than the actual pick delay gets active
		Date order2CreationDate = new Date(System.currentTimeMillis() - ((pickDelayOrder2 - 1) * MINUTE_IN_MILLIS));
		order2Shipment.setCreatedDate(order2CreationDate);
		secondOrder.addShipment(order2Shipment);

		final OrderShipment newShipment = this.getMockPhysicalOrderShipment();
		newShipment.setCreatedDate(now);
		newShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);

		// set created date an hour later than the actual pick delay gets active
		Date order1CreationDate = new Date(System.currentTimeMillis() - ((pickDelayOrder1 + 1) * MINUTE_IN_MILLIS));
		final OrderShipment oldShipment = this.getMockPhysicalOrderShipment();
		oldShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		oldShipment.setCreatedDate(order1CreationDate);

		order.addShipment(oldShipment);
		order.addShipment(newShipment);
		final List <Order> orderList = new LinkedList<Order>();
		orderList.add(order);
		orderList.add(secondOrder);

		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("listOrderStatus", Arrays.asList(OrderStatus.IN_PROGRESS, OrderStatus.PARTIALLY_SHIPPED));
		parameters.put("shipmentStatus", OrderShipmentStatus.INVENTORY_ASSIGNED);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with("ORDERS_BY_ORDER_STATUS_AND_SHIPMENT_STATUS"), with(parameters));
				will(returnValue(orderList));

				allowing(getMockPersistenceEngine()).get(OrderImpl.class, new Long(1));
				will(returnValue(order));

				allowing(getMockPersistenceEngine()).get(OrderImpl.class, new Long(2));
				will(returnValue(secondOrder));

				allowing(getMockPersistenceEngine()).merge(with(any(Persistable.class)));
				will(returnValue(order));

				allowing(getMockPersistenceEngine()).save(with(any(Persistable.class)));

				allowing(mockPaymentService).adjustShipmentPayment(with(any(OrderShipment.class)));
				will(returnValue(new PaymentResultImpl()));

				allowing(getMockFetchPlanHelper()).addField(with(any(Class.class)), with(any(String.class)));
			}
		});
		this.orderServiceImpl.updateOrderShipmentStatus();

		assertEquals(OrderShipmentStatus.RELEASED, oldShipment.getShipmentStatus());
		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, newShipment.getShipmentStatus());
		assertEquals(OrderShipmentStatus.INVENTORY_ASSIGNED, order2Shipment.getShipmentStatus());

	}

	/** Test that you cannot cancel an order that is not cancellable. */
	@Test
	public void testProcessOrderCancellationNotCancellable() {
		Order order = new OrderImpl() {
			private static final long serialVersionUID = 4330059855392807314L;

			@Override
			public boolean isCancellable() {
				return false;
			}
		};

		try {
			orderServiceImpl.processOrderCancellation(order);
			fail("Shouldn't be allowed to cancel this order");
		} catch (EpServiceException expected) {
			assertNotNull(expected);
		}
	}

	/** Test that you cannot cancel an order that is not cancellable. */
	@Test
	public void testProcessOrderCancellationIsCancellableNoOrderSkus() {

		// Mock out getEventOriginator to make the test easier
		orderServiceImpl = new OrderServiceImpl() {
			@Override
			String getEventOriginator(final Order order) {
				return InventoryAudit.EVENT_ORIGINATOR_WS;
			}
			@Override
			protected void sanityCheck() {
				// Always be sane
			}
			@Override
			public Order update(final Order order) throws EpServiceException {
				return order;  // ignore persistence - not testing that
			}
		};

		// This order is cancellable
		Order order = new OrderImpl() {
			private static final long serialVersionUID = -392157231837612308L;

			@Override
			public boolean isCancellable() {
				return true;
			}
		};

		Order processedOrder = orderServiceImpl.processOrderCancellation(order);
		assertEquals(OrderStatus.CANCELLED, processedOrder.getStatus());
	}

	/**
	 * Test that when a capture fails we deal with it nicely.
	 */
	@Test
	public void testProcessOrderShipment() {
		stubGetBean(ContextIdNames.EVENT_ORIGINATOR, EventOriginatorImpl.class);

		final String orderNumber = "1";
		final String shipmentNumber = "2";
		final List<OrderShipment> shipmentList = new ArrayList<OrderShipment>();

		final PhysicalOrderShipmentImpl physicalOrderShipmentImpl = this.getMockPhysicalOrderShipment();
		physicalOrderShipmentImpl.setShipmentNumber(shipmentNumber);

		shipmentList.add(physicalOrderShipmentImpl);

		final Order order = new OrderImpl() {
			private static final long serialVersionUID = 5193999827719720358L;

			@Override
			public String getOrderNumber() {
				return orderNumber;
			}

		};
		order.setUidPk(1);
		order.setStoreCode(store.getCode());
		order.addShipment(physicalOrderShipmentImpl);
		context.checking(new Expectations() {
			{

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("PHYSICAL_SHIPMENT_BY_SHIPMENT_NUMBER"), with(any(Object[].class)));
				will(returnValue(shipmentList));

				allowing(getMockPersistenceEngine()).merge(with(any(Persistable.class)));
				will(returnValue(order));

				allowing(getMockPersistenceEngine()).save(with(any(Persistable.class)));
				oneOf(mockPaymentService).finalizeShipment(with(same(physicalOrderShipmentImpl)));
			}
		});
		final Order result = orderServiceImpl.processOrderShipment(shipmentNumber, null, null, getEventOriginatorHelper().getSystemOriginator());
		assertEquals("Order should now be in the COMPLETED state.", OrderStatus.COMPLETED, result.getStatus());
		assertEquals("Shipment should not be in the SHIPPED state.", OrderShipmentStatus.SHIPPED, result.getAllShipments().iterator().next()
				.getShipmentStatus());
	}

	/**
	 * Test method for {@link OrderServiceImpl#get(long, FetchGroupLoadTuner).
	 */
	@Test
	public void testGetWithFGLoadTuner() {
		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);
		final FetchGroupLoadTuner mockFGLoadTuner = context.mock(FetchGroupLoadTuner.class);

		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(OrderImpl.class, order.getUidPk());
				will(returnValue(order));

				allowing(getMockFetchPlanHelper()).configureFetchGroupLoadTuner(with(same(mockFGLoadTuner)));
				allowing(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		assertSame(order, orderServiceImpl.get(order.getUidPk(), mockFGLoadTuner));
		assertStoreIsPopulated(store, order);

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(OrderImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});
		assertNull(orderServiceImpl.get(nonExistUid, mockFGLoadTuner));

		assertEquals(0, orderServiceImpl.get(0, mockFGLoadTuner).getUidPk());
	}

	/**
	 * Tests adding order return and appropriate index update notification sending.
	 */
	@Test
	public void testAddOrderReturn() {
		final OrderReturn orderReturn = new OrderReturnImpl();

		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).merge(with(order));
				will(returnValue(order));
			}
		});

		order.setStoreCode(store.getCode());
		final Order returned = orderServiceImpl.addOrderReturn(order, orderReturn);
		assertEquals("Return should have been added", Collections.singleton(orderReturn), returned.getReturns());
		assertEquals("Store should be populated on return", store, returned.getStore());
	}

	@Test
	public void testProcessRefundOrderPayment() {
		final double amount = 10.0;
		
		final PaymentGateway gateway = context.mock(PaymentGateway.class);
		store.setPaymentGateways(Collections.singleton(gateway));

		final PhysicalOrderShipmentImpl shipment = getMockPhysicalOrderShipment();
		order.addShipment(shipment);
		shipment.setStatus(OrderShipmentStatus.SHIPPED);

		final OrderPayment payment = new OrderPaymentImpl();
		payment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		payment.setPaymentMethod(PaymentType.CREDITCARD);
		payment.setAmount(new BigDecimal(amount));
		payment.setCurrencyCode("CAD");
		order.addOrderPayment(payment);

		BigDecimal refundAmount = new BigDecimal(amount);
		EventOriginator originator = new EventOriginatorImpl();

		stubGetBean(ContextIdNames.ORDER, OrderImpl.class);
		stubGetBean(ContextIdNames.ORDER_EVENT, OrderEventImpl.class);
		context.checking(new Expectations() { {
			allowing(gateway).getPaymentType(); will(returnValue(PaymentType.CREDITCARD));

			allowing(getMockFetchPlanHelper()).addField(with(AbstractOrderShipmentImpl.class), with(any(String.class)));
			allowing(getMockPersistenceEngine()).get(OrderImpl.class, order.getUidPk()); will(returnValue(order));
			allowing(getMockPersistenceEngine()).merge(order); will(returnValue(order));

			oneOf(gateway).refund(payment, null);
		} });
		orderServiceImpl.processRefundOrderPayment(order.getUidPk(), shipment.getShipmentNumber(), payment, refundAmount, originator);
	}

	/**
	 * Get the {@link EventOriginatorHelper event originator helper} for order related test cases.
	 * @return EventOriginatorHelper the event originator helper
	 */
	protected EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	private void assertStoreIsPopulated(final Store expectedStore, final Order order) {
		//  Pointless PMD-enforced craziness
		assertEquals("Store should be populated", expectedStore, order.getStore());
	}
	
	private Map<PaymentType, OrderEventPaymentDetailFormatter> createOrderEventHelperFormatterMap() {
		Map<PaymentType, OrderEventPaymentDetailFormatter> formatterMap = new HashMap<PaymentType, OrderEventPaymentDetailFormatter>();
		formatterMap.put(PaymentType.CREDITCARD, new OrderEventCreditCardDetailsFormatter());
		formatterMap.put(PaymentType.GIFT_CERTIFICATE, new OrderEventGiftCertificateDetailsFormatter());
		formatterMap.put(PaymentType.PAYMENT_TOKEN, new OrderEventPaymentTokenDetailsFormatter());
		return formatterMap;
	}

}
