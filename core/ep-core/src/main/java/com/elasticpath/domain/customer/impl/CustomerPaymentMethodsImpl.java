/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Default implementation of {@link com.elasticpath.domain.customer.CustomerPaymentMethods}. Delegates persistence of the payment method collection
 * and default to {@link CustomerImpl}. Ideally this could be represented as an {@link javax.persistence.Embeddable}, but due to a limitation of
 * OpenJPA this does not work correctly. Specifically, OpenJPA does not correctly delete orphaned elements from a {@link javax.persistence.OneToMany}
 * relationship defined on an <code>@Embeddable</code>, even when {@link org.apache.openjpa.persistence.ElementDependent} is specified.
 */
public class CustomerPaymentMethodsImpl implements CustomerPaymentMethods, Serializable {
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	private final CustomerImpl customer;

	/**
	 * Constructor.
	 * @param customer the customer to bind to
	 */
	CustomerPaymentMethodsImpl(final CustomerImpl customer) {
		this.customer = customer;
	}

	@Override
	public PaymentMethod getDefault() {
		return customer.getDefaultPaymentMethod();
	}

	@Override
	public void setDefault(final PaymentMethod defaultPaymentMethod) {
		customer.setDefaultPaymentMethod(defaultPaymentMethod);
	}

	@Override
	public Collection<PaymentMethod> all() {
		return Collections.unmodifiableCollection(customer.getPaymentMethodsInternal());
	}

	@Override
	public boolean contains(final PaymentMethod paymentMethod) {
		return all().contains(paymentMethod);
	}

	@Override
	public boolean addAll(final Collection<PaymentMethod> paymentMethods) {
		return customer.getPaymentMethodsInternal().addAll(paymentMethods);
	}

	@Override
	public boolean add(final PaymentMethod paymentMethod) {
		return customer.getPaymentMethodsInternal().add(paymentMethod);
	}

	@Override
	public boolean remove(final PaymentMethod paymentMethod) {
		return customer.getPaymentMethodsInternal().remove(paymentMethod);
	}

	@Override
	public void removeAll(final Collection<PaymentMethod> paymentMethods) {
		customer.getPaymentMethodsInternal().removeAll(paymentMethods);
	}

}
