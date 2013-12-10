package com.elasticpath.domain.customer;

import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>CustomerRole</code> represents a customer's role.
 *
 *
 */
public interface CustomerRole extends GrantedAuthority, Persistable {

	/**
	 * Initializes the <code>CustomerRole</code> object given its authority.
	 * Call setElasticPath before initializing.
	 */
	void init();

	/**
	 * Gets the authority for this <code>CustomerRole</code>.
	 *
	 * @return the authority as an identifier of the customer's role.
	 */
	String getAuthority();

	/**
	 * Sets the authority for this <code>CustomerRole</code>.
	 *
	 * @param authority the identifier of the customer's role.
	 */
	void setAuthority(final String authority);

	/**
	 * Copy the customer role properties.
	 *
	 * @param customerRole the customer role copy from
	 */
	void copyFrom(final CustomerRole customerRole);
}

