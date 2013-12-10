/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc;

import com.elasticpath.domain.customer.Customer;

/**
 * Helper for constructing email properties.
 */
public interface CustomerEmailPropertyHelper {

	/**
	 * Returns email properties.
	 * 
	 * @param customer the customer object
	 * 
	 * @return {@link EmailProperties}
	 */
	EmailProperties getPasswordConfirmationEmailProperties(final Customer customer);

	/**
	 * Returns email properties.
	 * 
	 * @param customer the customer object
	 * @param newPassword the new password
	 * @return {@link EmailProperties}
	 */
	EmailProperties getNewlyRegisteredCustomerEmailProperties(final Customer customer, final String newPassword);

	/**
	 * Returns email properties.
	 * 
	 * @param customer the customer object
	 * @param newPassword the new password
	 * @return {@link EmailProperties}
	 */
	EmailProperties getForgottenPasswordEmailProperties(final Customer customer, final String newPassword);

	/**
	 * Gets the email properties on creation of a new customer account.
	 * 
	 * @param customer the customer
	 * @return {@link EmailProperties}
	 */
	EmailProperties getNewAccountEmailProperties(final Customer customer);

}