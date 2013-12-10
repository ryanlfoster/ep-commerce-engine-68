package com.elasticpath.tools.sync.target.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.tools.sync.job.JobEntry;

/**
 * Provides callback method to add index notifications about removes and updates by the data sync tool.
 * If the callback is not used the index builders may miss objects changed during a long DST transaction.
 */
public class IndexNotificationJobTransactionCallback extends AbstractJobTransactionCallbackAdapter {
	
	private static final Logger LOG = Logger.getLogger(IndexNotificationJobTransactionCallback.class);
	
	private IndexNotificationService indexNotificationService;

	private BeanFactory beanFactory;
	
	private Map<String, IndexType> indexNameMap = new HashMap<String, IndexType>();

	@Override
	public void postUpdateJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		addIndexNotification(UpdateType.UPDATE, jobEntry.getType(), targetPersistence.getUidPk());
	}

	@Override
	public void postRemoveJobEntryHook(final JobEntry jobEntry, final Persistable targetPersistence) {
		if (targetPersistence == null) {
			LOG.warn(String.format("Cannot register an index notification event. "
					+ "Target persistable object was not provided for jobEntry: %s", jobEntry));
		} else {
			addIndexNotification(UpdateType.DELETE, jobEntry.getType(), targetPersistence.getUidPk());
		}
	}

	/**
	 * Adds an index notification for the given update type, type, and uidpk. Will not
	 * add the notification if the type does not map to a known search index.
	 *
	 * @param updateType whether the update is an UPDATE, DELETE, REBUILD
	 * @param type the class of the object which has been updated.
	 * @param uidPk the uidPk of the updated object
	 */
	protected void addIndexNotification(final UpdateType updateType, final Class< ? > type, final long uidPk) {
		IndexType indexType = getIndexType(type);

		//indexType will be null if we do not have a search index for the specified class, so we shouldn't
		//add a notification for it.
		if (indexType != null) {
			indexNotificationService.add(createNotification(updateType, uidPk, indexType));
		}
	}

	private IndexNotification createNotification(final UpdateType updateType, final long uidPk, final IndexType indexType) {
		final IndexNotification notification = (IndexNotification) getBean(ContextIdNames.INDEX_NOTIFICATION);
		notification.setAffectedEntityType(AffectedEntityType.SINGLE_UNIT);
		notification.setAffectedUid(uidPk);
		notification.setIndexType(indexType);
		notification.setUpdateType(updateType);
		return notification;
	}

	/**
	 * Get bean with specified id from bean factory.
	 *
	 * @param beanID id string of the bean to get
	 * @return the bean
	 */
	protected Object getBean(final String beanID) {
		return beanFactory.getBean(beanID);
	}

	/**
	 * Get the index type to notify based on the class supplied.
	 *
	 * @param type the class of the object which has been updated
	 * @return the corresponding IndexType for the index to be updated
	 */
	protected IndexType getIndexType(final Class< ? > type) {
		if (!indexNameMap.containsKey(type.getCanonicalName())) {
			return null;
		}
		
		return indexNameMap.get(type.getCanonicalName());
	}

	/**
	 * @param indexNotificationService the indexNotificationService to set
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}

	/**
	 * Set the spring bean factory to use.
	 * 
	 * @param beanFactory instance
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Set the indexNameMap which maps fully qualified class names to index names known
	 * by IndexType.
	 *
	 * @param indexNameMap map of class name index name pairs 
	 */
	public void setIndexNameMap(final Map<String, IndexType> indexNameMap) {
		this.indexNameMap = indexNameMap;
	}

	@Override
	public String getCallbackID() {
		return "Index Notification Callback";
	}
}
