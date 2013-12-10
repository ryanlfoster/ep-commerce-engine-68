/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;

/**
 * Form bean for creating new addresses during the checkout.
 */
public interface CheckoutAddressFormBean extends EpFormBean {

	/**
	 * Get a new <code>Address</code> object to store a newly entered address.
	 *
	 * @return a new address object
	 */
	CustomerAddress getNewAddress();

	/**
	 * Set the address object to be used to record a new address.
	 *
	 * @param address the new address
	 */
	void setNewAddress(final CustomerAddress address);

	/**
	 * Returns the currently selected address.
	 *
	 * @return the currently selected address
	 */
	Address getSelectedAddress();

	/**
	 * Set the currently selected address.
	 *
	 * @param address the address
	 */
	void setSelectedAddress(final Address address);

	/**
	 * Set the available addresses that the customer may choose to ship to.
	 *
	 * @param existingAddresses the set of addresses
	 */
	void setExistingAddresses(final List< ? extends Address> existingAddresses);

	/**
	 * Get the available addresses that the customer may choose to ship to.
	 *
	 * @return the set of addresses
	 */
	List< ? extends Address> getExistingAddresses();

	/**
	 *	get the frequency map.
	 * @return frequency/money  map.
	 */
	Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap();

	/**
	 * Returns true if the shipping address is being set. If false, then the billing address is being set.
	 *
	 * @return true for shipping, false for billing
	 */
	boolean isShippingAddress();

	/**
	 * set the frequency map.
	 * @param frequencyMap  a map.
	 */
	void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap);
	
	/**
	 * Set whether the shipping address or billing address is being specified.
	 *
	 * @param shippingAddress true for shipping, false for billing
	 */
	void setShippingAddress(final boolean shippingAddress);
	
}
