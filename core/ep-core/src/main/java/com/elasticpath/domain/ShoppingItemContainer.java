/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Operations that can be performed on a ShoppingItemContainer.
 */
public interface ShoppingItemContainer {

	/**
	 * Returns the shopping item which matches the given GUID.
	 *
	 * @param itemGuid The guid of the shopping item to find.
	 * @return The requested shopping item, or null if it cannot be found.
	 */
	ShoppingItem getShoppingItemByGuid(String itemGuid);

	/**
	 * @return unmodifiable collection of root shopping items in the container.
	 */
	Collection< ? extends ShoppingItem> getRootShoppingItems();

	/**
	 * @return unmodifiable collection of leaf shopping items in the container.
	 */
	Collection< ? extends ShoppingItem> getLeafShoppingItems();

	/**
	 * Return the shipment types that occur on items within the container.
	 *
	 * @return the shipment types
	 */
	Set<ShipmentType> getShipmentTypes();
}
