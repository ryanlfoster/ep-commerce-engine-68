package com.elasticpath.domain.order.jobs;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.settings.SettingsService;

/**
 * Job which will remove order locks that have expired past a defined amount of time, which
 * is determined by a setting.
 */
public interface OrderLockCleanupJob {

	/**
	 * Cleans up the order locks that have been created before a certain amount of time, the
	 * amount of time is defined by a system setting.
	 * @return the number of order locks removed
	 */
	OrderLockCleanupResult cleanUpOrderLocks();
	
	/**
	 * Returns the settings service.
	 *
	 * @return the settings service
	 */
	SettingsService getSettingsService();
	
	/**
	 * Sets the settings service.
	 *
	 * @param settingsService - the settings service to set
	 */
	void setSettingsService(final SettingsService settingsService);
	
	/**
	 * Returns the order lock service.
	 *
	 * @return the order lock service
	 */
	OrderLockService getOrderLockService();
	
	/**
	 * Sets the order lock service.
	 *
	 * @param orderLockService - the order lock service to set
	 */
	void setOrderLockService(final OrderLockService orderLockService);
	
	/**
	 * @param timeService the timeService to set
	 */
	void setTimeService(final TimeService timeService);

	/**
	 * @return the timeService
	 */
	TimeService getTimeService();
}
