/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.sfweb.formbean.CustomerAddressFormBean;

/**
 * Form bean for creating new addresses during the checkout.
 *
 */
public class CustomerAddressFormBeanImpl extends EpFormBeanImpl implements CustomerAddressFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private CustomerAddress customerAddress;
	private boolean preferredBillingAddress = false;
	private long addressUidPk = 0;

	/**
	 * Get a <code>CustomerAddress</code> object to store a newly entered address.
	 *
	 * @return a customer address object
	 */
	public CustomerAddress getCustomerAddress() {
		return this.customerAddress;
	}

	/**
	 * Set the address object to be used to record a new address.
	 *
	 * @param customerAddress the customer address
	 */
	public void setCustomerAddress(final CustomerAddress customerAddress) {
		this.customerAddress = customerAddress;
	}

	/**
	 * Returns true if the preferred billing address is being set.
	 *
	 * @return true for preferred billing address
	 */
	public boolean isPreferredBillingAddress() {
		return this.preferredBillingAddress;
	}

	/**
	 * Set whether the preferred billing address is being specified.
	 *
	 * @param preferredBillingAddress true for preferred billing address
	 */
	public void setPreferredBillingAddress(final boolean preferredBillingAddress) {
		this.preferredBillingAddress = preferredBillingAddress;
	}
	
	/**
	 * Get the address uidpk, if editing a new existing address, this uidPk should be same uidPk of that address in session,
	 * if creating a new address, it should be zero.
	 *
	 * @return address object uidPk
	 */
	public long getAddressUidPk() {
		return this.addressUidPk;
	}

	/**
	 * Set the address uidPk.
	 *
	 * @param addressUidPk the customer address uidPk
	 */
	public void setAddressUidPk(final long addressUidPk) {
		this.addressUidPk = addressUidPk;
	}

}
