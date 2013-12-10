/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;

/**
 * This DAO works on change sets providing CRUD 
 * operations to the service layer. 
 */
public interface ChangeSetDao {

	/**
	 * Adds a new {@link ChangeSet} instance to the data store.
	 * 
	 * @param changeSet the change set to add
	 * @return the updated change set
	 */
	ChangeSet add(ChangeSet changeSet);
	
	/**
	 * Updates a change set to the data store.
	 * 
	 * @param changeSet the change set to update
	 * @return the updated instance
	 */
	ChangeSet update(ChangeSet changeSet);
	
	/**
	 * Removes a change set from the data store.
	 * 
	 * @param changeSetGuid the change set GUID to use
	 */
	void remove(String changeSetGuid);
	
	/**
	 * Returns all the change sets in the system.
	 * 
	 * @return a collection of change sets
	 */
	Collection<ChangeSet> findAllChangeSets();
	
	/**
	 * Looks for a change set by its GUID.
	 *  
	 * @param guid the guid to use
	 * @return the change set instance or null if none found
	 */
	ChangeSet findByGuid(String guid);

	/**
	 * Finds a change set by an object descriptor and change set states.
	 * 
	 * @param objectDescriptor the object descriptor
	 * @param states a collection of states
	 * @return a collection of change sets
	 */
	Collection<ChangeSet> findByObjectDescriptor(BusinessObjectDescriptor objectDescriptor, Collection<ChangeSetStateCode> states);

	/**
	 * Find all accessible change sets for a given user guid.
	 * @param userGuid is the user guid
	 * @return list of accessible change sets
	 */
	Collection<ChangeSet> findAllChangeSetsByUserGuid(String userGuid);

	/**
	 * Get list of available change set user views.
	 * 
	 * @param permissions the permissions the users must have assigned
	 * @return collection of change set user views
	 */
	Collection<ChangeSetUserView> getAvailableUsers(String... permissions);

	/**
	 * Return list of change set user views given a set of change set guids.
	 * @param changeSetUsersGuids change set guids
	 * @return a collection of change set user view objects
	 */
	Collection<ChangeSetUserView> getChangeSetUserViews(Collection<String> changeSetUsersGuids);

	/**
	 * Find Change Sets by search criteria. 
	 * @param criteria change set search criteria
	 * @return a collection of change sets
	 */
	Collection<ChangeSet> findByCriteria(ChangeSetSearchCriteria criteria);

	/**
	 * Filters change set GUIDs that exist and belong to change sets which are not in the provided states.
	 * 
	 * @param groupIds the group IDs to filter
	 * @param stateCodes the state codes the change sets should have
	 * @return a filtered collection of IDs which holds change sets of the specified state codes
	 */
	Collection<String> findAvailableChangeSets(Collection<String> groupIds, Collection<ChangeSetStateCode> stateCodes);
	
	/**
	 * Return GUID of change sets in the given states that the given object descriptor belongs to.
	 * 
	 * @param objectDescriptor the object descriptor to find
	 * @param stateCodes the state codes the change sets should have
	 * @return the guids of the change sets containing the given object descriptor
	 */
	Collection<String> findGuidByObjectDescriptor(final BusinessObjectDescriptor objectDescriptor, 
			final Collection<ChangeSetStateCode> stateCodes);

	/**
	 * Find a limited number of Change Sets by search criteria.
	 * 
	 * @param criteria change set search criteria
	 * @param start the start index for the results
	 * @param maxResults the maximum number of results
	 * @return a collection of change sets
	 */
	List<ChangeSet> findByCriteria(ChangeSetSearchCriteria criteria, int start, int maxResults);

	/**
	 * Get the total count of change sets matching the given criteria.
	 * 
	 * @param searchCriteria the criteria to match
	 * @return the number of results
	 */
	long getCountByCriteria(ChangeSetSearchCriteria searchCriteria);

}
