/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * An import notification is used to store events related to import job processing.
 */
public interface ImportNotification extends ImportJobRequest, Persistable {

	/**
	 * The date this notification was created.
	 * 
	 * @return the creation date
	 */
	Date getDateCreated();

	/**
	 * Sets the date and time this notification was created.
	 * 
	 * @param dateCreated the date and time this notification was created
	 */
	void setDateCreated(final Date dateCreated);

	/**
	 * Sets the action type.
	 * 
	 * @param action th import action type
	 */
	void setAction(ImportAction action);
	
	/**
	 * Gets the import action.
	 * 
	 * @return the import action
	 */
	ImportAction getAction();
	
	/**
	 * The import job process ID.
	 * 
	 * @return the ID of the import job process
	 */
	String getProcessId();

	/**
	 * Sets the process ID.
	 * 
	 * @param processId the process ID
	 */
	void setProcessId(String processId);
	
	/**
	 * Sets the state of this notification.
	 * 
	 * @param state the state
	 */
	void setState(ImportNotificationState state);
	
	/**
	 * Returns state of this notification.
	 * 
	 * @return the import notification state
	 */
	ImportNotificationState getState();
}
