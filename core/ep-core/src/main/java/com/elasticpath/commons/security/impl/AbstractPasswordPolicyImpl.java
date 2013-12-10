/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationError;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.settings.SettingsService;

/**
 * Base abstract implementation of <code>CmPasswordPolicy</code> that validates the password for a <code>PasswordHolder</code>.
 */
public abstract class AbstractPasswordPolicyImpl implements PasswordPolicy {

	private static final String MIN_PASSWORD_LENGTH = "COMMERCE/APPSPECIFIC/RCP/minimumPasswordLength";

	private BeanFactory beanFactory;

	private SettingsService settingsService;

	private PasswordGenerator passwordGenerator;

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

	/**
	 * Creates a ValidationResult containing one error.
	 * 
	 * @param errorKey the error key
	 * @return the validation result
	 */
	protected ValidationResult createValidationResultWithError(final String errorKey) {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addError(new ValidationError(errorKey));
		return validationResult;
	}

	/**
	 * Returns the bean factory object.
	 * 
	 * @return the bean factory object.
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
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
