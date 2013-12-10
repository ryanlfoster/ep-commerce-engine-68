/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shoppingcart;

import java.util.List;

/**
 * Represents collections of each shipment type.
 */
public interface ShipmentTypeCollections {

	/**
	 * @return An unmodifiable list of electronic skus.
	 */
	List<ShoppingItem> getElectronicSkus();

	/**
	 * @return An unmodifiable list of physical skus.
	 */
	List<ShoppingItem> getPhysicalSkus();

	/**
	 * @return An unmodifiable list of service skus.
	 */
	List<ShoppingItem> getServiceSkus();

}
