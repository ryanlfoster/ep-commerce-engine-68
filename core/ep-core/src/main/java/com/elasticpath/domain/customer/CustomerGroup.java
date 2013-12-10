/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Set;

import com.elasticpath.persistence.api.Entity;

/**
 * <code>CustomerGroup</code> represents a customer group.
 * 
 */
public interface CustomerGroup extends Entity { 
	/** The default customer group name. Every customer is by default in this group. */
	String DEFAULT_GROUP_NAME = "PUBLIC";
	
	/**
	 * Gets the name of this <code>CustomerGroup</code>.
	 * 
	 * @return the name of the customer group.
	 * @domainmodel.property
	 */
	String getName();

	/**
	 * Sets the name for this <code>CustomerGroup</code>.
	 * 
	 * @param name the new user identifier.
	 */
	void setName(final String name);
	
	/**
	 * Gets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 * 
	 * @return the set of customerRoles.
	 *
	 */
	Set<CustomerRole> getCustomerRoles();

	/**
	 * Sets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 * 
	 * @param customerRoles the new set of customerRoles.
	 */
	void setCustomerRoles(final Set<CustomerRole> customerRoles);
	
}
