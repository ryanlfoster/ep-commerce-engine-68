package com.elasticpath.service.changeset.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMutator;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.ChangeSetPolicyException;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.TimeService;

/**
 * {@link ChangeSetManagementService} default implementation. 
 */
public class ChangeSetManagementServiceImpl extends AbstractEpServiceImpl implements ChangeSetManagementService {

	private static final Logger LOG = Logger.getLogger(ChangeSetManagementServiceImpl.class);

	private ChangeSetDao changeSetDao;
	private TimeService timeService;
	private ChangeSetMemberDao changeSetMemberDao;
	private ChangeSetHelper changeSetHelper;

	private ChangeSetPolicy changeSetPolicy;

	@Override
	public ChangeSet add(final ChangeSet changeSet) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Add a change set: " + changeSet);
		}

		String groupId = changeSetMemberDao.generateChangeSetGroupId();
		changeSet.addAssignedUser(changeSet.getCreatedByUserGuid());			
		
		// for consistency, always use the time service to set the created date
		changeSet.setCreatedDate(timeService.getCurrentTime());
		
		// if no state code was set the default is OPEN
		if (changeSet.getStateCode() == null) {
			changeSet.setStateCode(ChangeSetStateCode.OPEN);
		}
				
		getChangeSetMutator(changeSet).setObjectGroupId(groupId);
		return changeSetDao.add(changeSet);
	}
	
	/**
	 * Returns the {@link ChangeSetMutator} of the given given {@link ChangeSet}.
	 * 
	 * @param changeSet The {@link ChangeSet} to cast to {@link ChangeSetMutator}.
	 * @return the {@link ChangeSetMutator}
	 */
	private ChangeSetMutator getChangeSetMutator(final ChangeSet changeSet) {
		if (!(changeSet instanceof ChangeSetMutator)) {
			throw new EpDomainException("ChangeSet implementation does not implement ChangeSetMutator interface.");
		}
		return (ChangeSetMutator) changeSet;
	}

	@Override
	public void remove(final String objectGroupId) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Remove a change set by objectGroupId: " + objectGroupId);
		}

		checkCanRemove(objectGroupId);
		
		//remove the change set itself
		changeSetDao.remove(objectGroupId);
	}

	/**
	 * Verifies if a change set can be removed.
	 * 
	 * @param objectGroupId the object group ID
	 */
	protected void checkCanRemove(final String objectGroupId) {
		if (!canRemove(objectGroupId)) {
			throw new ChangeSetPolicyException("Cannot remove change set with GUID: " + objectGroupId);
		}
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
	
	@Override
	public ChangeSet update(final ChangeSet changeSet, final LoadTuner loadTuner) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Update a change set: " + changeSet);
		}
		
		checkChangeAllowed(changeSet.getGuid());
		
		ChangeSet updatedChangedSet = changeSetDao.update(changeSet);
				
		addMemberObjectsToChangeSet(updatedChangedSet, loadTuner);
		
		return updatedChangedSet;
	}

	/**
	 *
	 * @param changeSet the change set to add members to
	 * @param loadTuner the load tuner to use
	 */
	protected void addMemberObjectsToChangeSet(final ChangeSet changeSet, final LoadTuner loadTuner) {
		if (!isLoadingMembersEnabled(loadTuner)) {
			return;
		}
		ChangeSetMutator changeSetWithMembers = getChangeSetMutator(changeSet);
		
		Collection<BusinessObjectGroupMember> businessObjectGroupMembers = changeSetMemberDao.findGroupMembersByGroupId(changeSet.getGuid());
		
		Collection<BusinessObjectMetadata> memberObjectsMetadata = Collections.emptySet();
		
		if (isLoadingMembersMetadataEnabled(loadTuner)) {
			memberObjectsMetadata = changeSetMemberDao.findBusinessObjectMetadataByGroupId(changeSet.getGuid());
		}
		Collection<ChangeSetMember> memberObjects = 
			changeSetHelper.convertGroupMembersToChangeSetMembers(businessObjectGroupMembers, memberObjectsMetadata);
		
		changeSetWithMembers.setMemberObjects(memberObjects);
	}

	/**
	 * 
	 * @param loadTuner the provided load tuner
	 * @return true if loading members is enabled by the load tuner
	 */
	protected boolean isLoadingMembersEnabled(final LoadTuner loadTuner) {
		if (loadTuner instanceof ChangeSetLoadTuner) {
			ChangeSetLoadTuner changeSetLoadTuner = (ChangeSetLoadTuner) loadTuner;
			return changeSetLoadTuner.isLoadingMemberObjects();
		}
		// by default the members loading is enabled
		return true;
	}

	/**
	 * 
	 * @param loadTuner the provided load tuner
	 * @return true if loading members is enabled by the load tuner
	 */
	protected boolean isLoadingMembersMetadataEnabled(final LoadTuner loadTuner) {
		if (loadTuner instanceof ChangeSetLoadTuner) {
			ChangeSetLoadTuner changeSetLoadTuner = (ChangeSetLoadTuner) loadTuner;
			return changeSetLoadTuner.isLoadingMemberObjectsMetadata();
		}
		// by default the members metadata loading is enabled
		return true;
	}

	@Override
	public ChangeSet get(final String guid, final LoadTuner loadTuner) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Get a change set by GUID: " + guid);
		}

		ChangeSet changeSet = changeSetDao.findByGuid(guid);
		
		if (changeSet != null) {
			addMemberObjectsToChangeSet(changeSet, loadTuner);
		}
		
		return changeSet;
	}

	/**
	 * Sets the {@link ChangeSetDao}.
	 * 
	 * @param changeSetDao the changeSetDao to set
	 */
	public void setChangeSetDao(final ChangeSetDao changeSetDao) {
		this.changeSetDao = changeSetDao;
	}

	@Override
	public Collection<ChangeSet> findAllChangeSets(final LoadTuner loadTuner) {
		Collection<ChangeSet> allChangeSets = changeSetDao.findAllChangeSets();
		for (ChangeSet changeSet : allChangeSets) {
			addMemberObjectsToChangeSet(changeSet, loadTuner);
		}
		return allChangeSets;
	}
	
	@Override
	public Collection<ChangeSet> findAllChangeSetsByUserGuid(final String userGuid, final LoadTuner loadTuner) {
		Collection<ChangeSet> changeSets = changeSetDao.findAllChangeSetsByUserGuid(userGuid);
		for (ChangeSet changeSet : changeSets) {
			addMemberObjectsToChangeSet(changeSet, loadTuner);
		}
		return changeSets;
	}


	@Override
	public Collection<ChangeSet> findByCriteria(final ChangeSetSearchCriteria criteria, final LoadTuner loadTuner) {
		Collection<ChangeSet> changeSets = changeSetDao.findByCriteria(criteria);
		for (ChangeSet changeSet : changeSets) {
			addMemberObjectsToChangeSet(changeSet, loadTuner);
		} 
			
		return changeSets;
	}
	
	@Override
	public List<ChangeSet> findByCriteria(final ChangeSetSearchCriteria criteria, final LoadTuner loadTuner,
			final int start, final int maxResults) {
		List<ChangeSet> changeSets = changeSetDao.findByCriteria(criteria, start, maxResults);
		for (ChangeSet changeSet : changeSets) {
			addMemberObjectsToChangeSet(changeSet, loadTuner);
		} 
			
		return changeSets;
	}
	
	@Override
	public ChangeSet updateState(final String changeSetGuid, final ChangeSetStateCode stateCode, final LoadTuner loadTuner) {
		if (StringUtils.isEmpty(changeSetGuid) || stateCode == null) {
			throw new IllegalArgumentException(String.format("Must specify change set GUID: %s and a state code: %s", 
					new Object[] { changeSetGuid, stateCode }));
		}
		ChangeSet changeSet = changeSetDao.findByGuid(changeSetGuid);
		if (changeSet == null) {
			throw new EpServiceException("No change set with GUID '" + changeSetGuid + "' could be found.");
		}
		changeSet.setStateCode(stateCode);
		
		final ChangeSet updatedChangedSet = changeSetDao.update(changeSet);
		
		addMemberObjectsToChangeSet(updatedChangedSet, loadTuner);
		
		return updatedChangedSet;
	}
	
	/**
	 * Returns a service to get the current date and time.
	 * 
	 * @return The {@link TimeService}
	 */
	public final TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Set the {@link TimeService} to use for determining the current date and time.
	 * 
	 * @param timeService The {@link TimeService} to use.
	 */
	public final void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
	
	
	/**
	 * Set change set member dao.
	 * @param changeSetMemberDao instance of change set member dao
	 */
	public void setChangeSetMemberDao(final ChangeSetMemberDao changeSetMemberDao) {
		this.changeSetMemberDao = changeSetMemberDao;
	}
	
	/**
	 * Set change set helper. 
	 * @param changeSetHelper the instance of change set helper
	 */
	public void setChangeSetHelper(final ChangeSetHelper changeSetHelper) {
		this.changeSetHelper = changeSetHelper;
	}
	
	@Override
	public Pair<ChangeSet, ChangeSet> updateAndMoveObjects(final String originChangeSetGuid, 
			                                      final String targetChangeSetGuid, 
			                                      final Collection<BusinessObjectDescriptor> checkedElementsCollection,
			                                      final LoadTuner loadTuner) {
		
		this.changeSetMemberDao.updateAndMoveObjects(originChangeSetGuid, targetChangeSetGuid, checkedElementsCollection);
		
		return new Pair<ChangeSet, ChangeSet>(get(originChangeSetGuid, loadTuner),
				get(targetChangeSetGuid, loadTuner));

	}
	
	/**
	 * Sets the policy to be used.
	 * 
	 * @param changeSetPolicy the change set policy 
	 */
	public void setChangeSetPolicy(final ChangeSetPolicy changeSetPolicy) {
		this.changeSetPolicy = changeSetPolicy;
	}

	@Override
	public boolean isChangeAllowed(final String changeSetGuid) {
		return changeSetPolicy.isChangeAllowed(changeSetGuid);
	}

	@Override
	public boolean canRemove(final String guid) {
		return changeSetPolicy.canRemove(guid);
	}

	/**
	 * Get the change set dao.
	 * 
	 * @return the changeSetDao
	 */
	protected ChangeSetDao getChangeSetDao() {
		return changeSetDao;
	}

	/**
	 * Get the change set member dao.
	 * 
	 * @return the changeSetMemberDao
	 */
	protected ChangeSetMemberDao getChangeSetMemberDao() {
		return changeSetMemberDao;
	}

	/**
	 * Get the change set helper.
	 * 
	 * @return the changeSetHelper
	 */
	protected ChangeSetHelper getChangeSetHelper() {
		return changeSetHelper;
	}

	/**
	 * Get the change set policy.
	 * 
	 * @return the changeSetPolicy
	 */
	protected ChangeSetPolicy getChangeSetPolicy() {
		return changeSetPolicy;
	}

	@Override
	public long getCountByCriteria(final ChangeSetSearchCriteria searchCriteria) {
		return changeSetDao.getCountByCriteria(searchCriteria);
	}

}
