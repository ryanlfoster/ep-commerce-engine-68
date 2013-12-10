package com.elasticpath.search.index.pipeline.stats;

import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * This interface is tasked with keeping track of the overall status of the pipeline, including determining if it's safe to shutdown, if it's busy
 * working or if it's <b>COMPLETED</b>.
 */
public interface PipelineStatus {

	/**
	 * Items come into the pipeline and are counted. Usually this would be done in {@code IndexingPipeline#start(Object)}.
	 * <p>
	 * This method must be thread safe.
	 * 
	 * @param count the number of "items" coming into the pipeline.
	 */
	void incrementIncomingItems(long count);

	/**
	 * For each item that enters the pipeline, an item must be counted leaving the pipeline. This includes items which failed or otherwise didn't
	 * process correctly.
	 * <p>
	 * This method must be thread safe.
	 * 
	 * @param count the positive number of "items" now done.
	 */
	void incrementCompletedItems(long count);

	/**
	 * Return the total count of incoming items. This method is thread-safe.
	 * 
	 * @return total count of incoming items for the associated pipeline.
	 */
	long getIncomingCount();

	/**
	 * Return the total count of completed items. This method is thread-safe.
	 * 
	 * @return total count of completed items for the associated pipeline.
	 */
	long getCompletedCount();

	/**
	 * This method will reset the counts of incoming/complete items. Doing this while the pipeline is busy can result in weird values.
	 * <p>
	 * This method must be thread safe.
	 */
	void reset();

	/**
	 * Retrieve the last time {@code IndexingPipeline#start(Object)} was called. Callers should lock around the status lock ({@see #getStatusLock()})
	 * to ensure consistency.
	 * 
	 * @return the last date started.
	 */
	Date getLatestIndexingStart();

	/**
	 * Set the last time indexing was started. Callers should have the {@code #getStatusLock()} before calling this method.
	 * 
	 * @param lastStart usually the current time.
	 */
	void setLatestIndexingStart(Date lastStart);

	/**
	 * The last successful completion time. This is written to the appropriate places and nullified when it's no longer needed. Generally this value
	 * was previously set in {@code #setLatestIndexingStart(Date)} and moved here when the pipeline is no longer busy.
	 * 
	 * @return the time the last finished indexing run was started
	 */
	Date getCompletionDate();

	/**
	 * Usually set to the value in {@code #getLatestIndexingStart()} once that run is completed.
	 * 
	 * @param date the time that index *started*
	 */
	void setCompletionDate(Date date);

	/**
	 * A lock used for determining the state of the pipeline at a given point in time. Activities which would alter the state of the pipeline are
	 * required to obtain the lock from here in order to enforce that the state change does not happen while making decisions.
	 * 
	 * @return a lock which can be used to create a reasonable point in time to figure out the status of the pipeline
	 */
	Lock getStatusLock();

	/**
	 * Blocks until the current {@code IndexingPipeline} completes. Note, if this is called while no work is being done, it will block until work is
	 * added to the pipeline and then completed. It is usually unblocked when someone calls {@code #notifyCompleted()}.
	 * 
	 * @throws InterruptedException as this is really an {@code Object#wait()}.
	 */
	void waitUntilCompleted() throws InterruptedException;

	/**
	 * Notify everyone who is currently blocked on {@code #waitUntilCompleted()} that the pipeline has completed processing (ie, all the queues are
	 * empty). This resets the value set in {@code #markStarted()}.
	 */
	void notifyCompleted();

	/**
	 * If the pipeline is about to begin processing work, then this method must be called first. Before it is called, callers who call
	 * {@code #waitUntilCompleted()} will return right away. After it is called, they will wait.
	 */
	void markStarted();
}
