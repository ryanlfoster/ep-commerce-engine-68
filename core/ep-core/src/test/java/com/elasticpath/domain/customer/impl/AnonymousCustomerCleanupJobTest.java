/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.service.customer.AnonymousCustomerCleanupService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;


/**
 * Test for {@link AnonymousCustomerCleanupJob}.
 */
public class AnonymousCustomerCleanupJobTest {

	private static final String ANONYMOUS_CUSTOMER_MAX_HISTORY = "COMMERCE/SYSTEM/ANONYMOUSCUSTOMERCLEANUP/maxHistory";
	private static final String ANONYMOUS_CUSTOMER_BATCH_SIZE = "COMMERCE/SYSTEM/ANONYMOUSCUSTOMERCLEANUP/batchSize";
	private static final int EXPIRY_DAYS = 5;
	private static final int BATCH_SIZE = 10;
	private static final int REMOVED_CUSTOMERS = BATCH_SIZE;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private AnonymousCustomerCleanupJob anonymousCustomerCleanupJob;
	private AnonymousCustomerCleanupService anonymousCustomerCleanupService;
	private SettingsReader settingsReader;
	private TimeService timeService;

	/**
	 * Initialize mocks and object under test.
	 */
	@Before
	public void initializeMocksAndObjectUnderTest() {
		anonymousCustomerCleanupService = context.mock(AnonymousCustomerCleanupService.class);
		settingsReader = context.mock(SettingsReader.class);
		timeService = context.mock(TimeService.class);
		anonymousCustomerCleanupJob = new AnonymousCustomerCleanupJob();
		anonymousCustomerCleanupJob.setAnonymousCustomerCleanupService(anonymousCustomerCleanupService);
		anonymousCustomerCleanupJob.setSettingsReader(settingsReader);
		anonymousCustomerCleanupJob.setTimeService(timeService);
	}

	/**
	 * Test successful anonymous customer purge.
	 */
	@Test
	public void testSuccessfulAnonymousCustomerPurge() {
		Date now = new Date();
		shouldGetMaxHistoryOf(EXPIRY_DAYS);
		shouldGetBatchSizeOf(BATCH_SIZE);
		shouldGetCurrentTime(now);
		Date expectedExpiryDate = DateUtils.addDays(now, -EXPIRY_DAYS);
		shouldDeleteAnonymousCustomers(expectedExpiryDate, BATCH_SIZE, REMOVED_CUSTOMERS);

		int result = anonymousCustomerCleanupJob.purgeAnonymousCustomers();
		assertEquals("The expected number of customers were removed.", REMOVED_CUSTOMERS, result);
	}

	/**
	 * Verify removal date calculation.
	 */
	@Test
	public void verifyRemovalDateCalculation() {
		Date now = new Date();
		Date expiredDate = DateUtils.addDays(now, -EXPIRY_DAYS);
		SettingValue expirySettingValue = createSettingValue(EXPIRY_DAYS);

		shouldGetSettingValue(ANONYMOUS_CUSTOMER_MAX_HISTORY, expirySettingValue);
		shouldGetCurrentTime(now);

		Date result = anonymousCustomerCleanupJob.getCandidateRemovalDate();
		assertEquals("The resulting date should be as expected.", expiredDate, result);
	}

	/**
	 * Verify batch size.
	 */
	@Test
	public void verifyBatchSize() {
		SettingValue batchSettingValue = createSettingValue(BATCH_SIZE);

		shouldGetSettingValue(ANONYMOUS_CUSTOMER_BATCH_SIZE, batchSettingValue);

		int result = anonymousCustomerCleanupJob.getBatchSize();
		assertEquals("The resulting value should be as expected.", BATCH_SIZE, result);
	}

	private void shouldGetBatchSizeOf(final int batchSize) {
		SettingValue batchSettingValue = createSettingValue(batchSize);
		shouldGetSettingValue(ANONYMOUS_CUSTOMER_BATCH_SIZE, batchSettingValue);
	}

	private void shouldGetMaxHistoryOf(final int expiryDays) {
		SettingValue expirySettingValue = createSettingValue(expiryDays);
		shouldGetSettingValue(ANONYMOUS_CUSTOMER_MAX_HISTORY, expirySettingValue);
	}

	private void shouldDeleteAnonymousCustomers(final Date expiredDate, final int batchSize, final int result) {
		context.checking(new Expectations() {
			{
				oneOf(anonymousCustomerCleanupService).deleteAnonymousCustomers(expiredDate, batchSize);
				will(returnValue(result));
			}
		});
	}

	private void shouldGetCurrentTime(final Date date) {
		context.checking(new Expectations() {
			{
				oneOf(timeService).getCurrentTime();
				will(returnValue(date));
			}
		});
	}

	private void shouldGetSettingValue(final String path, final SettingValue settingValue) {
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(path);
				will(returnValue(settingValue));
			}
		});
	}

	private SettingValue createSettingValue(final int value) {
		SettingValue result = new SettingValueImpl();
		result.setIntegerValue(value);
		return result;
	}
}
