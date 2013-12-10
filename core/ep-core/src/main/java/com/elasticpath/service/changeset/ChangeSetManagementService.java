package com.elasticpath.service.changeset;

import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * This service creates and manages change sets life cycle.
 */
public interface ChangeSetManagementService extends EpService {

	/**
	 * Add a change set to a data store.
	 * 
	 * @param changeSet the change set to add
	 * @return the updated change set
	 */
	ChangeSet add(ChangeSet changeSet);
	
	/**
	 * Delete a change set given a change set GUID.
	 * 
	 * @param changeSetGuid the change set GUID to use
	 */
	void remove(String changeSetGuid);
	
	/**
	 * Update an existing change set.
	 * 
	 * @param changeSet the change set to update
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return the updated change set with the member objects
	 */
	ChangeSet update(ChangeSet changeSet, LoadTuner loadTuner);	
	
	/**
	 * Find a change set by its GUID.
	 * 
	 * @param changeSetGuid the change set GUID to use
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return change set or null if none was found
	 */
	ChangeSet get(String changeSetGuid, LoadTuner loadTuner);
	
	/**
	 * Finds all change sets in the system.
	 * 
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return a list of change sets
	 */
	Collection<ChangeSet> findAllChangeSets(LoadTuner loadTuner);
	
	/**
	 * Find all accessible change sets for a given user.
	 * 
	 * @param userGuid is the user guid
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return a list of accessible change sets
	 */
	Collection<ChangeSet> findAllChangeSetsByUserGuid(String userGuid, LoadTuner loadTuner);


	/**
	 * Move a set of member objects from the origin change set to the target change set.
	 * @param originChangeSetGuid is the origin change set guid
	 * @param targetChangeSetGuid is the target change set guid
	 * @param checkedElementsCollection is the list of objects to move
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return a pair of change sets (origin, target)
	 */
	Pair<ChangeSet, ChangeSet> updateAndMoveObjects(String originChangeSetGuid, 
			                               		    String targetChangeSetGuid, 
			                               		    Collection<BusinessObjectDescriptor> checkedElementsCollection,
			                               		    LoadTuner loadTuner);
	
	/**
	 * Changes the state of a change set to the provided stateCode.
	 * 
	 * @param changeSetGuid the change set GUID. Must not be null.
	 * @param stateCode the new state code to be set. Must not be null.
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return the updated change set
	 * @throws EpServiceException when the changeSetGuid does not point to an existing {@link ChangeSet}.
	 */
	ChangeSet updateState(String changeSetGuid, ChangeSetStateCode stateCode, LoadTuner loadTuner) throws EpServiceException;
	
	
	/**
	 * Find Change Sets by search criteria. 
	 * 
	 * @param criteria change set search criteria
	 * @param loadTuner the load tuner to use to load the change sets
	 * @return a collection of change sets
	 */
	Collection<ChangeSet> findByCriteria(ChangeSetSearchCriteria criteria, LoadTuner loadTuner);

	/**
	 * Find a page of Change Sets by search criteria. 
	 * 
	 * @param criteria change set search criteria
	 * @param loadTuner the load tuner to use to load the change sets
	 * @param start the index of the first result to return
	 * @param maxResults the maximum number of results to return
	 * @return a collection of change sets
	 */
	List<ChangeSet> findByCriteria(final ChangeSetSearchCriteria criteria, final LoadTuner loadTuner,
			final int start, final int maxResults);

	/**
	 * Is the change set allowed to be changed. 
	 *
	 * @param changeSetGuid the change set guid
	 * @return true if change is allowed
	 */
	boolean isChangeAllowed(String changeSetGuid);
	
	/**
	 * Checks whether a change set could be removed from the system.
	 * 
	 * @param guid the change set GUID
	 * @return true if a change set could be removed
	 */
	boolean canRemove(String guid);

	/**
	 * Get the total count of change sets matching the given criteria.
	 * 
	 * @param searchCriteria the criteria to match
	 * @return the number of results
	 */
	long getCountByCriteria(ChangeSetSearchCriteria searchCriteria);

}
