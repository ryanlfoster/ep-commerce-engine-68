/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.dao.impl;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;

/**
 * The default implementation of {@link ImportNotificationDao} based of the usage of {@link PersistenceEngine}.
 */
public class ImportNotificationDaoImpl implements ImportNotificationDao {

	private PersistenceEngine persistenceEngine;
	
	@Override
	public ImportNotification add(final ImportNotification notification) {
		persistenceEngine.save(notification);
		return notification;
	}

	@Override
	public List<ImportNotification> findByActionAndState(final ImportAction action, final ImportNotificationState state, final int maxResults) {
		return persistenceEngine.retrieveByNamedQuery("FIND_BY_ACTION_AND_STATE", new Object[] { action, state }, 0, maxResults);
	}

	@Override
	public void remove(final ImportNotification notification) {
		persistenceEngine.delete(notification);
	}

	/**
	 *
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 *
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	public List<ImportNotification> findByCriteria(final String importJobGuid, final String userGuid, final ImportAction action) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_JOB_ID_USER_GUID_AND_ACTION", importJobGuid, userGuid, action);
	}

	@Override
	public List<ImportNotification> findByProcessId(final String importJobProcessGuid, final ImportAction action) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_PROCESS_ID_AND_ACTION", importJobProcessGuid, action);
	}

	@Override
	public void update(final ImportNotification notification) {
		getPersistenceEngine().merge(notification);
	}

	@Override
	public List<ImportNotification> findByActionAndState(final ImportAction action, final ImportNotificationState state) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_ACTION_AND_STATE", action, state);
	}
}
