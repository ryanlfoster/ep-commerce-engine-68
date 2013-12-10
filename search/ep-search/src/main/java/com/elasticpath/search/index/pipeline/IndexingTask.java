package com.elasticpath.search.index.pipeline;

import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * The {@code AbstractIndexingStage} works on the assumption that each stage is handled by a {@code IndexingTask}. This task is created by the
 * {@code IndexingStage} implementation and then the {@code AbstractIndexingStage} will pass that to its own internally configured
 * {@code TaskExecutor}.
 * <p>
 * The {@code IndexingTask} is generated from a Factory and therefore is a prototype bean.
 * <p>
 * The {@code IndexingTask} is expected to send its OUT payload to the next stage.
 * <p>
 * See implementations of {@code IndexingTask} for examples.
 * 
 * @param <IN> a type of input data, refined in implementations and extensions
 * @param <OUT> a type of output data, refined in implementations and extensions
 */
public interface IndexingTask<IN, OUT> extends Runnable {

	/**
	 * Each {@code IndexingTask} is given the next {@code IndexingStage} so that it can send its <b>OUT</b> to the next stage via
	 * {@code IndexingStage#send(Object)}.
	 * 
	 * @param nextStage set usually by {@code AbstractIndexingStage}.
	 */
	void setNextStage(IndexingStage<OUT, ?> nextStage);

	/**
	 * Each {@code IndexingTask} can provide performance counters or timings to the {@code IndexingStatistics}. The {@code PipelinePerformance} is
	 * managed by the specific instance of a {@code IndexingPipeline} and therefore must be injected into the task by the pipeline.
	 * 
	 * @param performance an instace of {@code PipelinePerformance} which can be written to.
	 */
	void setPipelinePerformance(PipelinePerformance performance);
}