package com.elasticpath.service.cmuser;

import java.util.List;

import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide userRole-related business service.
 */
public interface UserRoleService extends EpPersistenceService {

	/**
	 * Adds the given userRole.
	 *
	 * @param userRole the userRole to add
	 * @return the persisted instance of userRole
	 * @throws UserRoleNameExistException - if userRole with the specified name already exists
	 */
	UserRole add(final UserRole userRole) throws UserRoleNameExistException;

	/**
	 * Updates the given userRole.
	 *
	 * @param userRole the userRole to update
	 * @return the updated instance of userRole
	 * @throws UserRoleNameExistException - if userRole with the specified name already exists
	 */
	UserRole update(final UserRole userRole) throws UserRoleNameExistException;

	/**
	 * Delete the userRole.
	 *
	 * @param userRole the userRole to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final UserRole userRole) throws EpServiceException;


	/**
	 * Load the userRole with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param userRoleUid the userRole UID
	 *
	 * @return the userRole if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	UserRole load(final long userRoleUid) throws EpServiceException;

	/**
	 * Get the userRole with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param userRoleUid the userRole UID
	 *
	 * @return the userRole if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	UserRole get(final long userRoleUid) throws EpServiceException;

	/**
	 * Find the userRole with the given name.
	 *
	 * @param name the userRole name.
	 * @return the userRole that matches the given name, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	UserRole findByName(final String name) throws EpServiceException;


	/**
	 * Find the {@link UserRole} with the given guid.
	 *
	 * @param guid the {@link UserRole} guid.
	 * @return the {@link UserRole} if name exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	UserRole findByGuid(final String guid) throws EpServiceException;
	
	/**
	 * Checks whether the given userRole name exists or not.
	 *
	 * @param name the userRole name.
	 * @return true if the given name exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	boolean nameExists(final String name) throws EpServiceException;

	/**
	 * Check whether the given userRole's name exists or not.
	 *
	 * @param userRole the userRole to check
	 * @return true if a different userRole with the given userRole's name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean nameExists(final UserRole userRole) throws EpServiceException;

	/**
	 * Lists all userRoles stored in the database.
	 *
	 * @return a list of userRoles
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<UserRole> list() throws EpServiceException;

	/**
	 * Lists a subset of UserRoles stored in the database.
	 *
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @return a subset of the UserRoles stored in the database
	 * @throws EpServiceException in case of any errors
	 */
	List<UserRole> list(final int startIndex, final int maxResults) throws EpServiceException;

	/**
	 * Get the list of uids of <code>UserRole</code> used by existing <code>CmUser</code>s.
	 * @return the list of uids of <code>UserRole</code>s in use.
	 */
	List<Long> getUserRoleInUseUidList();
}