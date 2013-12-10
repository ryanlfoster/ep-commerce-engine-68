/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security;

import com.elasticpath.commons.util.PasswordGenerator;

/**
 * <code>PasswordPolicy</code> interface is a basic interface for all password policies.
 */
public interface PasswordPolicy {
	/**
	 * Validates the given password. Information is supplied in a <code>PasswordHolder</code>.
	 * 
	 * @param passwordHolder <code>PasswordHolder</code>
	 * @return <code>ValidationResult</code> that contains <code>ValidationError</code>s.
	 */
	ValidationResult validate(PasswordHolder passwordHolder);

	/**
	 * Gets <code>PasswordGenerator</code> for this policy.
	 * 
	 * @return passwordGenerator configured password generator
	 */
	PasswordGenerator getPasswordGenerator();
}
