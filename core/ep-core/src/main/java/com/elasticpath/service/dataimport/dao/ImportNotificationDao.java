/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.dao;

import java.util.List;

import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;

/**
 * A DAO for dealing with {@link ImportNotification} objects.
 */
public interface ImportNotificationDao {

	/**
	 * Adds a notification to the data source.
	 * 
	 * @param notification the notification
	 * @return the updated notification
	 */
	ImportNotification add(ImportNotification notification);

	/**
	 * Removes a notification from the data source.
	 * 
	 * @param notification the notification
	 */
	void remove(ImportNotification notification);
	
	/**
	 * Finds the list of all import notifications with the given action.
	 * 
	 * @param action the action type requested
	 * @param state the notification state
	 * @param maxResults the max results expected
	 * @return a list of import notifications
	 */
	List<ImportNotification> findByActionAndState(ImportAction action, ImportNotificationState state, int maxResults);

	/**
	 * Finds a list of import notifications.
	 * 
	 * @param importJobGuid the import job GUID
	 * @param userGuid the user GUID
	 * @param action the action
	 * @return a list of notifications
	 */
	List<ImportNotification> findByCriteria(String importJobGuid, String userGuid, ImportAction action);

	/**
	 * Finds a list of import notifications.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param action the action
	 * @return a list of import notifications
	 */
	List<ImportNotification> findByProcessId(String importJobProcessId, ImportAction action);

	/**
	 * Updates a notification.
	 * 
	 * @param notification the notification to update
	 */
	void update(ImportNotification notification);

	/**
	 * Finds a list of import notifications.
	 * 
	 * @param action the action
	 * @param state the state
	 * @return a list of import notifications
	 */
	List<ImportNotification> findByActionAndState(ImportAction action, ImportNotificationState state);

}
