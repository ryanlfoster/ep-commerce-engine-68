/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.domain.shoppingcart.ShipmentTypeCollections;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;

/**
 * This class is used to determine which of three lists a shopping cart's items belong to: service, physical, or electronic.
 */
public class ShipmentTypeShoppingCartVisitor implements ShoppingCartVisitor, ShipmentTypeCollections {
	
	private final List<ShoppingItem> electronicSkus;
	private final List<ShoppingItem> physicalSkus;
	private final List<ShoppingItem> serviceSkus;
	private final ShoppingItemHasRecurringPricePredicate recurringPricePredicate;
	
	/**
	 * Constructor.
	 * @param predicate Used to determine if an item has a recurring price.
	 */
	public ShipmentTypeShoppingCartVisitor(final ShoppingItemHasRecurringPricePredicate predicate) {
		this.electronicSkus = new ArrayList<ShoppingItem>();
		this.physicalSkus = new ArrayList<ShoppingItem>();
		this.serviceSkus = new ArrayList<ShoppingItem>();
		this.recurringPricePredicate = predicate;
	}
	
	@Override
	public List<ShoppingItem> getElectronicSkus() {
		return Collections.unmodifiableList(electronicSkus);
	}
	
	@Override
	public List<ShoppingItem> getPhysicalSkus() {
		return Collections.unmodifiableList(physicalSkus);
	}
	
	@Override
	public List<ShoppingItem> getServiceSkus() {
		return Collections.unmodifiableList(serviceSkus);
	}
	
	/**
	 * Does nothing.
	 * {@inheritDoc}
	 */
	public void visit(final ShoppingCart cart) {
		// Does nothing.
	}

	/**
	 * If the given item is a bundle then it is ignored.
	 * Otherwise the item is added to one of the three lists: service, physical, or electronic.
	 * {@inheritDoc}
	 */
	public void visit(final ShoppingItem item) {
		if (item.isBundle()) {
			return;
		}
		
		if (recurringPricePredicate.evaluate(item)) {
			serviceSkus.add(item);
		} else if (item.getProductSku().isShippable()) {
			physicalSkus.add(item);
		} else {
			electronicSkus.add(item);
		}
	}

}
