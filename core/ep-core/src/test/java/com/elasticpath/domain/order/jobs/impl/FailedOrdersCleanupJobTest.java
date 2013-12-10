package com.elasticpath.domain.order.jobs.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Tests {@link FailedOrdersCleanupJob}.
 */
public class FailedOrdersCleanupJobTest {
	private static final int DAY = 24;
	private static final int MONTH = Calendar.DECEMBER;
	private static final int YEAR = 2011;

	private static final int MAX_HISTORY_IN_DAYS = 5;

	private static final int BATCH_SIZE = 100;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private OrderService orderService;
	private TimeService timeService;
	private SettingsReader settingsReader;
	private FailedOrdersCleanupJob job;
	
	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		orderService = context.mock(OrderService.class);
		timeService = context.mock(TimeService.class);
		settingsReader = context.mock(SettingsReader.class);
		job = new FailedOrdersCleanupJob();
		job.setOrderService(orderService);
		job.setSettingsReader(settingsReader);
		job.setTimeService(timeService);
	}
	
	/**
	 * Tests {@link FailedOrdersCleanupJob#getCandidateRemovalDate()}.
	 */
	@Test
	public void testGetCandidateRemovalDate() {
		final Date now = getDay(YEAR, MONTH, DAY); //using a fixed date instead of new Date() to make sure the code is using timeService
		final SettingValue maxHistorySettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settingsReader).getSettingValue(FailedOrdersCleanupJob.FAILED_ORDER_MAX_HISTORY); 
					will(returnValue(maxHistorySettingValue));
				allowing(maxHistorySettingValue).getIntegerValue();
					will(returnValue(MAX_HISTORY_IN_DAYS));
				atLeast(1).of(timeService).getCurrentTime(); will(returnValue(now));
			}
		});
		
		Date candidateRemovalDate = job.getCandidateRemovalDate();
		final Date expectedDay = getDay(YEAR, MONTH, DAY - MAX_HISTORY_IN_DAYS);
		
		assertTrue("candidate removal date should be '5' days before 'now' ", DateUtils.isSameDay(expectedDay, candidateRemovalDate));
	}
	
	
	/**
	 * Tests {@link FailedOrdersCleanupJob#getBatchSize()}.
	 */
	@Test
	public void testGetBatchSize() {
		final SettingValue batchSizeValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settingsReader).getSettingValue(FailedOrdersCleanupJob.FAILED_ORDER_BATCH_SIZE); 
					will(returnValue(batchSizeValue));
				allowing(batchSizeValue).getIntegerValue();
					will(returnValue(BATCH_SIZE));
			}
		});
		
		int batchSize = job.getBatchSize();
		assertEquals("batch size should come from the value in the settings", BATCH_SIZE, batchSize);
	}
	
	/**
	 * Tests {@link FailedOrdersCleanupJob#removeFailedOrders()}.
	 */
	@Test
	public void testRemoveFailedOrders() {
		final Date date = new Date();
		final FailedOrdersCleanupJob job = stubForDateAndBatchSize(date, BATCH_SIZE);
		final List<Long> list = Arrays.asList(0L);
		context.checking(new Expectations() {
			{	
				oneOf(orderService).getFailedOrderUids(date, BATCH_SIZE);
					will(returnValue(list));
				
				oneOf(orderService).deleteOrders(list);
			}
		});
		
		int removed = job.removeFailedOrders();
		assertEquals("the number of removed failed orders should be equal to the number of failed orders found", list.size(), removed);
	}

	private FailedOrdersCleanupJob stubForDateAndBatchSize(final Date removalDate, final int batchSize) {
		FailedOrdersCleanupJob job = new FailedOrdersCleanupJob() {
			@Override
			protected int getBatchSize() {
				return batchSize;
			}
			
			@Override
			protected Date getCandidateRemovalDate() {
				return removalDate;
			}
		};
		job.setOrderService(orderService);
		return job;
	}
	
	
	private static Date getDay(final int year, final int month, final int day) {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

}
