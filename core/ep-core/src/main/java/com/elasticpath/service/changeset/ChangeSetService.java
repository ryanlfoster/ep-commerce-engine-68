/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.ChangeSetDependencyDto;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpService;

/**
 * This service deals with adding/removing objects to/from a change set.
 */
public interface ChangeSetService extends EpService {

	/**
	 * Adds an object to a change set.
	 * 
	 * @param changeSetGuid the change set GUID, it cannot be null.
	 * @param object the object to add, it cannot be null.
	 * @param metadata the meta data map, it could be null if no meta data is applied for the business object
	 */
	void addObjectToChangeSet(String changeSetGuid, Object object, Map<String, String> metadata);
	
	/**
	 * Adds a new member object to a change set using the object descriptor.
	 * 
	 * @param changeSetGuid the change set code, it cannot be null.
	 * @param objectDescriptor the descriptor, it cannot be null.
	 * @param metadata the meta data map, it could be null if no meta data is applied for the business object 
	 */
	void addObjectToChangeSet(String changeSetGuid, BusinessObjectDescriptor objectDescriptor, Map<String, String> metadata);

	/**
	 * Adds a new member object to a change set using the object descriptor.
	 * 
	 * @param changeSetGuid the change set code, it cannot be null.
	 * @param objectDescriptor the descriptor, it cannot be null.
	 * @param metadata the meta data map, it could be null if no meta data is applied for the business object
	 * @param resolveMetadata indicate whether the metadata should be resolved 
	 */
	void addObjectToChangeSet(String changeSetGuid, BusinessObjectDescriptor objectDescriptor, Map<String, String> metadata, boolean resolveMetadata);
	
	/**
	 * Removes a member object from the change set specified by its code.
	 * 
	 * @param changeSetGuid the change set code
	 * @param objectDescriptor the descriptor to use
	 */
	void removeObjectFromChangeSet(String changeSetGuid, BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Removes a member object from the change set specified by its code.
	 * 
	 * @param changeSetGuid the change set code
	 * @param object the object to use remove
	 */
	void removeObjectFromChangeSet(String changeSetGuid, Object object);

	/**
	 * Retrieves the current status of an object.
	 * 
	 * @param objectDescriptor the descriptor specifying the object data
	 * @return the object status
	 */
	ChangeSetObjectStatus getStatus(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Retrieves the current status of an object.
	 * 
	 * @param object the domain object to determine the change set status for
	 * @return the object status
	 */
	ChangeSetObjectStatus getStatus(Object object);

	/**
	 * Finds change set member by object descriptor.
	 * 
	 * @param changeSetGuid the change set GUID the object belongs to
	 * @param objectDescriptor the object descriptor
	 * @return the change member
	 */
	Map<String, String> findChangeSetMemberMetadata(String changeSetGuid, BusinessObjectDescriptor objectDescriptor);

	/**
	 * Finds change set member by object descriptor.
	 * 
	 * @param changeSetGuid the change set GUID the object belongs to
	 * @param object the business object
	 * @return the change member
	 */
	Map<String, String> findChangeSetMemberMetadata(String changeSetGuid, Object object);

	/**
	 * Finds change set member by business object.
	 * 
	 * @param object the business object
	 * @return the change member 
	 */
	ChangeSet findChangeSet(Object object);

	/**
	 * Finds change set member by business object descriptor.
	 * 
	 * @param objectDescriptor the business object descriptor
	 * @return the change member 
	 */
	ChangeSet findChangeSet(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Get available users. (This needs to move to another service).
	 * 
	 * @param permissions the permissions the user must have assigned
	 * @return a list of change set users that have at least one of the given permissions.
	 * 			In case no permission is specified an illegal argument exception will be thrown
	 */
	Collection<ChangeSetUserView> getAvailableUsers(String... permissions);
	
	/**
	 * Get change set user view objects from a list of change set user guids.
	 * @param changeSetUsersGuids a collection of change set user guids
	 * @return collection of change set user view objects
	 */
	Collection<ChangeSetUserView> getChangeSetUserViews(Collection<String> changeSetUsersGuids);

	/**
	 * Finds the corresponding class for the provided object descriptor.
	 *  
	 * @param objectDescriptor the object descriptor
	 * @return the class or null if none exists
	 */
	Class< ? > findObjectClass(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Resolves the given object guid.
	 * 
	 * @param object The object to resolve.
	 * @return a BusinessObjectDescriptor of the given object, or null if not resolvable.
	 */
	String resolveObjectGuid(Object object);

	/**
	 * Looks for change set members in the specified range.
	 * 
	 * @param changeSetGuid the change set GUID
	 * @param startIndex the starting index
	 * @param maxResults maximum results to be returned
	 * @param sortingFields the fields to sort by
	 * @param loadTuner the load tuner
	 * @return a collection of change set members
	 */
	List<ChangeSetMember> findMembersByChangeSetGuid(String changeSetGuid, 
			int startIndex, 
			int maxResults, 
			DirectedSortingField [] sortingFields,
			LoadTuner loadTuner);

	/**
	 * Looks for change set members in the specified range.
	 * 
	 * @param changeSetGuid the change set GUID
	 * @param startIndex the starting index
	 * @param maxResults maximum results to be returned
	 * @param sortingFields the fields to sort by
	 * @param loadTuner the load tuner
	 * @param objectTypeFilter a list of object types to filter out from the query
	 * @return a collection of change set members
	 */
	List<ChangeSetMember> findFilteredMembersByChangeSetGuid(String changeSetGuid, 
			int startIndex, 
			int maxResults, 
			DirectedSortingField [] sortingFields,
			LoadTuner loadTuner,
			List<String> objectTypeFilter);

	/**
	 * Finds the count of all the members of a change set.
	 * 
	 * @param changeSetGuid the change set GUID
	 * @return number of members
	 */
	long getChangeSetMemberCount(String changeSetGuid);

	/**
	 * Finds the count of all the members of a change set.
	 * 
	 * @param groupId the group Id
	 * @param objectTypeFilter the list of object types to be filter out of the results
	 * @return number of members
	 */
	long getFilteredChangeSetMemberCount(String groupId, List<String> objectTypeFilter);

	/**
	 * Checks whether the object pointed by the given object descriptor exists in the specific change set.
	 * 
	 * @param groupId the change set group id
	 * @param objectDescriptor the object descriptor
	 * @return true if the object has not been deleted
	 */
	boolean objectExists(final String groupId, final BusinessObjectDescriptor objectDescriptor);

	/**
	 * determine is change set enabled.
	 * 
	 * @return true if change set is enabled
	 */
	boolean isChangeSetEnabled();

	/**
	 * Find the guid of an active change set by business object descriptor.
	 * 
	 * @param objectDescriptor the object descriptor to look for
	 * @return the guid of the active change set containing the object descriptor
	 */
	String findChangeSetGuid(final BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Find the guid of an active change set by object.
	 *  
	 * @param object the object to look for
	 * @return the guid of the active change set containing the object
	 */
	String findChangeSetGuid(final Object object);
	
	/**
	 * Retrieves map of locked objects with change sets they are assigned to.  
	 *
	 * @param objects - array of objects which statuses will be retrieved.
	 * @return map of objects and change set guids.
	 */
	Map<Object, String> getObjectsLocked(final Object[] objects);

	/**
	 * Find the dependent objects.
	 * 
	 * @param object the source object
	 * @return a set of business object descriptor which the source object depends on
	 */
	Set<BusinessObjectDescriptor> findDependentObjects(BusinessObjectDescriptor object);

	/**
	 * find changeSet by the business object descriptor and not in the excluded change set.
	 * 
	 * @param objects a set of business object descriptors
	 * @param excludedChangeSet the excluded change set
	 * @return a map of business object and changeset
	 */
	Map<BusinessObjectDescriptor, ChangeSet> findChangeSet(Set<BusinessObjectDescriptor> objects, ChangeSet excludedChangeSet);
	
	/**
	 * Update the resolved metadata for the members of the given change set.
	 * 
	 * @param changeSetGuid the guid of the change set whose members should be updated
	 */
	void updateResolvedMetadata(final String changeSetGuid);
	
	/**
	 * Get a list of change set objects that are dependent on members of the given change set. 
	 * 
	 * @param changeSet the change set.
	 * @param sortingField the sorting fields.
	 * @return the change set dependencies.
	 */
	List<ChangeSetDependencyDto> getChangeSetDependencies(final ChangeSet changeSet, final DirectedSortingField sortingField);

}
