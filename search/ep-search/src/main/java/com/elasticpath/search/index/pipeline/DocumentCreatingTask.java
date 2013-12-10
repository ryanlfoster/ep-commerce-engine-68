package com.elasticpath.search.index.pipeline;

import org.apache.solr.common.SolrInputDocument;

/**
 * The document creating task is created by the {@code DocumentCreatingTaskFactory} and which is run during the {@code DocumentCreatingStage} of the
 * {@code IndexingPipelineImpl}.
 * <p>
 * It is the job of this task to create a Solr document (likely a {@code SolrInputDocument}).
 * <p>
 * The implementation must be thread-safe.
 * 
 * @param <IN> likely a domain entity, passed in from the previous {@code IndexingStage}
 * @param <OUT> a {@code SolrInputDocument} to be sent for publishing.
 */
public interface DocumentCreatingTask<IN, OUT> extends IndexingTask<IN, SolrInputDocument> {

	/**
	 * Provide the entity to the task which it should create a {@code SolrInputDocument} from.
	 * 
	 * @param payload a domain entity, fetched from the {@code EntityLoadingStage}.
	 */
	void setEntity(IN payload);

}
