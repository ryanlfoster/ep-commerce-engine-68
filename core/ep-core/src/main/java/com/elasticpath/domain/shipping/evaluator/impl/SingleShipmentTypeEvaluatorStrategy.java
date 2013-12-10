/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.InvalidShoppingItemException;
import com.elasticpath.domain.shipping.evaluator.MultipleMatchingShipmentTypesFoundException;
import com.elasticpath.domain.shipping.evaluator.NoMatchingShipmentTypeFoundException;
import com.elasticpath.domain.shipping.evaluator.ShipmentTypeEvaluatorStrategy;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * This shipment type evaluator strategy determines which shipment type should be applied to a given shopping item. <br>
 * The predicate list is traversed in order, and each predicate is applied to the {@link ShoppingItem).
 * Only one predicate should match, and only one {@link ShipmentType} should be returned.
 * If no predicates match or more than one predicate matches, an exception is thrown.
 * {@link ProductBundles} do not have a {@link ShipmentType}, and an exception is thrown if one is evaluated.
 */
public class SingleShipmentTypeEvaluatorStrategy implements ShipmentTypeEvaluatorStrategy {

	private final Map<ShipmentType, Predicate> shipmentTypePredicates;

	/**
	 * Construct a new {@link SingleShipmentTypeEvaluatorStrategy}.
	 *
	 * @param shipmentTypePredicates an ordered mapping of {@link ShipmentType} to {@link ShipmentTypePredicate}.
	 */
	public SingleShipmentTypeEvaluatorStrategy(final Map<ShipmentType, Predicate> shipmentTypePredicates) {
		this.shipmentTypePredicates = shipmentTypePredicates;
	}

	/**
	 * Determine the shipment type that the shopping item corresponds to.
	 *
	 * @param item the shopping item
	 * @return the shipment type that the shopping item corresponds to if a match is found, otherwise an empty set
	 */
	@Override
	public ShipmentType evaluate(final ShoppingItem item) {
		List<ShipmentType> itemShipmentTypes = new ArrayList<ShipmentType>(shipmentTypePredicates.size());

		if (item.isBundle()) {
			throw new InvalidShoppingItemException("Bundles do not have a Shipment Type. Shipping item: " + item.getGuid());
		}

		for (ShipmentType shipmentType : shipmentTypePredicates.keySet()) {
			Predicate shipmentTypePredicate = shipmentTypePredicates.get(shipmentType);
			if (shipmentTypePredicate.evaluate(item)) {
				itemShipmentTypes.add(shipmentType);
			}
		}

		if (itemShipmentTypes.isEmpty()) {
			throw new NoMatchingShipmentTypeFoundException("No shipment types found for shipping item: " + item.getGuid());
		}

		if (itemShipmentTypes.size() > 1) {
			throw new MultipleMatchingShipmentTypesFoundException(String.format("Multiple shipment types %s found for shipping item: %s",
					itemShipmentTypes.toString(),
					item.getGuid()));
		}

		return itemShipmentTypes.get(0);
	}

}
