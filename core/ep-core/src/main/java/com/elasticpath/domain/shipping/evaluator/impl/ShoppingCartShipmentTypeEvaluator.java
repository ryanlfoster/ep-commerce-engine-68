/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.ShipmentTypeEvaluatorStrategy;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartEvaluator;
import com.elasticpath.domain.shoppingcart.ShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * An evaluator which determines the shipment types that can be applied to a shopping item.
 */
public class ShoppingCartShipmentTypeEvaluator implements ShoppingCartEvaluator<ShipmentType>, ShoppingCartVisitor {

	private final Set<ShipmentType> shipmentTypes = new HashSet<ShipmentType>();

	private final ShipmentTypeEvaluatorStrategy shipmentTypeEvaluatorStrategy;

	/**
	 * Constructor.
	 *
	 * @param shipmentTypeEvaluatorStrategy the shipment type evaluator strategy
	 */
	public ShoppingCartShipmentTypeEvaluator(final ShipmentTypeEvaluatorStrategy shipmentTypeEvaluatorStrategy) {
		this.shipmentTypeEvaluatorStrategy = shipmentTypeEvaluatorStrategy;
	}

	public Set<ShipmentType> getShipmentTypes() {
		return Collections.unmodifiableSet(shipmentTypes);
	}

	@Override
	public ShipmentType evaluate(final ShoppingItem item) {
		return shipmentTypeEvaluatorStrategy.evaluate(item);
	}

	/**
	 * Determine the shipment type of the shopping item. <br>
	 * If all shipment types have occurred then do not visit the item to determine it's shipping type.
	 *
	 * @param item the shopping item to be evaluated
	 */
	@Override
	public void visit(final ShoppingItem item) {
		if (!allShipmentTypesFound()) {
			shipmentTypes.add(evaluate(item));
		}
	}

	@Override
	public void visit(final ShoppingCart cart) {
		throw new UnsupportedOperationException("This method is not supported.");
	}

	private boolean allShipmentTypesFound() {
		return ShipmentType.values().size() == shipmentTypes.size();
	}

}
