package com.elasticpath.search.index.pipeline.impl;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.IndexingTask;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * A simple implementation of a {@code IndexingStage} which assumes it should create an {@code IndexingTask} and pass it to its own configured
 * {@code TaskExecutor}. In this case, the {@code IndexingTask} is responsible for passing the data to the next stage.
 * <p>
 * {@code IndexingStage}s are expected to be thread-safe.
 * 
 * @param <IN> An input payload, passed to {@code #send(Object)}.
 * @param <OUT> an output payload, passed as input to the next {@code IndexingStage}.
 */
public abstract class AbstractIndexingStage<IN, OUT> implements IndexingStage<IN, OUT> {
	private static final Logger LOG = Logger.getLogger(AbstractIndexingStage.class);
	private IndexingStage<OUT, ?> nextStage;

	private PipelinePerformance performance;

	private TaskExecutor taskExecutor;

	public void setNextStage(final IndexingStage<OUT, ?> nextStage) {
		this.nextStage = nextStage;
	}

	/**
	 * Concrete implementations of this class are required to implement this method. In it, you are given the specified <b>IN</b> payload and and
	 * must configure your {@code IndexingTask} and return it. You <b>are not</b> responsible for setting the next stage on the {@code IndexingTask}
	 * 
	 * @param payload payload, as sent in from the previous {@code IndexingStage}.
	 * @return a {@code IndexingTask}
	 */
	abstract IndexingTask<IN, OUT> create(IN payload);

	/**
	 * A task wrapper to catch errors inside tasks.
	 * 
	 * @param <IN> input type
	 * @param <OUT> output type
	 */
	private static class LogWrappedIndexingTask<IN, OUT> implements IndexingTask<IN, OUT> {
		private final IndexingTask<IN, OUT> wrappedTask;

		public LogWrappedIndexingTask(final IndexingTask<IN, OUT> wrappedTask) {
			this.wrappedTask = wrappedTask;
		}

		@Override
		public void run() {
			try {
				wrappedTask.run();
			} catch (RuntimeException e) {
				LOG.error("Error executing indexing task", e);
				throw e;
			}
		}

		@Override
		public void setNextStage(final IndexingStage<OUT, ?> nextStage) {
			wrappedTask.setNextStage(nextStage);
		}

		@Override
		public void setPipelinePerformance(final PipelinePerformance performance) {
			wrappedTask.setPipelinePerformance(performance);
		}
	}

	@Override
	public void send(final IN payload) {
		final IndexingTask<IN, OUT> task = create(payload);
		task.setPipelinePerformance(performance);
		task.setNextStage(nextStage);

		taskExecutor.execute(new LogWrappedIndexingTask<IN, OUT>(task));
	}

	/**
	 * {@inheritDoc} This implementation knows how to look into {@code SyncTaskExecutor} and {@code ThreadPoolTaskExecutor} to determine if they're
	 * "busy" or not.
	 */
	public boolean isBusy() {

		if (taskExecutor instanceof SyncTaskExecutor) {
			return false;
		} else if (taskExecutor instanceof ThreadPoolTaskExecutor) {
			ThreadPoolExecutor executor = ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor();
			return !executor.getQueue().isEmpty() || executor.getActiveCount() != 0;
		} else {
			throw new EpSystemException("The implementation of the taskExecutor (which is " + taskExecutor.getClass()
					+ ") for this class is not known, so we don't know how to check if it's busy or not.");
		}
	}

	public void setTaskExecutor(final TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setPipelinePerformance(final PipelinePerformance performance) {
		this.performance = performance;
	}

	public PipelinePerformance getPipelinePerformance() {
		return performance;
	}

}