package com.elasticpath.search.index.pipeline.stats.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.elasticpath.search.index.pipeline.stats.PipelineStatus;

/**
 * An implementation of {@code PipelineStatus}. There is one for each {@code IndexingPipeline}.
 */
public class PipelineStatusImpl implements PipelineStatus {

	private static final int DEFAULT_WAIT_TIME_MS = 250;

	private AtomicLong totalInCount = new AtomicLong();

	private AtomicLong totalOutCount = new AtomicLong();

	private Date completion;

	private Date latestIndexingStart;

	private final ReentrantLock statusLock = new ReentrantLock(true);

	private final AtomicBoolean completed = new AtomicBoolean(false);

	private int waitTime = DEFAULT_WAIT_TIME_MS;

	@Override
	public void incrementIncomingItems(final long count) {
		totalInCount.addAndGet(count);
	}

	@Override
	public void incrementCompletedItems(final long count) {
		totalOutCount.addAndGet(count);
	}

	@Override
	public void reset() {
		totalInCount = new AtomicLong();
		totalOutCount = new AtomicLong();
	}

	@Override
	public long getIncomingCount() {
		return this.totalInCount.get();
	}

	@Override
	public long getCompletedCount() {
		return this.totalOutCount.get();
	}

	@Override
	public Date getLatestIndexingStart() {
		return latestIndexingStart;
	}

	@Override
	public void setLatestIndexingStart(final Date latestIndexingStart) {
		this.latestIndexingStart = latestIndexingStart;
	}

	@Override
	public Date getCompletionDate() {
		return completion;
	}

	@Override
	public void setCompletionDate(final Date date) {
		this.completion = date;
	}

	public ReentrantLock getStatusLock() {
		return this.statusLock;
	}

	@Override
	public void markStarted() {
		synchronized (completed) {
			completed.set(false);
		}
	}

	@Override
	public void waitUntilCompleted() throws InterruptedException {
		synchronized (completed) {
			while (!completed.get()) {
				completed.wait(waitTime);
			}
		}
	}

	@Override
	public void notifyCompleted() {
		synchronized (completed) {
			completed.set(true);
			completed.notifyAll();
		}
	}

	public void setWaitTime(final int waitTime) {
		this.waitTime = waitTime;
	}

}
