/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.order;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.PurchaseHistorySearchCriteria;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * Provides storage and access to <code>Order</code> objects.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface OrderService extends EpPersistenceService {

	/**
	 * Adds the given order.
	 *
	 * @param order the order to add
	 * @return the persisted instance of order
	 * @throws EpServiceException - in case of any errors
	 */
	Order add(final Order order) throws EpServiceException;

	/**
	 * Updates the given order.
	 *
	 * @param order the order to update
	 * @return the persisted instance of order
	 * @throws EpServiceException - in case of any errors
	 */
	Order update(final Order order) throws EpServiceException;

	/**
	 * Retrieve the list of orders with the specified statuses.
	 *
	 * @param orderStatus the status of the order
	 * @param paymentStatus the status of the paymentreturnSummaryNode
	 * @param shipmentStatus the status of the shipment
	 * @return the list of orders with the specified statuses
	 */
	List<Order> findOrderByStatus(final OrderStatus orderStatus, final OrderPaymentStatus paymentStatus, final OrderShipmentStatus shipmentStatus);

	/**
	 * Retrieves list of <code>Order</code> where the created date is later than the specified date.
	 *
	 * @param date date to compare with the created date
	 * @return list of <code>Order</code> whose created date is later than the specified date
	 */
	List<Order> findByCreatedDate(final Date date);

	/**
	 * Retrieve the list of orders, whose specified property matches the given criteria value.
	 *
	 * @param propertyName order property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the given criteria.
	 */
	List<Order> findOrder(final String propertyName, final String criteriaValue, final boolean isExactMatch);

	/**
	 * Retrieve the list of orders by the customer's guid.
	 *
	 * @param customerGuid the customer's guid
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the customer's guid.
	 */
	List<Order> findOrderByCustomerGuid(final String customerGuid, final boolean isExactMatch);

	/**
	 * Retrieve the list of orders by the customer's guid and store code.
	 *
	 * @param customerGuid the customer's guid
	 * @param storeCode the store code
	 * @param retrieveFullInfo if set to true, will retrieve entire object graph, otherwise only retrieves basic information
	 * @return list of orders matching the criteria.
	 */
	List<Order> findOrdersByCustomerGuidAndStoreCode(final String customerGuid, final String storeCode, final boolean retrieveFullInfo);

	/**
	 * Retrieve the list of orders by the customer's email address.
	 *
	 * @param customerEmail the customer's email address.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the customer's email address.
	 */
	List<Order> findOrderByCustomerEmail(final String customerEmail, final boolean isExactMatch);

	/**
	 * Advanced order search function based on the orderSearchCriteria and the max number of results to return.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param maxResults the max number of orders to return on search.
	 * @return the list of orders matching the given criteria.
	 */
	List<Order> findOrderAdvanced(final AdvancedOrderSearchCriteria orderSearchCriteria, final int maxResults);

	/**
	 * order count function based on the OrderSearchCriteria.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @return the count of orders matching the given criteria.
	 */
	long getOrderCountBySearchCriteria(final OrderSearchCriteria orderSearchCriteria);

	/**
	 * order search function based on the OrderSearchCriteria.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @return the list of orders matching the given criteria.
	 */
	List<Order> findOrdersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final int start, final int maxResults);

	/**
	 * Find orders by search criteria using the given load tuner.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @param loadTuner the load tuner
	 * @return the list of orders matching the given criteria.
	 */
	List<Order> findOrdersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final int start, final int maxResults,
			final LoadTuner loadTuner);

	/**
	 * Returns a list of <code>Order</code> based on the given uids. The returned orders will be populated based on the given load tuner.
	 *
	 * @param orderUids a collection of order uids
	 * @return a list of <code>Order</code>s
	 */
	List<Order> findByUids(final Collection<Long> orderUids);

	/**
	 * Returns a list of fully initialized <code>Order</code> objects based on the given uids.
	 * The returned orders will be populated based on the given load tuner.
	 *
	 * @param orderUids a collection of order uids
	 * @return a list of <code>Order</code>s
	 */
	List<Order> findDetailedOrdersByUids(final Collection<Long> orderUids);

	/**
	 * List all orders stored in the database.
	 *
	 * @return a list of orders
	 * @throws EpServiceException - in case of any errors
	 */
	List<Order> list() throws EpServiceException;

	/**
	 * Get the order with the given UID. Return null if no matching record exists.
	 *
	 * @param orderUid the order UID
	 * @return the order if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Order get(final long orderUid) throws EpServiceException;

	/**
	 * Get the order with the given UID. Return <code>null</code> if no matching record exists.
	 * Fine tune the order with the given load tuner. If <code>null</code> is given, the default
	 * load tuner will be used.
	 *
	 * @param orderUid the order UID
	 * @param loadTuner the load tuner to use (or <code>null</code> for the default)
	 * @return the order if UID exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	Order get(long orderUid, FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	Object getObject(final long uid) throws EpServiceException;

	/**
	 * Return the fully initialized order object.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Order getOrderDetail(final long uid) throws EpServiceException;

	/**
	 * Capture the order balance amount and update the <code>OrderShipment</code> status on success.
	 *
	 * @param shipmentNumber the shipment number to be released (GUID)
	 * @param trackingCode the trackingCode for the orderShipment to be released (optional)
	 * @param captureFunds true if funds should be captured, false if not
	 * @param shipmentDate the date that the shipment was completed (optional, defaults to now)
	 * @param sendConfEmail need to send customer a shipment confirmation email or not (optional, defaults to false)\
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator.
	 * See {@link com.elasticpath.domain.event.EventOriginatorHelper }
	 * @return the modified Order containing the modified Shipment
	 * @throws CompleteShipmentFailedException on error setting up the payments
	 */
	Order completeShipment(final String shipmentNumber,
			final String trackingCode,
			final boolean captureFunds,
			final Date shipmentDate,
			final boolean sendConfEmail,
			final EventOriginator eventOriginator);

	/**
	 * Called by quartz job to release order shipments after a preconfigured period after order has been placed.
	 */
	void updateOrderShipmentStatus();

	/**
	 * Partially or fully Refund the order based on the amount.
	 *
	 * @param orderUid the uid of the order.
	 * @param refundAmount the amount of the orderpayment to be refunded.
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator.
	 * See {@link com.elasticpath.domain.event.EventOriginatorHelper }
	 * @return a stripped down version of order (for performance reasons) containing only single order payment that was being refunded at the refund
	 *         operation
	 */
	Order refundOrderPayment(final long orderUid, final BigDecimal refundAmount,
			final EventOriginator eventOriginator);

	/**
	 * Partially or fully Refund the order based on the amount.
	 *
	 * @param orderUid the uid of the order.
	 * @param shipmentNumber the shipment number
	 * @param orderPayment order payment to be used for refunding.
	 * @param refundAmount the amount of the orderpayment to be refunded.
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator.
	 * See {@link com.elasticpath.domain.event.EventOriginatorHelper }
	 * @return the Order on which the refund occurred
	 */
	Order processRefundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment orderPayment,
			final BigDecimal refundAmount, final EventOriginator eventOriginator);

	/**
	 * Partially or fully Refund the order based on the amount.
	 *
	 * @param orderUid the uid of the order.
	 * @param shipmentNumber the shipment number
	 * @param orderPayment order payment to be used for refunding.
	 * @param refundAmount the amount of the orderpayment to be refunded.
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator.
	 * See {@link com.elasticpath.domain.event.EventOriginatorHelper }
	 * @return the Order on which the refund occurred
	 */
	Order refundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment orderPayment, final BigDecimal refundAmount,
			final EventOriginator eventOriginator);

	/**
	 * Get the orderSku uid -> returned quantity map for the order with given uid.
	 *
	 * @param orderUid the uid of the order.
	 * @return the orderSku uid -> returned quantity map.
	 */
	Map<Long, Integer> getOrderSkuReturnQtyMap(final long orderUid);

	/**
	 * Returns all order uids as a list.
	 *
	 * @return all order uids as a list
	 */
	List<Long> findAllUids();

	/**
	 * Retrieves list of <code>Order</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Order</code> whose last modified date is later than the specified date
	 */
	List<Long> findUidsByModifiedDate(Date date);

	/**
	 * Retrieve the list of orders by the gift certificate code.
	 *
	 * @param giftCertificateCode the gift certificate code
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the gift certificate code.
	 */
	List<Order> findOrderByGiftCertificateCode(final String giftCertificateCode, final boolean isExactMatch);

	/**
	 * Returns a list of ProductCodes for all Products of which the product's skus have been purchased by a
	 * particular user, given additional search criteria.
	 * @param criteria the criteria to use in finding the product codes
	 * @return distinct list of product codes corresponding to skus that were purchased by a user, filtered by
	 * the given search criteria
	 */
	List<String> findProductCodesPurchasedByUser(final PurchaseHistorySearchCriteria criteria);

	/**
	 * Update the address for order, sometimes we don't want to update the whole order.
	 *
	 * @param address the given address.
	 */
	void updateAddress(final OrderAddress address);

	/**
	 * Searches for an order shipment with the specified shipment number and shipment type.
	 * @param shipmentNumber the order shipment number
	 * @param shipmentType the type of shipment (physical or electronic)
	 * @return the shipment requested, or null if not found
	 */
	OrderShipment findOrderShipment(final String shipmentNumber, final ShipmentType shipmentType);

	/**
	 * IMPORTANT: This method is defined for the transaction issue purpose,
	 * and should not be called by your code. Refactor the code to notify Spring
	 * to start a transaction on the processOrderShipment function.
	 * Release the order shipment after the payment is captured. Here will be executed
	 * some extra tasks, eg. capture the payment for gift certificate.
	 * This method will run as an atomic DB transaction (this is specified in the Spring configuration).
	 *
	 * @param shipmentNumber the number of the orderShipment to be released.
	 * @param trackingCode the trakcingCode for the orderShipment to be released.
	 * @param shipmentDate the date of shipment process
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator.
	 * See {@link com.elasticpath.domain.event.EventOriginatorHelper }
	 * @return the updated order
	 */
	Order processOrderShipment(String shipmentNumber, final String trackingCode, final Date shipmentDate,
			final EventOriginator eventOriginator);

	/**
	 * Add the given <code>OrderReturn</code> to the order with given uid.
	 *
	 * @param order the order.
	 * @param orderReturn orderReturn to be added.
	 * @return the updated order.
	 */
	Order addOrderReturn(final Order order, final OrderReturn orderReturn);

	/**
	 * Remove an order. Should only be called on an unpopulated order object.
	 *
	 * @param order the order to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final Order order) throws EpServiceException;


	/**
	 * Releases the shipment for pick/pack.
	 *
	 * @param orderShipment the order shipment to be released
	 * @return the updated order shipment
	 * @throws ReleaseShipmentFailedException on error setting up the authorization payments
	 */
	OrderShipment processReleaseShipment(OrderShipment orderShipment) throws ReleaseShipmentFailedException;

	/**
	 * Return list of shipments which are awaiting shipping.
	 *
	 * @param warehouse warehouse for which list of shipments is requested.
	 * @return list of shipments which are awaiting shipping
	 */
	List<PhysicalOrderShipment> getAwaitingShipments(Warehouse warehouse);

	/**
	 * Count number of shipments which are awaiting shipping.
	 *
	 * @param warehouse warehouse for which count of shipments is requested.
	 * @return number of shipments which are awaiting shipping
	 */
	Long getAwaitingShipmentsCount(Warehouse warehouse);

	/**
	 * Releases order lock if order was locked and updates the order.
	 *
	 * @param order the order to be unlocked and updated.
	 * @param cmUser the user which is releasing the order lock.
	 * @throws EpServiceException - in case of any errors,
	 * InvalidUnlockerException if when the orderLock was obtained not by the cmUser, but by some other user.
	 */
	void unlockAndUpdate(final Order order, final CmUser cmUser);

	/**
	 * Cancel an order.
	 * Will not allow you to cancel an order that is not cancellable.
	 *
	 * @param order the order to be canceled.
	 * @return the updated order
	 */
	Order cancelOrder(final Order order);

	/**
	 * Put a hold on an order.
	 *
	 * @param order the order to be put on hold.
	 * @return the updated order
	 */
	Order holdOrder(final Order order);

	/**
	 * Releases a hold on an order.
	 * @param order the order on which to release the hold
	 * @return the updated order
	 */
	Order releaseHoldOnOrder(final Order order);

	/**
	 * Cancel an order, update the db and objects in one transaction.
	 * Not designed to be called by external code.
	 *
	 * @param order the order to be canceled.
	 * @return the updated order
	 */
	Order processOrderCancellation(final Order order);

	/**
	 * Cancel an orderShipment.
	 *
	 * @param orderShipment the orderShipment to be canceled.
	 * @return the updated orderShipment
	 */
	PhysicalOrderShipment cancelOrderShipment(final PhysicalOrderShipment orderShipment);

	/**
	 * Cancel an orderShipment, update the db and objects in one transaction.
	 *
	 * @param orderShipment the orderShipment to be canceled.
	 * @return the updated orderShipment
	 */
	PhysicalOrderShipment processOrderShipmentCancellation(final PhysicalOrderShipment orderShipment);

	/**
	 * Finds order by order number.
	 *
	 * @param orderNumber order number.
	 * @return the order
	 */
	Order findOrderByOrderNumber(String orderNumber);

	/**
	 * Places order which exchange requires physical return to AWAITING_EXCHANGE state.
	 * @param order the order upon which to place a hold
	 * @return the modified order
	 */
	Order awaitExchnageCompletionForOrder(final Order order);

	/**
	 * Processes an order resulting from a shopping cart checkout. The order processing involves all record-creation steps such as creating and
	 * storing the order, shipment, decrementing Inventory, etc. This method will run as an atomic DB transaction (This is specified in the Spring
	 * configuration)
	 *
	 * @param order the order to process
	 * @param isExchangeOrder true if order is created for an exchange
	 * @return the completed order object
	 * @throws EpServiceException on error
	 */
	Order processOrderOnCheckout(Order order, boolean isExchangeOrder);

	/**
	 * Process the payment for a shipment. This will attempt to capture funds for the shipment and return a <code>PaymentResult</code>. If the
	 * payment processing fails, then any failed capture transactions will be saved to the order.
	 *
	 * @param shipmentNumber the shipment number to process a payment on (GUID)
	 * @return the result of the payment processing
	 */
	PaymentResult processOrderShipmentPayment(String shipmentNumber);

	/**
	 * Increases the record of the number of uses of limited usage promotion codes if the promotions were applied to the shopping cart.
	 *
	 * @param appliedRuleUids the UIDs of the rules that have been applied to the shopping cart
	 * @param limitedUsagePromotionCodes - list of Limited Usage Promotion codes (stored in ShoppingCart)
	 */
	void updateLimitedUsagePromotionCurrentNumbers(final Collection<Long> appliedRuleUids, final List<String> limitedUsagePromotionCodes);

	/**
	 * Get the order count for a given customer email address.
	 *
	 * @param email the email address of the customer
	 * @param storeId the store to get orders from
	 * @return the order count
	 */
	long getCustomerOrderCountByEmail(String email, long storeId);

	/**
	 * Gets a list of order UIDs for "failed" orders that are created before the given date.
	 *
	 * @param toDate the date. orders that are created before this date will be returned.
	 * @param maxResults the maximum number of results to be returned
	 * @return a list of order UIDs
	 */
	List<Long> getFailedOrderUids(Date toDate, int maxResults);

	/**
	 * Delete orders with the given UIDs.
	 *
	 * @param orderUids UIDs of the orders to be removed
	 */
	void deleteOrders(List<Long> orderUids);

	/**
	 * Find latest order GUID by cart order GUID.
	 *
	 * @param cartOrderGuid the cart order GUID
	 * @return the Order GUID if one exists, null otherwise.
	 */
	String findLatestOrderGuidByCartOrderGuid(String cartOrderGuid);


	/**
	 * Find the numbers of all the orders owned by a customer in a certain store, given customer's GUID and store code.
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 *
	 * @return the list of order numbers
	 */
	List<String> findOrderNumbersByCustomerGuid(String storeCode, String customerGuid);

	/**
	 * Find order numbers by search criteria.
	 *
	 * @param orderSearchCriteria the order search criteria
	 * @param start the start
	 * @param maxResults the max results
	 * @return the list
	 */
	List<String> findOrderNumbersBySearchCriteria(OrderSearchCriteria orderSearchCriteria, int start, int maxResults);
}
