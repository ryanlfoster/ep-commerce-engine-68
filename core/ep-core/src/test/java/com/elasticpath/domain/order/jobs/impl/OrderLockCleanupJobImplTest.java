package com.elasticpath.domain.order.jobs.impl;

import java.util.Date;

import org.jmock.Expectations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.misc.impl.DatabaseServerTimeServiceImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Unit tests for {@link OrderLockCleanupJob}.
 */
public class OrderLockCleanupJobImplTest  {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private OrderLockCleanupJobImpl orderLockCleanupJob;
	
	private SettingsService settingsService;
	
	private static final int MINS_BEFORE_DEFAULT = 10;
	
	private static final int BATCH_SIZE_DEFAULT = 10;
	
	private static final String ORDERLOCK_BATCH_SETTING = "COMMERCE/SYSTEM/ORDERLOCK/batchSize";
	
	private static final String ORDERLOCK_CLEANUP_SETTING = "COMMERCE/SYSTEM/ORDERLOCK/minsBeforeCleanUp";
	
	private TimeService timeService;
	
	/**
	 * Prepare for the testing of the order lock cleanup job.
	 */
	@Before
	public void setUp() {
		timeService = new DatabaseServerTimeServiceImpl() {

			/**
			 * @return current time
			 */
			@Override
			public Date getCurrentTime() {
				return new Date();
			}
			
		};
		orderLockCleanupJob = new OrderLockCleanupJobImpl();
		settingsService = context.mock(SettingsService.class);
		orderLockCleanupJob.setSettingsService(settingsService);
		orderLockCleanupJob.setTimeService(timeService);
		
	}
	
	/**
	 * Tests that a null pointer exception is thrown when the minutes until cleanup setting value that is returned by
	 * the setting service contains no value.
	 */
	@Test
	public void testGetOrderLockCleanupSettingNullException() {
		
		final SettingValue batchSetting = new SettingValueImpl();
		batchSetting.setIntegerValue(BATCH_SIZE_DEFAULT);
		
		final SettingValue minBeforeSetting = new SettingValueImpl();
		
		context.checking(new Expectations() { {
			oneOf(settingsService).getSettingValue(ORDERLOCK_CLEANUP_SETTING);
			will(returnValue(minBeforeSetting));
			oneOf(settingsService).getSettingValue(ORDERLOCK_BATCH_SETTING);
			will(returnValue(batchSetting));
			
		} });

		try {
			orderLockCleanupJob.cleanUpOrderLocks();
			fail();
		} catch (Exception e) {
			assertNotNull(e);
			//This is expected, no value was set for the setting value and therefore a null pointer exception
			//was expected to be thrown.
		}
	}
	
	/**
	 * Tests that a null pointer exception is thrown when the batch setting value that is returned by
	 * the setting service contains no value.
	 */
	@Test
	public void testGetOrderLockBatchSizeSettingNullException() {
		final SettingValue batchSetting = new SettingValueImpl();
		
		final SettingValue minBeforeSetting = new SettingValueImpl();
		minBeforeSetting.setIntegerValue(MINS_BEFORE_DEFAULT);
		
		context.checking(new Expectations() { {
			allowing(settingsService).getSettingValue(ORDERLOCK_CLEANUP_SETTING);
			will(returnValue(minBeforeSetting));
			
			allowing(settingsService).getSettingValue(ORDERLOCK_BATCH_SETTING);
			will(returnValue(batchSetting));
		} });
		
		try {
			orderLockCleanupJob.cleanUpOrderLocks();
			fail();
		} catch (Exception e) {
			assertNotNull(e);
			//This is expected, no value was set for the setting value and therefore a null pointer exception 
			//was expected to be thrown.
		}
	}
	
	/**
	 * Ensures that the isBatchSizeValid returns true if the value that is
	 * provided is of a positive value.
	 */
	@Test
	public void testIsBatchSizeValid() {
		final int batchSize = 5;
		assertEquals(true, orderLockCleanupJob.isBatchSizeValid(batchSize));
	}
	
	/**
	 * Ensures that the isBatchSizeValid returns false if the value that is 
	 * provided is of a negative value.
	 */
	@Test
	public void testIsBatchSizeValidNegative() {
		final int batchSize = -10;
		assertEquals(false, orderLockCleanupJob.isBatchSizeValid(batchSize));
	}
	
	/**
	 * Ensures that the isMinsToExpireLocksValid return true if the value that is
	 * provided is of a positive value.
	 */
	@Test
	public void testMinsToExpireLocksValid() {
		final int minsToExpire = 5;
		assertEquals(true, orderLockCleanupJob.isMinsToExpireLocksValid(minsToExpire));
	}
	
	/**
	 * Ensures that the isMinsToExpireLocksValid returns false if the value that is
	 * provided is of a negative value.
	 */
	@Test
	public void testMinsToExpireLocksValidNegative() {
		final int minsToExpire = -10;
		assertEquals(false, orderLockCleanupJob.isMinsToExpireLocksValid(minsToExpire));
	}
	
}
