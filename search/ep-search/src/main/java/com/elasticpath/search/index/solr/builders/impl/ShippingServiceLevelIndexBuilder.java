package com.elasticpath.search.index.solr.builders.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * An implementation of <code>IndexBuilder</code> to create index for Shipping Service Levels.
 */
public class ShippingServiceLevelIndexBuilder extends AbstractIndexBuilder {

	private ShippingServiceLevelService shippingServiceLevelService;

	private IndexingPipeline<Collection<Long>, ShippingServiceLevel> shippingServiceLevelIndexingPipeline;

	/**
	 * @return index build service name
	 */
	public String getName() {
		return SolrIndexConstants.SHIPPING_SERVICE_LEVEL_CORE;
	}

	@Override
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return shippingServiceLevelService.findUidsByDeletedDate(lastBuildDate);
	}

	@Override
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		return shippingServiceLevelService.findUidsByModifiedDate(lastBuildDate);
	}

	@Override
	public List<Long> findAllUids() {
		return shippingServiceLevelService.findAllUids();
	}

	@Override
	public Collection<Long> findUidsByNotification(final IndexNotification notifications) {
		throw new UnsupportedOperationException("not implemented.");
	}

	@Override
	public IndexType getIndexType() {
		return IndexType.SHIPPING_SERVICE_LEVEL;
	}

	/**
	 * @param shippingServiceLevelService shipping service level service
	 */
	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	@Override
	public void submit(final Collection<Long> uids) {
		shippingServiceLevelIndexingPipeline.start(uids);
	}

	/**
	 * @param shippingServiceLevelIndexingPipeline the shippingServiceLevelIndexingPipeline to set
	 */
	public void setShippingServiceLevelIndexingPipeline(
			final IndexingPipeline<Collection<Long>, ShippingServiceLevel> shippingServiceLevelIndexingPipeline) {
		this.shippingServiceLevelIndexingPipeline = shippingServiceLevelIndexingPipeline;
	}

	/**
	 * @return the shippingServiceLevelIndexingPipeline
	 */
	public IndexingPipeline<Collection<Long>, ShippingServiceLevel> getShippingServiceLevelIndexingPipeline() {
		return shippingServiceLevelIndexingPipeline;
	}
}
