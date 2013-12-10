/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import org.apache.log4j.Logger;

import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;

/**
 * Resets the status of an import job to NEW which allows for the next processor to take it over and run it again.
 */
public class ResetStatusStaleImportNotificationProcessorImpl implements StaleImportNotificationProcessor {

	private static final Logger LOG = Logger.getLogger(ResetStatusStaleImportNotificationProcessorImpl.class);
	
	private ImportNotificationDao importNotificationDao;

	/**
	 * Processes an import notification by setting its state to NEW and updating it to the data store.
	 * 
	 * @param importNotification the import notification to process
	 */
	public void process(final ImportNotification importNotification) {
		LOG.debug("Resetting the state of this import notification: " + importNotification);
		importNotification.setState(ImportNotificationState.NEW);
		getImportNotificationDao().update(importNotification);

	}

	/**
	 *
	 * @return the importNotificationDao
	 */
	public ImportNotificationDao getImportNotificationDao() {
		return importNotificationDao;
	}

	/**
	 *
	 * @param importNotificationDao the importNotificationDao to set
	 */
	public void setImportNotificationDao(final ImportNotificationDao importNotificationDao) {
		this.importNotificationDao = importNotificationDao;
	}

}
