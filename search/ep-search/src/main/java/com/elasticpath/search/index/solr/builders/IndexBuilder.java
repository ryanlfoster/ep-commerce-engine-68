package com.elasticpath.search.index.solr.builders;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;

import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.IndexNotificationProcessor;
import com.elasticpath.search.index.solr.IndexBuildEventListener;
import com.elasticpath.service.search.IndexType;

/**
 * An interface for Index Builders.
 */
public interface IndexBuilder {

	/**
	 * Return the index type the subclass builds.
	 * 
	 * @return the index type the subclass builds.
	 */
	IndexType getIndexType();
	
	/**
	 * Returns index build service name.
	 * 
	 * @return index build service name
	 */
	String getName();

	/**
	 * Retrieve all UIDs.
	 * 
	 * @return all UIDs
	 */
	List<Long> findAllUids();
	
	/**
	 * Retrieves a set of all UIDs that represent the given notification.
	 *
	 * @param notifications the notifications
	 * @return a set of all UIDs that represent the given notification
	 */
	Collection<Long> findUidsByNotification(IndexNotification notifications);
		
	/**
	 * Retrieve added or modified UIDs since last build.
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	List<Long> findAddedOrModifiedUids(Date lastBuildDate);
	
	/**
	 * Retrieve deleted UIDs.
	 * 
	 * @param lastBuildDate the last build date
	 * @return deleted UIDs.
	 */
	List<Long> findDeletedUids(Date lastBuildDate);
	
	/**
	 * Action that is performed after the index has been updated. This only occurs when there are
	 * items to process. Override in subclass to perform actions.
	 * 
	 * @param server the server that was used to update the index
	 */
	void onIndexUpdated(SolrServer server);

	/**
	 * Action that is performed before updating the index. This only occurs when there are items
	 * to process. Override in subclass to perform actions.
	 * 
	 * @param server the server that is going to be used to update the index
	 */
	void onIndexUpdating(SolrServer server);
	
	/**
	 * Gets the index builder event listener.
	 * 
	 * @return the index builder event listener
	 */
	IndexBuildEventListener getIndexBuildEventListener();
	
	/**
	 * Gets the index notification processor.
	 * 
	 * @return the index notification processor
	 */
	IndexNotificationProcessor getIndexNotificationProcessor();
	
	
	/**
	 * Request that the {@code IndexBuilder} build the given uids. An index builder is for one specific {@code IndexType} so the type is implicit.
	 * Note that with the introduction of {@code IndexingPipeline} this call will return after the work has been sent to the pipeline.
	 * 
	 * @param uids a {@code Collection} of uids; preferably a {@code Set}, but we're flexible for backwards compatability.
	 */
	void submit(Collection<Long> uids);
}
