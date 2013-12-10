/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.domain.shipping.evaluator;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Strategy to manage the Evaluation of ShipmentTypes on {@link ShoppingItem}.
 */
public interface ShipmentTypeEvaluatorStrategy {

	/**
	 * Evaluate a ShoppingItem to determine the shipment types it matches against.
	 *
	 * @param item the item
	 * @return the shipment types that match against the shopping item
	 */
	ShipmentType evaluate(ShoppingItem item);
}
