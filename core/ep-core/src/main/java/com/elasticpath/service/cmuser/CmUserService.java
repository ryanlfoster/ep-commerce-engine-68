/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.cmuser;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.commons.exception.EmailNonExistException;
import com.elasticpath.commons.exception.EmailSendException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide cmuser-related business service.
 */
public interface CmUserService extends EpPersistenceService {
	/**
	 * Adds the given cmUser.
	 * 
	 * @param cmUser the cmUser to add
	 * @return the persisted instance of cmUser
	 * @throws UserNameExistException - the specified user name is taken by another user.
	 * @throws EmailExistException - the specified email is taken by another user.
	 * @throws EmailSendException - email could not
	 */
	CmUser add(final CmUser cmUser) throws UserNameExistException, EmailExistException, EmailSendException;

	/**
	 * Updates the given cmUser.
	 * 
	 * @param cmUser the cmUser to update
	 * @return the updated CmUser object instance
	 * @throws UserNameExistException - the specified user name is taken by another user.
	 * @throws EmailExistException - the specified email is taken by another user.
	 */
	CmUser update(final CmUser cmUser) throws UserNameExistException, EmailExistException;

	/**
	 * Delete the cmUser.
	 * 
	 * @param cmUser the cmUser to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final CmUser cmUser) throws EpServiceException;

	/**
	 * Checks the given userName exists or not.
	 * 
	 * @param userName the userName
	 * @return true if the given userName exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean userNameExists(final String userName) throws EpServiceException;
	
	/**
	 * Updates user after successful login (update field lastLoginDate and failed login attempts).
	 * 
	 * @param cmUser the user to update
	 * @return updated user
	 */
	CmUser updateUserAfterLogin(final CmUser cmUser);

	/**
	 * Checks whether the given CmUser's userName exists in the database already, except when the userName is owned by the given CmUser.
	 * 
	 * @param cmUser the cmUser to check
	 * @return true if the given userName exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean userNameExists(final CmUser cmUser) throws EpServiceException;

	/**
	 * Checks whether the given CmUser's email address exists in the database already, except when the email address is owned by the given CmUser.
	 * 
	 * @param email the email address
	 * @return true if the given email exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean emailExists(final String email) throws EpServiceException;

	/**
	 * Check the given cmUser's email exists or not.
	 * 
	 * @param cmUser the cmUser to check
	 * @return true if the given email exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean emailExists(final CmUser cmUser) throws EpServiceException;

	/**
	 * List all cmUsers stored in the database.
	 * 
	 * @return a list of cmUsers
	 * @throws EpServiceException - in case of any errors
	 */
	List<CmUser> list() throws EpServiceException;
	
	/**
	 * Finds all user uids.
	 * 
	 * @return a list of uids
	 * @throws EpServiceException in case of any errors
	 */
	List<Long> findAllUids() throws EpServiceException;

	/**
	 * Lists a subset of CmUsers stored in the database.
	 * 
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @return a subset of the CmUsers stored in the database
	 * @throws EpServiceException in case of any errors
	 */
	List<CmUser> list(final int startIndex, final int maxResults) throws EpServiceException;

	/**
	 * Gets the count of CmUsers stored in the database.
	 * 
	 * @return the number of CmUsers stored in the database
	 * @throws EpServiceException - in case of any errors
	 */
	long count() throws EpServiceException;

	/**
	 * Load the cmUser with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param cmUserUid the cmUser UID
	 * @return the cmUser if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser load(final long cmUserUid) throws EpServiceException;

	/**
	 * Get the cmUser with the given UID. Return null if no matching record exists.
	 * 
	 * @param cmUserUid the cmUser UID
	 * @return the cmUser if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser get(final long cmUserUid) throws EpServiceException;

	/**
	 * Find the cmUser with the given email address.
	 * 
	 * @param email the cmUser email address
	 * @return the cmUser if email address exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser findByEmail(final String email) throws EpServiceException;
	
	/**
	 * Retrieves list of <code>CmUser</code> uids where the last modified date is later than
	 * the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>CmUser</code>s whose last modified date is later than the
	 *         specified date
	 */
	List<Long> findUidsByModifiedDate(final Date date);

	/**
	 * List the CmUsers who have a particular role assigned.
	 * 
	 * @param roleUidPk the role's uidPk
	 * @return list of CmUsers who have the role, otherwise an empty list
	 * @throws EpServiceException - in case of any errors
	 */
	List<CmUser> findByRoleId(final long roleUidPk) throws EpServiceException;

	/**
	 * Find the cmUser with the given userName.
	 * 
	 * @param userName the cmUser userName
	 * @return the cmUser if userName exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser findByUserName(final String userName) throws EpServiceException;
	
	/**
	 * Find the cmUser contains access info by the given userName.
	 * 
	 * @param userName the cmUser userName
	 * @return the cmUser if userName exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser findByUserNameWithAccessInfo(final String userName) throws EpServiceException;

	/**
	 * Generate a new password for the cmUser with the given email address and send the new password to the cmUser by email.
	 * 
	 * @param email the email address
	 * @return the cmUser
	 * @throws EmailNonExistException if the given email address doesn't exist
	 * @throws EmailSendException if email could not be sent
	 */
	CmUser resetUserPassword(final String email) throws EmailNonExistException;

	/**
	 * Finds a list of <code>CmUser</code> objects by the list of UIDs.
	 * 
	 * @param cmUserUids CmUser UIDs to search by
	 * @return a list of required CM users
	 */
	List<CmUser> findByUids(final Collection<Long> cmUserUids);
	
	/**
	 * Finds a user by its GUID.
	 * 
	 * @param guid the GUID
	 * @return the CmUser or null if not found 
	 * @throws EpServiceException - in case of any errors
	 */
	CmUser findByGuid(String guid);	

	/**
	 * Finds a user by its GUID and loads the user according to the fetch plan.
	 * 
	 * @param guid the GUID
	 * @param loadTuner - the associated load tuner
	 * @throws EpServiceException - in case of any errors
	 * @return the CmUser or null if not found 
	 */
	CmUser findByGuid(String guid, FetchGroupLoadTuner loadTuner);	
	/**
	 * Removes price lists assignments to the users by price list guid.
	 * 
	 * @param priceListGuid - price list guid
	 */
	void removePriceListFromUsers(final String priceListGuid); 
	
	
}
