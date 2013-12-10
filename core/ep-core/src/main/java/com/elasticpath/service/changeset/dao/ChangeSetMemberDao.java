package com.elasticpath.service.changeset.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Change set member dao.
 */
public interface ChangeSetMemberDao {

	/**
	 * Finds business object meta data by check set group id.
	 *
	 * @param guid the group id of check set
	 * @return the collection of business object meta data
	 */
	Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupId(String guid);

	/**
	 * Finds business object meta data by business object descriptor.
	 *
	 * @param businessObjectDescriptor business object descriptor
	 * @return the collection of business object meta data
	 */
	Collection<BusinessObjectMetadata> findBusinessObjectMetadataByDescriptor(BusinessObjectDescriptor businessObjectDescriptor);


	/**
	 * Removes business object and its meta data by object descriptor.
	 *
	 * @param objectDescriptor the business object descriptor
	 * @param objectGroupId change set id to remove object from
	 */
	void removeByObjectDescriptor(BusinessObjectDescriptor objectDescriptor, String objectGroupId);

	/**
	 * Adds business object with its meta data.
	 *
	 * @param groupMember business object
	 * @param metadata meta data of the business object
	 */
	void add(BusinessObjectGroupMember groupMember,	Collection<BusinessObjectMetadata> metadata);

	/**
	 * Generates change set group id.
	 *
	 * @return the change set group id
	 */
	String generateChangeSetGroupId();

	/**
	 * Removes change set members by group id.
	 *
	 * @param objectGroupId group id
	 */
	void removeChangeSetMembersByGroupId(String objectGroupId);

	/**
	 * Delegating method of BusinessObjectGroupDao.findGroupMembersByGroupId().
	 * @param groupId the group ID to use
	 * @return a collection of group members
	 */
	Collection<BusinessObjectGroupMember> findGroupMembersByGroupId(String groupId);

	/**
	 * Update and move objects between an origin and target change set.
	 * @param targetChangeSetGuid is the target change set guid
	 * @param sourceChangeSetGuid is the source change set guid
	 * @param checkedElementsCollection are the checked elements
	 */
	void updateAndMoveObjects(String sourceChangeSetGuid, String targetChangeSetGuid,
			Collection<BusinessObjectDescriptor> checkedElementsCollection);

	/**
	 *
	 * @param groupId the group ID
	 * @param startIndex the start index
	 * @param maxResults the max results
	 * @param sortingFields the sorting field
	 * @param loadTuner the load tuner
	 * @return a collection of change set members
	 */
	List<ChangeSetMember> findChangeSetMembersByGroupId(String groupId,
			int startIndex, int maxResults, DirectedSortingField [] sortingFields, LoadTuner loadTuner);

	/**
	 *
	 * @param groupId the group ID
	 * @param startIndex the start index
	 * @param maxResults the max results
	 * @param sortingFields the sorting field
	 * @param loadTuner the load tuner
	 * @param objectTypeFilter a list with String representing object types that will be filtered out the query
	 *
	 * @return a collection of change set members
	 */
	List<ChangeSetMember> findFilteredChangeSetMembersByGroupId(String groupId,
			int startIndex, int maxResults, DirectedSortingField [] sortingFields, LoadTuner loadTuner, List<String> objectTypeFilter);

	/**
	 *
	 * @param groupId the group ID
	 * @return the count of group members belonging to a group with the given groupId
	 */
	long getChangeSetMemberCount(String groupId);

	/**
	 *
	 * @param groupId the group ID
	 * @param objectTypeFilter the object type list filter
	 * @return the count of group members belonging to a group with the given groupId
	 */
	long getFilteredChangeSetMemberCount(String groupId, List<String> objectTypeFilter);

	/**
	 *
	 * @param changeSetGuid the change set GUID
	 * @param objectDescriptor the object descriptor
	 * @return a collection of {@link BusinessObjectMetadata} instances
	 */
	Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupIdAndDescriptor(String changeSetGuid,
			BusinessObjectDescriptor objectDescriptor);

	/**
	 * Adds or updates the metadata.
	 * @param businessObjectMetadata The data to update.
	 * @return the updated
	 */
	BusinessObjectMetadata addOrUpdateObjectMetadata(BusinessObjectMetadata businessObjectMetadata);

	/**
	 * Find business object metadata by group id and metadata key.
	 *
	 * @param guid the group id of the change set.
	 * @param key the key to the metadata
	 * @return the collection of business object meta data
	 */
	Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupIdAndMetadataKey(String guid, String key);

}
