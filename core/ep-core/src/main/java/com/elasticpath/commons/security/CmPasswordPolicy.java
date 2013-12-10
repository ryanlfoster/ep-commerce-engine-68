package com.elasticpath.commons.security;

/**
 * This is a basic interface for cm policies.
 */
public interface CmPasswordPolicy extends PasswordPolicy {
	/**
	 * Returns number of passwords without repetition in history including current CM user password.
	 * 
	 * @return password history length
	 */
	int getPasswordHistoryLength();
}
