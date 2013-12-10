package com.elasticpath.search.index.pipeline.stats;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.service.search.IndexType;

/**
 * Singleton which keeps an eye on all the various metainformation relating to <b>all</b> {@code IndexingPipeline}s.
 */
public interface IndexingStatistics {

	/**
	 * Return the {@code PipelineStatus} for the given {@code IndexType}. The {@code PipelineStatus} is managed by the particular
	 * {@code IndexingPipeline} and is only kept here so that it can play together with the other {@code PipelineStatus}s (to determine if all
	 * pipelines are done, etc).
	 * <p>
	 * You can attach your {@code PipelineStatus} via {@code IndexingStatistics#attachPipelineStatus(IndexType, PipelineStatus)}.
	 * 
	 * @param indexType {@code IndexType}
	 * @return The associated {@code PipelineStatus}, or null if none is set.
	 */
	PipelineStatus getPipelineStatus(IndexType indexType);

	/**
	 * Return the {@code PipelinePerformance} for the given {@code IndexType}. The {@code PipelinePerformance} is managed by the particular
	 * {@code IndexingPipeline} and is only kept here so that it can play together with the other {@code PipelinePerformance}s (to aggregate data,
	 * etc).
	 * <p>
	 * You can attach your {@code PipelinePerformance} via {@code IndexingStatistics#attachPipelinePerformance(IndexType, PipelinePerformance)}.
	 * 
	 * @param indexType {@code IndexType}
	 * @return The associated {@code PipelinePerformance}, or null if none is set.
	 */
	PipelinePerformance getPerformance(IndexType indexType);

	/**
	 * Before the introduction of {@code IndexingPipeline}, {@code AbstractIndexServiceImpl} kept track of its status in a {@code IndexBuildStatus}
	 * object which is persisted to the database. This object can now be attached to the Indexing Statistics and managed from there. Callers must
	 * still persist the object via the DAO if needed.
	 * 
	 * @param indexType {@code IndexType}
	 * @return The associated {@code IndexBuildStatus} or null if none is set.
	 */
	IndexBuildStatus getIndexBuildStatus(IndexType indexType);

	/**
	 * Attach or replace the {@code PipelineStatus} associated with the given {@code IndexType}.
	 * 
	 * @param indexType see {@code IndexType}
	 * @param status a {@code PipelineStatus}. Don't pass in null.
	 */
	void attachPipelineStatus(IndexType indexType, PipelineStatus status);

	/**
	 * Attach or replace the {@code IndexBuildStatus} associated with the given {@code IndexType}.
	 * 
	 * @param indexType see {@code IndexType}
	 * @param indexBuildStatus usually initially created by {@code AbstractIndexServiceImpl}, but updated by {@code IndexingPipelineImpl}
	 */
	void attachIndexBuildStatus(IndexType indexType, IndexBuildStatus indexBuildStatus);

	/**
	 * Attach or replace the {@code PipelinePerformance} associated with the given {@code IndexType}.
	 * 
	 * @param indexType see {@code IndexType}
	 * @param performance a {@code PipelinePerformance}. Don't pass in null.
	 */
	void attachPipelinePerformance(IndexType indexType, PipelinePerformance performance);

	/**
	 * This method will walk through all the {@code PipelineStatus} and {@code PipelinePerformance} objects attached and call reset() on them. This
	 * is used to "clear out" values between runs and should likely not be called automatically. As {@code IndexBuildStatus} predates having
	 * {@code IndexingPipeline}s, they are not reset by this method.
	 */
	void reset();
}
