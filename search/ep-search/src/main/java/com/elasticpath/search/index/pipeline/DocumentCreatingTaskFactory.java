package com.elasticpath.search.index.pipeline;

import org.apache.solr.common.SolrInputDocument;

/**
 * This factory creates {@code DocumentCreatingTask}s for the {@code IndexingPipelineImpl}.
 * 
 * @param <IN> see {@code DocumentCreatingTask}
 * @param <OUT> see {@code DocumentCreatingTask}
 */
public interface DocumentCreatingTaskFactory<IN, OUT> extends IndexingTaskFactory<IN, SolrInputDocument> {

	@Override
	DocumentCreatingTask<IN, SolrInputDocument> create();
}
