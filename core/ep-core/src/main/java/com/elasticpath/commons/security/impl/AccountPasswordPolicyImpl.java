/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security.impl;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;

/**
 * Implementation of <code>PasswordPolicy</code> that validates an account password for a <code>PasswordHolder</code>.
 */
public class AccountPasswordPolicyImpl extends AbstractPasswordPolicyImpl implements PasswordPolicy {

	@Override
	public ValidationResult validate(final PasswordHolder passwordHolder) {
		PasswordPolicy retryAttemptPasswordPolicy = getBeanFactory().getBean("retryAttemptPasswordPolicy");
		return retryAttemptPasswordPolicy.validate(passwordHolder);
	}

}
