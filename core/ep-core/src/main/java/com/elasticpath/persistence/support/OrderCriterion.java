/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.order.AdvancedOrderSearchCriteria;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * <code>OrderCriterion</code> generates the proper criteria for order domain object.
 */
public interface OrderCriterion {

	/**
	 * The Enum ResultType.
	 */
	enum ResultType {

		/** Query returns COUNT. */
		COUNT,

		/** Query returns OrderImpl.orderNumber. */
		ORDER_NUMBER,

		/** Query returns ENTITY. */
		ENTITY
	}

	/**
	 * Returns the criteria used to query by status.
	 *
	 * @param orderStatus the status of the order
	 * @param paymentStatus the status of the payment
	 * @param shipmentStatus the status of the shipment
	 * @return the criteria used to query by status
	 */
	String getStatusCriteria(final OrderStatus orderStatus, final OrderPaymentStatus paymentStatus, final OrderShipmentStatus shipmentStatus);

	/**
	 * Returns the criteria used to query by customer property of String type, i.e. 'email', 'firstName', 'lastName' etc.
	 *
	 * @param propertyName customer property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	String getOrderCustomerCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch);

	/**
	 * Returns the criteria used to query by customer profile value such as 'CP_EMAIL'.
	 *
	 * @param attributeKey customer profile property to search on.
	 * @param attributeValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	String getOrderCustomerProfileCriteria(final String attributeKey, final String attributeValue, final boolean isExactMatch);

	/**
	 * Return the criteria used for advanced order search. Search criteria cover order properties, order customerProperties
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @return the criteria used to complete advaned order search
	 */
	String getAdvancedOrderCriteria(final AdvancedOrderSearchCriteria orderSearchCriteria);

	/**
	 * Returns the criteria used to query by giftCertificate with name and value type, i.e. 'giftCertificateCode'.
	 *
	 * @param propertyName customer property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	String getOrderGiftCertificateCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch);

	/**
	 * Return the criteria used for order search.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param parameters the query parameters to be returned
	 * @param stores the list of store filters to be returned
	 * @param resultType the result type
	 * @return the query string used to complete order search
	 */
	String getOrderSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
								  final List<Object> parameters,
								  final Collection<String> stores,
								  final ResultType resultType);


	/**
	 * Return the criteria used for order return search.
	 *
	 * @param orderReturnSearchCriteria the order return search criteria.
	 * @param parameters the query parameters to be returned
	 * @param resultType the result type
	 * @return the query string used to complete order return search
	 */
	String getOrderReturnSearchCriteria(
			OrderReturnSearchCriteria orderReturnSearchCriteria,
			List<Object> parameters, ResultType resultType);
}
