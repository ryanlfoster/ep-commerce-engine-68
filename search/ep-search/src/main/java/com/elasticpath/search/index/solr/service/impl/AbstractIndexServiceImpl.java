package com.elasticpath.search.index.solr.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.search.index.pipeline.stats.IndexingStatistics;
import com.elasticpath.search.index.pipeline.stats.PipelineStatus;
import com.elasticpath.search.index.solr.builders.IndexBuilder;
import com.elasticpath.search.index.solr.builders.IndexBuilderFactory;
import com.elasticpath.search.index.solr.service.IndexBuildService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;

/**
 * A high level implementation of an Indexing Service. Can be subclassed to implement specific indexing algorithms.
 */
public abstract class AbstractIndexServiceImpl extends AbstractEpPersistenceServiceImpl implements IndexBuildService {
	/**
	 * A logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(AbstractIndexServiceImpl.class);

	private TimeService timeService;

	private IndexBuildStatusDao indexBuildStatusDao;

	private IndexBuilderFactory indexBuilderFactory;

	private IndexingStatistics indexingStatistics;

	private ObjectFactory<IndexBuildStatus> indexBuildStatusFactory;

	private final Map<IndexType, Date> lastInitialized = new ConcurrentHashMap<IndexType, Date>();

	/**
	 * Default constructor.
	 */
	public AbstractIndexServiceImpl() {
		// setup up required properties
	}

	/**
	 * Concrete subclasses should implement the build algorithm in this method.
	 * 
	 * @param indexType the index type
	 * @param rebuild provides a request for the index to be rebuilt
	 */
	protected abstract void build(final IndexType indexType, boolean rebuild);

	private void initialize(final IndexType indexType) {
		sanityCheck();

		lastInitialized.put(indexType, timeService.getCurrentTime());

		synchronized (indexType) {
			IndexBuildStatus buildStatus = indexingStatistics.getIndexBuildStatus(indexType);
			if (buildStatus == null) {
				buildStatus = indexBuildStatusDao.get(indexType);
				if (buildStatus == null) {
					buildStatus = indexBuildStatusFactory.getObject();
					buildStatus.setIndexType(indexType);
				}
			}

			buildStatus.setOperationStartDate(lastInitialized.get(indexType));

			indexingStatistics.attachIndexBuildStatus(indexType, buildStatus);
		}
	}

	/**
	 * Returns the date of the last time the index was built.
	 * 
	 * @param indexType the IndexType we're asking about
	 * @return the date of the last time the index was built
	 */
	protected Date getLastBuildDate(final IndexType indexType) {
		return getIndexBuildStatus(indexType).getLastBuildDate();
	}

	private boolean wasRebuildInterrupted(final IndexType indexType) {
		return IndexStatus.REBUILD_IN_PROGRESS.equals(getIndexBuildStatus(indexType).getIndexStatus());
	}

	/**
	 * Sets the date of the last time the index was built. We keep track of the <b>start time</b> of the last successful build, so we can always
	 * build-from-last-build-date to catch up if needed. We also bump up the time even if we didn't do anything by using the {@code #lastInitialized}
	 * map. This code existed before {@code IndexingPipeline}, as such its lifecycle is different, so it needs to do some checks to see if
	 * {@code PipelineStatus} has been initialized and such.
	 * 
	 * @param indexType the index type we're working on.
	 */
	protected void setLastBuildDate(final IndexType indexType) {
		final IndexBuildStatus buildStatus = getIndexBuildStatus(indexType);

		if (buildStatus == null) {
			// Build Status may be null if we're running through the TestApplicationContext.
			return;
		}

		Date buildDate = null;
		PipelineStatus pipelineStatus = indexingStatistics.getPipelineStatus(indexType);

		if (pipelineStatus == null) {
			buildStatus.setLastBuildDate(null);
			indexBuildStatusDao.saveOrUpdate(buildStatus);
			return;
		}

		buildDate = pipelineStatus.getCompletionDate();

		// pick the latest of last-successful-build or last-initialised

		if (lastInitialized.get(indexType) != null && (buildDate == null || lastInitialized.get(indexType).after(buildDate))) {
			buildDate = lastInitialized.get(indexType);
		}
		buildStatus.setLastBuildDate(buildDate);
		indexBuildStatusDao.saveOrUpdate(buildStatus);
	}

	/**
	 * This is convenience method for running quartz jobs. Builds the search index.
	 * 
	 * @param indexType the string representation of index type
	 */
	public void buildIndexJobRunner(final String indexType) {
		final long startTime = System.currentTimeMillis();
		LOG.info("Index build job started for type:" + indexType + " at " + new Date(startTime));
		buildIndex(IndexType.findFromName(indexType));
		LOG.info("Index build job for type:" + indexType + " completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Builds the search index.
	 * 
	 * @param indexType the index type
	 */
	public void buildIndex(final IndexType indexType) {

		initialize(indexType);

		final boolean rebuildRequired = wasRebuildInterrupted(indexType) || getLastBuildDate(indexType) == null || isRebuildRequired(indexType);

		setIndexStatusInProgress(indexType, rebuildRequired);

		build(indexType, rebuildRequired);

		setIndexStatusComplete(indexType);

	}

	/**
	 * Should return the proper value for whether a rebuild is required according to the implementor. This value will be passed to the
	 * {@link #build(IndexType, boolean)} method along with the considerations of the abstract implementation.
	 * 
	 * @param indexType the {@link IndexType} to be checked out
	 * @return true if rebuild required
	 */
	public abstract boolean isRebuildRequired(IndexType indexType);

	/**
	 * Gets the current index builder by index type.
	 * 
	 * @param indexType the index type
	 * @return index builder
	 */
	protected IndexBuilder getIndexBuilder(final IndexType indexType) {
		return indexBuilderFactory.getIndexBuilder(indexType);
	}

	private void setIndexStatusComplete(final IndexType indexType) {
		synchronized (indexType) {
			final IndexBuildStatus buildStatus = getIndexBuildStatus(indexType);
			buildStatus.setIndexStatus(IndexStatus.COMPLETE);
			updateStatus(buildStatus);
		}
	}

	/**
	 * Gets the current build status.
	 * 
	 * @param indexType the index type we're curious about
	 * @return the index build status.
	 */
	protected IndexBuildStatus getIndexBuildStatus(final IndexType indexType) {
		return indexingStatistics.getIndexBuildStatus(indexType);
	}

	private void setIndexStatusInProgress(final IndexType indexType, final boolean rebuild) {
		final IndexStatus indexStatus;
		if (rebuild) {
			indexStatus = IndexStatus.REBUILD_IN_PROGRESS;
		} else {
			indexStatus = IndexStatus.UPDATE_IN_PROGRESS;
		}
		synchronized (indexType) {
			final IndexBuildStatus buildStatus = getIndexBuildStatus(indexType);
			buildStatus.setIndexStatus(indexStatus);
			updateStatus(buildStatus);
		}
	}

	/**
	 * Saves the current IndexBuildStatus.
	 * 
	 * @param buildStatus the IndexBuildStatus.
	 */
	protected void updateStatus(final IndexBuildStatus buildStatus) {
		indexingStatistics.attachIndexBuildStatus(buildStatus.getIndexType(), indexBuildStatusDao.saveOrUpdate(buildStatus));
	}

	/**
	 * Checks that the required objects have been set.
	 */
	@Override
	protected void sanityCheck() {
		if (indexBuildStatusDao == null) {
			throw new EpServiceException("The indexBuildStatus dao has not been set.");
		}
		if (getElasticPath() == null) {
			throw new EpServiceException("The elasticpath has not been set.");
		}
	}

	/**
	 * This is a stub implementation. It should never be called.
	 * 
	 * @param uid the persistent instance uid
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case it is called
	 */
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("Should never reach this method.");
	}

	/**
	 * Sets the time service.
	 * 
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Gets the time service.
	 * 
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the index build status dao.
	 * 
	 * @param indexBuildStatusDao the index build status dao
	 */
	public void setIndexBuildStatusDao(final IndexBuildStatusDao indexBuildStatusDao) {
		this.indexBuildStatusDao = indexBuildStatusDao;
	}

	/**
	 * Sets the index builder factory.
	 * 
	 * @param indexBuilderFactory the index builder factory
	 */
	public void setIndexBuilderFactory(final IndexBuilderFactory indexBuilderFactory) {
		this.indexBuilderFactory = indexBuilderFactory;
	}

	public void setIndexingStatistics(final IndexingStatistics indexingStatistics) {
		this.indexingStatistics = indexingStatistics;
	}

	public void setIndexBuildStatusFactory(final ObjectFactory<IndexBuildStatus> indexBuildStatusFactory) {
		this.indexBuildStatusFactory = indexBuildStatusFactory;
	}

	public IndexingStatistics getIndexingStatistics() {
		return indexingStatistics;
	}
}
