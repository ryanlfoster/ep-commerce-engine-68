/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.security.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test <code>RetryAttemptPasswordPolicyImpl</code>.
 */
public class RetryAttemptPasswordPolicyImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD = 4;

	private static final String ACCOUNT_LOCKOUT_THRESHOLD = "COMMERCE/APPSPECIFIC/RCP/accountLockoutThreshold";


	/**
	 * Test method for valid retry attempt.
	 */
	@Test
	public void testValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createRetryAttemptPasswordPolicy(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);
		PasswordHolder passwordHolder = createMockPasswordHolder(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD - 1);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertEquals(false, result.containsError(RetryAttemptPasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS));
		assertEquals(true, result.isValid());
	}

	/**
	 * Test method for invalid retry attempt.
	 */
	@Test
	public void testinValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createRetryAttemptPasswordPolicy(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);
		PasswordHolder passwordHolder = createMockPasswordHolder(DEFAULT_ACCOUNT_LOCKOUT_THRESHOLD);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertEquals(true, result.containsError(RetryAttemptPasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS));
		assertEquals(false, result.isValid());
	}

	private RetryAttemptPasswordPolicyImpl createRetryAttemptPasswordPolicy(final int accountLockoutThreshold) {
		RetryAttemptPasswordPolicyImpl passwordPolicy = new RetryAttemptPasswordPolicyImpl();
		SettingsService mockSettingsService = context.mock(SettingsService.class);
		setSettingValueInMockSettingsService(mockSettingsService, ACCOUNT_LOCKOUT_THRESHOLD, String.valueOf(accountLockoutThreshold));
		passwordPolicy.setSettingsService(mockSettingsService);

		return passwordPolicy;
	}

	private void setSettingValueInMockSettingsService(final SettingsService mockSettingsService, final String settingName, final String returnValue) {
		final SettingValue settingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue();
				will(returnValue(returnValue));

				allowing(mockSettingsService).getSettingValue(settingName);
				will(returnValue(settingValue));
			}
		});
	}

	private PasswordHolder createMockPasswordHolder(final int failedLoginAttempts) {
		final PasswordHolder mockPasswordHolder = context.mock(PasswordHolder.class);
		context.checking(new Expectations() {
			{
				allowing(mockPasswordHolder).getFailedLoginAttempts();
				will(returnValue(failedLoginAttempts));
			}
		});
		return mockPasswordHolder;
	}

}
