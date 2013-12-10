/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A {@link Predicate} that evaluates whether a {@link ShoppingItem} is a physical shipment type.
 */
public class PhysicalShipmentTypePredicate implements Predicate, Serializable {

    private static final long serialVersionUID = 1L;

    /**
	 * Evaluate that the object is a ShoppingItem and that it is a physical shipment type.
	 *
	 * @param obj the object to be evaluated, expecting a {@link ShoppingItem}
	 * @return true if the object is a shopping item and is a physical shipment type
	 */
	@Override
	public boolean evaluate(final Object obj) {
		return ((ShoppingItem) obj).isShippable();
	}

}
