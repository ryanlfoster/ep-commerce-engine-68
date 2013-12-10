/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.ChangeSetDependencyDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.changeset.ChangeSetObjectStatusMutator;
import com.elasticpath.domain.changeset.ChangeSetUserView;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.ChangeSetPolicyException;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * This service manages change sets and their members.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ChangeSetServiceImpl extends AbstractEpServiceImpl implements ChangeSetService {
	
	private static final Logger LOG = Logger.getLogger(ChangeSetServiceImpl.class);
	
	private ChangeSetMemberDao changeSetMemberDao;

	private ChangeSetPolicy changeSetPolicy;

	private ChangeSetDao changeSetDao;

	private SettingsReader settingsReader;
	
	
	/**
	 * Set the settings reader.
	 * 
	 * @param settingsReader the settings reader instance
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * determine is change set enabled.
	 * 
	 * @return true if change set is enabled
	 */
	public boolean isChangeSetEnabled() {
		boolean changeSetsEnabled = false;
		SettingValue settingValue = settingsReader.getSettingValue("COMMERCE/SYSTEM/CHANGESETS/enable");
		if (settingValue == null) {
			changeSetsEnabled = false;
		} else {
			changeSetsEnabled = settingValue.getBooleanValue();
		}
		return changeSetsEnabled;
	}


	@Override
	public void addObjectToChangeSet(final String changeSetGuid, 
			final BusinessObjectDescriptor objectDescriptor, final Map<String, String> objectMetadata) {
		addObjectToChangeSet(changeSetGuid, objectDescriptor, objectMetadata, true);
	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public void addObjectToChangeSet(final String changeSetGuid, 
			final BusinessObjectDescriptor objectDescriptor, final Map<String, String> objectMetadata, final boolean resolveMetadata) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering method addObjectToChangeSet(" + changeSetGuid + ", " + objectDescriptor + ")");
		}
		
		if (changeSetGuid == null || !changeSetExists(changeSetGuid)) {
			throw new IllegalArgumentException(String.format(
					"Cannot add object to a change set with null or invalid GUID: %s", changeSetGuid));
		}
		if (objectDescriptor == null) {
			throw new IllegalArgumentException("The object descriptor cannot be null.");
		}
		
		// check whether a change set can be changed
		checkChangeAllowed(changeSetGuid);
		
		final ChangeSetObjectStatus objectStatus = getStatus(objectDescriptor);
		
		if (objectStatus.isMember(changeSetGuid)) {
			if (LOG.isInfoEnabled()) {
				LOG.info("The object: " + objectDescriptor + " is already a member of change set: " + changeSetGuid);
			}
			return;
		}
		
		if (!objectStatus.isAvailable(changeSetGuid)) {
			throw new ChangeSetPolicyException("Object " + objectDescriptor 
					+ " is a member of another change set and cannot be added to change set: " + changeSetGuid);
		}
		
		BusinessObjectGroupMember groupMember = newBusinessObjectGroupMemberInstance();
		
		groupMember.setGroupId(changeSetGuid);
		groupMember.setObjectIdentifier(objectDescriptor.getObjectIdentifier());
		groupMember.setObjectType(objectDescriptor.getObjectType());
		
		Collection<BusinessObjectMetadata> metadata = new LinkedList<BusinessObjectMetadata>();
		addObjectMetadataToList(objectMetadata, groupMember, metadata);

		if (resolveMetadata) {
			Map<String, String> resolvedData = changeSetPolicy.resolveMetaData(objectDescriptor);
			addObjectMetadataToList(resolvedData, groupMember, metadata);
		}

		changeSetMemberDao.add(groupMember, metadata);
	}

	/**
	 * Add the given key/value pair metadata to the list of business object metadata objects
	 * for the given group member.
	 * 
	 * @param objectMetadata the object metadata key/value map
	 * @param groupMember the business group member
	 * @param metadata the metadata object list to add to
	 */
	private void addObjectMetadataToList(final Map<String, String> objectMetadata, final BusinessObjectGroupMember groupMember,
			final Collection<BusinessObjectMetadata> metadata) {
		if (MapUtils.isNotEmpty(objectMetadata)) {
			for (String metadataKey : objectMetadata.keySet()) {
				BusinessObjectMetadata businessObjectMetadata = newBusinessObjectMetadataInstance();
				businessObjectMetadata.setBusinessObjectGroupMember(groupMember);
				
				businessObjectMetadata.setMetadataKey(metadataKey);
				businessObjectMetadata.setMetadataValue(objectMetadata.get(metadataKey));
				
				metadata.add(businessObjectMetadata);
			}
		}
	}

	/**
	 */
	private boolean changeSetExists(final String changeSetGuid) {
		return changeSetDao.findByGuid(changeSetGuid) != null;
	}

	private BusinessObjectMetadata newBusinessObjectMetadataInstance() {
		return getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
	}

	/**
	 * Creates a new instance of {@link BusinessObjectGroupMember}.
	 * 
	 * @return an instance of business object group member
	 */
	protected BusinessObjectGroupMember newBusinessObjectGroupMemberInstance() {
		return getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_MEMBER);
	}

	/**
	 * Removes an object member from this change set.
	 * 
	 * @param changeSetGuid the change set GUID
	 * @param objectDescriptor the descriptor of the object
	 */
	public void removeObjectFromChangeSet(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering method removeObjectFromChangeSet(" + changeSetGuid + ", " + objectDescriptor + ")");
		}
		
		// check whether a change set can be changed
		checkChangeAllowed(changeSetGuid);

		changeSetMemberDao.removeByObjectDescriptor(objectDescriptor, changeSetGuid);
	}
	
	@Override
	public void removeObjectFromChangeSet(final String changeSetGuid, final Object object) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering method removeObjectFromChangeSet(" + changeSetGuid + ", " + object + ")");
		}
		
		// check whether a change set can be changed
		checkChangeAllowed(changeSetGuid);
		
		BusinessObjectDescriptor objectDescriptor = changeSetPolicy.resolveObjectDescriptor(object);
		if (objectDescriptor == null) {
			throw new ChangeSetPolicyException("Object with class: " 
					+ object.getClass().getName()
					+ " cannot be resolved to an object descriptor. It is not supported by the Change Set Policy.");
		}

		changeSetMemberDao.removeByObjectDescriptor(objectDescriptor, changeSetGuid);
	}

	/**
	 *
	 * @param objectDescriptor the descriptor of the object
	 * @return the change set object status
	 */
	public ChangeSetObjectStatus getStatus(final BusinessObjectDescriptor objectDescriptor) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting change set object status for object descriptor: " + objectDescriptor);
		}
		final ChangeSetObjectStatus status = getChangeSetObjectStatusInstance();
		
		if (status instanceof ChangeSetObjectStatusMutator) {
			if (objectDescriptor != null) {
				final Collection<String> objectMembershipGuids = changeSetPolicy.getObjectMembershipGuids(objectDescriptor);
				
				ChangeSetObjectStatusMutator updatableStatus = (ChangeSetObjectStatusMutator) status;
				updatableStatus.setObjectDescriptor(objectDescriptor);
				updatableStatus.setChangeSetGuids(objectMembershipGuids);
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("ChangeSet Object Status: " + status);
				}
			}
		} else {
			throw new EpDomainException("The ChangeSetObjectStatus bean does not implement ChangeSetObjectStatusMutator interface");
		}
		return status;
	}
	
	@Override
	public ChangeSetObjectStatus getStatus(final Object object) {
		return getStatus(changeSetPolicy.resolveObjectDescriptor(object));
	}

	@Override	
	public Map<Object, String> getObjectsLocked(final Object[] objects) {
		Map<Object, String> returnMap = new HashMap<Object, String>();
		for (Object object : objects) {
			ChangeSetObjectStatus status = getStatus(object);
			if (status.isLocked()) {
				returnMap.put(object, findChangeSetGuid(status.getObjectDescriptor()));
				}
			}
		return returnMap;
	}
	/**
	 * Creates a new instance of {@link ChangeSetObjectStatus}.
	 * 
	 * @return a new instance
	 */
	protected ChangeSetObjectStatus getChangeSetObjectStatusInstance() {
		return getBean(ContextIdNames.CHANGESET_OBJECT_STATUS);
	}

	@Override
	public void addObjectToChangeSet(final String changeSetGuid, final Object object, final Map<String, String> metadata) {
		if (changeSetGuid == null || object == null) {
			throw new IllegalArgumentException("Cannot add object: " + object + " to change set (GUID): " + changeSetGuid);
		}
		BusinessObjectDescriptor objectDescriptor = changeSetPolicy.resolveObjectDescriptor(object);
		if (objectDescriptor == null) {
			throw new ChangeSetPolicyException("Object with class: " 
					+ object.getClass().getName()
					+ " cannot be resolved to an object descriptor. It is not supported by the Change Set Policy.");
		}
		this.addObjectToChangeSet(changeSetGuid, objectDescriptor, metadata);
	}

	/**
	 * Verifies that a change set could be modified.
	 * 
	 * @param objectGroupId the change set object group ID
	 * @throws ChangeSetPolicyException in case the change set cannot be modified
	 */
	protected void checkChangeAllowed(final String objectGroupId) throws ChangeSetPolicyException {
		// check whether a change set could be modified
		if (!this.changeSetPolicy.isChangeAllowed(objectGroupId)) {
			throw new ChangeSetPolicyException("No change can be made to change set with object group ID: " + objectGroupId);
		}
	}

	/**
	 * Sets the policy to be used.
	 * 
	 * @param changeSetPolicy the change set policy 
	 */
	public void setChangeSetPolicy(final ChangeSetPolicy changeSetPolicy) {
		this.changeSetPolicy = changeSetPolicy;
	}
	
	/**
	 * Sets the change set member DAO.
	 * 
	 * @param changeSetMemberDao the DAO implementation to set
	 */
	public void setChangeSetMemberDao(final ChangeSetMemberDao changeSetMemberDao) {
		this.changeSetMemberDao = changeSetMemberDao;
	}

	/**
	 * Sets the change set DAO.
	 * 
	 * @param changeSetDao the DAO implementation to set
	 */
	public void setChangeSetDao(final ChangeSetDao changeSetDao) {
		this.changeSetDao = changeSetDao;
	}

	@Override
	public Map<String, String> findChangeSetMemberMetadata(final String changeSetGuid, final BusinessObjectDescriptor objectDescriptor) {
		Collection<BusinessObjectMetadata> result = changeSetMemberDao.
			findBusinessObjectMetadataByGroupIdAndDescriptor(changeSetGuid, objectDescriptor);
		Map<String, String> metadata = new HashMap<String, String>();
		for (BusinessObjectMetadata objectMetadata : result) {
			metadata.put(objectMetadata.getMetadataKey(), objectMetadata.getMetadataValue());
		}
		return metadata;
	}

	@Override
	public Map<String, String> findChangeSetMemberMetadata(final String changeSetGuid, final Object object) {
		return this.findChangeSetMemberMetadata(changeSetGuid, changeSetPolicy.resolveObjectDescriptor(object));
	}

	@Override
	public ChangeSet findChangeSet(final BusinessObjectDescriptor objectDescriptor) {
		Collection<ChangeSet> changeSets = changeSetDao.findByObjectDescriptor(objectDescriptor, changeSetPolicy.getNonFinalizedStates());
		if (changeSets.size() > 1) {
			throw new IllegalStateException("Inconsistent data. An object descriptor can only belong to one change set");
		} else if (changeSets.isEmpty()) {
			return null;
		}
		return changeSets.iterator().next();
	}

	@Override
	public ChangeSet findChangeSet(final Object object) {
		if (object == null) {
			return null;
		}
		
		BusinessObjectDescriptor businessObjectDescriptor = null;
		try {
			businessObjectDescriptor = changeSetPolicy.resolveObjectDescriptor(object);
		}  catch (ChangeSetPolicyException e) {	
			// This exception is thrown if the object descriptor cannot be resolved
			// e.g. if the object has a null guid.
			// Our interface says to return a null if we can't find the change set.
			return null; 
		}
		
		if (businessObjectDescriptor == null) {
			return null;
		}
		
		return this.findChangeSet(businessObjectDescriptor); 
	}
	
	@Override
	public Collection<ChangeSetUserView> getAvailableUsers(final String... permissions) {
		return changeSetDao.getAvailableUsers(permissions);
	}
	
	@Override
	public Collection<ChangeSetUserView> getChangeSetUserViews(final Collection<String> changeSetUsersGuids) {
		return changeSetDao.getChangeSetUserViews(changeSetUsersGuids);
	}

	@Override
	public Class< ? > findObjectClass(final BusinessObjectDescriptor objectDescriptor) {
		return changeSetPolicy.getObjectClass(objectDescriptor);
	}

	@Override
	public String resolveObjectGuid(final Object object) {
		return changeSetPolicy.resolveObjectGuid(object);
	}

	@Override
	public List<ChangeSetMember> findMembersByChangeSetGuid(final String groupId, final int startIndex, 
			final int maxResults, final DirectedSortingField [] sortingFields, final LoadTuner loadTuner) {
		return changeSetMemberDao.findChangeSetMembersByGroupId(groupId, startIndex, maxResults, sortingFields, loadTuner);
	}

	@Override
	public List<ChangeSetMember> findFilteredMembersByChangeSetGuid(final String groupId, final int startIndex, 
			final int maxResults, final DirectedSortingField [] sortingFields, final LoadTuner loadTuner, 
			final List<String> objectTypeFilter) {
		return changeSetMemberDao.findFilteredChangeSetMembersByGroupId(groupId, startIndex, maxResults, sortingFields, loadTuner, objectTypeFilter);
	}

	@Override
	public long getChangeSetMemberCount(final String groupId) {
		return changeSetMemberDao.getChangeSetMemberCount(groupId);
	}

	@Override
	public long getFilteredChangeSetMemberCount(final String groupId, final List<String> objectTypeFilter) {
		return changeSetMemberDao.getFilteredChangeSetMemberCount(groupId, objectTypeFilter);
	}

	@Override
	public boolean objectExists(final String groupId, final BusinessObjectDescriptor objectDescriptor) {
		Collection<BusinessObjectMetadata> metadataCollection = changeSetMemberDao
				.findBusinessObjectMetadataByGroupIdAndDescriptor(groupId, objectDescriptor);
		for (BusinessObjectMetadata metadataObj : metadataCollection) {
			if ("action".equals(metadataObj.getMetadataKey())
					&& ObjectUtils.equals(metadataObj.getMetadataValue(), ChangeSetMemberAction.DELETE.getName())) {
				return false;
			}
		}
		return true;
	}
	
	@Override	
	public String findChangeSetGuid(final BusinessObjectDescriptor objectDescriptor) {
		Collection<String> guids = changeSetDao.findGuidByObjectDescriptor(objectDescriptor, changeSetPolicy.getNonFinalizedStates());
		if (guids.size() > 1) {
			throw new IllegalStateException("Inconsistent data. An object descriptor can only belong to one change set");
		} else if (guids.isEmpty()) {
			return null;
		}
		return guids.iterator().next();
	}
	
	@Override
	public String findChangeSetGuid(final Object object) {
		if (object == null) {
			return null;
		}
		
		BusinessObjectDescriptor businessObjectDescriptor = changeSetPolicy.resolveObjectDescriptor(object);
		if (businessObjectDescriptor == null) {
			return null;
		}
		
		return this.findChangeSetGuid(businessObjectDescriptor);
		
	}
	
	@Override
	public Set<BusinessObjectDescriptor> findDependentObjects(final BusinessObjectDescriptor businessObject) {
		return changeSetPolicy.getDependentObjects(businessObject, findObjectClass(businessObject));
	}
	
	@Override
	public Map<BusinessObjectDescriptor, ChangeSet> findChangeSet(
			final Set<BusinessObjectDescriptor> objects, final ChangeSet excludedChangeSet) {
		Map<BusinessObjectDescriptor, ChangeSet> retMap = new HashMap<BusinessObjectDescriptor, ChangeSet>();
		
		for (BusinessObjectDescriptor objectDescriptor : objects) {
			ChangeSet changeSet = this.findChangeSet(objectDescriptor);
			if (changeSet != null && !excludedChangeSet.equals(changeSet)) {
				retMap.put(objectDescriptor, changeSet);
			}
		}
		
		return retMap;
	}

	@Override
	public void updateResolvedMetadata(final String changeSetGuid) {
		Collection<BusinessObjectGroupMember> groupMembers = changeSetMemberDao.findGroupMembersByGroupId(changeSetGuid);

		for (BusinessObjectGroupMember member : groupMembers) {
			updateResolvedMetadata(member);
		}
	}
	
	/**
	 * Update the resolved metadata for the given change set member.
	 * 
	 * @param businessObjectGroupMember the member to update
	 */
	protected void updateResolvedMetadata(final BusinessObjectGroupMember businessObjectGroupMember) {

		BusinessObjectDescriptor objectDescriptor = getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		objectDescriptor.setObjectIdentifier(businessObjectGroupMember.getObjectIdentifier());
		objectDescriptor.setObjectType(businessObjectGroupMember.getObjectType());

		Map<String, String> resolvedData = changeSetPolicy.resolveMetaData(objectDescriptor);
		if (MapUtils.isEmpty(resolvedData)) {
			return;
		}
		
		Collection<BusinessObjectMetadata> metadataCollection = changeSetMemberDao.findBusinessObjectMetadataByDescriptor(objectDescriptor);

		// Update existing values
		for (BusinessObjectMetadata metadata : metadataCollection) {
			String key = metadata.getMetadataKey();
			if (resolvedData.containsKey(key)) {
				metadata.setMetadataValue(resolvedData.get(key));
				resolvedData.remove(key);
				changeSetMemberDao.addOrUpdateObjectMetadata(metadata);		
			}
		}

		// Add new values
		metadataCollection = new ArrayList<BusinessObjectMetadata>();
		addObjectMetadataToList(resolvedData, businessObjectGroupMember, metadataCollection);
		for (BusinessObjectMetadata metadata : metadataCollection) {
			changeSetMemberDao.addOrUpdateObjectMetadata(metadata);		
		}

	}
	
	@Override
	public List<ChangeSetDependencyDto> getChangeSetDependencies(final ChangeSet changeSet, final DirectedSortingField sortingField) {
		List<ChangeSetDependencyDto> dependencyDtos = new LinkedList<ChangeSetDependencyDto>();
		List<ChangeSetMember> memberObjects = findMembersByChangeSetGuid(
				changeSet.getGuid(), 0, Integer.MAX_VALUE, new DirectedSortingField []{ sortingField }, null);
		for (ChangeSetMember changeSetMember : memberObjects) {
			BusinessObjectDescriptor businessObjectDescriptor = changeSetMember.getBusinessObjectDescriptor();
			Map<String, String> sourceMetaData = changeSetMember.getMetadata();
			dependencyDtos.addAll(getBusinessObjectDependencies(changeSet, businessObjectDescriptor, sourceMetaData));
		}
		return dependencyDtos;
	}
	
	/**
	 * Get the dependency objects in other change sets for one business object descriptor.
	 * 
	 * @param changeSet the source change set
	 * @param businessObjectDescriptor the source dependency object
	 * @param sourceMetaData the source meta data
	 * @return a list of change set dependency dto
	 */
	private List<ChangeSetDependencyDto> getBusinessObjectDependencies(final ChangeSet changeSet,
			final BusinessObjectDescriptor businessObjectDescriptor, final Map<String, String> sourceMetaData) {
		List<ChangeSetDependencyDto> dependencyDtos = new LinkedList<ChangeSetDependencyDto>();
		Set<BusinessObjectDescriptor> dependencies = findDependentObjects(businessObjectDescriptor);
		Map<BusinessObjectDescriptor, ChangeSet> retMap = findChangeSet(dependencies, changeSet);
		for (BusinessObjectDescriptor dependent : retMap.keySet()) {
			ChangeSet dependencyChangeSet = retMap.get(dependent);
			Map<String, String> dependencyMetaData = findChangeSetMemberMetadata(dependencyChangeSet.getGuid(), dependent);
			ChangeSetDependencyDto dependencyDto = new ChangeSetDependencyDto(
					businessObjectDescriptor, sourceMetaData, dependent, dependencyMetaData, dependencyChangeSet);
			dependencyDtos.add(dependencyDto);
		}
		return dependencyDtos;
	}

}
