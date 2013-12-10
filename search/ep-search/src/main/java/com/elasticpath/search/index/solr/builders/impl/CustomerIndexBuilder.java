package com.elasticpath.search.index.solr.builders.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * An implementation of <code>IndexBuilder</code> to create index for customer.
 */
public class CustomerIndexBuilder extends AbstractIndexBuilder {

	private CustomerService customerService;

	private IndexingPipeline<Collection<Long>, Customer> customerIndexingPipeline;

	/**
	 * Returns index build service name.
	 * 
	 * @return index build service name
	 */
	public String getName() {
		return SolrIndexConstants.CUSTOMER_SOLR_CORE;
	}

	/**
	 * Retrieve deleted UIDs.
	 * 
	 * @param lastBuildDate the last build date
	 * @return deleted UIDs.
	 */
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return customerService.findUidsByDeletedDate(lastBuildDate);
	}

	/**
	 * Retrieve added or modified UIDs since last build.
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		return customerService.findUidsByModifiedDate(lastBuildDate);
	}

	/**
	 * Retrieve all UIDs.
	 * 
	 * @return all UIDs
	 */
	public List<Long> findAllUids() {
		return customerService.findAllUids();
	}

	/**
	 * Get the customer from the persistence layer.
	 * 
	 * @param uid the UID for the customer
	 * @return the customer with the given UID, or null if one cannot be found
	 */
	protected Customer getCustomer(final long uid) {
		return customerService.get(uid);
	}

	/**
	 * Publishes updates to the Solr server for the specified {@link Customer} uids.
	 * 
	 * @param uids the {@link Customer} uids to publish.
	 */

	public void submit(final Collection<Long> uids) {
		customerIndexingPipeline.start(uids);
	}

	/**
	 * Sets the customer service.
	 * 
	 * @param customerService the customer service
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Return the index type this class builds.
	 * 
	 * @return the index type this class builds.
	 */
	public IndexType getIndexType() {
		return IndexType.CUSTOMER;
	}

	@Override
	public Collection<Long> findUidsByNotification(final IndexNotification notification) {
		throw new UnsupportedOperationException("not supported");
	}

	/**
	 * @param customerIndexingPipeline the customerIndexingPipeline to set
	 */
	public void setCustomerIndexingPipeline(final IndexingPipeline<Collection<Long>, Customer> customerIndexingPipeline) {
		this.customerIndexingPipeline = customerIndexingPipeline;
	}

	/**
	 * @return the customerIndexingPipeline
	 */
	public IndexingPipeline<Collection<Long>, Customer> getCustomerIndexingPipeline() {
		return customerIndexingPipeline;
	}
}
