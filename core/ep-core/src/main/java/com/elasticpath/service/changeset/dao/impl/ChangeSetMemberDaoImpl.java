package com.elasticpath.service.changeset.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;
/**
 * The implementation class of ChangeSetMemberDao.
 */
public class ChangeSetMemberDaoImpl implements ChangeSetMemberDao {

	private PersistenceEngine persistenceEngine;

	private BusinessObjectGroupDao businessObjectGroupDao;

	private ChangeSetHelper changeSetHelper;

	@Override
	public Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupId(
			final String groupId) {
		return persistenceEngine.retrieveByNamedQuery("FIND_OBJECT_METADATA_BY_GROUPID", groupId);
	}

	/**
	 * Sets the persistence engine to use.
	 *
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	private void addObjectMetadata(final BusinessObjectMetadata businessObjectMetadata) {
		persistenceEngine.save(businessObjectMetadata);
	}

	@Override
	public BusinessObjectMetadata addOrUpdateObjectMetadata(final BusinessObjectMetadata businessObjectMetadata) {
		return persistenceEngine.saveOrUpdate(businessObjectMetadata);
	}

	@Override
	public void removeByObjectDescriptor(
			final BusinessObjectDescriptor objectDescriptor, final String objectGroupId) {
		persistenceEngine.executeNamedQuery("DELETE_OBJECT_METADATA_BY_OBJ_TYPE_AND_ID_AND_GROUP_ID",
				objectDescriptor.getObjectType(), objectDescriptor.getObjectIdentifier(), objectGroupId);

		businessObjectGroupDao.removeGroupMember(objectDescriptor, objectGroupId);
	}

	private void removeObjectMetadataByGroupId(final String objectGroupId) {
		persistenceEngine.executeNamedQuery("DELETE_OBJECT_METADATA_BY_GROUP_ID",
				objectGroupId);
	}

	@Override
	public void add(final BusinessObjectGroupMember groupMember, final Collection<BusinessObjectMetadata> metadata) {
		businessObjectGroupDao.addGroupMember(groupMember);

		for (BusinessObjectMetadata businessObjectMetadata : metadata) {
			addObjectMetadata(businessObjectMetadata);
		}
	}

	@Override
	public String generateChangeSetGroupId() {
		return businessObjectGroupDao.generateGroupId();
	}

	@Override
	public void removeChangeSetMembersByGroupId(final String objectGroupId) {
		removeObjectMetadataByGroupId(objectGroupId);

		businessObjectGroupDao.removeGroupMembersByGroupId(objectGroupId);
	}

	/**
	 * Sets business object group dao.
	 *
	 * @param businessObjectGroupDao the instance of business object group dao
	 */
	public void setBusinessObjectGroupDao(final BusinessObjectGroupDao businessObjectGroupDao) {
		this.businessObjectGroupDao = businessObjectGroupDao;
	}

	@Override
	public Collection<BusinessObjectMetadata> findBusinessObjectMetadataByDescriptor(
			final BusinessObjectDescriptor businessObjectDescriptor) {
		return persistenceEngine.retrieveByNamedQuery("FIND_OBJECT_METADATA_BY_OBJ_TYPE_AND_ID",
				businessObjectDescriptor.getObjectType(),
				businessObjectDescriptor.getObjectIdentifier());
	}

	@Override
	public Collection<BusinessObjectGroupMember> findGroupMembersByGroupId(final String groupId) {
		return businessObjectGroupDao.findGroupMembersByGroupId(groupId);
	}

	@Override
	public void updateAndMoveObjects(final String sourceChangeSetGuid, final String targetChangeSetGuid,
									 final Collection<BusinessObjectDescriptor> checkedElementsCollection) {

		for (BusinessObjectDescriptor businessObjectDescriptor : checkedElementsCollection) {
			persistenceEngine.executeNamedQuery("UPDATE_CHANGESETOBJECTS_FOR_GUIDS",
					targetChangeSetGuid, sourceChangeSetGuid,
					businessObjectDescriptor.getObjectType(),
					businessObjectDescriptor.getObjectIdentifier());
		}
	}

	@Override
	public List<ChangeSetMember> findChangeSetMembersByGroupId(final String groupId, final int startIndex,
			final int maxResults, final DirectedSortingField [] sortingFields, final LoadTuner loadTuner) {
		return findFilteredChangeSetMembersByGroupId(groupId, startIndex, maxResults, sortingFields, loadTuner, null);
	}

	@Override
	public List<ChangeSetMember> findFilteredChangeSetMembersByGroupId(final String groupId, final int startIndex,
			final int maxResults, final DirectedSortingField [] sortingFields, final LoadTuner loadTuner,
			final List<String> objectTypeFilter) {

		Collection<BusinessObjectGroupMember> businessObjectMembers =
			businessObjectGroupDao.findFilteredGroupMembersByGroupId(groupId, startIndex, maxResults, sortingFields, objectTypeFilter);

		boolean loadMetadata = true;
		if (loadTuner instanceof ChangeSetLoadTuner) {
			ChangeSetLoadTuner csLoadTuner = (ChangeSetLoadTuner) loadTuner;
			loadMetadata = csLoadTuner.isLoadingMemberObjectsMetadata();
		}
		Collection<BusinessObjectMetadata> memberObjectsMetadata = new ArrayList<BusinessObjectMetadata>();
		if (loadMetadata) {
			memberObjectsMetadata = findBusinessObjectMetadataByGroupId(groupId);
		}

		return changeSetHelper.convertGroupMembersToChangeSetMembers(businessObjectMembers, memberObjectsMetadata);
	}

	/**
	 *
	 * @return the changeSetHelper
	 */
	public ChangeSetHelper getChangeSetHelper() {
		return changeSetHelper;
	}

	/**
	 *
	 * @param changeSetHelper the changeSetHelper to set
	 */
	public void setChangeSetHelper(final ChangeSetHelper changeSetHelper) {
		this.changeSetHelper = changeSetHelper;
	}

	/**
	 *
	 * @param changeSetGuid the change set GUID
	 * @return the count of members belonging to a change set
	 */
	public long getChangeSetMemberCount(final String changeSetGuid) {
		return businessObjectGroupDao.getGroupMembersCount(changeSetGuid);
	}

	/**
	 *
	 * @param changeSetGuid the change set GUID
	 * @param objectTypeFilter a list of string specifying the object types to be filtered out of the results
	 * @return the count of members belonging to a change set
	 */
	public long getFilteredChangeSetMemberCount(final String changeSetGuid, final List<String> objectTypeFilter) {
		return businessObjectGroupDao.getFilteredGroupMembersCount(changeSetGuid, objectTypeFilter);
	}

	@Override
	public Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupIdAndDescriptor(final String changeSetGuid,
			final BusinessObjectDescriptor objectDescriptor) {
		return persistenceEngine.retrieveByNamedQuery("FIND_OBJECT_METADATA_BY_GROUPID_OBJ_TYPE_AND_ID",
				changeSetGuid,
				objectDescriptor.getObjectType(),
				objectDescriptor.getObjectIdentifier());
	}

	@Override
	public Collection<BusinessObjectMetadata> findBusinessObjectMetadataByGroupIdAndMetadataKey(final String changeSetGuid, final String key) {
		return persistenceEngine.retrieveByNamedQuery("FIND_OBJECT_METADATA_BY_GROUPID_AND_KEY", changeSetGuid, key);
	}

}
