package com.elasticpath.search.index.pipeline;

import java.util.Set;

/**
 * Implementors of this task are required to preload the entities requested in a thread-safe manner. See the description in
 * {@code IndexingPipelineImpl} for more details. These tasks are created from the {@code EntityLoadingTaskFactory}
 * 
 * @param <IN> a collection of uids
 * @param <OUT> a single entity, this task will call the next stage once for each entity retrieved.
 */
public interface EntityLoadingTask<IN, OUT> extends IndexingTask<Set<Long>, OUT> {

	/**
	 * Set the sublist of uids which this task should be responsible for loading.
	 * 
	 * @param payload a collection of uids which can be loaded in some optimized fashion.
	 */
	void setBatch(Set<Long> payload);

}
