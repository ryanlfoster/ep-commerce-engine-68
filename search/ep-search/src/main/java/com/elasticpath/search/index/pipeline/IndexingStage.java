package com.elasticpath.search.index.pipeline;

import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * This represents a stage in an {@code IndexingPipeline}. Each {@code IndexStage} is responsble for performing a particular task and passing its
 * result to the next stage. This interface is generified to take IN and OUT. A stage is performed by sending a payload to it. This payload is of
 * type IN. The {@code IndexStage} will send its results to the next {@code IndexStage} as defined by {@code #setNextStage(IndexingStage)}. The next
 * stage uses the currents stage OUT as its IN.
 * <p>
 * An {@code IndexingStage} <b>may</b> call {@code #setNextStage(IndexingStage)} as many times as needed.
 * <p>
 * IndexStages are expected to be threadsafe.
 * 
 * @param <IN> Any type that an implementation of IndexingStage is expecting.
 * @param <OUT> Any type that is generated by the current IndexStage.
 */
public interface IndexingStage<IN, OUT> {

	/**
	 * Callers to this stage send the <b>IN</b> payload to this method. This method is to {@code IndexingStage} what execute is to {@code Runnable}.
	 * 
	 * @param payload sent in from a previous IndexingStage.
	 */
	void send(IN payload);

	/**
	 * Each stage has a reference to its next stage. The input to the next stage is the output of the current one, that is to say the <b>OUT</b> of
	 * this stage is the <b>IN</b> of the next stage.
	 * 
	 * @param nextStage generally wired in via the {@code IndexingPipeline} - {@see IndexingPipelineImpl#initialize()}.
	 */
	void setNextStage(IndexingStage<OUT, ?> nextStage);

	/**
	 * An {@code IndexingPipeline} is responsible for handing the {@code PipelinePerformance} to the individual {@code IndexingStage}s.
	 * 
	 * @param performance an initialised {@code PipelinePerformance} associated with the pipeline.
	 */
	void setPipelinePerformance(PipelinePerformance performance);

	/**
	 * Is the current stage "busy". This is a soft term to help decide if the pipeline is "done" or not, or if it's safe to shutdown. This method may
	 * return false positives (claiming to be busy when it's not), but mustn't always return true. If a stage has a backlog of work, it is considered
	 * to also be busy.
	 * 
	 * @return see above.
	 */
	boolean isBusy();
}