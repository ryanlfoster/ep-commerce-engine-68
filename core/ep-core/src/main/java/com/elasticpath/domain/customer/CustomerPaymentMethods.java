/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer;

import java.util.Collection;

import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Represents an association between a customer and a collection of {@link PaymentMethod}s.
 */
public interface CustomerPaymentMethods {
	/**
	 * Gets a customer's default payment method.
	 * @return the default payment method
	 */
	PaymentMethod getDefault();

	/**
	 * Sets the default payment method.
	 * @param defaultPaymentMethod the method to set as default
	 */
	void setDefault(PaymentMethod defaultPaymentMethod);

	/**
	 * Returns a list of payment methods.
	 * @return the list of payment methods
	 */
	Collection<PaymentMethod> all();

	/**
	 * Returns true if this customer payment methods contains at least one element that is equal to the given element.
	 * @param paymentMethod element whose presence is to be tested
	 * @return true if this customer payment methods contains the specified element.
	 */
	boolean contains(PaymentMethod paymentMethod);

	/**
	 * Adds all elements of the specified collection to this customer payment methods.
	 * @param paymentMethods the collection of payment methods to add
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean addAll(Collection<PaymentMethod> paymentMethods);

	/**
	 * Adds the given payment method to this customer payment methods.
	 * @param paymentMethod the payment method to add
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean add(PaymentMethod paymentMethod);

	/**
	 * Removes the given payment method from this customer payment methods.
	 * @param paymentMethod the payment method to remove
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean remove(PaymentMethod paymentMethod);

	/**
	 * Removes the given payment methods from this customer payment methods.
	 * @param paymentMethods the collection of payment methods to remove
	 */
	void removeAll(Collection<PaymentMethod> paymentMethods);
}
