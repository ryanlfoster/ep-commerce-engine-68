package com.elasticpath.search.index.pipeline;

/**
 * This factory creates {@code UidGroupingTask}s for the {@code IndexingPipelineImpl}. See the Javadoc in {@code IndexingPipelineImpl} for more
 * information.
 * 
 * @param <IN> likely a List<Long> of uids
 * @param <OUT> multiple smaller List<Long> of uids
 */
public interface UidGroupingTaskFactory<IN, OUT> extends IndexingTaskFactory<IN, OUT> {

	@Override
	UidGroupingTask<IN, OUT> create();
}
