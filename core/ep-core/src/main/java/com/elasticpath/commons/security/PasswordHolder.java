/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.security;

import java.util.Date;
import java.util.List;

import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;

/**
 * <code>PasswordHolder</code> interface supplies all the information necessary for the password validation.
 */
public interface PasswordHolder {
	/**
	 * Gets the encrypted password.
	 * 
	 * @return the encrypted password.
	 */
	String getPassword();

	/**
	 * Gets the clear-text password (only available at the creation time).
	 * 
	 * @return the clear-text password.
	 */
	String getUserPassword();

	/**
	 * Gets the list of user's password history items.
	 * 
	 * @return the list of <code>UserPasswordHistoryItem</code> instances
	 */
	List<UserPasswordHistoryItem> getPasswordHistoryItems();

	/**
	 * Gets this user's last changed password date.
	 * 
	 * @return user's last changed password date.
	 */
	Date getLastChangedPasswordDate();

	/**
	 * Gets this user's failed login attempts number.
	 * 
	 * @return failed login attempts number
	 */
	int getFailedLoginAttempts();

	/**
	 * Gets this user's last login date.
	 * 
	 * @return user's last login date.
	 */
	Date getLastLoginDate();
}
