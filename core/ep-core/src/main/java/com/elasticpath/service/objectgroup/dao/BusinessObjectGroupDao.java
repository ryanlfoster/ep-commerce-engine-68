/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.objectgroup.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;

/**
 * Allows for CRUD operations on business object groups and related classes.
 */
public interface BusinessObjectGroupDao {

	/**
	 * Adds a new group member to the data store.
	 * 
	 * @param objectGroupMember the object group member to add
	 */
	void addGroupMember(BusinessObjectGroupMember objectGroupMember);
	
	/**
	 * Removes an object group member represented by the given descriptor.
	 * 
	 * @param descriptor the descriptor holding the information about 
	 * the group member to be remove from the data store
	 * @param objectGroupId the change set id
	 */
	void removeGroupMember(BusinessObjectDescriptor descriptor, final String objectGroupId);
	
	/**
	 * Looks for a group member by its GUID.
	 * 
	 * @param guid the group member GUID to be used
	 * @return an instance of an object group member or null if none found
	 */
	BusinessObjectGroupMember findGroupMemberByGuid(String guid);
	
	/**
	 * Looks for all instances available in datastore for a specific group ID.
	 * 
	 * @param groupId the group ID to use
	 * @return a collection of group members
	 */
	Collection<BusinessObjectGroupMember> findGroupMembersByGroupId(String groupId);
			
	/**
	 * Looks for group members by their groupId and uses the other arguments to limit and sort the result.
	 * 
	 * @param groupId the group ID. Cannot be null.
	 * @param startIndex the start index
	 * @param maxResults the max number of results to be returned
	 * @param sortingFields sorting field to be used. Cannot be null.
	 * @return a collection of {@link BusinessObjectGroupMember}s or empty collection if none was found
	 */
	Collection<BusinessObjectGroupMember> findGroupMembersByGroupId(final String groupId, 
			final int startIndex, final int maxResults, final DirectedSortingField [] sortingFields);

	/**
	 * Looks for group members by their groupId and uses the other arguments to limit and sort the result.
	 * It also filters out object types specified in the objectTypeFilter
	 * 
	 * @param groupId the group ID. Cannot be null.
	 * @param startIndex the start index
	 * @param maxResults the max number of results to be returned
	 * @param sortingFields sorting field to be used. Cannot be null.
	 * @param objectTypeFilter a list with String representing object types that will be filtered out the query
	 * @return a collection of {@link BusinessObjectGroupMember}s or empty collection if none was found
	 */
	Collection<BusinessObjectGroupMember> findFilteredGroupMembersByGroupId(final String groupId, 
			final int startIndex, final int maxResults, final DirectedSortingField [] sortingFields, final List<String> objectTypeFilter);
	
	/**
	 * Check whether a group member referring to an object 
	 * described by <code>objectDescritor</code> exists in the system.
	 * 
	 * @param objectDescriptor the object descriptor
	 * @return true if a group member already exists
	 */
	boolean groupMemberExists(BusinessObjectDescriptor objectDescriptor);

	/**
	 * Finds all the group IDs an object exists in.
	 * 
	 * @param objectDescriptor the object descriptor
	 * @return a collection of group IDs
	 */
	Collection<String> findGroupIdsByDescriptor(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Generates a new group ID.
	 * 
	 * @return the unique group ID
	 */
	String generateGroupId();

	/**
	 * Removes all group members by the given group ID.
	 *  
	 * @param objectGroupId the object group ID
	 */
	void removeGroupMembersByGroupId(String objectGroupId);
	
	/**
	 * Finds group member by object descriptor.
	 * @param objectDescriptor the object descriptor
	 * @return the business object group member
	 */
	BusinessObjectGroupMember findGroupMemberByObjectDescriptor(final BusinessObjectDescriptor objectDescriptor);

	/**
	 * Gets the count of group members belonging to a group with groupId.
	 * 
	 * @param groupId the group ID
	 * @return the members count
	 */
	long getGroupMembersCount(String groupId);

	/**
	 * Gets the count of group members belonging to a group with groupId.
	 * 
	 * @param groupId the group ID
	 * @param objectTypeFilter the list of object types to be filter out of the results
	 * @return the members count
	 */
	long getFilteredGroupMembersCount(String groupId, List<String> objectTypeFilter);

}
