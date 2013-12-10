package com.elasticpath.search.index.pipeline;

import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * A simple class from which {@code IndexingTask}s may extend. Takes care of setting the nextStage and {@code PipelinePerformance} injection.
 * 
 * @author idcmp
 * @param <IN> see {@code IndexingTask}
 * @param <OUT> see {@code IndexingTask}
 */
public abstract class AbstractIndexingTask<IN, OUT> implements IndexingTask<IN, OUT> {

	private IndexingStage<OUT, ?> nextStage = null;

	private PipelinePerformance performance;

	public void setNextStage(final IndexingStage<OUT, ?> nextStage) {
		this.nextStage = nextStage;
	}

	public void setPipelinePerformance(final PipelinePerformance performance) {
		this.performance = performance;
	}

	public PipelinePerformance getPipelinePerformance() {
		return this.performance;
	}

	public IndexingStage<OUT, ?> getNextStage() {
		return this.nextStage;
	}

}
