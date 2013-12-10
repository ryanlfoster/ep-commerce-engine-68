package com.elasticpath.service.auth;

import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.commons.exception.UserIdNonExistException;

/**
 * <code>UserIdentityService</code> provides services for managing a user
 * identity, which is the principal used for authentication.
 */
public interface UserIdentityService {

	/**
	 * Create a new user identity.
	 * @param userId - the ID of the user identity to create
	 * @param password - the password to associate with the user identity
	 * @throws UserIdExistException - The userId already exists
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	void add(String userId, String password) throws UserIdExistException, IdentityServiceException;

	/**
	 * Secure method for changing the password, checks authentication first.
	 * @param userId - the ID of the user identity to change the password of
	 * @param oldPassword - the old password for authentication purposes
	 * @param newPassword - the new password to set
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	void changePassword(String userId, String oldPassword, String newPassword) throws UserIdNonExistException, IdentityServiceException;

	/**
	 * Check whether the given user exists.
	 * @param userId - the ID of the user identity to check
	 * @return true if the user exists
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	boolean exists(String userId) throws IdentityServiceException;

	/**
	 * Remove a user identity.
	 * @param userId - the ID of the user identity to remove
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	void remove(String userId) throws UserIdNonExistException, IdentityServiceException;

	/**
	 * Change the password without requiring authentication, for use by administrators.
	 * @param userId - the ID of the user identity to set the password of
	 * @param password - the password to set
	 * @throws UserIdNonExistException - The user does not exist
	 * @throws IdentityServiceException - A problem occured servicing the add request
	 */
	void setPassword(String userId, String password) throws UserIdNonExistException, IdentityServiceException;

}
