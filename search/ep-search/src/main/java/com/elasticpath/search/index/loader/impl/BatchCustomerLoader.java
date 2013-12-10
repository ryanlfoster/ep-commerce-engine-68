package com.elasticpath.search.index.loader.impl;

import java.util.Collection;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Fetches a batch of {@link Customer}s.
 */
public class BatchCustomerLoader extends AbstractEntityLoader<Customer> {

	private CustomerService customerService;

	/**
	 * Loads the {@link Customer}s for the batched ids and loads each batch in bulk.
	 * 
	 * @return the loaded {@link Customer}s
	 */
	public Collection<Customer> loadBatch() {

		Collection<Customer> loadedCustomerBatch = getCustomerService().findByUids(getUidsToLoad());

		return loadedCustomerBatch;
	}

	/**
	 * @param customerService the customerService to set
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * @return the customerService
	 */
	public CustomerService getCustomerService() {
		return customerService;
	}

}
