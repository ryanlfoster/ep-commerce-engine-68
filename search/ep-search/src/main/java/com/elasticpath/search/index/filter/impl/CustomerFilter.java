package com.elasticpath.search.index.filter.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.search.index.pipeline.AbstractIndexingTask;
import com.elasticpath.search.index.pipeline.UidFilteringTask;
import com.elasticpath.service.customer.CustomerService;

/**
 * Filters uids that should not be searchable out of a given list of uids.
 */
public class CustomerFilter extends AbstractIndexingTask<Collection<Long>, Collection<Long>>
		implements UidFilteringTask<Collection<Long>, Collection<Long>> {

	private final Set<Long> uids = new HashSet<Long>();

	private CustomerService customerService;

	@Override
	public void run() {
		validateConfiguration();
		getPipelinePerformance().addCount("filter:uids_in", uids.size());

		if (uids.isEmpty()) {
			return;
		}

		final long start = System.currentTimeMillis();

		Collection<Long> filteredUids = getCustomerService().filterSearchable(uids);
		getNextStage().send(filteredUids);

		getPipelinePerformance().addValue("filter:filter_time", System.currentTimeMillis() - start);
		getPipelinePerformance().addCount("filter:uids_out", filteredUids.size());
	}

	@Override
	public void setUids(final Collection<Long> uids) {
		this.uids.addAll(uids);
	}

	private void validateConfiguration() {
		if (getNextStage() == null) {
			throw new IllegalArgumentException("Next stage must be set.");
		}
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
