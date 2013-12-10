/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.GroupExistException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * The default implementation of <code>CustomerGroupService</code>.
 */
public class CustomerGroupServiceImpl extends AbstractEpPersistenceServiceImpl
		implements CustomerGroupService {
	/**
	 * Adds the given customerGroup.
	 *
	 * @param customerGroup the customerGroup to add
	 * @return the persisted instance of customerGroup
	 * @throws GroupExistException - if a customerGroup with the specified name already exists.
	 */
	public CustomerGroup add(final CustomerGroup customerGroup) throws GroupExistException {
		sanityCheck();
		if (groupExists(customerGroup.getName())) {
			throw new GroupExistException(
					"Customer group with the name \"" + customerGroup + "\"already exists");
		}
		getPersistenceEngine().save(customerGroup);
		return customerGroup;
	}


	/**
	 * Updates the given customerGroup, first checking if a CustomerGroup with the same name already exists.
	 *
	 * @param customerGroup the customerGroup to update
	 * @return the updated instance of CustomerGroup
	 * @throws GroupExistException - if a customerGroup with the specified name already exists.
	 * @see CustomerGroup
	 */
	public CustomerGroup update(final CustomerGroup customerGroup) throws GroupExistException {
		sanityCheck();

		if (groupExists(customerGroup)) {
			throw new GroupExistException(
					"Customer group with the name \"" + customerGroup + "\"already exists");
		}
		return getPersistenceEngine().merge(customerGroup);
	}

	/**
	 * Delete the customerGroup.
	 *
	 * @param customerGroup the customerGroup to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final CustomerGroup customerGroup) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(customerGroup);
	}

	/**
	 * List all customerGroups stored in the database.
	 *
	 * @return a list of customerGroups
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public List<CustomerGroup> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("CUSTOMERGROUP_SELECT_ALL");
	}

	/**
	 * Load the customerGroup with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param customerGroupUid the customerGroup UID
	 *
	 * @return the customerGroup if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public CustomerGroup load(final long customerGroupUid) throws EpServiceException {
		sanityCheck();
		CustomerGroup customerGroup = null;
		if (customerGroupUid <= 0) {
			customerGroup = getBean(ContextIdNames.CUSTOMER_GROUP);
		} else {
			customerGroup = getPersistentBeanFinder().load(ContextIdNames.CUSTOMER_GROUP, customerGroupUid);
		}
		return customerGroup;
	}

	/**
	 * Get the customerGroup with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param customerGroupUid the customerGroup UID
	 *
	 * @return the customerGroup if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public CustomerGroup get(final long customerGroupUid) throws EpServiceException {
		sanityCheck();
		CustomerGroup customerGroup = null;
		if (customerGroupUid <= 0) {
			customerGroup = getBean(ContextIdNames.CUSTOMER_GROUP);
		} else {
			customerGroup = getPersistentBeanFinder().get(ContextIdNames.CUSTOMER_GROUP, customerGroupUid);
		}
		return customerGroup;
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
	 * Return the deafalt customerGroup, namely, the group with name "PUBLIC".
	 *
	 * @return the default customerGroup.
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public CustomerGroup getDefaultGroup() throws EpServiceException {
		return this.findByGroupName(CustomerGroup.DEFAULT_GROUP_NAME);
	}

	/**
	 * Check the given customer group's name exists or not.
	 *
	 * @param groupName the group name to check
	 * @return true if the given group name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean groupExists(final String groupName) throws EpServiceException {
		if (groupName == null) {
			return false;
		}
		final CustomerGroup customerGroup = this.findByGroupName(groupName);
		boolean nameExists = false;
		if (customerGroup != null) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Check if a different customer group with the given customer group's name exists exists or not.
	 *
	 * @param customerGroup - the customerGroup to check
	 * @return true if a different customer group with the same name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean groupExists(final CustomerGroup customerGroup) throws EpServiceException {
		if (customerGroup.getName() == null) {
			return false;
		}
		final CustomerGroup existingCG = this.findByGroupName(customerGroup.getName());
		boolean groupExists = false;
		if (existingCG != null && existingCG.getUidPk() != customerGroup.getUidPk()) {
			groupExists = true;
		}
		return groupExists;
	}

	/**
	 * Find the customer group with the given group name.
	 *
	 * @param groupName - the customer group name
	 *
	 * @return the customerGroup with the given name if exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public CustomerGroup findByGroupName(final String groupName) throws EpServiceException {
		sanityCheck();
		if (groupName == null) {
			throw new EpServiceException("Cannot retrieve null groupName.");
		}

		final List<CustomerGroup> results = getPersistenceEngine().retrieveByNamedQuery("CUSTOMERGROUP_FIND_BY_NAME", groupName);
		CustomerGroup customerGroup = null;
		if (results.size() == 1) {
			customerGroup = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException(
					"Inconsistent data -- duplicate customer group name exist -- "
							+ groupName);
		}
		return customerGroup;
	}
	
	@Override
	public CustomerGroup findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		final List<CustomerGroup> results = getPersistenceEngine().retrieveByNamedQuery("CUSTOMERGROUP_FIND_BY_GUID", guid);
		CustomerGroup customerGroup = null;
		if (results.size() == 1) {
			customerGroup = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate customer group guid exists -- " + guid);
		}
		return customerGroup;
	}
}
