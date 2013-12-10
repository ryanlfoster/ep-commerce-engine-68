package com.elasticpath.search.index.pipeline;

/**
 * This task is is created by the {@code UidGroupingTaskFactory} and is run during the {@code IndexingPipelineImpl} via the
 * {@code AbstractIndexingStage}.
 * <p>
 * It is the job of this task to split the incoming uids into smaller bite-size batches.
 * <p>
 * The implementation must be thread-safe.
 * 
 * @param <IN> a potentially huge list of uids to be loaded.
 * @param <OUT> multiple smaller sets of uids.
 */
public interface UidGroupingTask<IN, OUT> extends IndexingTask<IN, OUT> {

	/**
	 * @param uids the full list of uids to be broken up.
	 */
	void setUids(IN uids);

}
