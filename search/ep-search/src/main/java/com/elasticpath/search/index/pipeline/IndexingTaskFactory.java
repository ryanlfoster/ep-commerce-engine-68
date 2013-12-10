package com.elasticpath.search.index.pipeline;

/**
 * An {@code IndexingTask} is a {@code Runnable} which is used by the {@code AbstractIndexingStage} to perform the work of an {@code IndexingStage}
 * in a configured {@code TaskExecutor}.
 * <p>
 * Factories are used to created these {@code Runnable} objects as they have a prototype bean style lifecycle.
 * <p>
 * Factories are expected to be thread-safe.
 * <p>
 * 
 * @param <IN> a type of input data, refined in implementations and extensions
 * @param <OUT> a type of output data, refined in implementations and extensions
 */
public interface IndexingTaskFactory<IN, OUT> {

	/**
	 * Create the {@code IndexingTask} as per the specific factory. This task must be thread-safe and will be prototype scoped.
	 * 
	 * @return something that implements {@code IndexingTask}
	 */
	IndexingTask<IN, OUT> create();
}
