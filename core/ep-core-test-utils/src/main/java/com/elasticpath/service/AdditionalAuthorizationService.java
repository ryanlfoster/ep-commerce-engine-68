package com.elasticpath.service;

import java.util.List;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.ReAuthorizationItem;

/**
 * Service provides methods for checking necessity and processing additional authorization of order shipments.
 */
public interface AdditionalAuthorizationService {

	/**
	 * Prepare the list of reauthorization items. If the list is empty then additional authorization doesn't required
	 * 
	 * @param order order.
	 * @return list of reauthorization items.
	 */
	List<ReAuthorizationItem> getReAuthorizationItemList(Order order);

	/**
	 * Process reauthorization of adjusted and new shipments.
	 * 
	 * @param reAuthorizationList the list of containers with information necessary for authorization
	 */
	void authorizeOrder(List<ReAuthorizationItem> reAuthorizationList);

	/**
	 * Set reAuthItem.newPayment and fill it with information about credit card or gift certificate and payment gateways.
	 * 
	 * @param reAuthorizationItem container with payment information to be updated with new payment
	 * @param orderPayment order payment selected in UI
	 * @return reauthorization item
	 */
	ReAuthorizationItem setNewPaymentInformation(ReAuthorizationItem reAuthorizationItem, OrderPayment orderPayment);
}
