/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.order.jobs.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * This class removes Orders which have an OrderStatus of FAILED.
 * Which Orders it removes and how many to do at once are controlled by settings in TSettingDefinition.
 */
public class FailedOrdersCleanupJob {
	
	/** See the setting in TSettingDefinition for information. */
	public static final String FAILED_ORDER_MAX_HISTORY = "COMMERCE/SYSTEM/FAILEDORDERCLEANUP/maxHistory";
	
	/** See the setting in TSettingDefinition for information. */
	public static final String FAILED_ORDER_BATCH_SIZE = "COMMERCE/SYSTEM/FAILEDORDERCLEANUP/batchSize";
	
	private static final Logger LOG = Logger.getLogger(FailedOrdersCleanupJob.class);
	private OrderService orderService;
	private TimeService timeService;
	private SettingsReader settingsReader;

	/**
	 * Removes failed Orders.
	 * @return The total number of failed Orders that were removed.
	 */
	public int removeFailedOrders() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start remove failed orders quartz job at: " + new Date(startTime));
		
		final Date removalDate = getCandidateRemovalDate();
		if (removalDate == null) {
			throw new EpServiceException("removalDate must be supplied.");
		}
		final int maxResults = getBatchSize();
		
		LOG.info("Starting failed Orders cleanup job.");
		List<Long> failedOrderUids = getOrderService().getFailedOrderUids(removalDate, maxResults);
		getOrderService().deleteOrders(failedOrderUids);

		int removed = failedOrderUids.size();
		LOG.info(String.format("Finished failed Orders cleanup job. Removed %d Orders.", removed));
		LOG.info("Remove failed orders quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return removed;
	}

	/**
	 * @return The candidate removal date.
	 */
	protected Date getCandidateRemovalDate() {
		final SettingValue maxHistorySetting = getSettingsReader().getSettingValue(FAILED_ORDER_MAX_HISTORY);
		final int days = maxHistorySetting.getIntegerValue();
		return DateUtils.addDays(getTimeService().getCurrentTime(), -days);
	}

	/**
	 * @return The batch size.
	 */
	protected int getBatchSize() {
		final SettingValue batchSize = getSettingsReader().getSettingValue(FAILED_ORDER_BATCH_SIZE);
		return batchSize.getIntegerValue();
	}

	/**
	 * @param settingsReader The settings reader.
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @return The settings reader.
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * @param timeService The time service.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @return The time service.
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * @param orderService The OrderService.
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * @return The OrderService.
	 */
	protected OrderService getOrderService() {
		return orderService;
	}
}
