package com.elasticpath.search.index.solr.builders.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * An implementation of <code>IndexBuilder</code> to create index for category.
 */
public class CmUserIndexBuilder extends AbstractIndexBuilder {

	private CmUserService cmUserService;

	private IndexingPipeline<Collection<Long>, CmUser> cmUserIndexingPipeline;

	/**
	 * Publishes updates to the Solr server for the specified {@link CmUser} uids.
	 * 
	 * @param uids the {@link CmUser} uids to publish.
	 */
	public void submit(final Collection<Long> uids) {
		cmUserIndexingPipeline.start(uids);
	}

	@Override
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		return cmUserService.findUidsByModifiedDate(lastBuildDate);
	}

	@Override
	public List<Long> findAllUids() {
		return cmUserService.findAllUids();
	}

	@Override
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return Collections.emptyList();
	}

	@Override
	public Collection<Long> findUidsByNotification(final IndexNotification notifications) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public IndexType getIndexType() {
		return IndexType.CMUSER;
	}

	@Override
	public String getName() {
		return SolrIndexConstants.CMUSER_SOLR_CORE;
	}

	/**
	 * @return the cmUserService
	 */
	public CmUserService getCmUserService() {
		return cmUserService;
	}

	/**
	 * @param cmUserService the cmUserService to set
	 */
	public void setCmUserService(final CmUserService cmUserService) {
		this.cmUserService = cmUserService;
	}

	/**
	 * Sets the {@link IndexingPipeline} for {@link CmUser}.
	 * 
	 * @param cmUserIndexingPipeline the {@link IndexingPipeline} for {@link CmUser}
	 */
	public void setCmUserIndexingPipeline(final IndexingPipeline<Collection<Long>, CmUser> cmUserIndexingPipeline) {
		this.cmUserIndexingPipeline = cmUserIndexingPipeline;
	}

	/**
	 * Gets the {@link IndexingPipeline} for {@link CmUser}.
	 * 
	 * @return the {@link IndexingPipeline} for {@link CmUser}
	 */
	public IndexingPipeline<Collection<Long>, CmUser> getCmUserIndexingPipeline() {
		return cmUserIndexingPipeline;
	}
}
