package com.elasticpath.service.misc.impl;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;

/**
 * Caching wrapper for a time service. This class will only call the wrapped time service after a configured amount of time to calculate difference
 * in time between the current JVM and the remote (DB) server. Time returned by service will be calculated from the last calculated time difference
 * from the remote server.
 */
@SuppressWarnings("PMD.AvoidUsingVolatile")
public class CachedSyncingServerTimeServiceImpl implements TimeService {
	private static final Logger LOG = Logger.getLogger(CachedSyncingServerTimeServiceImpl.class);
	private TimeService timeService;
	private volatile long cacheTimeout;
	private final AtomicLong drift = new AtomicLong();
	private final AtomicLong recheckTime = new AtomicLong();
	
	/**
	 * Provide cached access to the wrapped time service.
	 * Syncs up difference between the current JVM and the remote (DB) server after every interval.
	 * 
	 * This call is thread safe and atomically sets the drift time. 
	 * Though unlikely, it is possible for two threads to in and see that it is time to recheck and reset drift time at the same time,
	 * updates to drift is still atomic and safe.
	 * 
	 * @return date the cached date
	 */
	public Date getCurrentTime() {
		if (isRecheckTime()) {
			drift.set(calculateDrift());
		}
		//Get the time with correction for any time drift during the cache time.
		return new Date(getSystemTime() - drift.get()); 
	}

	/**
	 * Get the difference (drift) between current server JVM time and remote server time.
	 * 
	 * @return drift in time between server 
	 */
	protected long calculateDrift() {
		final long difference = getSystemTime() - this.timeService.getCurrentTime().getTime();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Appserver time difference - " + difference + " ms");
		}
		return difference;
	}
	
	/**
	 * Check if its time to recheck the wrapped time service.
	 * If it is, note the next time to recheck.
	 *  
	 * @return true if its time to recheck.
	 */
	protected boolean isRecheckTime() {
		final long currentDrift = recheckTime.get();
		final long now = getSystemTime();
		if (now > currentDrift) {
			return recheckTime.compareAndSet(currentDrift, now + cacheTimeout);
		}
		return false;
	}
	
	/**
	 * Hook for testing.
	 * @return the system time.
	 */
	protected long getSystemTime() {
		return System.currentTimeMillis();
	}
	
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(final long uid, final Collection<String> fieldsToLoad)
			throws EpServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PersistenceEngine getPersistenceEngine() {
		return null;
	}

	@Override
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		//Unused
	}

	@Override
	public ElasticPath getElasticPath() {
		return null;
	}

	@Override
	public void setElasticPath(final ElasticPath elasticpath) {
		//Unused
	}

	/**
	 * Set the wrapped time service.
	 * 
	 * @param service a TimeService.
	 */
	public void setWrappedTimeService(final TimeService service) {
		this.timeService = service;
	}
	
	/**
	 * Set the length of timeout for the time value. 
	 * 
	 * @param cacheTimeout the length in milliseconds
	 */
	public void setCacheTimeout(final long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
}
