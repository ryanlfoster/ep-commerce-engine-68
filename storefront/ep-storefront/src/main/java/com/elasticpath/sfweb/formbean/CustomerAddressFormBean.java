/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

import com.elasticpath.domain.customer.CustomerAddress;

/**
 * Form bean for creating new addresses during the checkout.
 */
public interface CustomerAddressFormBean extends EpFormBean {

	/**
	 * Get a new <code>CustomerAddress</code> object to store a newly entered address.
	 *
	 * @return a customer address object
	 */
	CustomerAddress getCustomerAddress();

	/**
	 * Set the address object to be used to record a new address.
	 *
	 * @param address the new address
	 */
	void setCustomerAddress(final CustomerAddress address);

	/**
	 * Returns true if the preferred billing address is being set.
	 *
	 * @return true for preferred billing address
	 */
	boolean isPreferredBillingAddress();

	/**
	 * Set whether the preferred billing address is being specified.
	 *
	 * @param preferredBillingAddress true for preferred billing address
	 */
	void setPreferredBillingAddress(final boolean preferredBillingAddress);
	
	/**
	 * Get the address uidpk, if editing a new existing address, this uidPk should be same uidPk of that address in session,
	 * if creating a new address, it should be zero.
	 *
	 * @return address object uidPk
	 */
	long getAddressUidPk();

	/**
	 * Set the address uidPk.
	 *
	 * @param addressUidPk the customer address uidPk
	 */
	void setAddressUidPk(final long addressUidPk);
	
}
