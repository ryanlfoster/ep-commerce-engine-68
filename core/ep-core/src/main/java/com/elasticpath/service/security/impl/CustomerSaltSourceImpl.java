package com.elasticpath.service.security.impl;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.domain.customer.Customer;

/**
 * Salt Source that gets the salt from the Customer Authentication object.
 */
public class CustomerSaltSourceImpl implements SaltSource {

	@Override
	public Object getSalt(final UserDetails user) {
		return ((Customer) user).getCustomerAuthentication().getSalt();
	}

}
