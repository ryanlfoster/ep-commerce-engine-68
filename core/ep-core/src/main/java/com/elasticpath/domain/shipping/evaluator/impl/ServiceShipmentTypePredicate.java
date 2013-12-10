/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;

/**
 * A {@link Predicate} that evaluates whether a {@link ShoppingItem} is a service shipment type.
 */
public class ServiceShipmentTypePredicate implements Predicate, Serializable {

    private static final long serialVersionUID = 1L;

	private final ShoppingItemHasRecurringPricePredicate recurringPricePredicate;

	/**
	 * Constructor.
	 *
	 * @param recurringPricePredicate wrapped predicate basis
	 */
	public ServiceShipmentTypePredicate(final ShoppingItemHasRecurringPricePredicate recurringPricePredicate) {
		this.recurringPricePredicate = recurringPricePredicate;
	}

	/**
	 * Evaluate that the object is a ShoppingItem and that it is a service shipment type.
	 *
	 * @param obj the object to be evaluated, expecting a {@link ShoppingItem}
	 * @return true if the object is a shopping item and is a service shipment type
	 */
	@Override
	public boolean evaluate(final Object obj) {
		ShoppingItem shoppingItem = (ShoppingItem) obj;
		return recurringPricePredicate.evaluate(shoppingItem);
	}

}
