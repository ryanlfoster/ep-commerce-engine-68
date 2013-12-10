package com.elasticpath.domain.order.jobs.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.jobs.OrderLockCleanupJob;
import com.elasticpath.domain.order.jobs.OrderLockCleanupResult;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.settings.SettingsService;

/**
 * Job which will remove order locks that have expired past a defined amount of time, which
 * is determined by a setting.
 */
public class OrderLockCleanupJobImpl implements OrderLockCleanupJob {

	private SettingsService settingsService;
	
	private OrderLockService orderLockService;
	
	private TimeService timeService;
	
	private static final String ORDERLOCK_CLEANUP_SETTING = "COMMERCE/SYSTEM/ORDERLOCK/minsBeforeCleanUp";
	
	private static final String BATCH_SIZE_SETTING = "COMMERCE/SYSTEM/ORDERLOCK/batchSize";
	
	private static final String BATCH_JOB_NAME = "CleanupOrderLocksJob";
	
	private static final Logger LOGGER = Logger.getLogger(OrderLockCleanupJobImpl.class);
	
	@Override
	public OrderLockCleanupResult cleanUpOrderLocks() {
		final long startTime = System.currentTimeMillis();
		LOGGER.info("Start cleanup order locks quartz job at: " + new Date(startTime));
		
		int batchSize = getSettingsService().getSettingValue(BATCH_SIZE_SETTING).getIntegerValue(); 
		int minsToExpireLocks = getSettingsService().getSettingValue(ORDERLOCK_CLEANUP_SETTING).getIntegerValue();

		if (isBatchSizeValid(batchSize) && isMinsToExpireLocksValid(minsToExpireLocks)) {
			LOGGER.info("Cleanup order locks quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
			return executeCleanup(batchSize, minsToExpireLocks);
		}
		
		LOGGER.info("Cleanup order locks quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return new OrderLockCleanupResultImpl(0, 0, 0);
	}

	private OrderLockCleanupResultImpl executeCleanup(final int batchSize, final int minsToExpireLocks) {
		long startTime = System.currentTimeMillis();
		
		List<OrderLock> orderLocks = null;
		long time = getOrderLockDeleteTime(minsToExpireLocks).getTime();
		int totalNumberLocksRemoved = 0;
		int totalNumberBatchRuns = 0;
		
		while (orderLocks == null || orderLocks.size() == batchSize) {
			totalNumberBatchRuns++;
			orderLocks = getOrderLockService().findAllOrderLocksBeforeDate(time, 0, batchSize);
			totalNumberLocksRemoved += releaseOrderLocks(orderLocks);
		}
		
		long endTime = System.currentTimeMillis();
		return new OrderLockCleanupResultImpl(totalNumberBatchRuns, totalNumberLocksRemoved,  endTime - startTime);
	}

	private int releaseOrderLocks(final List<OrderLock> orderLocks) {
		int numberOfLocksRemoved = 0;
		for (OrderLock orderLock : orderLocks) {
			try {
				getOrderLockService().forceReleaseOrderLock(orderLock);
				numberOfLocksRemoved++;
				
			} catch (EpServiceException e) {
				LOGGER.error(BATCH_JOB_NAME + " Failed to release order lock " + orderLock.getOrder().getOrderNumber(), e);
			}
		}
		return numberOfLocksRemoved;
	}

	/**
	 * Checks if the value provided in the "minsBeforeCleanUp" setting is valid, being greater
	 * then or equal to zero.
	 *
	 * @param minsToExpireLocks - the value to check if valid
	 * @return true if valid, false otherwise
	 */
	boolean isMinsToExpireLocksValid(final int minsToExpireLocks) {
		
		if (minsToExpireLocks < 0) {
			LOGGER.error(BATCH_JOB_NAME + " Failed: minsToExpireLocks must be >= 0");
			return false;
		}
		
		return true;
	}

	/**
	 * Checks if the value provided in the "batchSize" setting is valid, being greater then or
	 * equal to zero.
	 *
	 * @param batchSize - the value to check if valid
	 * @return true if valid, false otherwise
	 */
	boolean isBatchSizeValid(final int batchSize) {
		if (batchSize <= 0) {
			LOGGER.error(BATCH_JOB_NAME + " Failed: batchSize must be > 0");
			return false;
		}
		
		return true;
	}

	/**
	 * Determines the time we must check order locks against, if the order locks were
	 * created before this delete them then they must be removed otherwise they are
	 * still valid order locks.
	 *
	 * @param minsToExpireLocks - the number of minutes we must look backwards in time
	 * @return the calendar with the time we must discriminate order locks against
	 */
	protected Date getOrderLockDeleteTime(final int minsToExpireLocks) {
		return DateUtils.addMinutes(getTimeService().getCurrentTime(), -minsToExpireLocks);
	}

	@Override
	public OrderLockService getOrderLockService() {
		return orderLockService;
	}

	@Override
	public SettingsService getSettingsService() {
		return settingsService;
	}

	@Override
	public void setOrderLockService(final OrderLockService orderLockService) {
		this.orderLockService = orderLockService;
	}

	@Override
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService() {
		return timeService;
	}

}
