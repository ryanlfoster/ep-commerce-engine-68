/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.store;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>Warehouse</code> represents a warehouse where inventory is stored.
 */
public interface Warehouse extends Persistable {

	/**
	 * Gets the name of this <code>Warehouse</code>.
	 * 
	 * @return the name of this <code>Warehouse</code>.
	 */
	String getName();

	/**
	 * sets the name of this <code>Warehouse</code>.
	 * 
	 * @param name the name of this <code>Warehouse</code>.
	 */
	void setName(final String name);

	/**
	 * Gets the pick delay of this <code>Warehouse</code>. The pick delay is defined as the
	 * amount of time (in minutes) when an order is placed to when it is available for packing in
	 * the warehouse.
	 * 
	 * @return the pick delay (in minutes)
	 */
	int getPickDelay();

	/**
	 * Sets the pick delay of this <code>Warehouse</code>. The pick delay is defined as the
	 * amount of time (in minutes) when an order is placed to when it is available for packing in
	 * the warehouse.
	 * 
	 * @param pickDelay the pick delay (in minutes)
	 */
	void setPickDelay(final int pickDelay);

	/**
	 * Gets the <code>WarehouseAddress</code> of this <code>Warehouse</code>.
	 * 
	 * @return the <code>WarehouseAddress</code> of this <code>Warehouse</code>
	 */
	WarehouseAddress getAddress();

	/**
	 * Sets the <code>WarehouseAddress</code> of this <code>Warehouse</code>.
	 * 
	 * @param address the <code>WarehouseAddress</code> of this <code>Warehouse</code>
	 */
	void setAddress(final WarehouseAddress address);
	
	/**
	 * Gets the unique code associated with the <code>Store</code>.
	 * 
	 * @return the unique code associated with the <code>Store</code>
	 */
	String getCode();
	
	/**
	 * Sets the unique code associated with the <code>Store</code>.
	 * 
	 * @param code the unique code associated with the <code>Store</code>
	 */
	void setCode(final String code);
}
