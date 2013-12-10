package com.elasticpath.search.index.solr.builders.impl;

import java.util.Collection;

import org.apache.solr.client.solrj.SolrServer;

import com.elasticpath.search.IndexNotificationProcessor;
import com.elasticpath.search.index.solr.IndexBuildEventListener;
import com.elasticpath.search.index.solr.builders.IndexBuilder;

/**
 * A skeleton implementation of <code>IndexBuild</code>.
 */
public abstract class AbstractIndexBuilder implements IndexBuilder {

	private IndexNotificationProcessor indexNotificationProcessor;

	/**
	 * Action that is performed after the index has been updated. This only occurs when there are items to process. Override in subclass to perform
	 * actions.
	 *
	 * @param server the server that was used to update the index
	 */
	public void onIndexUpdated(final SolrServer server) {
		// subclass to perform actions
	}

	/**
	 * Action that is performed before updating the index. This only occurs when there are items to process. Override in subclass to perform actions.
	 *
	 * @param server the server that is going to be used to update the index
	 */
	public void onIndexUpdating(final SolrServer server) {
		// subclass to perform actions
	}

	@Override
	public IndexBuildEventListener getIndexBuildEventListener() {
		return null; // default implementation
	}

	@Override
	public IndexNotificationProcessor getIndexNotificationProcessor() {
		return indexNotificationProcessor;
	}

	/**
	 * Sets the {@link IndexNotificationProcessor} instance to use.
	 *
	 * @param indexNotificationProcessor the {@link IndexNotificationProcessor} instance to use
	 */
	public void setIndexNotificationProcessor(final IndexNotificationProcessor indexNotificationProcessor) {
		this.indexNotificationProcessor = indexNotificationProcessor;
	}

	@Override
	public abstract void submit(Collection<Long> uids);
}
