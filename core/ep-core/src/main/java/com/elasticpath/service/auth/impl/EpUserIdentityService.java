package com.elasticpath.service.auth.impl;

import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.commons.exception.UserIdNonExistException;
import com.elasticpath.service.auth.IdentityServiceException;
import com.elasticpath.service.auth.UserIdentityService;

/**
 * This is a default indentity service for EP authentication. As <code>Customer</code> will set/update
 * password through persistence automatically, so this implementation of <code>UserIdentityService</code>
 * is empty.
 */
public class EpUserIdentityService implements UserIdentityService {

	/**
	 * Create a new user identity.
	 * @param userId - the ID of the user identity to create
	 * @param password - the password to associate with the user identity
	 * @throws UserIdExistException - The userId already exists
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	public void add(final String userId, final String password) throws UserIdExistException, IdentityServiceException {
		//empty
	}

	/**
	 * Secure method for changing the password, checks authentication first.
	 * @param userId - the ID of the user identity to change the password of
	 * @param oldPassword - the old password for authentication purposes
	 * @param newPassword - the new password to set
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	public void changePassword(final String userId, final String oldPassword, final String newPassword)
											throws UserIdNonExistException, IdentityServiceException {
		//empty
	}

	/**
	 * Check whether the given user exists.
	 * @param userId - the ID of the user identity to check
	 * @return true if the user exists
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	public boolean exists(final String userId) throws IdentityServiceException {
		return false;
	}

	/**
	 * Remove a user identity.
	 * @param userId - the ID of the user identity to remove
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	public void remove(final String userId) throws UserIdNonExistException, IdentityServiceException {
		//empty
	}

	/**
	 * Change the password without requiring authentication, for use by administrators.
	 * @param userId - the ID of the user identity to set the password of
	 * @param password - the password to set
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	public void setPassword(final String userId, final String password) throws UserIdNonExistException, IdentityServiceException {
		//empty
	}

}
