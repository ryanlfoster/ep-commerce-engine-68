package com.elasticpath.service.cmuser.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cmuser.UserRoleNameExistException;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Test <code>UserRoleServiceImpl</code>.
 */
public class UserRoleServiceImpl extends AbstractEpPersistenceServiceImpl implements UserRoleService {

	/**
	 * Adds the given userRole.
	 *
	 * @param userRole the userRole to add
	 * @return the persisted instance of userRole
	 * @throws UserRoleNameExistException - if userRole with the specified name already exists
	 */
	public UserRole add(final UserRole userRole) throws UserRoleNameExistException {
		sanityCheck();
		if (nameExists(userRole.getName())) {
			throw new UserRoleNameExistException(
					"UserRole with the given name already exists.");
		}

		getPersistenceEngine().save(userRole);
		return userRole;
	}

	/**
	 * Updates the given userRole.
	 *
	 * @param userRole the userRole to update
	 * @return the updated instance of userRole
	 * @throws UserRoleNameExistException - if userRole with the specified name already exists
	 */
	public UserRole update(final UserRole userRole) throws UserRoleNameExistException {
		sanityCheck();

		if (nameExists(userRole)) {
			throw new UserRoleNameExistException("UserRole with the given name already exists.");
		}

		return getPersistenceEngine().merge(userRole);
	}

	/**
	 * Delete the userRole.
	 *
	 * @param userRole the userRole to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final UserRole userRole) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(userRole);
	}


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
	public UserRole load(final long userRoleUid) throws EpServiceException {
		sanityCheck();
		UserRole userRole = null;
		if (userRoleUid <= 0) {
			userRole = getBean(ContextIdNames.USER_ROLE);
		} else {
			userRole = getPersistentBeanFinder().load(ContextIdNames.USER_ROLE, userRoleUid);
		}
		return userRole;
	}

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
	public UserRole get(final long userRoleUid) throws EpServiceException {
		sanityCheck();
		UserRole userRole = null;
		if (userRoleUid <= 0) {
			userRole = getBean(ContextIdNames.USER_ROLE);
		} else {
			userRole = getPersistentBeanFinder().get(ContextIdNames.USER_ROLE, userRoleUid);
		}
		return userRole;
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid
	 *            the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Find the UserRole with the given name.
	 *
	 * @param name the userRole name.
	 * @return the userRole if name exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public UserRole findByName(final String name) throws EpServiceException {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve null name.");
		}

		final List<UserRole> results = getPersistenceEngine().retrieveByNamedQuery("USERROLE_FIND_BY_NAME", name);
		UserRole userRole = null;
		if (results.size() == 1) {
			userRole = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate userRole name exist -- " + name);
		}
		return userRole;
	}

	@Override
	public UserRole findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		final List<UserRole> results = getPersistenceEngine().retrieveByNamedQuery("USERROLE_FIND_BY_GUID", guid);
		UserRole userRole = null;
		if (results.size() == 1) {
			userRole = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate userRole guid exists -- " + guid);
		}
		return userRole;
	}
	
	/**
	 * Checks whether the given userRole name exists or not.
	 *
	 * @param name the userRole name.
	 * @return true if the given name exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public boolean nameExists(final String name) throws EpServiceException {
		if (name == null) {
			return false;
		}
		final UserRole userRole = this.findByName(name);
		boolean nameExists = false;
		if (userRole != null) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Check whether the given userRole's name exists, discounting the given userRole.
	 *
	 * @param userRole the userRole to check
	 * @return true if a different userRole with the given userRole's name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean nameExists(final UserRole userRole) throws EpServiceException {
		if (userRole.getName() == null) {
			return false;
		}
		final UserRole existingUserRole = this.findByName(userRole.getName());
		boolean nameExists = false;
		if (existingUserRole != null && existingUserRole.getUidPk() != userRole.getUidPk()) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Lists all userRoles stored in the database.
	 *
	 * @return a list of userRoles
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<UserRole> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("USERROLE_SELECT_ALL");
	}

	/**
	 * Lists a subset of <code>UserRole</code>s stored in the database.
	 *
	 * @param startIndex the index of the start of the subset
	 * @param maxResults the maximum number of records to return
	 * @return a subset of the <code>UserRole</code>s stored in the database
	 * @throws EpServiceException in case of any errors
	 */
	public List<UserRole> list(final int startIndex, final int maxResults) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("USERROLE_SELECT_ALL", startIndex, maxResults);
	}

	/**
	 * Get the list of uids of <code>UserRole</code> used by existing <code>CmUser</code>s.
	 * @return the list of uids of <code>UserRole</code>s in use.
	 */
	public List<Long> getUserRoleInUseUidList() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("USERROLE_UID_IN_USE");
	}
}
