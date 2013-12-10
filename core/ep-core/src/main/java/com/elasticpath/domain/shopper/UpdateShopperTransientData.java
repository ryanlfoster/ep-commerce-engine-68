package com.elasticpath.domain.shopper;

import com.elasticpath.domain.customer.CustomerSession;

/**
 * An interface to update any of the transient data on {@link CustomerSession}. 
 */
public interface UpdateShopperTransientData {

	/**
	 * Updates transient data on {@link CustomerSession} that comes from {@link CustomerSession}.
	 *
	 * @param customerSession {@link CustomerSession} which contains the transient data that {@link CustomerSession} requires.
	 */
	void updateTransientDataWith(CustomerSession customerSession);

	/**
	 * Update the transient data on this {@link CustomerSession} that is associated with its {@link com.elasticpath.domain.customer.Customer}. 
	 */
	void updateTransientDataForCustomer();
	
}
