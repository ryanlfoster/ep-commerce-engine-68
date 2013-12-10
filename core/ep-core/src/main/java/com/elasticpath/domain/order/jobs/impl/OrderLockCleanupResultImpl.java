package com.elasticpath.domain.order.jobs.impl;

import com.elasticpath.domain.order.jobs.OrderLockCleanupResult;

/**
 * Implementation of the order lock cleanup result, houses information regarding
 * the invocation of the cleanup job, this information is the number
 * of batch runs occurred and the total number of order locks removed.
 */
public class OrderLockCleanupResultImpl implements OrderLockCleanupResult {

	private int numberBatchRuns;
	private int numberLocksRemoved;
	private long timeToExecute;
	
	/**
	 * Creates an order lock clean up result object, and sets the total number of batch runs along with
	 * with the total number of locks removed.
	 *
	 * @param totalNumberBatchRuns - the total number of batch runs
	 * @param totalNumberLocksRemoved - the total number of order locks removed
	 * @param timeToExecute - the time taken to execute the clean up job
	 */
	public OrderLockCleanupResultImpl(final int totalNumberBatchRuns, final int totalNumberLocksRemoved, final long timeToExecute) {
		this.numberBatchRuns = totalNumberBatchRuns;
		this.numberLocksRemoved = totalNumberLocksRemoved;
		this.timeToExecute = timeToExecute;
	}

	@Override
	public int getNumberOfBatchRuns() {
		return numberBatchRuns;
	}

	@Override
	public int getNumberOfLocksRemoved() {
		return numberLocksRemoved;
	}

	@Override
	public void setNumberOfBatchRuns(final int numberBatchRuns) {
		this.numberBatchRuns = numberBatchRuns;
	}

	@Override
	public void setNumberOfLocksRemoved(final int numberLocksRemoved) {
		this.numberLocksRemoved = numberLocksRemoved;
	}

	@Override
	public long getTimeToExecute() {
		return timeToExecute;
	}

	@Override
	public void setTimeToExecute(final long timeToExecute) {
		this.timeToExecute = timeToExecute;
	}
}
