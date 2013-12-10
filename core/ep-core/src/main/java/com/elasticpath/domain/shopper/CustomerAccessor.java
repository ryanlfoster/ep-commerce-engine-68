package com.elasticpath.domain.shopper;

import com.elasticpath.domain.customer.Customer;

/**
 * Provides access to a {@link Customer} on the implementing object. 
 */
public interface CustomerAccessor {

	/**
	 * Gets a {@link Customer}.
	 *
	 * @return a {@link Customer}.
	 */
	Customer getCustomer();

	/**
	 * Sets a {@link Customer}.
	 *
	 * @param customer the {@link Customer}.
	 */
	void setCustomer(Customer customer);

	/**
	 * Set to true if the {@link Customer} is signed in.
	 *
	 * @param signedIn set to true if the {@link Customer} is signed in.
	 */
	void setSignedIn(boolean signedIn);

	/**
	 * Returns true if the {@link Customer} is signed in.
	 *
	 * @return true if the {@link Customer} is signed in.
	 */
	boolean isSignedIn();
	
}