/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.security.impl;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

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
 * Test <code>MaximumAgePasswordPolicyImpl</code>.
 */
public class MaximumAgePasswordPolicyImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final int DEFAULT_MAXIMUM_PASSWORD_AGE = 90;

	private static final String MAXIMUM_PASSWORD_AGE = "COMMERCE/APPSPECIFIC/RCP/maximumPasswordAge";


	/**
	 * Test method for valid maximum password age.
	 */
	@Test
	public void testValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createMaximumAgePasswordPolicy(DEFAULT_MAXIMUM_PASSWORD_AGE);
		Calendar calendar = Calendar.getInstance();
		Date lastLoginDate = calendar.getTime();
		calendar.add(Calendar.DATE, -DEFAULT_MAXIMUM_PASSWORD_AGE + 1);
		Date lastChangedPasswordDate = calendar.getTime();
		PasswordHolder passwordHolder = createMockPasswordHolder(lastChangedPasswordDate, lastLoginDate);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertEquals(false, result.containsError(MaximumAgePasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE));
		assertEquals(true, result.isValid());
	}

	/**
	 * Test method for invalid maximum password age.
	 */
	@Test
	public void testinValidRetryAttempt() {
		PasswordPolicy passwordPolicy = createMaximumAgePasswordPolicy(DEFAULT_MAXIMUM_PASSWORD_AGE);
		Calendar calendar = Calendar.getInstance();
		Date lastLoginDate = calendar.getTime();
		calendar.add(Calendar.DATE, -DEFAULT_MAXIMUM_PASSWORD_AGE);
		Date lastChangedPasswordDate = calendar.getTime();
		PasswordHolder passwordHolder = createMockPasswordHolder(lastChangedPasswordDate, lastLoginDate);

		ValidationResult result = passwordPolicy.validate(passwordHolder);
		assertEquals(true, result.containsError(MaximumAgePasswordPolicyImpl.PASSWORD_VALIDATION_ERROR_MAXIMUM_PASSWORD_AGE));
		assertEquals(false, result.isValid());
	}

	private MaximumAgePasswordPolicyImpl createMaximumAgePasswordPolicy(final int maximumPasswordAge) {
		MaximumAgePasswordPolicyImpl passwordPolicy = new MaximumAgePasswordPolicyImpl();
		SettingsService mockSettingsService = context.mock(SettingsService.class);
		setSettingValueInMockSettingsService(mockSettingsService, MAXIMUM_PASSWORD_AGE, String.valueOf(maximumPasswordAge));
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

	private PasswordHolder createMockPasswordHolder(final Date lastChangedPasswordDate, final Date lastLoginDate) {
		final PasswordHolder mockPasswordHolder = context.mock(PasswordHolder.class);
		context.checking(new Expectations() {
			{
				allowing(mockPasswordHolder).getLastChangedPasswordDate();
				will(returnValue(lastChangedPasswordDate));

				allowing(mockPasswordHolder).getLastLoginDate();
				will(returnValue(lastLoginDate));
			}
		});
		return mockPasswordHolder;
	}

}
