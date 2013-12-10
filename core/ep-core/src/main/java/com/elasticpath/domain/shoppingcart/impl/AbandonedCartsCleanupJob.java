/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.service.shoppingcart.ShoppingCartCleanupService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Job to purge abandoned shopping carts.<br>
 */
public class AbandonedCartsCleanupJob {

	private static final String ABANDONDED_CART_MAX_HISTORY = "COMMERCE/SYSTEM/ABANDONEDCARTCLEANUP/maxHistory";

	private static final String ABANDONDED_CART_BATCH_SIZE = "COMMERCE/SYSTEM/ABANDONEDCARTCLEANUP/batchSize";

	private static final Logger LOG = Logger.getLogger(AbandonedCartsCleanupJob.class);

	private TimeService timeService;

	private SettingsReader settingsReader;

	private ShoppingCartCleanupService shoppingCartCleanupService;

	/**
	 * Purge the abandoned shopping carts.<br>
	 * This will remove all shopping cart records that have a last modified date older than the number of days specified by a system setting defined
	 * by <code>ABANDONDED_CART_MAX_HISTORY</code>. <br>
	 * It will also cap the number of ShoppingCarts that it will delete to the setting defined in <code>ABANDONDED_CART_BATCH_SIZE</code>.<br>
	 * (e.g. If <code>ABANDONDED_CART_BATCH_SIZE</code> is set to 1000, then no more than 1000 shopping carts are cleaned up in one go).
	 *
	 * @return the total number of shopping carts deleted
	 */
	public int purgeAbandonedShoppingCarts() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start purge abandoned shopping carts quartz job at: " + new Date(startTime));
		
		final Date removalDate = getCandidateRemovalDate();
		final int maxResults = getBatchSize();

		LOG.info("Starting abandoned shopping carts cleanup job...");
		final int removedShoppingCarts = getShoppingCartCleanupService().deleteAbandonedShoppingCarts(removalDate, maxResults);
		LOG.info(String.format("Finished abandoned shopping carts cleanup job. Removed %d Shopping Carts.", removedShoppingCarts));

		LOG.info("Purge abandoned shopping carts quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return removedShoppingCarts;
	}

	/**
	 * Gets the candidate removal date.
	 *
	 * @return the candidate removal date
	 */
	protected Date getCandidateRemovalDate() {
		final SettingValue maxHistorySetting = getSettingsReader().getSettingValue(ABANDONDED_CART_MAX_HISTORY);
		final int days = maxHistorySetting.getIntegerValue();
		return DateUtils.addDays(getTimeService().getCurrentTime(), -days);
	}

	/**
	 * Gets the batch size.
	 *
	 * @return the batch size
	 */
	protected int getBatchSize() {
		final SettingValue batchSize = getSettingsReader().getSettingValue(ABANDONDED_CART_BATCH_SIZE);
		return batchSize.getIntegerValue();
	}

	/**
	 * Set the settings reader.
	 *
	 * @param settingsReader the settings reader
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Get the settings reader.
	 *
	 * @return the settings reader.
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Get the time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the shopping cart cleanup service.
	 *
	 * @param shoppingCartCleanupService the new shopping cart cleanup service
	 */
	public void setShoppingCartCleanupService(final ShoppingCartCleanupService shoppingCartCleanupService) {
		this.shoppingCartCleanupService = shoppingCartCleanupService;
	}

	/**
	 * Gets the shopping cart cleanup service.
	 *
	 * @return the shopping cart cleanup service
	 */
	protected ShoppingCartCleanupService getShoppingCartCleanupService() {
		return shoppingCartCleanupService;
	}
}
