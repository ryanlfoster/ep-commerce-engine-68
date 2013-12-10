/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.persistence.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.tools.ant.util.DateUtils;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.persistence.openjpa.impl.AbstractPersistenceEngineEntityListener;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.objectgroup.dao.BusinessObjectGroupDao;

/**
 * Listens to persistence events and updates the metadata for the change set.
 */
public class ChangeSetPersistenceListener extends AbstractPersistenceEngineEntityListener {

	private static final Logger LOG = Logger.getLogger(ChangeSetPersistenceListener.class);

	private static final String KEY_ACTION = "action";
	private BeanFactory beanFactory;

	private ChangeSetMemberDao changeSetMemberDao;
	private BusinessObjectGroupDao businessObjectGroupDao;
	private JpaPersistenceEngine persistenceEngine;
	private ThreadLocalMap<String, Object> metadataMap;
	private TimeService timeService;
	private ChangeSetService changeSetService;
	private ChangeSetHelper changeSetHelper;
	private Set<String> ignoreClasses = new HashSet<String>();

	private ThreadLocalMap<BusinessObjectDescriptor, BusinessObjectMetadata> actionMetadataMap;

	private static final Map<ChangeSetMemberAction, Integer> ACTION_PRIORITY = new HashMap<ChangeSetMemberAction, Integer>();

	static {
		// the lower the number the lower the priority
		ACTION_PRIORITY.put(ChangeSetMemberAction.ADD, 2);
		ACTION_PRIORITY.put(ChangeSetMemberAction.DELETE, 2);
		ACTION_PRIORITY.put(ChangeSetMemberAction.EDIT, 1);
		ACTION_PRIORITY.put(ChangeSetMemberAction.UNDEFINED, 0);
	}

	/**
	 * Adds or updates an existing object group member with its object metadata.
	 *
	 * @param objectDescriptor the object descriptor
	 * @param changeType the change operation type
	 */
	protected void addOrUpdateMetadata(final BusinessObjectDescriptor objectDescriptor, final ChangeType changeType) {

		BusinessObjectMetadata actionMetadata = getActionMetadataMap().get(objectDescriptor);
		boolean actionChanged = false;

		if (actionMetadata == null) {
			// no cached meta data exists for the object descriptor
			actionMetadata = createAndPopulateMetadata(KEY_ACTION, changeType, objectDescriptor);
			actionChanged = true;
		} else {
			// see if the cached meta data action needs updating based on the new action
			String oldActionName = actionMetadata.getMetadataValue();
			String newActionName = resolveAction(ChangeSetMemberAction.getChangeSetMemberAction(oldActionName), getAction(changeType)).getName();
			if (!StringUtils.equals(newActionName, oldActionName)) {
				actionMetadata.setMetadataValue(newActionName);
				getActionMetadataMap().put(objectDescriptor, actionMetadata);
				actionChanged = true;
			}
		}
		if (actionMetadata.getBusinessObjectGroupMember() == null) {
			//Nothing to do.
			//The object should be already added into change set in stage 1
			//This method should only be executed in stage2.
			//If the business object group member cannot be found, it means the object is related to some other object.
			//For example, single sku will not be added into change set, the product will be added into change set instead.
			LOG.debug(objectDescriptor + " is not in the change set. It may be a dependency of another object.");
			return;
		}

		if (actionChanged) {
			getChangeSetMemberDao().addOrUpdateObjectMetadata(actionMetadata);
		}
	}

	/**
	 * Adds an object to a change set.
	 *
	 * @param objectDescriptor the object descriptor
	 */
	protected void addObjectToChangeSet(final BusinessObjectDescriptor objectDescriptor) {
		if (objectDescriptor != null && getMetadataMap().containsKey("addToChangeSetFlag")) {

			Map<String, String> changeSetMetaDataMap = new HashMap<String, String>();

			changeSetMetaDataMap.put("addedByUserGuid", getChangeSetUserGuid());
			changeSetMetaDataMap.put("dateAdded", getCurrentDateString());

			getChangeSetService().addObjectToChangeSet(getChangeSetGuid(), objectDescriptor, changeSetMetaDataMap, false);
		}
	}

	/**
	 *
	 * @return the change set user GUID
	 */
	protected String getChangeSetUserGuid() {
		return (String) getMetadataMap().get("changeSetUserGuid");
	}

	private String getCurrentDateString() {
		return DateUtils.format(getTimeService().getCurrentTime().getTime(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * @return the time service
	 */
	public TimeService getTimeService() {
		if (timeService == null) {
			timeService = beanFactory.getBean(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}

	/**
	 *
	 * @return the changeSetService
	 */
	ChangeSetService getChangeSetService() {
		if (changeSetService == null) {
			changeSetService = beanFactory.getBean(ContextIdNames.CHANGESET_SERVICE);
		}
		return changeSetService;
	}


	/**
	 * Resolves which action should be set to the change set memeber.
	 *
	 * @param currentAction the currently set action
	 * @param newAction the new action to set
	 * @return the change set member action to set
	 */
	ChangeSetMemberAction resolveAction(final ChangeSetMemberAction currentAction, final ChangeSetMemberAction newAction) {
		int currentActionPriority = ACTION_PRIORITY.get(currentAction);
		int newActionPriority = ACTION_PRIORITY.get(newAction);
		if (currentActionPriority > newActionPriority) {
			return currentAction;
		}
		return newAction;
	}

	/**
	 * Checks whether the listener is active.
	 *
	 * @return true if active
	 */
	@SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
	boolean isListenerActive() {
		return StringUtils.isNotEmpty(getChangeSetGuid())
			&& ObjectUtils.equals("stage2", getMetadataMap().get("activeImportStage"));
	}

	/**
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final JpaPersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Get the OpenJPA Entity Manager.
	 *
	 * @return an <code>OpenJPAEntityManager</code> instance
	 */
	protected OpenJPAEntityManager getOpenJPAEntityManager() {
		return OpenJPAPersistence.cast(persistenceEngine.getEntityManager());
	}

	/**
	 * Creates a {@code BusinessObjectMetaData} object, populates it with the parameters and returns it.
	 *
	 * @param key The key.
	 * @param changeType The type of change.
	 * @param objectDescriptor The businessObjectDescriptor.
	 * @return The new object.
	 */
	protected BusinessObjectMetadata createAndPopulateMetadata(final String key,
			final ChangeType changeType, final BusinessObjectDescriptor objectDescriptor) {

		BusinessObjectMetadata metadata = beanFactory.getBean(ContextIdNames.BUSINESS_OBJECT_METADATA);
		metadata.setMetadataKey(key);
		metadata.setMetadataValue(getAction(changeType).getName());
		BusinessObjectGroupMember businessObjectGroupMember = getBusinessObjectGroupDao().findGroupMemberByObjectDescriptor(objectDescriptor);
		metadata.setBusinessObjectGroupMember(businessObjectGroupMember);
		return metadata;
	}

	/**
	 * @return The guid for the change set or null if change sets are not operating.
	 */
	protected String getChangeSetGuid() {
		return (String) getMetadataMap().get("changeSetGuid");
	}

	private ChangeSetPolicy getChangeSetPolicy() {
		return getBeanFactory().getBean("changeSetPolicy");
	}

	/**
	 * Resolves the object descriptor for {@code source}.
	 * @param source The object to resolve for.
	 * @return The BusinessObjectDescriptor or null if {@code source} is not supported by the change set framework.
	 */
	protected BusinessObjectDescriptor getBusinessObjectDescriptor(final Object source) {
		return getChangeSetPolicy().resolveObjectDescriptor(source);
	}

	/**
	 * Converts a change set type into a change set member action.
	 */
	private ChangeSetMemberAction getAction(final ChangeType changeType) {
		if (changeType == ChangeType.CREATE) {
			return ChangeSetMemberAction.ADD;
		} else if (changeType == ChangeType.DELETE) {
			return ChangeSetMemberAction.DELETE;
		} else if (changeType == ChangeType.UPDATE) {
			return ChangeSetMemberAction.EDIT;
		}
		return null;
	}

	/**
	 *
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 *
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("fallthrough")
	@Override
	public void eventOccurred(final LifecycleEvent event) {
		if (!isListenerActive()) {
			// the listener is not enabled
			return;
		}
		Object object = event.getSource();
		if (ignoreObject(object)) {
			return;
		}

		// The getBusinessObjectDescriptor should *only* be run on these events. If it's run for other events then
		// the object may no longer be there (because it's been deleted).

		switch (event.getType()) {
			case LifecycleEvent.BEFORE_ATTACH:
			case LifecycleEvent.BEFORE_STORE:
				final BusinessObjectDescriptor objectDescriptor = getBusinessObjectDescriptor(object);
				PersistenceCapable pcObject = (PersistenceCapable) object;
				if (pcObject.pcGetStateManager() == null) {
					processChangeSetData(objectDescriptor, ChangeType.CREATE);
				} else {
					processChangeSetData(objectDescriptor, ChangeType.UPDATE);
				}
				break;
			case LifecycleEvent.BEFORE_UPDATE:
				final BusinessObjectDescriptor objectDescriptor1 = getBusinessObjectDescriptor(object);
				processChangeSetData(objectDescriptor1, ChangeType.UPDATE);
				break;
			case LifecycleEvent.BEFORE_PERSIST:
				final BusinessObjectDescriptor objectDescriptor2 = getBusinessObjectDescriptor(object);
				processChangeSetData(objectDescriptor2, ChangeType.CREATE);
				break;
			case LifecycleEvent.BEFORE_DELETE:
				final BusinessObjectDescriptor objectDescriptor3 = getBusinessObjectDescriptor(object);
				processChangeSetData(objectDescriptor3, ChangeType.DELETE);
				break;
			default:
				// not supported event
		}
	}

	private boolean ignoreObject(final Object object) {
		if (ignoreClasses.contains(object.getClass().getName())) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(object.getClass().getName() + " is ignored by changesetPersistenceListener!!!");
			}
			return true;
		}
		return false;
	}

	/**
	 * Process the change set data.
	 *
	 * @param objectDescriptor the object descriptor
	 * @param changeType the change operation type
	 */
	protected void processChangeSetData(final BusinessObjectDescriptor objectDescriptor, final ChangeType changeType) {
		if (objectDescriptor != null) {
			// switch off temporarily the auto commit
			// in order to avoid issues with committing data before everything has been put together
			getOpenJPAEntityManager().setFlushMode(FlushModeType.COMMIT);
			// in case that step is enabled - add the object to the change set
			addObjectToChangeSet(objectDescriptor);
			addOrUpdateMetadata(objectDescriptor, changeType);

			// enable again the auto commit
			getOpenJPAEntityManager().setFlushMode(FlushModeType.AUTO);
		}
	}


	/**
	 *
	 * @return The dao for changeSetMembers.
	 */
	protected ChangeSetMemberDao getChangeSetMemberDao() {
		if (changeSetMemberDao == null) {
			changeSetMemberDao = beanFactory.getBean(ContextIdNames.CHANGE_SET_MEMBER_DAO);
		}
		return changeSetMemberDao;
	}

	/**
	 *
	 * @return the dao for BusinessObjectGroups
	 */
	protected BusinessObjectGroupDao getBusinessObjectGroupDao() {
		if (businessObjectGroupDao == null) {
			businessObjectGroupDao = beanFactory.getBean(ContextIdNames.BUSINESS_OBJECT_GROUP_DAO);
		}
		return businessObjectGroupDao;
	}

	/**
	 * Get the map of metadata for this listener.
	 *
	 * @return the threadLocalMap
	 */
	protected ThreadLocalMap<String, Object> getMetadataMap() {
		return metadataMap;
	}

	/**
	 * Set the map of metadata for this listener.
	 *
	 * @param threadLocalMap the threadLocalMap to set
	 */
	public void setMetadataMap(final ThreadLocalMap<String, Object> threadLocalMap) {
		metadataMap = threadLocalMap;
	}

	/**
	 * Set the ignore classes.
	 *
	 * @param ignoreClasses the ignore classes
	 */
	public void setIgnoreClasses(final Set<String> ignoreClasses) {
		this.ignoreClasses = ignoreClasses;
	}

	/**
	 * Gets the change set helper.
	 *
	 * @return the change set helper
	 */
	public ChangeSetHelper getChangeSetHelper() {
		if (changeSetHelper == null) {
			changeSetHelper = beanFactory.getBean(ContextIdNames.CHANGESET_HELPER);
		}
		return changeSetHelper;
	}

	/**
	 * Gets the action metadata map.
	 *
	 * @return the action metadata map
	 */
	public ThreadLocalMap<BusinessObjectDescriptor, BusinessObjectMetadata> getActionMetadataMap() {
		if (actionMetadataMap == null) {
			actionMetadataMap = new ThreadLocalMap<BusinessObjectDescriptor, BusinessObjectMetadata>();
			Collection<BusinessObjectMetadata> actionMetaDataCollection =
				getChangeSetMemberDao().findBusinessObjectMetadataByGroupIdAndMetadataKey(getChangeSetGuid(), KEY_ACTION);
			for (BusinessObjectMetadata metadata : actionMetaDataCollection) {
				BusinessObjectDescriptor memberDescriptor =
					getChangeSetHelper().convertGroupMemberToDescriptor(metadata.getBusinessObjectGroupMember());
				actionMetadataMap.put(memberDescriptor, metadata);
			}
		}
		return actionMetadataMap;
	}

}
