/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.service.order.impl; // NOPMD All Imports required

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CreditCardEncrypter;
import com.elasticpath.commons.util.security.impl.CreditCardEncrypterImpl;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLocaleDependantFieldsImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerAuthenticationImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.event.impl.EventOriginatorImpl;
import com.elasticpath.domain.event.impl.OrderEventHelperImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderReturnImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuImpl;
import com.elasticpath.domain.order.impl.OrderReturnSkuReasonImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.domain.payment.impl.PaymentHandlerFactoryImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.FixedBaseAndOrderTotalPercentageMethodImpl;
import com.elasticpath.domain.shipping.impl.ShippingCostCalculationParameterImpl;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.persistence.support.impl.OrderCriterionImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.misc.impl.DatabaseServerTimeServiceImpl;
import com.elasticpath.service.misc.impl.OpenJPAFetchPlanHelperImpl;
import com.elasticpath.service.order.IllegalReturnStateException;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.payment.impl.PaymentResultImpl;
import com.elasticpath.service.payment.impl.PaymentServiceImpl;
import com.elasticpath.service.shipping.impl.ShippingServiceLevelServiceImpl;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.impl.OrderSkuFactoryImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxJurisdictionService;
import com.elasticpath.service.tax.impl.DefaultTaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxJurisdictionServiceImpl;
import com.elasticpath.test.factory.TestCustomerSessionFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>OrderReturnImpl</code>.
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.TooManyFields" })
public class OrderReturnAndExchangeServiceTest extends AbstractEPServiceTestCase {

	private static final String ORDER_NUMBER = "10000";

	private static final BigDecimal SUB_TOTAL = new BigDecimal("44.00");

	private static final BigDecimal TOTAL = new BigDecimal("60.34");

	private static final BigDecimal SHIPPING_TAX = new BigDecimal("1.22");

	private static final BigDecimal ITEM_TAX = new BigDecimal("5.72");

	private static final BigDecimal TAX_TOTAL = new BigDecimal("6.94");

	private static final BigDecimal BEFORE_TAX_TOTAL = new BigDecimal("53.40");

	private static final BigDecimal SHIPPING_COST = new BigDecimal("9.40");

	private static final BigDecimal CALCULATE_SHIPPING_COST = new BigDecimal("10.99");

	private static final BigDecimal CALCULATE_SHIPPING_DISCAUNT = new BigDecimal("1.99");

	private static final BigDecimal BEFORE_TAX_SHIPPING_COST = new BigDecimal("9.40");

	private static final String CARD_HOLDER_NAME = "John";

	private static final String SALES_TAX_CODE_GOODS = "GOODS";

	private static final Currency CURRENCY = Currency.getInstance("CAD");

	private static final BigDecimal PRODUCT_PRICE = new BigDecimal("44");

	private static final String PST_TAX_CODE = "PST";

	private static final String GST_TAX_CODE = "GST";

	private static final String REGION_CODE_CA = "CA";

	private static final Locale DEFAULT_LOCALE = Locale.CANADA;

	private static final String REGION_CODE_BC = "BC";

	private static final String FAKE_ORDER_NUMBER = ORDER_NUMBER;

	private static final long FAKE_UIDPK = 10000L;

	private Order orderImpl;

	private ReturnAndExchangeServiceImpl exchangeService;

	private OrderServiceImpl orderServiceImpl;

	private CheckoutService mockCheckoutService;

	private CustomerSessionService mockCustomerSessionService;

	private ShopperService mockShopperService;

	private OrderCriterion orderCriterion;

	private TimeService mockTimeService;

	private ProductSkuService mockProductSkuService;

	private ShippingServiceLevel shippingServiceLevel;

	private ProductInventoryManagementServiceImpl productInventoryManagementService;

	private AllocationServiceImpl allocationServiceImpl;

	private ProductSku productSku;

	private Store store;

	private static final long DAY = 1000 * 60 * 60 * 24;

	private static final String CART_CURRENCY = "CAD";
	private StoreService storeService;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		stubGetBean(ContextIdNames.CUSTOMER_AUTHENTICATION, CustomerAuthenticationImpl.class);
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);


		/** setup order. */
		setupOrder();
		/** setup order service impl. */
		setupOrderService();
		/** setup checkout service: */
		setupCheckoutService();
		/** set up exchange service */
		setupReturnService();
		/** setup for order auditing */
		setUpForOrderAuditing();

		setUpAllocationService(productInventoryManagementService);

		
		stubGetBean(ContextIdNames.ORDER_EVENT_HELPER, new OrderEventHelperImpl() {
			@Override
			public void logOrderExchangeCreated(final Order order) {
				// skip logging of the event
			}
		});

		stubGetBean(ContextIdNames.ORDER_SKU_FACTORY,
				new OrderSkuFactoryImpl() {
					@Override
					protected OrderSku createSimpleOrderSku() {
						return new OrderSkuImpl();
					}
				});
	}

	private void setUpAllocationService(final ProductInventoryManagementService productInventoryManagementService) {
		allocationServiceImpl = new AllocationServiceImpl();

		allocationServiceImpl.setPersistenceEngine(getPersistenceEngine());
		allocationServiceImpl.setProductInventoryManagementService(productInventoryManagementService);

		stubGetBean(ContextIdNames.ALLOCATION_SERVICE, allocationServiceImpl);

		orderServiceImpl.setAllocationService(allocationServiceImpl);
	}

	/**
	 * 
	 */
	protected void setupOrder() {
		orderImpl = new TestOrderImpl();
		orderImpl.initialize();
		final Store store = getMockedStore();
		final Set <TaxCode> taxCodes = new HashSet <TaxCode>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		store.setTaxCodes(taxCodes);
		orderImpl.setStoreCode(store.getCode());


		orderImpl.setUidPk(FAKE_UIDPK);
		orderImpl.setOrderNumber(FAKE_ORDER_NUMBER);
		orderImpl.setCurrency(CURRENCY);
		orderImpl.setLocale(DEFAULT_LOCALE);
		orderImpl.setCustomer(getCustomer());
		orderImpl.setBillingAddress(mockOrderAddress());
		final OrderShipment shipment = mockOrderShipment();
		orderImpl.addShipment(shipment);
		orderImpl.setOrderPayments(mockOrderPayments(shipment));
		orderImpl.setModifiedBy(new EventOriginatorImpl());
	}

	private void setupCheckoutService() {
		mockCheckoutService = context.mock(CheckoutService.class);
	}

	private void setUpForOrderAuditing() {
		final OrderEventHelperImpl orderEventHelper = new OrderEventHelperImpl();
		orderEventHelper.setTimeService(new DummyTimeService());

		stubGetBean(ContextIdNames.ORDER_EVENT, OrderEventImpl.class);
		stubGetBean(ContextIdNames.ORDER_EVENT_HELPER, orderEventHelper);
		stubGetBean(ContextIdNames.EVENT_ORIGINATOR, EventOriginatorImpl.class);
		stubGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, EventOriginatorHelperImpl.class);
	}

	private void setupOrderService() {
		orderServiceImpl = new OrderServiceImpl() {

			@Override
			public Order get(final long orderUid) throws EpServiceException {
				final Order order = new OrderImpl();
				order.setOrderPayments(mockOrderPayments(null));
				return order;
			}

			@Override
			public Order add(final Order order) throws EpServiceException {
				final Order savedOrder = super.add(order);
				savedOrder.setUidPk(1);
				savedOrder.setStoreCode(getMockedStore().getCode());
				return savedOrder;
			}

			@Override
			public Order update(final Order order) throws EpServiceException {
				order.setStoreCode(getMockedStore().getCode());
				return order;
			}

			@Override
			public Order processRefundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment refundPayment,
					final BigDecimal refundAmount, final EventOriginator eventOriginator) {
				final Order returnOrder = new OrderImpl();
				refundPayment.setAmount(refundAmount);
				refundPayment.setStatus(OrderPaymentStatus.APPROVED);
				final Set<OrderPayment> paymentList = new HashSet<OrderPayment>();
				paymentList.add(refundPayment);
				returnOrder.setOrderPayments(paymentList);
				return returnOrder;
			}
		};
		orderServiceImpl.setPersistenceEngine(getPersistenceEngine());
		orderServiceImpl.setFetchPlanHelper(getFetchPlanHelper());

		orderCriterion = new OrderCriterionImpl();
		stubGetBean(ContextIdNames.ORDER_CRITERION, orderCriterion);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		orderServiceImpl.setTimeService(mockTimeService);

		stubGetBean(ContextIdNames.ORDER_SERVICE, orderServiceImpl);

	}

	private void setupReturnService() {
		exchangeService = new ReturnAndExchangeServiceImpl() {
			@Override
			protected ReturnAndExchangeService getReturnAndExchangeService() {
				return exchangeService;
			}

			@Override
			protected OrderEventHelper getOrderEventHelper() {
				final OrderEventHelperImpl orderEventHelper = new OrderEventHelperImpl();
				orderEventHelper.setTimeService(new DummyTimeService());
				return orderEventHelper;
			}
		};
		exchangeService.setPersistenceEngine(getPersistenceEngine());

		mockCustomerSessionService = context.mock(CustomerSessionService.class);
		exchangeService.setCustomerSessionService(mockCustomerSessionService);

		mockShopperService = context.mock(ShopperService.class);
		exchangeService.setShopperService(mockShopperService);

		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		exchangeService.setTimeService(mockTimeService);

		final TaxJurisdictionService taxJurisdictionService = new TaxJurisdictionServiceImpl() {
			@Override
			public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
				return getTaxJurisdiction();
			}
		};

		mockProductSkuService = context.mock(ProductSkuService.class);
		exchangeService.setProductSkuService(mockProductSkuService);

		final long uidPk = 1L;
		shippingServiceLevel = getShippingServiceLevel(uidPk);

		// final Stub[] stubs = new Stub[1];
		final ShippingServiceLevelServiceImpl serviceLevelServiceImpl = new ShippingServiceLevelServiceImpl() {
			@Override
			public List<ShippingServiceLevel> retrieveShippingServiceLevel(final ShoppingCart shoppingCart) {
				final List<ShippingServiceLevel> lst = new ArrayList<ShippingServiceLevel>();
				lst.add(shippingServiceLevel);
				return lst;
			}
		};

		serviceLevelServiceImpl.setPersistenceEngine(getPersistenceEngine());

		exchangeService.setShippingServiceLevelService(serviceLevelServiceImpl);

		stubGetBean(ContextIdNames.CUSTOMER_SESSION, new CustomerSessionImpl());

		storeService = context.mock(StoreService.class);
		context.checking(new Expectations() { {
			allowing(storeService).findStoreWithCode(getMockedStore().getCode()); will(returnValue(getMockedStore()));
		} });

		final DefaultTaxCalculationServiceImpl calculationService = new DefaultTaxCalculationServiceImpl();
		calculationService.setTaxJurisdictionService(taxJurisdictionService);
		calculationService.setStoreService(storeService);

		stubGetBean(ContextIdNames.TAX_CALCULATION_SERVICE, calculationService);

		final OrderReturnSkuReason orderReturnSkuReason = new OrderReturnSkuReasonImpl();
		orderReturnSkuReason.setPropertiesMap(new HashMap<String, Map<Object, Object>>());
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU_REASON, orderReturnSkuReason);

		exchangeService.setCheckoutService(mockCheckoutService);
		exchangeService.setOrderService(orderServiceImpl);
		exchangeService.setPaymentService(getPaymentService());
		exchangeService.setStoreService(storeService);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.add()'.
	 */
	@Test
	public void testAdd() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(orderReturn)));
			}
		});
		exchangeService.add(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.add() with dummy payment'.
	 */
	@Test
	public void testAdd2() {
		stubGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);
		
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		final OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setUidPk(1);
		orderReturn.setReturnPayment(orderPayment);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(with(same(OrderPaymentImpl.class)), with(orderPayment.getUidPk()));
				will(returnValue(orderPayment));

				oneOf(getMockPersistenceEngine()).save(with(same(orderReturn)));
			}
		});

		exchangeService.add(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.findAllUids()'.
	 */
	@Test
	public void testFind() {
		final List<Long> listOfUids = new ArrayList<Long>();
		listOfUids.add(FAKE_UIDPK);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("ORDER_RETURN_UIDS_LIST"), with(any(Object[].class)));
				will(returnValue(listOfUids));
			}
		});

		assertEquals(listOfUids, exchangeService.findAllUids());
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.update()'.
	 */
	@Test
	public void testUpdate() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setOrder(new OrderImpl());
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).merge(with(same(orderReturn)));
				will(returnValue(null));
			}
		});
		exchangeService.update(orderReturn);
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list(final long uidPk)'.
	 */
	@Test
	public void testList() {
		final long uid = 1L;
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<OrderReturn>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("ORDER_RETURN_LIST", uid);
				will(returnValue(orderReturns));
			}
		});
		assertSame(orderReturns, exchangeService.list(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list(final long uidPk, final OrderReturnType returnType)'.
	 */
	@Test
	public void testList2() {
		final long uid = 1L;
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<OrderReturn>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						"ORDER_RETURN_LIST_BY_RETURN_TYPE", uid, OrderReturnType.RETURN);
				will(returnValue(orderReturns));
			}
		});
		assertSame(orderReturns, exchangeService.list(uid, OrderReturnType.RETURN));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.list()'.
	 */
	@Test
	public void testList3() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		final List<OrderReturn> orderReturns = new ArrayList<OrderReturn>();
		orderReturns.add(orderReturn);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(
						with("ORDER_EXCHANGE_AND_RETURN_LIST_BY_ORDER_UID"), with(any(Object[].class)));
				will(returnValue(orderReturns));
			}
		});

		assertSame(orderReturns, exchangeService.list());
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.get(uid)'.
	 */
	@Test
	public void testGet() {
		final OrderReturn orderReturn = new OrderReturnImpl();
		stubGetBean(ContextIdNames.ORDER_RETURN, orderReturn);
		
		final FetchGroupLoadTuner mockFetchGroupLoadTuner = context.mock(FetchGroupLoadTuner.class);
		stubGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, mockFetchGroupLoadTuner);
		assertSame(orderReturn, exchangeService.get(0));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.get(uid)'.
	 */
	@Test
	public void testGet2() {
		stubGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
		stubGetBean(ContextIdNames.ORDER_RETURN, OrderReturnImpl.class);

		final long uidPk = 1;
		final OrderReturn orderReturn = new OrderReturnImpl();
		orderReturn.setUidPk(uidPk);

		exchangeService.setFetchPlanHelper(new OpenJPAFetchPlanHelperImpl() {
			@Override
			public void clearFetchPlan() {
				// empty
			}

			@Override
			public void configureFetchGroupLoadTuner(final FetchGroupLoadTuner loadTuner) {
				// empty
			}

		});
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(with(same(OrderReturnImpl.class)), with(uidPk));
				will(returnValue(orderReturn));
			}
		});

		assertSame(orderReturn, exchangeService.get(uidPk));
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.populateShoppingCart()'.
	 */
	@Test
	public void testPopulateShoppingCart() {
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		stubGetBean(ContextIdNames.ORDER_RETURN_SKU, OrderReturnSkuImpl.class);
		stubGetBean(ContextIdNames.SHOPPING_CART, ShoppingCartImpl.class);
		stubGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSessionWithContext(shopper);
		context.checking(new Expectations() {
			{
				allowing(mockCustomerSessionService).createWithShopper(with(any(Shopper.class)));
				will(returnValue(customerSession));

				allowing(mockShopperService).findOrCreateShopper(with(any(Customer.class)), with(any(String.class)));
				will(returnValue(shopper));
			}
		});

		final OrderReturn exchangeOrder = new OrderReturnImpl();

		exchangeOrder.populateOrderReturn(orderImpl, orderImpl.getAllShipments().get(0), OrderReturnType.RETURN);

		final List<ShoppingItem> cartItemList = mockCartItemList();
		context.checking(new Expectations() {
			{
				allowing(mockProductSkuService).getWithProduct(with(any(long.class)));
				will(returnValue(cartItemList.get(0).getProductSku()));
			}
		});

		ShoppingCart shoppingCart = exchangeService.populateShoppingCart(exchangeOrder, cartItemList, shippingServiceLevel, mockShippingAddress());

		final TaxCalculationResult taxCalculationResult = shoppingCart.getTaxCalculationResult();

		assertEquals(SUB_TOTAL, shoppingCart.getSubtotal());
		assertEquals(BEFORE_TAX_SHIPPING_COST, shoppingCart.getBeforeTaxShippingCost().getAmount());
		assertEquals(SHIPPING_COST, shoppingCart.getShippingCost().getAmount());
		assertEquals(BEFORE_TAX_TOTAL, shoppingCart.getBeforeTaxTotal().getAmount());
		assertEquals(TAX_TOTAL, taxCalculationResult.getTotalTaxes().getAmount());
		assertEquals(SHIPPING_TAX, taxCalculationResult.getShippingTax().getAmount());
		assertEquals(TOTAL, shoppingCart.getTotal());

		shoppingCart = exchangeService.populateShoppingCart(exchangeOrder, cartItemList, shippingServiceLevel, CALCULATE_SHIPPING_COST,
				CALCULATE_SHIPPING_DISCAUNT, mockShippingAddress());

		assertEquals(CALCULATE_SHIPPING_COST, shoppingCart.getShippingCost().getAmount());
		assertEquals(CALCULATE_SHIPPING_DISCAUNT, shoppingCart.getSubtotalDiscountMoney().getAmount());
	}

	/**
	 * Test that you can't edit an OrderReturn with a Completed status -
	 * an {@link IllegalReturnStateException} should be thrown.
	 */
	@Test
	public void testEditReturnCompletedStateThrowsException() {
		final OrderReturn mockOrderReturn = context.mock(OrderReturn.class);
		context.checking(new Expectations() {
			{
				allowing(mockOrderReturn).getReturnStatus();
				will(returnValue(OrderReturnStatus.COMPLETED));

				allowing(mockOrderReturn).isInTerminalState();
				will(returnValue(true));
			}
		});
		try {
			exchangeService.editReturn(mockOrderReturn);
			fail("Should never rich here.");
		} catch (final IllegalReturnStateException e) { //NOPMD
			//empty
		}
	}

	/**
	 * Test that when an OrderReturn is received, the order event helper logs the receipt and
	 * then the OrderReturn is saved by retrieving the ReturnAndExchange service again from Spring
	 * (for transactions).
	 */
	@Test
	public void testReceiveReturn() {
		final OrderReturn mockOrderReturn = context.mock(OrderReturn.class);
		final OrderReturn orderReturn = mockOrderReturn;
		final OrderEventHelper mockOrderEventHelper = context.mock(OrderEventHelper.class);
		final ReturnAndExchangeService mockReturnAndExchangeService = context.mock(ReturnAndExchangeService.class);
		context.checking(new Expectations() {
			{
				oneOf(mockOrderReturn).getOrder();

				oneOf(mockOrderEventHelper).logOrderReturnReceived(with(any(Order.class)), with(any(OrderReturn.class)));

				oneOf(mockReturnAndExchangeService).update(with(same(orderReturn)));
			}
		});

		final ReturnAndExchangeServiceImpl service = new ReturnAndExchangeServiceImpl() {
			@Override
			protected OrderEventHelper getOrderEventHelper() {
				return mockOrderEventHelper;
			}
			@Override
			protected ReturnAndExchangeService getReturnAndExchangeService() {
				return mockReturnAndExchangeService;
			}

		};
		service.receiveReturn(orderReturn);
	}

	/**
	 * Create the payment service.
	 * 
	 * @return the payment service.
	 */
	private PaymentService getPaymentService() {
		final PaymentHandlerFactory paymentHandlerFactory = new PaymentHandlerFactoryImpl();
		stubGetBean(ContextIdNames.PAYMENT_HANDLER_FACTORY, paymentHandlerFactory);
		stubGetBean(ContextIdNames.PAYMENT_RESULT, PaymentResultImpl.class);
		final PaymentServiceImpl paymentService = new PaymentServiceImpl();
		paymentService.setPaymentHandlerFactory(paymentHandlerFactory);

		final CreditCardEncrypter encrypter = context.mock(CreditCardEncrypter.class);
		context.checking(new Expectations() {
			{
				allowing(encrypter).encrypt(with(any(String.class)));
				will(returnValue("11111"));

				allowing(encrypter).decrypt(with(any(String.class)));
				will(returnValue("11111"));
			}
		});

		stubGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, CreditCardEncrypterImpl.class);
		stubGetBean(ContextIdNames.CREDIT_CARD_ENCRYPTER, encrypter);

		final CreditCardPaymentGateway creditCardPaymentGateway = context.mock(CreditCardPaymentGateway.class);
		final PaymentGateway exchangePaymentGateway = context.mock(PaymentGateway.class);
		context.checking(new Expectations() {
			{
				allowing(creditCardPaymentGateway).getPaymentType();
				will(returnValue(PaymentType.CREDITCARD));

				allowing(creditCardPaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(creditCardPaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(creditCardPaymentGateway).setValidateCvv2(with(any(boolean.class)));

				allowing(exchangePaymentGateway).getPaymentType();
				will(returnValue(PaymentType.RETURN_AND_EXCHANGE));

				allowing(exchangePaymentGateway).preAuthorize(with(any(OrderPayment.class)), with(any(Address.class)));
				allowing(exchangePaymentGateway).reversePreAuthorization(with(any(OrderPayment.class)));
				allowing(exchangePaymentGateway).capture(with(any(OrderPayment.class)));
				allowing(exchangePaymentGateway).sale(with(any(OrderPayment.class)), with(any(Address.class)));
			}
		});

		final Set<PaymentGateway> paymentGateways = new HashSet<PaymentGateway>();
		paymentGateways.add(creditCardPaymentGateway);
		paymentGateways.add(exchangePaymentGateway);

		getMockedStore().setPaymentGateways(paymentGateways);

		final StoreImpl store = new StoreImpl();
		store.setPaymentGateways(paymentGateways);

		return paymentService;
	}

	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.manualRefundOrderReturn()'.
	 */
	@Test
	public void testRefundOrderReturnException() {
		try {
			exchangeService.createReturn(null, ReturnExchangeType.NEW_PAYMENT);
			fail("EpServiceException must be thrown, because of unexpected type");
		} catch (final EpServiceException ex) {
			// success
			assertNotNull(ex);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ReturnAndExchangeServiceImpl.completeExchangeOrder()'.
	 */
	@Test
	public void testCompleteExchangeOrderWithException() {
		// OrderReturn returnOrder = createOrderReturnForCompletion();

		try {
			exchangeService.completeExchange(null, ReturnExchangeType.PHYSICAL_RETURN_REQUIRED, null);
			fail("EpServiceException must be thrown");
		} catch (final EpServiceException ex) {
			// Success
			assertNotNull(ex);
		}
	}

	private Set<OrderSku> mockOrderSkuList() {
		final Set<OrderSku> orderSkus = new HashSet<OrderSku>();
		// Create mock cart items
		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		final ProductSku productSkuImpl = mockProductSku();
		orderSku.setProductSku(productSkuImpl);
		orderSku.setPrice(1, null);
		//Tax amount needs to be set, normally set by TaxCalculationService
		orderSku.setTax(ITEM_TAX);
		orderSkus.add(orderSku);
		return orderSkus;
	}

	private List<ShoppingItem> mockCartItemList() {
		final List<ShoppingItem> orderSkus = new ArrayList<ShoppingItem>();
		// Create mock cart items
		final ShoppingItem cartItem = new ShoppingItemImpl();
		cartItem.setUidPk(Calendar.getInstance().getTimeInMillis());
		final ProductSku productSkuImpl = mockProductSku();
		cartItem.setProductSku(productSkuImpl);

		final Price price = new PriceImpl();
		price.setListPrice(MoneyFactory.createMoney(PRODUCT_PRICE, CURRENCY));

		// any product sku price for the current cart item can be set up here.
		cartItem.setPrice(1, getPrice(CURRENCY, PRODUCT_PRICE, PRODUCT_PRICE));

		orderSkus.add(cartItem);
		return orderSkus;
	}

	private OrderAddress mockOrderAddress() {
		final OrderAddress address = new OrderAddressImpl();

		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry(REGION_CODE_CA);
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry(REGION_CODE_BC);
		address.setZipOrPostalCode("94043");
		return address;
	}

	private Address mockShippingAddress() {
		final Address address = new CustomerAddressImpl();

		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry(REGION_CODE_CA);
		address.setStreet1("1295 Charleston Road");
		address.setCity("Mountain View");
		address.setSubCountry(REGION_CODE_BC);
		address.setZipOrPostalCode("94043");
		return address;
	}

	private ProductSku mockProductSku() {
		productSku = new ProductSkuImpl();
		productSku.setUidPk(Calendar.getInstance().getTimeInMillis());
		productSku.setGuid("Irrelevant sku code");

		productSku.setWeight(BigDecimal.ONE);

		final Product productImpl = new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Test Display Name";
			}
		};
		productImpl.setGuid("Irrelevant product guid");
		productImpl.setProductSkus(new HashMap<String, ProductSku>());
		final ProductTypeImpl productTypeImpl = new ProductTypeImpl();
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(SALES_TAX_CODE_GOODS);
		productTypeImpl.setTaxCode(taxCode);
		productImpl.setProductType(productTypeImpl);
		productImpl.setTaxCode(taxCode);
		productSku.setProduct(productImpl);
		productImpl.addOrUpdateSku(productSku);

		final LocaleDependantFields ldf = new ProductLocaleDependantFieldsImpl();
		ldf.setDisplayName("TestDisplayName");
		ldf.setDescription("TestDescription");
		ldf.setLocale(DEFAULT_LOCALE);
		productImpl.addOrUpdateLocaleDependantFields(ldf);

		final InventoryDtoImpl inventoryDto = new InventoryDtoImpl();
		inventoryDto.setWarehouseUid(getMockedStore().getWarehouse().getUidPk());
		inventoryDto.setQuantityOnHand(1);
		inventoryDto.setSkuCode(productSku.getSkuCode());

		return productSku;
	}

	private Set<OrderPayment> mockOrderPayments(final OrderShipment shipment) {
		final OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setTransactionType(OrderPayment.CAPTURE_TRANSACTION);
		orderPayment.setCardHolderName(CARD_HOLDER_NAME);
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setOrderShipment(shipment);
		final Set<OrderPayment> paymentSet = new HashSet<OrderPayment>();
		paymentSet.add(orderPayment);
		return paymentSet;
	}

	private OrderShipment mockOrderShipment() {
		final PhysicalOrderShipment orderShipment = getMockPhysicalOrderShipment();
		orderShipment.setUidPk(BigDecimal.ONE.longValue());
				
		/* 
		* The status set to INVENTORY_ASSIGNED so that tax can be calculated and the transient variables can be
		* initialized in orderShipment.Currently tax is not getting recalculated for PhysicalOrderShipment with
		* status CANCELLED,SHIPPED or RELEASED.
		 */
		orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
		orderShipment.setSubtotalDiscount(BigDecimal.ZERO.setScale(2));
		orderShipment.setShippingCost(BigDecimal.ZERO.setScale(2));

		orderShipment.setShipmentAddress(mockOrderAddress());

		final Set<OrderSku> orderSkuSet = mockOrderSkuList();
		for (final OrderSku sku : orderSkuSet) {
			orderShipment.addShipmentOrderSku(sku);
		}

		return orderShipment;
	}

	private PhysicalOrderShipmentImpl getMockPhysicalOrderShipment() {

		final TaxCalculationResult result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 3596418723515022523L;

			@Override
			public Money getBeforeTaxShippingCost() {
				return MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance(Locale.US));
			}

			@Override
			public Money getBeforeTaxSubTotal() {
				return MoneyFactory.createMoney(BigDecimal.TEN, Currency.getInstance(Locale.US));
			}

			@Override
			public void applyTaxes(final Collection< ? extends ShoppingItem> shoppingItems) { //NOPMD
			}

		};
		result.setDefaultCurrency(Currency.getInstance(Locale.US));

		final PhysicalOrderShipmentImpl physicalOrderShipmentImpl = new PhysicalOrderShipmentImpl() {
			private static final long serialVersionUID = -8478099243134627014L;

			@Override
			protected TaxCalculationResult calculateTaxes() {
				return result;
			}
		};

		return physicalOrderShipmentImpl;

	}

	/**
	 * Creates tax jurisdiction jurisdiction. That jurisdiction is:<br>
	 * Region code: CA<br>
	 * Country category: GST<br>
	 * Tax Region: CA<br>
	 * Tax Values: SHIPPING==6%, GOODS==6%<br>
	 * Subcountry category: PST<br>
	 * Tax Region: BC<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 * Subcountry category: ANOTHER_CATEGORY<br>
	 * Tax Region: VANCOUVER<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 * The last category musn't be taken into account while calculating taxes. This category doesn't match the shipping address.
	 * 
	 * @return
	 */
	private TaxJurisdiction getTaxJurisdiction() {
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(GST_TAX_CODE);

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(new BigDecimal("6"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(new BigDecimal("6"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(PST_TAX_CODE);

		taxValueMap = new HashMap<String, TaxValue>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(new BigDecimal("7"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(new BigDecimal("7"));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		return taxJurisdiction;
	}


	/**
	 * Dummy implementation of the TimeService.
	 */
	public static final class DummyTimeService extends DatabaseServerTimeServiceImpl {
		/**
		 * Gets the current time.
		 * @return current date
		 */
		@Override
		public Date getCurrentTime() {
			return new Date();
		}
	}

	/**
	 * Implementation of Order with auto-recalculation enabled by default for testing purposes.
	 */
	class TestOrderImpl extends OrderImpl {
		private static final long serialVersionUID = -4395039923898026316L;

		/**
		 * Override default constructor to enable auto-recalculation.
		 */
		public TestOrderImpl() {
			super();
			enableRecalculation();
			setTaxCalculationService(new DefaultTaxCalculationServiceImpl() {
				private final BigDecimal taxValue = new BigDecimal("0.10");

				private final Currency defaultCurrency = Currency.getInstance(Locale.US);

				private static final int CALCULATION_FINAL_SCALE = 2;

				@Override
				public TaxCalculationResult calculateTaxes(final String storeCode, final Address address, final Currency currency,
						final Money shippingCostDefault, final Collection< ? extends ShoppingItem> shoppingItems, final Money preTaxDiscount) {
					final TaxCalculationResult taxResult = new TaxCalculationResultImpl();
					taxResult.setDefaultCurrency(Currency.getInstance(Locale.US));

					for (final ShoppingItem shoppingItem : shoppingItems) {
						final Money curTax = MoneyFactory.createMoney(
								shoppingItem.getTotal().getAmount().multiply(taxValue).setScale(CALCULATION_FINAL_SCALE,
								BigDecimal.ROUND_HALF_UP), defaultCurrency);
						taxResult.addItemTax(shoppingItem.getGuid(), curTax);
					}

					Money shippingCost = shippingCostDefault;
					if (shippingCost.getAmount() == null) {
						shippingCost = MoneyFactory.createMoney(BigDecimal.ZERO.setScale(CALCULATION_FINAL_SCALE), defaultCurrency);
					}
					final Money shippingTax = MoneyFactory.createMoney(
							shippingCost.getAmount().multiply(taxValue).setScale(CALCULATION_FINAL_SCALE, BigDecimal.ROUND_HALF_UP),
							defaultCurrency);
					taxResult.addShippingTax(shippingTax);
					taxResult.setBeforeTaxShippingCost(shippingCost);

					return taxResult;
				}
			});
		}
	}

	/**
	 * Tests the find last refund payment gets really the last by date successful refund payment.
	 */
	@Test
	public void testFindLastRefundPayment() {

		final ReturnAndExchangeServiceImpl serviceImpl = new ReturnAndExchangeServiceImpl();

		final Set<OrderPayment> orderPayments = new HashSet<OrderPayment>();

		final OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setCreatedDate(new Date(DAY));
		orderPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment);

		final OrderPayment orderPayment2 = new OrderPaymentImpl();
		orderPayment2.setCreatedDate(new Date(DAY * 2));
		orderPayment2.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment2.setStatus(OrderPaymentStatus.FAILED);

		orderPayments.add(orderPayment2);

		final OrderPayment orderPayment3 = new OrderPaymentImpl();
		orderPayment3.setCreatedDate(new Date(DAY * 2));
		orderPayment3.setTransactionType(OrderPayment.AUTHORIZATION_TRANSACTION);
		orderPayment3.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment3);

		final OrderPayment orderPayment4 = new OrderPaymentImpl();
		orderPayment4.setCreatedDate(new Date(DAY * 2));
		orderPayment4.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
		orderPayment4.setStatus(OrderPaymentStatus.APPROVED);

		orderPayments.add(orderPayment4);


		assertEquals(orderPayment4, serviceImpl.findLastRefundPayment(orderPayments));
	}
	
	/**
	 * Returns the default mocked store.
	 * 
	 * @return the default mocked store.
	 */
	protected Store getMockedStore() {
		if (store == null) {
			store = new StoreImpl();
			store.setCode("Irrelevant store scode");
			store.setDefaultLocale(Locale.US);
			store.setDefaultCurrency(Currency.getInstance(Locale.US));
			store.setContentEncoding("UTF-8");

			List<Warehouse> warehouseList = new ArrayList<Warehouse>();
			Warehouse warehouse = new WarehouseImpl();
			warehouse.setUidPk(1);
			warehouseList.add(warehouse);
			store.setWarehouses(warehouseList);
		}
		return store;
	}

	/**
	 * Returns a new <code>Customer</code> instance.
	 * 
	 * @return a new <code>Customer</code> instance.
	 */
	protected Customer getCustomer() {
		final Customer customer = new CustomerImpl();
		customer.setGuid((new RandomGuidImpl()).toString());
		customer.initialize();
		return customer;
	}

	/**
	 * Returns a newly created shippingServiceLevel of type
	 * fixedBaseAndOrderTotalPercentageMethod, with base value 5.0 and 10% of the order total and
	 * supports shippingRegion CA(AB, BC) and US.
	 * 
	 * @param uidPk - the uidPk for the newly created shippingServiceLevel.
	 * @return a newly created shippingServiceLevel
	 */
	protected ShippingServiceLevel getShippingServiceLevel(final long uidPk) {
		ShippingServiceLevel shippingServiceLevel = new ShippingServiceLevelImpl();
		shippingServiceLevel.setUidPk(uidPk);
		shippingServiceLevel.setCarrier("Fed Ex");

		ShippingCostCalculationMethod fixedBaseOrderTotPertMeth = new FixedBaseAndOrderTotalPercentageMethodImpl();
		ShippingCostCalculationParameter param1 = new ShippingCostCalculationParameterImpl();
		Set<ShippingCostCalculationParameter> paramSet = new HashSet<ShippingCostCalculationParameter>();
		param1.setKey(ShippingCostCalculationParametersEnum.FIXED_BASE.getKey());
		param1.setValue("5.0");
		param1.setCurrency(Currency.getInstance(CART_CURRENCY));
		paramSet.add(param1);
		ShippingCostCalculationParameter param2 = new ShippingCostCalculationParameterImpl();
		param2.setKey(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey());
		param2.setValue("10");
		param2.setCurrency(Currency.getInstance(CART_CURRENCY));
		paramSet.add(param2);
		fixedBaseOrderTotPertMeth.setParameters(paramSet);

		shippingServiceLevel.setShippingCostCalculationMethod(fixedBaseOrderTotPertMeth);
		shippingServiceLevel.setShippingRegion(getShippingRegion("[CA(AB,BC)][US()]"));

		return shippingServiceLevel;
	}
	
	/**
	 * Returns a newly created shippingRegion.
	 * 
	 * @param regionStr - the region str, i.e. "[CA(AB,BC)][US()]".
	 * @return a newly created shippingRegion
	 */
	private ShippingRegion getShippingRegion(final String regionStr) {
		ShippingRegionImpl shippingRegionImpl = new ShippingRegionImpl();
		shippingRegionImpl.setRegionStr(regionStr);
		return shippingRegionImpl;
	}

	/**
	 * Returns a new <code>Price</code> instance with the currency, listPrice, and salePrice
	 * set.
	 * 
	 * @param currency Currency
	 * @param listPrice the listPrice for this <code>Price</code>
	 * @param salePrice the salePrice for this <code>Price</code>
	 * @return a new <code>Price</code> instance.
	 */
	protected Price getPrice(final Currency currency, final BigDecimal listPrice, final BigDecimal salePrice) {
		Price price = getPrice();
		price.setCurrency(currency);

		Money listPriceMoney = MoneyFactory.createMoney(listPrice, currency);
		price.setListPrice(listPriceMoney);

		Money salePriceMoney = MoneyFactory.createMoney(salePrice, currency);
		price.setSalePrice(salePriceMoney);

		return price;
	}
	
	/**
	 * @return a new <code>Price</code> instance.
	 */
	private Price getPrice() {
		PriceImpl price = new PriceImpl();
		price.addOrUpdatePriceTier(getPriceTier());
		price.setCurrency(Currency.getInstance(CART_CURRENCY));
		price.initialize();
		return price;
	}

	/**
	 * @return a new <code>PriceTier</code> instance.
	 */
	private PriceTier getPriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return priceTier;
	}
}
