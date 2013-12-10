/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;

/**
 * Implementation of <code>PasswordPolicy</code> that validates a password's maximum number of retry attempts for a <code>PasswordHolder</code>.
 */
public class RetryAttemptPasswordPolicyImpl extends AbstractPasswordPolicyImpl implements PasswordPolicy {

	/**
	 * Maximum Retry Attempts error key.
	 */
	public static final String PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS = "PasswordValidationError_MaximumRetryAttempts";

	private static final String ACCOUNT_LOCKOUT_THRESHOLD = "COMMERCE/APPSPECIFIC/RCP/accountLockoutThreshold";

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		if (passwordHolder.getFailedLoginAttempts() >= Integer.valueOf(getSettingValue(ACCOUNT_LOCKOUT_THRESHOLD))) {
			return createValidationResultWithError(PASSWORD_VALIDATION_ERROR_MAXIMUM_RETRY_ATTEMPTS);
		}
		return ValidationResult.VALID;
	}

}
