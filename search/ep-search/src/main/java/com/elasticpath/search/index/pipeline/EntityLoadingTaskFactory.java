package com.elasticpath.search.index.pipeline;

import java.util.Set;

/**
 * The Factory for creating {@code EntityLoadingTask}s for the {@code IndexingPipelineImpl}. See the Javadoc in the {@code IndexingPipelineImpl}
 * for what the {@code EntityLoadingTask} does.
 * 
 * @param <IN> see {@code EntityLoadingTask}
 * @param <OUT> see {@code EntityLoadingTask}
 */
public interface EntityLoadingTaskFactory<IN, OUT> extends IndexingTaskFactory<Set<Long>, OUT> {
 
	@Override
	EntityLoadingTask<Set<Long>, OUT> create();

}
