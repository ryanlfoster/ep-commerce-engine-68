package com.elasticpath.search.index.pipeline.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.IndexingStatistics;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.pipeline.stats.PipelineStatus;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.pipeline.stats.impl.PipelineStatusImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;

/**
 * This is an implementation of a {@code IndexingPipeline} which breaks the indexing process into five separate stages.
 * <p>
 * <b>Stage One: Grouping.</b> The pipeline starts by being passed a list of uids. The grouping stage simply breaks the big list into smaller lists
 * of uids. These lists are small enough to make fetches for all of them at once.
 * <p>
 * <b>Stage Two: Loading.</b>The small batches of uids are handed to a loading task. This loading task <b>is {@code IndexType} and therefore
 * {@code SolrServer} specific</b>. How the loader fetches these objects is left to to itself, either multiple round trips, sneaky SQL or named
 * query.
 * <p>
 * <b>Stage Three: Document Creation.</b> With the entity loaded, it is sent to the Solr document creation process which is effectively
 * {@code IndexBuilder#createDocument(long)}.
 * <p>
 * <b> Stage Four: Publishing.</b> With the {@code SolrInputDocument} created, it is sent to the {@code SolrDocumentPublisher} to be published in
 * groups in a separate thread.
 * <p>
 * <b> Stage Five: Final Stage.</b> This is an internal stage, implemented by the inner class {@code FinalStage}. It's the sole repsonsibility of
 * this stage to update the {@code PipelineStatus}.
 * <p>
 *
 * @param <INITIALPAYLOAD>
 * @param <ENTITYTYPE>
 */
public class IndexingPipelineImpl<INITIALPAYLOAD, ENTITYTYPE> implements IndexingPipeline<Collection<Long>, ENTITYTYPE> {

	private static final Logger LOG = Logger.getLogger(IndexingPipelineImpl.class);

	private List<IndexingStage<?, ?>> stages = new ArrayList<IndexingStage<?, ?>>();

	private IndexingStage<Collection<Long>, ?> firstStage;

	private PipelinePerformance pipelinePerformance;

	private IndexingStatistics indexingStatistics;

	private IndexType indexType;

	private TimeService timeService;

	private final IndexingStage<Long, Void> finalStage = new FinalStage();

	private IndexBuildStatusDao indexBuildStatusDao;

	private final AtomicBoolean active = new AtomicBoolean(false);

	/**
	 * The final stage of the pipeline pairs up with the {@code IndexingPipeline#start(Object)} method, indicating how many input items have been
	 * output.
	 */
	class FinalStage implements IndexingStage<Long, Void> {

		@Override
		public void send(final Long count) {
			getPipelineStatus().incrementCompletedItems(count);
		}

		@Override
		public void setNextStage(final IndexingStage<Void, ?> nextStage) {
			// no-op
		}

		@Override
		public void setPipelinePerformance(final PipelinePerformance performance) {
			// no-op
		}

		@Override
		public boolean isBusy() {
			return false;
		}

	}

	/**
	 * {@inheritDoc} Connect the stages together in the pipeline.
	 */
	public void initialize() {

		if (CollectionUtils.isEmpty(stages)) {
			throw new IllegalStateException("A list of stages must be provided");
		}

		pipelinePerformance = new PipelinePerformanceImpl();

		indexingStatistics.attachPipelinePerformance(indexType, pipelinePerformance);
		indexingStatistics.attachPipelineStatus(indexType, new PipelineStatusImpl());

		connectStages();

		active.set(true);

	}

	/**
	 * Connects the stages of the pipeline to each other and sets up the start and final stages of the pipeline.
	 */
	@SuppressWarnings("unchecked")
	void connectStages() {
		firstStage = (IndexingStage<Collection<Long>, ?>) stages.get(0);

		int numberOfPairs = stages.size() - 1;

		for (int pairIndex = 0; pairIndex < numberOfPairs; pairIndex++) {
			IndexingStage<?, Object> first = (IndexingStage<?, Object>) stages.get(pairIndex);
			IndexingStage<Object, ?> second = (IndexingStage<Object, ?>) stages.get(pairIndex + 1);

			transition(first, second);
		}

		IndexingStage<?, Long> lastConfigured = (IndexingStage<?, Long>) stages.get(stages.size() - 1);

		transition(lastConfigured, finalStage);
	}

	protected PipelineStatus getPipelineStatus() {
		return indexingStatistics.getPipelineStatus(indexType);
	}

	@Override
	public void destroy() {
		LOG.debug("destroy start");
		if (!active.get()) {
			// There's nothing *wrong* with this, but if it happens, something else may be wrong.
			LOG.info("Destruction of inactive pipeline for " + indexType + " requested.");
			return;
		}

		/**
		 * Lock the pipeline. Obtain the status lock and if it pipeline is busy, wait for it to drain. The lock prevents more work from coming in,
		 * the waitUntilCompleted waits for existing work to complete.
		 */
		getPipelineStatus().getStatusLock().lock();
		try {
			if (isBusy()) {
				try {
					getPipelineStatus().waitUntilCompleted();
				} catch (InterruptedException e) {
					LOG.warn("Interrupted while waiting for pipeline for " + indexType + " to become unbusy, destruction of pipeline is unsafe.");
				}
			}

			/**
			 * We now have an idle pipeline, so we will set it to inactive and do one final flush of its state to the IndexBuildStatus table.
			 */
			active.set(false);
			periodicMonitor();
		} finally {
			getPipelineStatus().getStatusLock().unlock();

		}
		LOG.debug("destroy done");
	}

	@Override
	public boolean isBusy() {
		for (IndexingStage<?, ?> stage : stages) {
			if (stage.isBusy()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Wire up the current stage to the next stage. Also passes in the pipeline performance.
	 *
	 * @param currentStage
	 * @param nextStage
	 */
	private <T> void transition(final IndexingStage<?, T> currentStage, final IndexingStage<T, ?> nextStage) {
		currentStage.setNextStage(nextStage);
		currentStage.setPipelinePerformance(pipelinePerformance);
	}

	@Override
	public void start(final Collection<Long> uids) {
		getPipelineStatus().getStatusLock().lock();
		try {
			if (active.get()) {
				getPipelineStatus().markStarted();
				getPipelineStatus().setLatestIndexingStart(timeService.getCurrentTime());
				getPipelineStatus().incrementIncomingItems(uids.size());

				// at least total must be set here otherwise we get problems updating the processed records
				IndexBuildStatus buildStatus = indexingStatistics.getIndexBuildStatus(indexType);
				if (buildStatus != null) {
					buildStatus.setProcessedRecords(0);
					buildStatus.setTotalRecords(uids.size());
					indexBuildStatusDao.saveOrUpdate(buildStatus);
				}

				firstStage.send(uids);
			} else {
				LOG.warn("Pipeline for " + indexType + " is no longer active, and therefore rejected work on:" + uids);
				throw new IllegalStateException("Pipeline for " + indexType + " is no longer active, and therefore rejected work on:" + uids);
			}
		} finally {
			getPipelineStatus().getStatusLock().unlock();
		}
	}

	/**
	 * Called periodically in a separate set of threads to update the completion status and time as well as the records being processed via
	 * {@code IndexBuildStatusDao}.
	 */
	public void periodicMonitor() {
		boolean announceCompleted = false;
		IndexBuildStatus indexBuildStatus = null;

		PipelineStatus pipelineStatus = getPipelineStatus();

		/*
		 * Before trying to check the status of the pipeline we acquire the "status lock". This lock is also acquired by the start method.
		 */
		pipelineStatus.getStatusLock().lock();
		try {
			indexBuildStatus = indexingStatistics.getIndexBuildStatus(indexType);

			/**
			 * If the pipeline is busy then we provide new progress numbers to the IndexBuildStatus.
			 */
			if (isBusy() && indexBuildStatus != null) {
				updateIndexBuildStatus(indexBuildStatus, pipelineStatus);
			}

			/*
			 * If the pipeline is not busy and we haven't yet done something with the "latest indexing start", then we move that timestamp into the
			 * "completion date" and mark that we need to announce that we've completed things. Also ensure the index status is updated.
			 *
			 * The <b>start time</b> is used as the <b>completion time</b>. It's a bit confusing, but it means that it can be used on startup to say
			 * "get me data which has changed since <b>completion time</b>." and pick up any new changes.
			 */
			if (pipelineStatus.getLatestIndexingStart() != null && !isBusy()) {
				pipelineStatus.setCompletionDate(pipelineStatus.getLatestIndexingStart());
				pipelineStatus.setLatestIndexingStart(null);
				if (indexBuildStatus != null) {
					updateIndexBuildStatus(indexBuildStatus, pipelineStatus);
				}
				announceCompleted = true;
			}
		} finally {
			pipelineStatus.getStatusLock().unlock();
		}

		/*
		 * If we're done, then use {@code PipelineStatus#notifyCompleted} to let everyone know.
		 */
		if (announceCompleted) {
			LOG.debug("Pipeline for " + indexType + " finished processing work from " + pipelineStatus.getCompletionDate());
			pipelineStatus.notifyCompleted();
		}

		/*
		 * We've now released the status lock, so lets do database work. If indexBuildStatus is set, then it's been updated, so lets persist it.
		 */
		if (indexBuildStatus != null) {
			indexBuildStatusDao.saveOrUpdate(indexBuildStatus);
		}
	}

	/**
	 * Update index build status.
	 *
	 * @param indexBuildStatus the index build status
	 * @param pipelineStatus the pipeline status
	 */
	private void updateIndexBuildStatus(final IndexBuildStatus indexBuildStatus, final PipelineStatus pipelineStatus) {
		long cumulativeTotal = pipelineStatus.getIncomingCount();
		long cumulativeProcessed = pipelineStatus.getCompletedCount();

		/*
		 * We don't want to use the cumulative totals here otherwise consumers of IndexBuildStatus will get skewed
		 * results, i.e., when there has been a large number of records processed before this call, but we only want
		 * to process a small number of them.
		 */
		int processed = (int) (indexBuildStatus.getTotalRecords() - (cumulativeTotal - cumulativeProcessed));
		indexBuildStatus.setProcessedRecords(processed);
	}

	public void setStages(final List<IndexingStage<?, ?>> stages) {
		this.stages = stages;
	}

	public void setIndexingStatistics(final IndexingStatistics stats) {
		this.indexingStatistics = stats;
	}

	public void setIndexType(final IndexType indexType) {
		this.indexType = indexType;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setIndexBuildStatusDao(final IndexBuildStatusDao indexBuildStatusDao) {
		this.indexBuildStatusDao = indexBuildStatusDao;
	}

}
