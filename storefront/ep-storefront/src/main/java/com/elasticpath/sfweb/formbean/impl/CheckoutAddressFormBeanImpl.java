/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.sfweb.formbean.CheckoutAddressFormBean;

/**
 * Form bean for creating new addresses during the checkout.
 */
public class CheckoutAddressFormBeanImpl extends EpFormBeanImpl implements CheckoutAddressFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private CustomerAddress newAddress;
	private Address selectedAddress;
	private List< ? extends Address> existingAddresses;
	private boolean isShippingAddress = false; // NOPMD
	private Map<Quantity, FrequencyAndRecurringPrice> frequencyMap;

	/**
	 * Get a new <code>Address</code> object to store
	 * a newly entered address.
	 * @return a new address object
	 */
	public CustomerAddress getNewAddress() {
		return newAddress;
	}

	/**
	 * Set the address object to be used to record a new
	 * address.
	 * @param address the new address
	 */
	public void setNewAddress(final CustomerAddress address) {
		this.newAddress = address;
	}

	/**
	 * Returns the currently selected address.
	 *
	 * @return the currently selected address
	 */
	@Override
	public Address getSelectedAddress() {
		return selectedAddress;
	}

	/**
	 * Set the currently selected address.
	 *
	 * @param address the address
	 */
	@Override
	public void setSelectedAddress(final Address address) {
		selectedAddress = address;
	}

	/**
	 * Set the available addresses that the customer may
	 * choose to ship to.
	 * @param existingAddresses the list of addresses
	 */
	public void setExistingAddresses(final List< ? extends Address> existingAddresses) {
		this.existingAddresses = existingAddresses;
	}

	/**
	 * Get the available addresses that the customer may
	 * choose to ship to.
	 * @return the list of addresses
	 */
	public List< ? extends Address> getExistingAddresses() {
		return existingAddresses;
	}

	@Override
	public Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap() {
		return this.frequencyMap;
	}

	/**
	 * Returns true if the shipping address is being set. If false, then
	 * the billing address is being set.
	 * @return true for shipping, false for billing
	 */
	public boolean isShippingAddress() {
		return this.isShippingAddress;
	}
	
	@Override
	public void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}

	/**
	 * Set whether the shipping address or billing address is being specified.
	 * @param shippingAddress true for shipping, false for billing
	 */
	public void setShippingAddress(final boolean shippingAddress) {
		this.isShippingAddress = shippingAddress;
	}
}
