package com.elasticpath.search.index.solr.builders.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * An implementation of <code>IndexBuilder</code> to create index for promotions.
 */
public class PromotionIndexBuilder extends AbstractIndexBuilder {

	private RuleService ruleService;

	private IndexingPipeline<Collection<Long>, Rule> ruleIndexingPipeline;

	/**
	 * Returns index build service name.
	 * 
	 * @return index build service name
	 */
	public String getName() {
		return SolrIndexConstants.PROMOTION_SOLR_CORE;
	}

	/**
	 * Retrieve deleted UIDs.
	 * 
	 * @param lastBuildDate the last build date
	 * @return deleted UIDs.
	 */
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return Collections.emptyList(); // promotion rules cannot be deleted
	}

	/**
	 * Retrieve added or modified UIDs since last build.
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		return ruleService.findUidsByModifiedDate(lastBuildDate);
	}

	/**
	 * Retrieve all UIDs.
	 * 
	 * @return all UIDs
	 */
	public List<Long> findAllUids() {
		return ruleService.findAllUids();
	}

	/**
	 * Publishes updates to the Solr server for the specified {@link Rule} uids.
	 * 
	 * @param uids the {@link Rule} uids to publish.
	 */
	public void submit(final Collection<Long> uids) {
		ruleIndexingPipeline.start(uids);
	}

	/**
	 * Sets the rule service.
	 * 
	 * @param ruleService the rule service
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * Return the index type this class builds.
	 * 
	 * @return the index type this class builds.
	 */
	public IndexType getIndexType() {
		return IndexType.PROMOTION;
	}

	@Override
	public Collection<Long> findUidsByNotification(final IndexNotification notification) {
		throw new UnsupportedOperationException("not supported");
	}

	/**
	 * @param ruleIndexingPipeline the ruleIndexingPipeline to set
	 */
	public void setRuleIndexingPipeline(final IndexingPipeline<Collection<Long>, Rule> ruleIndexingPipeline) {
		this.ruleIndexingPipeline = ruleIndexingPipeline;
	}

	/**
	 * @return the ruleIndexingPipeline
	 */
	public IndexingPipeline<Collection<Long>, Rule> getRuleIndexingPipeline() {
		return ruleIndexingPipeline;
	}

}
