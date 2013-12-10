package com.elasticpath.service.security.impl;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Transparently upgrades a {@link Customer}'s password encryption by persisting the {@link Customer} on a successful authentication.
 */
public class UpgradingCustomerDaoAuthenticationProvider extends DaoAuthenticationProvider {
	private CustomerService customerService;

	/**
	 * Creates a successful {@link Authentication}, and updates the {@link Customer} as a side effect to perform
	 * a transparent encryption upgrade on the {@link Customer}'s password.
	 * @param principal the principal
	 * @param authentication the authentication
	 * @param user the {@link Customer}
	 * @return the successful {@link Authentication}
	 */
	protected Authentication createSuccessAuthentication(final Object principal, final Authentication authentication, final UserDetails user) {
		String presentedPassword = authentication.getCredentials().toString();
		Customer customer = (Customer) user;
		customer.setClearTextPassword(presentedPassword);
		getCustomerService().update(customer);
		return super.createSuccessAuthentication(principal, authentication, user);
	}

	protected CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}
}
