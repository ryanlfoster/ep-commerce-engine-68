/**
 */
package com.elasticpath.domain.customer.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.service.customer.CustomerSessionCleanupService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.service.shopper.ShopperCleanupService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Job to clean up old customer session & shopping cart records.
 */
public class SessionCleanupJob {

	/**
	 * Settings path for maxHistory.
	 */
	public static final String SESSION_CLEANUP_MAX_HISTORY = "COMMERCE/SYSTEM/SESSIONCLEANUP/maxHistory";

	/**
	 * Settings path for batchSize.
	 */
	public static final String SESSION_CLEANUP_BATCH_SIZE = "COMMERCE/SYSTEM/SESSIONCLEANUP/batchSize";

	private static final Logger LOG = Logger.getLogger(SessionCleanupJob.class);
	
	private TimeService timeService;

	private SettingsReader settingsReader;

	private CustomerSessionCleanupService customerSessionCleanupService;
	private ShopperCleanupService shopperCleanupService;

	private ShoppingCartService shoppingCartService;
	private WishListService wishlistService;

	/**
	 * Hidden class used to execute methods so that purgeSessionHistory is re-entrant.
	 */
	private class SessionCleanupRunner {

		private int totalSessionsDeleted;
		private int totalShoppersDeleted;
		private int totalShoppingCartsDeleted;
		private int totalWishListsDeleted;

		private final Date beforeDate;
		private final int batchSize;

		private final Predicate shopperWithCustomerPredicate = new Predicate() {
			@Override
			public boolean evaluate(final Object object) {
				// Note: Not bothering to check if object == shopper because this is all private and
				// whoever is writing/updating this better make sure this is the case.
				final ShopperMemento shopperMemento = (ShopperMemento) object;
				if (shopperMemento.getCustomer() != null) {
					return true;
				}
				return false;
			}
		};

		public SessionCleanupRunner(final Date beforeDate, final int batchSize) {
			this.beforeDate = beforeDate;
			this.batchSize = batchSize;
		}

		/**
		 * Runs.
		 */
		public void run() {
			deleteOldCustomerSessions();

			final List<ShopperMemento> orphanedShoppers = shopperCleanupService.findShoppersOrphanedFromCustomerSessions(batchSize);
			final List<ShopperMemento> registeredShoppers = getNewFilteredList(orphanedShoppers, shopperWithCustomerPredicate);
			final List<ShopperMemento> anonymousShoppers = getNewFilteredList(orphanedShoppers,
					PredicateUtils.notPredicate(shopperWithCustomerPredicate));
			
			deleteCascadeOnShopperList(anonymousShoppers);
			deleteCascadeEmptyOnShopperList(registeredShoppers);
		}

		private void deleteOldCustomerSessions() {
			final List<String> oldCustomerSessionGuids = new ArrayList<String>(
					customerSessionCleanupService.getOldCustomerSessionGuids(beforeDate, batchSize));
			totalSessionsDeleted += customerSessionCleanupService.deleteSessions(oldCustomerSessionGuids);
		}
		
		private List<ShopperMemento> getNewFilteredList(final List<ShopperMemento> shopperList, final Predicate predicate) {
			final List<ShopperMemento> results = new ArrayList<ShopperMemento>();
			results.addAll(shopperList);
			CollectionUtils.filter(results, predicate);

			return results;
		}

		/**
		 * Blindly delete all shoppingcarts, wishlists and shoppers based on the list of shoppers.
		 *
		 * @param shopperList
		 */
		private void deleteCascadeOnShopperList(final List<ShopperMemento> shopperList) {
			final List<Long> shopperUids = makeShopperUidList(shopperList);

			totalShoppingCartsDeleted += shoppingCartService.deleteAllShoppingCartsByShopperUids(shopperUids);
			totalWishListsDeleted += wishlistService.deleteAllWishListsByShopperUids(shopperUids);
			totalShoppersDeleted += shopperCleanupService.removeShoppersByUidList(shopperUids);
		}

		/**
		 * Only delete non-empty shoppingcarts and wishlists and then only shoppers that don't
		 * have a shoppingcart or wishlists associated with it.
		 *
		 * @param shopperList
		 */
		private void deleteCascadeEmptyOnShopperList(final List<ShopperMemento> shopperList) {
			final List<Long> shopperUids = makeShopperUidList(shopperList);

			totalShoppingCartsDeleted += shoppingCartService.deleteEmptyShoppingCartsByShopperUids(shopperUids);
			totalWishListsDeleted += wishlistService.deleteEmptyWishListsByShopperUids(shopperUids);
			totalShoppersDeleted += shopperCleanupService.removeShoppersByUidList(shopperUids);
		}

		private List<Long> makeShopperUidList(final List<ShopperMemento> shopperList) {
			final List<Long> results = new ArrayList<Long>();
			for (final ShopperMemento shopperMemento : shopperList) {
				results.add(shopperMemento.getUidPk());
			}
			return results;
		}

		public int getTotalSessionsDeleted() {
			return totalSessionsDeleted;
		}

		public int getTotalShoppersDeleted() {
			return totalShoppersDeleted;
		}

		public int getTotalShoppingCartsDeleted() {
			return totalShoppingCartsDeleted;
		}

		public int getTotalWishListsDeleted() {
			return totalWishListsDeleted;
		}
	}

	/**
	 * Purge the session history. This will remove all customer session records that have a last accessed date older than the 
	 * number of days specified by a system setting defined by SESSION_CLEANUP_MAX_HISTORY and an empty shopping cart.
	 * 
	 * It will also cap the number of CustomerSession and Shoppers that it will delete to the setting defined in 
	 * SESSION_CLEANUP_BATCH_SIZE. (e.g. If SESSION_CLEANUP_BATCH_SIZE is defined as 1000, then no more than 1000 CustomerSession 
	 * and 1000 Shoppers are cleaned up in one go). 
	 * 
	 * @return the total number of sessions deleted
	 */
	public int purgeSessionHistory() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start purge session history quartz job at: " + new Date(startTime));
		
		// Cleanup Strategy
		// ---
		// 1. Find all CustomerSessions older than "maxHistory" date.
		// 2. Delete all those CustomerSessions first.
		// 3. Find all orphaned Shoppers.
		// 4. For each Orphaned Shopper w/ no Customer attached to it (and hence no way to externally reference it),
		// 		delete cascade (so delete shopping carts and wishlists).
		// 5. For each Orphaned Shopper w/ a Customer, only cascade delete if both the ShoppingCart and the Wishlist are empty.

		final Date deleteBefore = getDeleteBeforeDate();
		final int maxBatchSize = getBatchSize();

		final SessionCleanupRunner sessionCleanupRunner = new SessionCleanupRunner(deleteBefore, maxBatchSize);
		sessionCleanupRunner.run();

		if (LOG.isDebugEnabled()) {
			final DateFormat dateFormat = DateFormat.getDateInstance();
			LOG.debug(String.format(
					"SessionCleanupJob.purgeSessionHistory() - Deleting before %s. %d Sessions, %d Shoppers, %d Carts, %d Wishlists",
					dateFormat.format(deleteBefore), sessionCleanupRunner.getTotalSessionsDeleted(),
					sessionCleanupRunner.getTotalShoppersDeleted(),
					sessionCleanupRunner.getTotalShoppingCartsDeleted(),
					sessionCleanupRunner.getTotalWishListsDeleted()));
		}
		
		LOG.info("Purge session history quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		return sessionCleanupRunner.getTotalSessionsDeleted();

	}

	private Date getDeleteBeforeDate() {
		final SettingValue maxHistorySetting = settingsReader.getSettingValue(SESSION_CLEANUP_MAX_HISTORY);
		final int maxHistory = maxHistorySetting.getIntegerValue();
		return getDaysBeforeDate(maxHistory);
	}

	private int getBatchSize() {
		final SettingValue batchSize = settingsReader.getSettingValue(SESSION_CLEANUP_BATCH_SIZE);
		return batchSize.getIntegerValue();
	}

	/**
	 * Gets the Date the number of daysBefore now.
	 *
	 * @param daysBefore number of days before today.
	 * @return date which is daysBefore days before today.
	 */
	protected Date getDaysBeforeDate(final int daysBefore) {
		return DateUtils.addDays(getTimeService().getCurrentTime(), -daysBefore);
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
	public SettingsReader getSettingsReader() {
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
	public TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Set the {@link CustomerSessionCleanupService} to use.
	 * 
	 * @param customerSessionCleanupService the {@link CustomerSessionCleanupService}
	 */
	public void setCustomerSessionCleanupService(final CustomerSessionCleanupService customerSessionCleanupService) {
		this.customerSessionCleanupService = customerSessionCleanupService;
	}

	/**
	 * Get the {@link CustomerSessionCleanupService}.
	 * 
	 * @return the <code>{@link CustomerSessionCleanupService}</code>
	 */
	public CustomerSessionCleanupService getCustomerSessionCleanupService() {
		return customerSessionCleanupService;
	}

	/**
	 * Sets the shopperCleanupService.
	 * 
	 * @param shopperCleanupService .
	 */
	public void setShopperCleanupService(final ShopperCleanupService shopperCleanupService) {
		this.shopperCleanupService = shopperCleanupService;
	}

	/**
	 * Sets the shoppingCartService.
	 * 
	 * @param shoppingCartService .
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * Sets the wishlistservice.
	 * 
	 * @param wishlistService .
	 */
	public void setWishlistService(final WishListService wishlistService) {
		this.wishlistService = wishlistService;
	}
}
