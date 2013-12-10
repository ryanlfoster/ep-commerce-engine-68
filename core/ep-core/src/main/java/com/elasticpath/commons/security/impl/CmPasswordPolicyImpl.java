/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.settings.SettingsService;

/**
 * Implementation of <code>CmPasswordPolicy</code> that validates the password for <code>CmUser</code>.
 */
public class CmPasswordPolicyImpl implements CmPasswordPolicy {

	private static final String MIN_PASSWORD_LENGTH = "COMMERCE/APPSPECIFIC/RCP/minimumPasswordLength";

	private SettingsService settingsService;

	private PasswordGenerator passwordGenerator;

	private final List<InternalPolicy> passwordPolicies;

	private BeanFactory beanFactory;

	/**
	 * <code>InternalPolicy</code>.
	 */
	private interface InternalPolicy {
		ValidationResult validate(PasswordHolder passwordHolder);
	}

	/**
	 * Constructs the instance of <code>CmPasswordPolicy</code>.
	 */
	public CmPasswordPolicyImpl() {
		super();
		passwordPolicies = new ArrayList<InternalPolicy>();
		passwordPolicies.add(minimumPasswordLength);
		passwordPolicies.add(maximumPasswordLength);
		passwordPolicies.add(containsAlphaNumeric);
		passwordPolicies.add(minimumNoRepeatPassword);
	}

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		return validate(passwordHolder, passwordPolicies);
	}

	private final InternalPolicy minimumPasswordLength = new InternalPolicy() {
		private static final String PASSWORD_VALIDATION_ERROR_MINIMUM_LENGTH = "PasswordValidationError_MinimumLength";

		@Override
		public ValidationResult validate(final PasswordHolder passwordHolder) {
			int minimumPasswordLength = Integer.valueOf(getSettingValue(MIN_PASSWORD_LENGTH));
			if (passwordHolder.getUserPassword().length() < minimumPasswordLength) {
				ValidationResult validationResult = new ValidationResult();
				validationResult.addError(new ValidationError(PASSWORD_VALIDATION_ERROR_MINIMUM_LENGTH, new Object[] { minimumPasswordLength }));
				return validationResult;
			}
			return ValidationResult.VALID;
		}
	};

	private final InternalPolicy maximumPasswordLength = new InternalPolicy() {
		@Override
		public ValidationResult validate(final PasswordHolder passwordHolder) {
			return ValidationResult.VALID;
		}
	};

	private final InternalPolicy containsAlphaNumeric = new InternalPolicy() {
		private static final String PASSWORD_VALIDATION_ERROR_CONTAINS_ALPHA_NUMERIC = "PasswordValidationError_ContainsAlphaNumeric";

		private static final String LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR = "(?=.*\\d)(?=.*([a-zA-Z])).*"; //$NON-NLS-1$

		private final Pattern pattern = Pattern.compile(LETTERS_AND_NUMBERS_REQUIRED_REG_EXPR);

		@Override
		public ValidationResult validate(final PasswordHolder passwordHolder) {
			final Matcher matcher = pattern.matcher(passwordHolder.getUserPassword());
			if (!matcher.matches()) {
				return createValidationResultWithError(PASSWORD_VALIDATION_ERROR_CONTAINS_ALPHA_NUMERIC);
			}
			return ValidationResult.VALID;
		}
	};

	private final InternalPolicy minimumNoRepeatPassword = new InternalPolicy() {
		private static final String PASSWORD_VALIDATION_ERROR_MINIMUM_NO_REPEAT_PASSWORD = "PasswordValidationError_MinimumNoRepeatPassword";

		private static final String PASSWORD_HISTORY_LENGTH = "COMMERCE/APPSPECIFIC/RCP/passwordHistoryLength";

		@Override
		public ValidationResult validate(final PasswordHolder passwordHolder) {
			final PasswordEncoder passwordEncoder = beanFactory.getBean(ContextIdNames.CM_PASSWORDENCODER);
			final String encodedPassword = passwordEncoder.encodePassword(passwordHolder.getUserPassword(), null);
			if (encodedPassword.equals(passwordHolder.getPassword())) {
				return createPasswordHistoryLengthValidationResult();
			}
			for (UserPasswordHistoryItem historyItem : passwordHolder.getPasswordHistoryItems()) {
				if (historyItem.getOldPassword().equals(encodedPassword)) {
					return createPasswordHistoryLengthValidationResult();
				}
			}
			return ValidationResult.VALID;
		}

		private ValidationResult createPasswordHistoryLengthValidationResult() {
			ValidationResult result = new ValidationResult();
			ValidationError error = new ValidationError(PASSWORD_VALIDATION_ERROR_MINIMUM_NO_REPEAT_PASSWORD, new Object[] { Integer
					.valueOf(getSettingValue(PASSWORD_HISTORY_LENGTH)) });
			result.addError(error);
			return result;
		}
	};

	/**
	 * @return settingsService
	 */
	public SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * @param settingsService <code>SettingsService</code>
	 */
	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Gets configured password generator. The <code>minimumPasswordLength</code> is set up properly for that impl.
	 * 
	 * @return passwordGenerator configured password generator
	 */
	public PasswordGenerator getPasswordGenerator() {
		passwordGenerator.setMinimumPasswordLength(Integer.valueOf(getSettingValue(MIN_PASSWORD_LENGTH)));
		return passwordGenerator;
	}

	/**
	 * @param passwordGenerator <code>passwordGenerator</code>
	 */
	public void setPasswordGenerator(final PasswordGenerator passwordGenerator) {
		this.passwordGenerator = passwordGenerator;
	}

	/**
	 * Gets setting value.
	 * 
	 * @param path the path of the setting
	 * @return the value for a given path and context
	 */
	protected String getSettingValue(final String path) {
		return settingsService.getSettingValue(path).getValue();
	}

	private ValidationResult createValidationResultWithError(final String errorKey) {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addError(new ValidationError(errorKey));
		return validationResult;
	}

	private ValidationResult validate(final PasswordHolder passwordHolder, final List<InternalPolicy> policies) {
		final List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

		for (final InternalPolicy policy : policies) {
			validationResults.add(policy.validate(passwordHolder));
		}

		final ValidationResult result = new ValidationResult();
		result.assembleResult(validationResults);

		return result;
	}

	@Override
	public int getPasswordHistoryLength() {
		return Integer.valueOf(settingsService.getSettingValue("COMMERCE/APPSPECIFIC/RCP/passwordHistoryLength").getValue());
	}

	/**
	 * Sets the bean factory object.
	 * 
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
