/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;

/**
 * Provides storage and access to <code>OrderReturn</code> objects.
 */
public interface ReturnAndExchangeService extends EpPersistenceService {
	/**
	 * Returns list of all <code>OrderReturn</code> objects.
	 * 
	 * @return Full list of Order exchanges and order returns.
	 * @throws EpServiceException in case of any errors
	 */
	List<OrderReturn> list() throws EpServiceException;
	
	/**
	 * Returns list of all <code>OrderReturn</code> uids.
	 * 
	 * @return Full list of Order exchanges and order return uids.
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> findAllUids() throws EpServiceException;

	/**
	 * Returns a list of <code>OrderReturn</code> based on the given uids.
	 * 
	 * @param orderUids a collection of order return uids
	 * @return a list of <code>OrderReturn</code>s
	 */
	List<OrderReturn> findByUids(final Collection<Long> orderUids);
	
	/**
	 * Retrieves list of <code>OrderReturn</code> uids where the last modified date is later than the specified date.
	 * 
	 * @param date date to compare with the last modified date
	 * @return list of <code>OrderReturn</code> whose last modified date is later than the specified date
	 */
	List<Long> findUidsByModifiedDate(final Date date);
	
	/**
	 * Returns list of <code>OrderReturn</code>'s that associated with order defined by uidPk.
	 * 
	 * @param uidPk the <code>Order</code> UID.
	 * @return List of Order exchanges and order returns for the specified order.
	 * @throws EpServiceException in case of any errors
	 */
	List<OrderReturn> list(final long uidPk) throws EpServiceException;

	/**
	 * Returns list of <code>OrderReturn</code>'s of concrete type type that associated with order defined by uidPk.
	 * 
	 * @param uidPk the <code>Order</code> UID.
	 * @param returnType type of the order(EXCHANGE or RETURN).
	 * @return List of exchanges or returns for the specified order.
	 * @throws EpServiceException in case of any errors
	 */
	List<OrderReturn> list(final long uidPk, final OrderReturnType returnType) throws EpServiceException;

	/**
	 * Get the order return with the given UID. Return null if no matching record exists.
	 * 
	 * @param orderReturnUid the order return UID
	 * @return the order return if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	OrderReturn get(final long orderReturnUid) throws EpServiceException;
	
	/**
	 * Gets the order return with the given UID. Returns <code>null</code> if no matching
	 * records exist. Give a load tuner to fine tune the result or <code>null</code> to load the
	 * default fields.
	 * 
	 * @param orderReturnUid the order return UID
	 * @param loadTuner the load tuner or <code>null</code> for the default
	 * @return the order return if the UID exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	OrderReturn get(final long orderReturnUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(final long uid) throws EpServiceException;

	/**
	 * Updates the given order return. Handles OptimisticLockException. If it's detected that 
	 * return was modified by another user, then the exception is wrapped into 
	 * <code>OrderReturnOutOfDateException</code> and thrown. 
	 * 
	 * @param orderReturn the order return to update
	 * @return the persisted instance of order return 
	 * @throws EpServiceException - in case of any errors, specifically
	 * OrderReturnOutOfDateException in case of version collision.	 
	 */
	OrderReturn update(final OrderReturn orderReturn) throws EpServiceException;

	/**
	 * Adds the given order return.
	 * 
	 * @param orderReturn the order return to add
	 * @return the persisted instance of order return
	 * @throws EpServiceException - in case of any errors
	 */
	OrderReturn add(final OrderReturn orderReturn) throws EpServiceException;

	/**
	 * Create exchange order using underlying shopping cart. Payment template will be derived from original order.
	 * Should not be generally called directly. Use createExchange() or completeExchange() methods instead. 
	 * 
	 * @param orderExchange order exchange to be populated with exchange order
	 * @param awaitExchangeCompletion if physical return required.
	 * @return created exchange order
	 */
	Order createExchangeOrder(final OrderReturn orderExchange, boolean awaitExchangeCompletion);

	/**
	 * Create exchange order using underlying shopping cart.
	 * Should not be generally called directly. Use createExchange() or completeExchange() methods instead.
	 * 
	 * @param orderExchange order exchange to be populated with exchange order
	 * @param awaitExchangeCompletion if physical return required.
	 * @param templatePayment payment to be used for authorization if the last is required.
	 * @return created exchange order
	 */
	Order createExchangeOrder(final OrderReturn orderExchange, final OrderPayment templatePayment,
			final boolean awaitExchangeCompletion);
		
	/**
	 * Populates internal exchange's shopping cart. This exchange shopping cart will be used to create exchange order.
	 * 
	 * @param orderExchange exchange shopping cart will be created for
	 * @param itemList the list of cart items
	 * @param shippingServiceLevel shipping service level
	 * @param shippingAddress shipping address
	 * @return recalculated shopping cart.
	 */
	ShoppingCart populateShoppingCart(final OrderReturn orderExchange, final Collection< ? extends ShoppingItem> itemList,
			final ShippingServiceLevel shippingServiceLevel, final Address shippingAddress);

	/**
	 * Populates internal exchange's shopping cart. This exchange shopping cart will be used to create exchange order.
	 * 
	 * @param orderExchange exchange shopping cart will be created for
	 * @param itemList the list of cart items
	 * @param shippingServiceLevel shipping service level
	 * @param shippingCost shipping cost for the exchange order
	 * @param shippingDiscount shipping discount for the exchange order
	 * @param shippingAddress shipping address
	 * @return recalculated shopping cart.
	 */
	ShoppingCart populateShoppingCart(final OrderReturn orderExchange, final Collection< ? extends ShoppingItem> itemList,
			final ShippingServiceLevel shippingServiceLevel, final BigDecimal shippingCost, final BigDecimal shippingDiscount,
			final Address shippingAddress);
	
	/**
	 * Gets exchange by order exchange's uidPk.
	 *
	 * @param uidPk exchange order uidPk for which exchange is obtained.
	 * @return exchange for order exchange if found.
	 */
	OrderReturn getExchange(final long uidPk);

	/**
	 * Creates the return. Logs the event.
	 *
	 * @param orderReturn order return to be created.
	 * @param type type of return is being created.
	 * @return processed return
	 */
	OrderReturn createReturn(final OrderReturn orderReturn, final ReturnExchangeType type);
	
	/**
	 * Creates the exchange. Logs the event.
	 *
	 * @param exchange order exchange to be created.
	 * @param type type of exchange is being created.
	 * @param authOrderPayment optional exchange order auth order payment. Must be passed just if additional auth is required.
	 * @return processed return 
	 */
	OrderReturn createExchange(final OrderReturn exchange, final ReturnExchangeType type, final OrderPayment authOrderPayment);
	
	/**
	 * Edits the return. Logs the event.
	 *
	 * @param orderReturn order return to be edited.
	 * @return processed return	 
	 */
	OrderReturn editReturn(final OrderReturn orderReturn);
	
	/**
	 * Completes the return. Logs the event.
	 *
	 * @param orderReturn order return to be completed.
	 * @param type type of return is being created.
	 * @return processed return
	 */
	OrderReturn completeReturn(final OrderReturn orderReturn, final ReturnExchangeType type);
	
	/**
	 * Completes the exchange. Logs the event.
	 *
	 * @param exchange order exchange to be completed.
	 * @param type type of exchange is being created.
	 * @param authOrderPayment optional exchange order auth order payment. Must be passed just if additional auth is required.
	 * @return processed return 
	 */
	OrderReturn completeExchange(final OrderReturn exchange, final ReturnExchangeType type, final OrderPayment authOrderPayment);
	
	/**
	 * Cancels the return. Logs the event.
	 *
	 * @param orderReturn order return to be canceled.
	 * @return processed return	 
	 */
	OrderReturn cancelReturnExchange(final OrderReturn orderReturn);
	
	/**
	 * Receives the return. Logs the event.
	 *
	 * @param orderReturn order return to be received.
	 * @return processed return	 
	 */
	OrderReturn receiveReturn(final OrderReturn orderReturn);
	
	/**
	 * order return count function based on the OrderReturnSearchCriteria.
	 * 
	 * @param orderReturnSearchCriteria the order return  search criteria.
	 * @return the count of orders matching the given criteria.
	 */
	long getOrderReturnCountBySearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria);
	
	/**
	 * order return search function based on the OrderReturnSearchCriteria.
	 * 
	 * @param orderReturnSearchCriteria the order return search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @return the list of orders matching the given criteria.
	 */
	List<OrderReturn> findOrderReturnBySearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria, 
			final int start, final int maxResults);
}
