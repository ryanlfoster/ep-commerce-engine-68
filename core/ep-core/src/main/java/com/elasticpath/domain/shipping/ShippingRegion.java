/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.persistence.api.Entity;

/**
 * A ShippingRegion represents a region that will be associated with one or more shipping services.
 * For now, it is composed of country and a subcountry, i.e. CA(country) and BC(subcountry).
 */
public interface ShippingRegion extends Entity, Comparable<ShippingRegion> {
	/**
	 * Get the shipping region name.
	 * @return the parameter name
	 */
	String getName();	
	
	/**
	 * Set the shipping region name.
	 * @param name the parameter name
	 */
	void setName(final String name);
	
	/**
	 * Get the Map of regions assoicated with this shippingregion. 
	 * The entry of the regionMap is countryCode -> <code>Region</code>.
	 * @return the map of regions assoicated with this shippingregion.
	 */
	Map<String, Region> getRegionMap();
	
	/**
	 * Set the Map of regions assoicated with this shippingregion.
	 * @param regionMap the map of regions to be assoicated with this shippingregion.
	 */
	void setRegionMap(final Map<String, Region> regionMap);
	
	/**
	 * Check if the given shippingAdress is in the range of this <code>ShippingRegion</code>.
	 * @param shippingAddress the shippingAddress to be evaluated.
	 * @return status of whether the given shippingAdress is in the range of this <code>ShippingRegion</code>.
	 */
	boolean isInShippingRegion(final Address shippingAddress);
	
}
