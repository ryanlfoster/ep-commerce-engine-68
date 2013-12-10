package com.elasticpath.commons.util;

import org.apache.log4j.Logger;

/**
 * Manages when a set time interval has past. 
 */
public class IntervalTimer {
	
	private static final Logger LOG = Logger.getLogger(IntervalTimer.class);
	
	private long startPoint;
	private final long intervalMillis;

	/**
	 * Sets the interval to count/wait for in milliseconds.
	 * @param intervalMillis intervalMillis
	 */
	public IntervalTimer(final long intervalMillis) {
		this.intervalMillis = intervalMillis;
		if (LOG.isDebugEnabled()) {
			LOG.debug("CountdownTimer created with interval = " + intervalMillis + "ms");
		}
	}
	
	/**
	 * Marks the start point to measure the interval from.
	 */
	public void setStartPointToNow() {
		startPoint = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug("setStartPointToNow called");
		}
	}
	
	/**
	 * Returns true if the current time is great than the time since setStartPointToNow was called plus the 
	 * given interval.
	 * @return true if the interval has passed
	 */
	public boolean hasIntervalPassed() {
		boolean intervalPassed = (System.currentTimeMillis() - startPoint) >= intervalMillis;
		if (LOG.isDebugEnabled() && intervalPassed) {
			LOG.debug("interval passed");
		}
		return intervalPassed;
	}
	
}
