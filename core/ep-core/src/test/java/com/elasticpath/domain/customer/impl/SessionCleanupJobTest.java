/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.service.customer.CustomerSessionCleanupService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.service.shopper.ShopperCleanupService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Unit test for SessionCleanupJob. 
 */
public class SessionCleanupJobTest {

	private static final int THIRTY_DAY_HISTORY = 30;
	private static final int BATCH_SIZE = 1000;
	private static final long SHOPPER1_UID = 1000L;
	private static final long SHOPPER2_UID = 1001L;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final SettingsReader settingsReader = context.mock(SettingsReader.class);
	private final TimeService timeService = context.mock(TimeService.class);
	private final CustomerSessionCleanupService customerSessionCleanupService = context.mock(CustomerSessionCleanupService.class);
	private final ShopperCleanupService shopperCleanupService = context.mock(ShopperCleanupService.class);
	private final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
	private final WishListService wishlistService = context.mock(WishListService.class);

	private SessionCleanupJob job;

	/**
	 * Setup required for each test.
	 */
	@Before
	public void setUp() {
		job = new SessionCleanupJob();
		job.setTimeService(timeService);
		job.setSettingsReader(settingsReader);
		job.setCustomerSessionCleanupService(customerSessionCleanupService);
		job.setShopperCleanupService(shopperCleanupService);
		job.setShoppingCartService(shoppingCartService);
		job.setWishlistService(wishlistService);
	}
	
	/**
	 * Test delete before date.
	 */
	@Test
	public void testGetDeleteBeforeDate() {
		final Calendar calendar = configureTimeService();
		calendar.add(Calendar.DATE, -THIRTY_DAY_HISTORY);
		final Date deleteBeforeDate = job.getDaysBeforeDate(THIRTY_DAY_HISTORY);
		assertNotNull(deleteBeforeDate);
		assertEquals("Expected delete date to match.", calendar.getTime(), deleteBeforeDate);
	}

	/**
	 * Test purge session history.
	 */
	@Test
	public void testPurgeSessionHistory() {
		final Calendar calendar = configureTimeService();
		calendar.add(Calendar.DATE, -THIRTY_DAY_HISTORY);
		final SettingValue maxHistory = context.mock(SettingValue.class, "maxHistory");
		final SettingValue batchSize = context.mock(SettingValue.class, "batchSize");
		final List<String> oldGuids = new ArrayList<String>();
		final List<ShopperMemento> orphanedShoppers = Arrays.asList(createAnonymousShopper(SHOPPER1_UID), createRegisteredShopper(SHOPPER2_UID));
		final List<Long> anonymousUids = Arrays.asList(SHOPPER1_UID);
		final List<Long> registeredUids = Arrays.asList(SHOPPER2_UID);
		
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(SessionCleanupJob.SESSION_CLEANUP_MAX_HISTORY); will(returnValue(maxHistory));
				oneOf(maxHistory).getIntegerValue(); will(returnValue(THIRTY_DAY_HISTORY));
				oneOf(settingsReader).getSettingValue(SessionCleanupJob.SESSION_CLEANUP_BATCH_SIZE); will(returnValue(batchSize));
				oneOf(batchSize).getIntegerValue(); will(returnValue(BATCH_SIZE));
				
				oneOf(customerSessionCleanupService).getOldCustomerSessionGuids(calendar.getTime(), BATCH_SIZE); will(returnValue(oldGuids));
				oneOf(customerSessionCleanupService).deleteSessions(oldGuids);
				
				oneOf(shopperCleanupService).findShoppersOrphanedFromCustomerSessions(BATCH_SIZE); will(returnValue(orphanedShoppers));

				oneOf(shoppingCartService).deleteAllShoppingCartsByShopperUids(anonymousUids);
				oneOf(wishlistService).deleteAllWishListsByShopperUids(anonymousUids);
				oneOf(shopperCleanupService).removeShoppersByUidList(anonymousUids);

				oneOf(shoppingCartService).deleteEmptyShoppingCartsByShopperUids(registeredUids);
				oneOf(wishlistService).deleteEmptyWishListsByShopperUids(registeredUids);
				oneOf(shopperCleanupService).removeShoppersByUidList(registeredUids);
			}
		});
		job.purgeSessionHistory();
	}

	/**
	 * Configure time service.
	 *
	 * @return the calendar
	 */
	private Calendar configureTimeService() {
		final Calendar calendar = Calendar.getInstance();
		
		context.checking(new Expectations() {
			{
				allowing(timeService).getCurrentTime();
				will(returnValue(calendar.getTime()));
			}
		});
		return calendar;
	}
	
	/**
	 * Creates the registered shopper.
	 *
	 * @param uidPk the uid pk
	 * @return the shopper memento
	 */
	private ShopperMemento createRegisteredShopper(final long uidPk) {
		ShopperMemento shopper = new ShopperMementoImpl();
		Customer customer = context.mock(Customer.class);
		shopper.setUidPk(uidPk);
		shopper.setCustomer(customer);
		return shopper;
	}
	
	/**
	 * Creates the anonymous shopper.
	 *
	 * @param uidPk the uid pk
	 * @return the shopper memento
	 */
	private ShopperMemento createAnonymousShopper(final long uidPk) {
		ShopperMemento shopper = new ShopperMementoImpl();
		shopper.setUidPk(uidPk);
		return shopper;
	}
	
}
