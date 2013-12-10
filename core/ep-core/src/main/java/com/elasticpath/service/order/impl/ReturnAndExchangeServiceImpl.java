/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.OrderCriterion.ResultType;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.IllegalReturnStateException;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.store.StoreService;

/**
 * Provides storage and access to <code>OrderReturn</code> objects.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public class ReturnAndExchangeServiceImpl extends AbstractEpPersistenceServiceImpl implements ReturnAndExchangeService {

	private static final BigDecimal CALCULATE_SHIPPING_COST = new BigDecimal(Integer.MIN_VALUE);

	private static final BigDecimal CALCULATE_SHIPPING_DISCOUNT = new BigDecimal(Integer.MIN_VALUE);

	private CheckoutService checkoutService;

	private TimeService timeService;

	private ProductSkuService productSkuService;

	private FetchPlanHelper fetchPlanHelper;

	private OrderService orderService;

	private PaymentService paymentService;

	private ShopperService shopperService;

	private ShippingServiceLevelService shippingServiceLevelService;

	private CustomerSessionService customerSessionService;
	private StoreService storeService;


	@Override
	public List<OrderReturn> list(final long uidPk) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_LIST", uidPk);
	}

	@Override
	public List<OrderReturn> list(final long uidPk, final OrderReturnType returnType) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_LIST_BY_RETURN_TYPE", uidPk, returnType);
	}

	@Override
	public OrderReturn get(final long orderReturnUid) throws EpServiceException {
		return get(orderReturnUid, null);
	}

	@Override
	public OrderReturn get(final long orderReturnUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		FetchGroupLoadTuner tuner = loadTuner;
		if (loadTuner == null) {
			tuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		}
		OrderReturn orderReturn = null;
		if (orderReturnUid <= 0) {
			orderReturn = getBean(ContextIdNames.ORDER_RETURN);
		} else {
			tuner.addFetchGroup(FetchGroupConstants.ORDER_NOTES); //added to ensure that the Order's orderEvents field is loaded
			fetchPlanHelper.configureFetchGroupLoadTuner(tuner);
			orderReturn = getPersistentBeanFinder().get(ContextIdNames.ORDER_RETURN, orderReturnUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return orderReturn;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public List<OrderReturn> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_EXCHANGE_AND_RETURN_LIST_BY_ORDER_UID");
	}

	@Override
	public List<Long> findAllUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_UIDS_LIST");
	}

	@Override
	public List<OrderReturn> findByUids(final Collection<Long> orderUids) {
		sanityCheck();

		if ((orderUids == null) || orderUids.isEmpty()) {
			return new ArrayList<OrderReturn>();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("ORDER_RETURNS_BY_UIDS", "list", orderUids);
	}

	/**
	 * Retrieves list of <code>OrderReturn</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>OrderReturn</code> whose last modified date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	@Override
	public OrderReturn update(final OrderReturn orderReturn) throws EpServiceException {
		sanityCheck();
		OrderReturn updatedOrderReturn = null;
		try {
			orderReturn.setLastModifiedDate(timeService.getCurrentTime());
			updatedOrderReturn = getPersistenceEngine().merge(orderReturn);
		} catch (OptimisticLockException e) {
			throw new OrderReturnOutOfDateException("Return cannot be updated as it has been updated by another user", e);
		}
		return updatedOrderReturn;
	}

	@Override
	public OrderReturn add(final OrderReturn orderReturn) throws EpServiceException {
		sanityCheck();
		orderReturn.setLastModifiedDate(timeService.getCurrentTime());
		if ((orderReturn.getReturnPayment() != null) && orderReturn.getReturnPayment().isPersisted()) {
			OrderPayment returnPayment = getPersistentBeanFinder().get(ContextIdNames.ORDER_PAYMENT, orderReturn.getReturnPayment().getUidPk());
			orderReturn.setReturnPayment(returnPayment);
		}

		getPersistenceEngine().save(orderReturn);

		return orderReturn;
	}

	@Override
	public OrderReturn getExchange(final long uidPk) {
		sanityCheck();
		List<OrderReturn> exchanges = getPersistenceEngine().retrieveByNamedQuery("EXCHANGE_BY_EXCHANGE_ORDER_UID", uidPk);
		if (!exchanges.isEmpty()) {
			return exchanges.get(0);
		}
		return null;
	}

	/**
	 * Partially or fully Refund the order based on the order return payment.
	 *
	 * @param orderReturn order return to be refunded.
	 * @return shrunk down version of <code>OrderReturn</code>. The return will contain return payment and optionally exchange order.
	 */
	private OrderReturn refundOrderReturn(final OrderReturn orderReturn) {
		final Order exchangeOrder = orderReturn.getExchangeOrder();
		OrderReturn updatedOrderReturn = orderReturn;

		final BigDecimal refundTotal;
		if (exchangeOrder == null) {
			refundTotal = orderReturn.getReturnTotal();
		} else {
			refundTotal = orderReturn.getReturnTotal().subtract(exchangeOrder.getTotal());
		}

		if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
			final Order order = orderService.refundOrderPayment(orderReturn.getOrder().getUidPk(), refundTotal, orderReturn.getOrder()
					.getModifiedBy());

			updatedOrderReturn = findUpdatedOrderReturn(orderReturn, order);

			updatedOrderReturn.setReturnPayment(findLastRefundPayment(order.getOrderPayments()));
		}

		return updatedOrderReturn;
	}

	/**
	 */
	private OrderReturn findUpdatedOrderReturn(final OrderReturn orderReturn, final Order order) {
		if (order.getReturns().contains(orderReturn)) {
			for (OrderReturn item : order.getReturns()) {
				if (orderReturn.equals(item)) {
					return item;
				}
			}
		}
		return orderReturn;
	}

	/**
	 * Finds the last successful refund in the payments set.
	 *
	 * @param orderPayments the order payments
	 * @return the last refund payment in the set
	 */
	OrderPayment findLastRefundPayment(final Set<OrderPayment> orderPayments) {
		OrderPayment lastRefundPayment = null;
		for (OrderPayment orderPayment : orderPayments) {
			if ((orderPayment.getTransactionType() == OrderPayment.CREDIT_TRANSACTION)
					&& (orderPayment.getStatus() == OrderPaymentStatus.APPROVED)) {
				if (lastRefundPayment == null) {
					lastRefundPayment = orderPayment;
				} else if (lastRefundPayment.getCreatedDate()
						.compareTo(orderPayment.getCreatedDate()) < 0) {
					lastRefundPayment = orderPayment;
				}
			}
		}
		return lastRefundPayment;
	}

	/**
	 * Creates new payment in order to keep amount that was refunded manually, e.g. by cash. The payment is required in order to properly calculate
	 * return's refunded money. Exchange order shouldn't and can't know if refund was processed manually or actually.
	 *
	 * @param orderReturn to be manually refunded.
	 * @return manual payment.
	 */
	private OrderReturn manualRefundOrderReturn(final OrderReturn orderReturn) {
		Order exchangeOrder = orderReturn.getExchangeOrder();

		final BigDecimal refundTotal;
		if (exchangeOrder == null) {
			refundTotal = orderReturn.getReturnTotal();
		} else {
			refundTotal = orderReturn.getReturnTotal().subtract(exchangeOrder.getTotal());
		}

		if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment refundPayment = getBean(ContextIdNames.ORDER_PAYMENT);
			refundPayment.setAmount(refundTotal);
			refundPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
			refundPayment.setStatus(OrderPaymentStatus.APPROVED);
			refundPayment.setCreatedDate(new Date());
			orderReturn.setReturnPayment(refundPayment);
		}

		return orderReturn;
	}

	/**
	 * Completes exchange. Places in to ONHOLD state, allocates inventory for shipments. Authorization can be provided for shipments, depending on
	 * exchange order total and exchange total.
	 *
	 * @param exchange exchange to be completed
	 * @param templateOrderPayment template payment to be used for authorization.
	 * @param processMoney whether do refund or auth
	 * @param refundToOriginal whether refund to original
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private OrderReturn completeExchangeOrderInternal(final OrderReturn exchange, final OrderPayment templateOrderPayment,
			final boolean processMoney, final boolean refundToOriginal) {
		Order exchangeOrder = exchange.getExchangeOrder();

		if (exchangeOrder == null) {
			throw new EpServiceException("Exchange order isn't specified.");
		}

		if (exchangeOrder.getStatus() != OrderStatus.AWAITING_EXCHANGE) {
			throw new EpServiceException("Cannot complete exchange order. Incorrect order state.");
		}

		exchangeOrder.setModifiedBy(getEventOriginator(exchange.getCreatedByCmUser()));
		exchangeOrder = orderService.releaseHoldOnOrder(exchangeOrder);

		if (processMoney) {
			BigDecimal refundTotal = exchange.getReturnTotal().subtract(exchangeOrder.getTotal());
			BigDecimal authTotal = exchangeOrder.getTotal().subtract(exchange.getReturnTotal());

			if ((authTotal.compareTo(BigDecimal.ZERO) > 0) && (templateOrderPayment != null)) {
				PaymentResult paymentResult = paymentService.initializePayments(exchangeOrder, templateOrderPayment, null);
				if (paymentResult.getResultCode() != PaymentResult.CODE_OK) {
					exchangeOrder = orderService.awaitExchnageCompletionForOrder(exchangeOrder);
					orderService.update(exchangeOrder);
					throw paymentResult.getCause();
				}
			}

			if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
				Order refundedOrder = null;
				try {
					if (refundToOriginal) {
						refundedOrder = orderService.refundOrderPayment(exchange.getOrder().getUidPk(), refundTotal, exchange.getOrder()
								.getModifiedBy());
					} else if (!refundToOriginal && (templateOrderPayment != null)) {
						refundedOrder = orderService.refundOrderPayment(exchange.getOrder().getUidPk(), templateOrderPayment.getOrderShipment()
								.getShipmentNumber(), templateOrderPayment, refundTotal, exchange.getOrder().getModifiedBy());
					}
				} catch (PaymentGatewayException pge) {
					exchangeOrder = orderService.awaitExchnageCompletionForOrder(exchangeOrder);
					orderService.update(exchangeOrder);
					throw pge;
				}

				if (refundedOrder != null) {
					exchange.setReturnPayment(findLastRefundPayment(refundedOrder.getOrderPayments()));
					// if refund didn't fail we guarantee that refunded order will contain one and only one payment - refunded payment
				}
			}
		}

		for (OrderShipment orderShipment : exchangeOrder.getPhysicalShipments()) {
			orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
			for (final OrderSku skus : orderShipment.getShipmentOrderSkus()) {
				if (!skus.isAllocated()) {
					orderShipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
				}
			}
		}

		final Order updatedExchangeOrder = orderService.update(exchangeOrder);
		return findUpdatedOrderReturn(exchange, updatedExchangeOrder);
	}

	/**
	 * @return
	 */
	private EventOriginator getEventOriginator(final CmUser cmUser) {
		EventOriginatorHelper helper = getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(cmUser);
	}

	@Override
	public Order createExchangeOrder(final OrderReturn orderExchange, final boolean awaitExchangeCompletion) {
		return createExchangeOrder(orderExchange, getExchangePayment(), awaitExchangeCompletion);
	}

	@Override
	public Order createExchangeOrder(final OrderReturn orderExchange, final OrderPayment templatePayment, final boolean awaitExchangeCompletion) {
		checkoutService.checkoutExchangeOrder(orderExchange, templatePayment, awaitExchangeCompletion);

		final ShoppingCart shoppingCart = orderExchange.getExchangeShoppingCart();
		final Order exchangeOrder = shoppingCart.getCompletedOrder();
		orderExchange.setExchangeOrder(exchangeOrder);

		return exchangeOrder;
	}

	private OrderPayment getExchangePayment() {
		final OrderPayment originalPayment = getBean(ContextIdNames.ORDER_PAYMENT);
		originalPayment.setPaymentMethod(PaymentType.RETURN_AND_EXCHANGE);
		return originalPayment;
	}

	@Override
	public ShoppingCart populateShoppingCart(final OrderReturn exchangeOrder, final Collection< ? extends ShoppingItem> itemList,
			final ShippingServiceLevel shippingServiceLevel, final Address shippingAddress) {
		return populateShoppingCart(exchangeOrder, itemList, shippingServiceLevel, CALCULATE_SHIPPING_COST, CALCULATE_SHIPPING_DISCOUNT,
				shippingAddress);
	}

	@Override
	public ShoppingCart populateShoppingCart(final OrderReturn exchange, final Collection< ? extends ShoppingItem> itemList,
			final ShippingServiceLevel shippingServiceLevel, final BigDecimal shippingCost, final BigDecimal shippingDiscount,
			final Address shippingAddress) {

		setupMinimalShoppingCartForOrderReturn(exchange);

		final ShoppingCart shoppingCart = exchange.getExchangeShoppingCart();
		shoppingCart.setShippingAddress(shippingAddress);

		final List<ShippingServiceLevel> validShippingServiceLevels = shippingServiceLevelService.retrieveShippingServiceLevel(shoppingCart);
		shoppingCart.setShippingServiceLevelList(validShippingServiceLevels);

		shoppingCart.setSelectedShippingServiceLevelUid(shippingServiceLevel.getUidPk());

		/*
		 * Workaround to prevent shopping cart from removing product sku items. see MSC-5254
		 */
		shoppingCart.clearItems();
		for (final ShoppingItem item : itemList) {
			final ProductSku productSku = productSkuService.getWithProduct(item.getProductSku().getUidPk());
			item.setProductSku(productSku);
			shoppingCart.addCartItem(item);
		}

		if (!CALCULATE_SHIPPING_COST.equals(shippingCost)) {
			shoppingCart.setShippingCost(shippingCost);
		}

		if (!CALCULATE_SHIPPING_DISCOUNT.equals(shippingDiscount)) {
			shoppingCart.setSubtotalDiscount(shippingDiscount, 0, 0);
		}

		shoppingCart.calculateShoppingCartTaxAndBeforeTaxPrices();

		return shoppingCart;
	}

	/**
	 * Create a minimal, partially {@ShoppingCart} for use in the exchange.
	 *
	 * @param exchange the {@link OrderReturn}
	 */
	protected void setupMinimalShoppingCartForOrderReturn(final OrderReturn exchange) {

		ShoppingCart shoppingCart = exchange.getExchangeShoppingCart();
		if (shoppingCart != null) {
			return;
		}

		final Order order = exchange.getOrder();
		shoppingCart = createShoppingCartForOrderAndShopper(order);

		shoppingCart.setExchangeOrderShoppingCart(true);
		exchange.setExchangeShoppingCart(shoppingCart);
	}

	private ShoppingCart createShoppingCartForOrderAndShopper(final Order order) {
		final CustomerSession customerSession = createCustomerSessionForOrder(order);
		final ShoppingCart shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());

		// FIXME: Change this to use Shopper instead once Shopper and ShoppingCart no longer need a
		// customer session to back it.
		shoppingCart.setCustomerSession(customerSession);
		customerSession.setShoppingCart(shoppingCart);

		shoppingCart.setBillingAddress(order.getBillingAddress());
		shoppingCart.setCurrency(order.getCurrency());
		shoppingCart.setLocale(order.getLocale());
		shoppingCart.setStore(store);

		return shoppingCart;
	}

	private CustomerSession createCustomerSessionForOrder(final Order order) {
		final Shopper shopper = shopperService.findOrCreateShopper(order.getCustomer(), order.getStoreCode());
		return customerSessionService.createWithShopper(shopper);
	}

	@Override
	public OrderReturn createReturn(final OrderReturn orderReturn, final ReturnExchangeType type) {
		OrderReturnStatus status = OrderReturnStatus.COMPLETED;
		OrderReturn updatedOrderReturn;
		switch (type) {
		case PHYSICAL_RETURN_REQUIRED:
			status = OrderReturnStatus.AWAITING_STOCK_RETURN;
			updatedOrderReturn = orderReturn;
			updatedOrderReturn.setPhysicalReturn(true);
			break;
		case REFUND_TO_ORIGINAL:
			updatedOrderReturn = refundOrderReturn(orderReturn);
			break;
		case MANUAL_RETURN:
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		default:
			throw new EpServiceException("Unexpected order return type");
		}

		updatedOrderReturn.setReturnStatus(status);

		updatedOrderReturn.normalizeOrderReturn();

		updatedOrderReturn.getOrder().addReturn(updatedOrderReturn);
		getOrderEventHelper().logOrderReturnCreated(updatedOrderReturn.getOrder(), updatedOrderReturn);
		return getReturnAndExchangeService().add(updatedOrderReturn);
	}

	@SuppressWarnings("fallthrough")
	@Override
	public OrderReturn createExchange(final OrderReturn orderReturn, final ReturnExchangeType type, final OrderPayment authOrderPayment) {

		OrderReturnStatus status = OrderReturnStatus.COMPLETED;
		OrderReturn updatedOrderReturn = orderReturn;
		final Order exchangeOrder;
		switch (type) {
		case CREATE_WO_PAYMENT:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			break;
		case PHYSICAL_RETURN_REQUIRED:
			status = OrderReturnStatus.AWAITING_STOCK_RETURN;
			exchangeOrder = createExchangeOrder(orderReturn, true);
			orderReturn.setExchangeOrder(exchangeOrder);
			orderReturn.setPhysicalReturn(true);
			break;
		case REFUND_TO_ORIGINAL:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			updatedOrderReturn = refundOrderReturn(orderReturn);
			break;
		case MANUAL_RETURN:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		case ORIGINAL_PAYMENT:

		case NEW_PAYMENT:
			if (authOrderPayment == null) {
				throw new EpServiceException("Auth payment must be specified if additional authorization required.");
			}
			exchangeOrder = createExchangeOrder(updatedOrderReturn, authOrderPayment, false);
			updatedOrderReturn.setExchangeOrder(exchangeOrder);
			break;
		default:
			throw new EpServiceException("Unexpected exchange type");
		}

		updatedOrderReturn.setReturnStatus(status);

		updatedOrderReturn.normalizeOrderReturn();

		getOrderEventHelper().logOrderExchnageCreated(updatedOrderReturn.getOrder(), updatedOrderReturn);
		return getReturnAndExchangeService().add(updatedOrderReturn);
	}

	@Override
	public OrderReturn editReturn(final OrderReturn orderReturn) {
		if (orderReturn.isInTerminalState()) {
			throw new IllegalReturnStateException("Detected state: " + orderReturn.getReturnStatus());
		}
		orderReturn.normalizeOrderReturn();
		getOrderEventHelper().logOrderReturnChanged(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(orderReturn);
	}

	@Override
	public OrderReturn completeReturn(final OrderReturn orderReturn, final ReturnExchangeType type) {
		OrderReturn updatedOrderReturn;
		switch (type) {
		case REFUND_TO_ORIGINAL:
			updatedOrderReturn = refundOrderReturn(orderReturn);
			break;
		case MANUAL_RETURN:
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		default:
			throw new EpServiceException("Unexpected order return type");
		}

		updatedOrderReturn.setReturnStatus(OrderReturnStatus.COMPLETED);

		// the logging should happen with the original order return as it has the event originator
		getOrderEventHelper().logOrderReturnCompleted(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(updatedOrderReturn);
	}

	@SuppressWarnings("fallthrough")
	@Override
	public OrderReturn completeExchange(final OrderReturn orderReturn, final ReturnExchangeType type, final OrderPayment authOrderPayment) {
		OrderReturn refundedReturn = null;
		switch (type) {
		case CREATE_WO_PAYMENT:
			completeExchangeOrderInternal(orderReturn, null, false, false);
			break;
		case REFUND_TO_ORIGINAL:
			refundedReturn = completeExchangeOrderInternal(orderReturn, null, true, true);
			orderReturn.setReturnPayment(refundedReturn.getReturnPayment());
			break;
		case MANUAL_RETURN:
			completeExchangeOrderInternal(orderReturn, null, false, false);
			manualRefundOrderReturn(orderReturn);
			break;
		case ORIGINAL_PAYMENT:

		case NEW_PAYMENT:
			if (authOrderPayment == null) {
				throw new EpServiceException("Auth payment must be specified if additional authorization required.");
			}
			completeExchangeOrderInternal(orderReturn, authOrderPayment, true, false);
			break;
		default:
			throw new EpServiceException("Unexpected order return type");
		}

		orderReturn.setReturnStatus(OrderReturnStatus.COMPLETED);

		// the logging should happen with the original order return as it has the event originator
		getOrderEventHelper().logOrderExchangeCompleted(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(orderReturn);
	}

	@Override
	public OrderReturn cancelReturnExchange(final OrderReturn orderReturn) {
		if (orderReturn.getReturnStatus() != OrderReturnStatus.AWAITING_STOCK_RETURN) {
			throw new IllegalReturnStateException("Detected state: " + orderReturn.getReturnStatus());
		}
		orderReturn.setReturnStatus(OrderReturnStatus.CANCELLED);

		// Log the return/exchange canceled.
		try {
			if (orderReturn.getReturnType() == OrderReturnType.EXCHANGE) {
				getOrderEventHelper().logOrderExchangeCanceled(orderReturn.getOrder(), orderReturn);
			} else {
				getOrderEventHelper().logOrderReturnCanceled(orderReturn.getOrder(), orderReturn);
			}
			return getReturnAndExchangeService().update(orderReturn);
		} catch (org.springframework.orm.jpa.JpaOptimisticLockingFailureException e) {
			throw new OrderReturnOutOfDateException("Return cannot be Canceled as it has been updated by another user.", e);
		}
	}

	@Override
	public OrderReturn receiveReturn(final OrderReturn orderReturn) {

		getOrderEventHelper().logOrderReturnReceived(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(orderReturn);
	}

	@Override
	public List<OrderReturn> findOrderReturnBySearchCriteria(
			final OrderReturnSearchCriteria orderReturnSearchCriteria, final int start,
			final int maxResults) {
		sanityCheck();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		List<Object> parameters = new LinkedList<Object>();
		String query = orderCriterion.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.ENTITY);

		List<OrderReturn> orderReturnList = null;

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_RETURN_INDEX);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);

		if (parameters.isEmpty()) {
			orderReturnList = getPersistenceEngine().retrieve(query);
		} else {
			orderReturnList = getPersistenceEngine().retrieve(query, parameters.toArray());
		}

		fetchPlanHelper.clearFetchPlan();

		return orderReturnList;
	}

	@Override
	public long getOrderReturnCountBySearchCriteria(
			final OrderReturnSearchCriteria orderReturnSearchCriteria) {
		sanityCheck();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		List<Object> parameters = new LinkedList<Object>();
		String query = orderCriterion.getOrderReturnSearchCriteria(orderReturnSearchCriteria, parameters, ResultType.COUNT);
		List<Long> orderCount = null;

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_RETURN_INDEX);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);

		if (parameters.isEmpty()) {
			orderCount = getPersistenceEngine().retrieve(query);
		} else {
			orderCount = getPersistenceEngine().retrieve(query, parameters.toArray());
		}

		fetchPlanHelper.clearFetchPlan();

		return orderCount.get(0);
	}

	protected ReturnAndExchangeService getReturnAndExchangeService() {
		return getBean(ContextIdNames.ORDER_RETURN_SERVICE);
	}

	protected OrderEventHelper getOrderEventHelper() {
		return getBean(ContextIdNames.ORDER_EVENT_HELPER);
	}

	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

	public ShopperService getShopperService() {
		return shopperService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}
}
