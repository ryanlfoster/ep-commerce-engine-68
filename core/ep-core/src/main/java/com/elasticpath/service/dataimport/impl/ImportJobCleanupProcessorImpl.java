/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.service.dataimport.ImportJobCleanupProcessor;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * The default implementation of {@link ImportJobCleanupProcessor}.
 * 
 * Import jobs remain in the database after successful processing. This processor is responsible for
 * calculating the age of the import job data and removing it from the database, based on an maximum age
 * setting defined.
 */
public class ImportJobCleanupProcessorImpl extends AbstractEpServiceImpl implements ImportJobCleanupProcessor {
	
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(ImportJobCleanupProcessorImpl.class);

	/**
	 * The import job max age setting path.
	 */
	public static final String SETTING_IMPORT_JOB_MAX_AGE = "COMMERCE/SYSTEM/IMPORT/importJobMaxAge";

	/**
	 * The import job timeout before a job is considered stale.
	 */
	public static final String SETTING_IMPORT_JOB_STALE_TIMEOUT = "COMMERCE/SYSTEM/IMPORT/staleImportJobTimeout";

	private static final long MILLI = 1000;

	private ImportNotificationDao importNotificationDao;
		
	private ImportJobStatusDao importJobStatusDao;
	
	private SettingsReader settingsReader;
	
	private TimeService timeService;

	private StaleImportNotificationProcessor staleImportNotificationProcessor;

	@Override
	public int cleanupImportJobData() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start cleanup import job quartz job at: " + new Date(startTime));
		
		int importJobsAffected = 0;
		
		SettingValue importJobMaxAgeValue = getSettingsReader().getSettingValue(SETTING_IMPORT_JOB_MAX_AGE);
		if (importJobMaxAgeValue == null) {
			throw new EpSystemException("Missing setting value for import job max age: " + SETTING_IMPORT_JOB_MAX_AGE);
		}
		
		// get a list of all processed launch notifications (success or failure of the process is not applicable) 
		List<ImportNotification> notifications = 
			importNotificationDao.findByActionAndState(ImportAction.LAUNCH_IMPORT, ImportNotificationState.PROCESSED);
		
		for (ImportNotification notification : notifications) {
			importJobsAffected += processNotifications(importJobMaxAgeValue, notification);
		}
		
		// also delete old import job attempts that failed validation or were cancelled by the user before a launch 
		// notification (clicking Cancel instead of Finish button in wizard), thus stuck in QUEUED_FOR_VALIDATION state
		List<ImportJobStatus> importJobStatusList = importJobStatusDao.findByState(ImportJobState.QUEUED_FOR_VALIDATION);
		
		for (ImportJobStatus importJobStatus : importJobStatusList) {
			importJobsAffected += processValidationStates(importJobMaxAgeValue, importJobStatus);
		}
		
		LOG.info("Import jobs cleaned up: " + importJobsAffected);
		LOG.info("Cleanup import job quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
		
		return importJobsAffected;
	}

	private int processValidationStates(final SettingValue importJobMaxAgeValue, final ImportJobStatus importJobStatus) {
		int importJobsAffected = 0;
		
		Date importTime = importJobStatus.getEndTime();
		if (importTime == null) {
			// let's not assume that the end time has always been set
			importTime = importJobStatus.getStartTime();
		}
		
		if (importTime == null) {
			throw new EpSystemException("No start or end time set for import job status with process ID: " 
					+ importJobStatus.getProcessId());
		}
		
		boolean maxAgeExceeded = isMaxAgeExceeded(importJobMaxAgeValue, importTime.getTime());
		
		if (maxAgeExceeded) {
			// bump up affected jobs counter
			importJobsAffected++;
			
			importJobStatusDao.remove(importJobStatus);
		}
		
		return importJobsAffected;
	}

	private int processNotifications(final SettingValue importJobMaxAgeValue, final ImportNotification notification) {
		int importJobsAffected = 0;
		
		// the import job has remained in the system too long if the 
		// current time is past the maximum age of the notification
		boolean maxAgeExceeded = isMaxAgeExceeded(importJobMaxAgeValue, notification.getDateCreated().getTime());
		
		if (maxAgeExceeded) {
			// bump up affected jobs counter
			importJobsAffected++;
			
			// delete the import job process status data, cascading the deletes over it's bad rows and fault tables
			ImportJobStatus importJobStatus = importJobStatusDao.findByProcessId(notification.getProcessId());
			importJobStatusDao.remove(importJobStatus);
			
			// delete all launch notifications associated with this import job
			List<ImportNotification> importNotifications = 
				importNotificationDao.findByProcessId(notification.getProcessId(), ImportAction.LAUNCH_IMPORT);
			for (ImportNotification importNotification : importNotifications) {
				importNotificationDao.remove(importNotification);
			}
			
			// delete all cancel notifications associated with this import job
			importNotifications = importNotificationDao.findByProcessId(notification.getProcessId(), ImportAction.CANCEL_IMPORT);
			for (ImportNotification importNotification : importNotifications) {
				importNotificationDao.remove(importNotification);
			}
		}
		
		return importJobsAffected;
	}

	private boolean isMaxAgeExceeded(final SettingValue importJobMaxAgeValue, final long dateCreated) {
		long now = timeService.getCurrentTime().getTime();
		long importJobMaxAgeInSeconds = Long.parseLong(importJobMaxAgeValue.getValue());
		long maxAge = dateCreated + (importJobMaxAgeInSeconds * MILLI);
		boolean maxAgeExceeded = now > maxAge;
		
		return maxAgeExceeded;
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

	/**
	 *
	 * @return the importJobStatusDao
	 */
	protected ImportJobStatusDao getImportJobStatusDao() {
		return importJobStatusDao;
	}

	/**
	 *
	 * @param importJobStatusDao the importJobStatusDao to set
	 */
	public void setImportJobStatusDao(final ImportJobStatusDao importJobStatusDao) {
		this.importJobStatusDao = importJobStatusDao;
	}

	/**
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}
	
	/**
	 * @return the timeService
	 */
	public TimeService getTimeService() {
		return timeService;
	}

	/**
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Finds and processes all stale import jobs.
	 */
	public void processStaleImportJobs() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start process stale import job quartz job at: " + new Date(startTime));
		LOG.debug("Starting up the stale import job clean up process.");
		
		// check whether a processor is stuck
		List<ImportNotification> jobsInProcess = getImportNotificationDao().findByActionAndState(
				ImportAction.LAUNCH_IMPORT, ImportNotificationState.IN_PROCESS);
		
		for (ImportNotification importNotification : jobsInProcess) {
			ImportJobStatus status = getImportJobStatusDao().findByProcessId(importNotification.getProcessId());
			// check whether the last updated date is earlier than the limit we have, based on current time
			Date cutOutTime = DateUtils.addMinutes(getTimeService().getCurrentTime(), -getStaleImportJobTimeoutInMinutes());
			if (status.getLastModifiedDate().compareTo(cutOutTime) < 0) {
				LOG.debug("Found a stale import job. Its import notification: " + importNotification);
				try {
					getStaleImportNotificationProcessor().process(importNotification);
				} catch (Exception exc) {
					LOG.error("Could not process a stale import notification.", exc);
				}
			}
		}
		
		LOG.info("Process stale import job quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Sets the stale import notification processor.
	 * 
	 * @param staleImportNotificationProcessor the processor
	 */
	public void setStaleImportNotificationProcessor(final StaleImportNotificationProcessor staleImportNotificationProcessor) {
		this.staleImportNotificationProcessor = staleImportNotificationProcessor;
	}

	/**
	 * Gets the timeout.
	 * 
	 * @return stale import job timeout in minutes
	 */
	protected int getStaleImportJobTimeoutInMinutes() {
		String staleImportJobTimeout = getSettingsReader().getSettingValue(SETTING_IMPORT_JOB_STALE_TIMEOUT).getValue();
		return Integer.valueOf(staleImportJobTimeout);
	}

	/**
	 *
	 * @return the staleImportNotificationProcessor
	 */
	protected StaleImportNotificationProcessor getStaleImportNotificationProcessor() {
		return staleImportNotificationProcessor;
	}
}
