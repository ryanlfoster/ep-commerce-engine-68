package com.elasticpath.domain.customer.impl;

import java.util.Collections;
import java.util.Set;

import com.elasticpath.domain.customer.Customer;

/**
 * Maps roles onto a Customer.
 */
public class CustomerRoleMapper {


	/** Customer role. */
	public static final String REGISTERED = "REGISTERED";

	/** Public role. */
	public static final String PUBLIC = "PUBLIC";



	private final Customer customer;
	
	/**
	 * Instantiates a new customer role mapper.
	 *
	 * @param customer the customer
	 */
	public CustomerRoleMapper(final Customer customer) {
		this.customer = customer;
	}
	
	/**
	 * Checks if the Customer associated with this mapper has the given role.
	 *
	 * @param role the role
	 * @return true, if Customer has the given role, false otherwise.
	 */
	public boolean hasRole(final String role) {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null.");
		}
		return getAllRoles().contains(role);
	}
	
	/**
	 * Gets all roles for this Customer.
	 *
	 * @return the roles
	 */
	public Set<String> getAllRoles() {
		return Collections.singleton(determineRole());
	}
	
	private String determineRole() {
		if (customer.isAnonymous()) {
			return PUBLIC;
		} else {
			return REGISTERED;
		}
	}
}
