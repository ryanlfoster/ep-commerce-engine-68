/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents the customer Authentication.
 */
public interface CustomerAuthentication extends Persistable {

	/**
	 * Gets the encrypted password.
	 *
	 * @return the encrypted password.
	 */
	String getPassword();

	/**
	 * Sets the encrypted password.
	 *
	 * @param password the encrypted password.
	 */
	void setPassword(String password);

	/**
	 * Get the salt value used in password encoding.
	 *
	 * @return the salt.
	 */
	String getSalt();

	/**
	 * Set the salt value used in password encoding.
	 *
	 * @param salt the salt.
	 */
	void setSalt(String salt);

	/**
	 * Sets the clear-text password. <br>
	 * The password will be encrypted using a secure hash like MD5 or SHA1 and saved as password.
	 *
	 * @param clearTextPassword the clear-text password.
	 */
	void setClearTextPassword(String clearTextPassword);

	/**
	 * Gets the clear-text password (only available at creation time).
	 *
	 * @domainmodel.property
	 * @return the clear-text password.
	 */
	String getClearTextPassword();

	/**
	 * Reset the customer's password.
	 *
	 * @return the reseted password
	 */
	String resetPassword();
}
