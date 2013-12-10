/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.order.impl;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * A {@link Predicate} that evaluates whether a {@link ShoppingItem} has recurring charges.
 */
public class ShoppingItemHasRecurringPricePredicate implements Predicate, Serializable {

    private static final long serialVersionUID = 1L;

    /**
   	 * Checks whether the item has any recurring charges.
   	 *
   	 * @param obj the shopping item
   	 * @return true if the item has recurring price
   	 */
    @Override
    public boolean evaluate(final Object obj) {
    	ShoppingItem shoppingItem = (ShoppingItem) obj;

    	return shoppingItem.getPrice() != null && shoppingItem.getPrice().getPricingScheme() != null
    			&& !shoppingItem.getPrice().getPricingScheme().getSchedules(PriceScheduleType.RECURRING).isEmpty();
    }

}
