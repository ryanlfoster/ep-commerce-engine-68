package com.elasticpath.search.index.pipeline;

/**
 * This task is is created by the {@code UidFilteringTaskFactory} and is run during the {@code IndexingPipelineImpl} via the
 * {@code AbstractIndexingStage}.
 * <p>
 * It is the job of this task to remove uids that should not be indexed from the incoming list.
 * <p>
 * The implementation must be thread-safe.
 *
 * @param <IN> a list of uids, including some that may not require indexing
 * @param <OUT> a single list of uids which require indexing
 */
public interface UidFilteringTask<IN, OUT> extends IndexingTask<IN, OUT> {

	/**
	 * @param uids the full list of uids that may require indexing
	 */
	void setUids(IN uids);

}
