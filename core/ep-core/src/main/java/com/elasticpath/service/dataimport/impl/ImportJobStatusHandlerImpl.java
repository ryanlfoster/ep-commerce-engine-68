/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportJobStatusMutator;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.service.dataimport.ImportJobStatusHandler;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.TimeService;

/**
 * The default implementation of {@link ImportJobStatusHandler} based on 
 * the {@link ImportJobStatusDao} and {@link ImportNotificationDao}.
 */
public class ImportJobStatusHandlerImpl extends AbstractEpServiceImpl implements ImportJobStatusHandler {

	private static final Logger LOG = Logger.getLogger(ImportJobStatusHandlerImpl.class);
	
	private ImportJobStatusDao importJobStatusDao;
	
	private ImportNotificationDao importNotificationDao;
	
	private TimeService timeService;
	
	/**
	 * Gets the current status of an import job.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @return a never null ImportJobStatus instance
	 */
	public ImportJobStatus getImportJobStatus(final String importJobProcessId) {
		final ImportJobStatus status = importJobStatusDao.findByProcessId(importJobProcessId);
		if (status == null) {
			throw new IllegalArgumentException("The import job status guid does not exist: " + importJobProcessId);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Import Job Status: " + status);
		}
		return status;
	}

	@Override
	public boolean isImportJobCancelled(final String importJobProcessId) {
		List<ImportNotification> result = importNotificationDao.findByProcessId(
				importJobProcessId, ImportAction.CANCEL_IMPORT);
		return CollectionUtils.isNotEmpty(result);
	}

	@Override
	public void reportBadRows(final String importJobProcessId, final ImportBadRow... importBadRows) {
		ImportJobStatusMutator importJobStatus = (ImportJobStatusMutator) getImportJobStatus(importJobProcessId);
		for (ImportBadRow badRow : importBadRows) {
			importJobStatus.addBadRow(badRow);
		}
		
		importJobStatusDao.saveOrUpdate(importJobStatus);
	}

	@Override
	public void reportCurrentRow(final String importJobProcessId, final int rowNumber) {
		ImportJobStatusMutator importJobStatus = (ImportJobStatusMutator) getImportJobStatus(importJobProcessId);
		importJobStatus.setCurrentRow(rowNumber);
		
		importJobStatusDao.saveOrUpdate(importJobStatus);
	}

	@Override
	public void reportFailedRows(final String importJobProcessId, final int failedRows) {
		ImportJobStatusMutator importJobStatus = (ImportJobStatusMutator) getImportJobStatus(importJobProcessId);
		importJobStatus.setFailedRows(importJobStatus.getFailedRows() + failedRows);

		importJobStatusDao.saveOrUpdate(importJobStatus);
	}

	/**
	 * Reports the total rows count for an import job. This will first check if the job exists, as during
	 * a client-side validation the job will not yet be created.
	 * 
	 * @param importJobProcessId the import job process ID
	 * @param totalRows the total rows
	 */
	public void reportTotalRows(final String importJobProcessId, final int totalRows) {
		if (doesImportJobExist(importJobProcessId)) {
			ImportJobStatusMutator importJobStatus = (ImportJobStatusMutator) getImportJobStatus(importJobProcessId);
			importJobStatus.setTotalRows(totalRows);
			
			importJobStatusDao.saveOrUpdate(importJobStatus);
		}
	}

	@Override
	public boolean verifyImportJobFailedRows(final String importJobProcessId, final int maxAllowedFailedRows) {
		ImportJobStatus importJobStatus = getImportJobStatus(importJobProcessId);
		boolean isJobOk = importJobStatus.getFailedRows() <= maxAllowedFailedRows;
		if (LOG.isInfoEnabled() && !isJobOk) {
			LOG.info(String.format("Import job failed because too many bad rows have been reported. Import job: %s, started by user %s", 
					importJobStatus.getImportJob().getName(), importJobStatus.getStartedBy().getUsername()));
		}

		return isJobOk;
	}

	/**
	 *
	 * @return the importNotificationDao
	 */
	protected ImportNotificationDao getImportNotificationDao() {
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

	@Override
	public void reportImportJobState(final String importJobProcessId, final ImportJobState state) {
		ImportJobStatusMutator importJobStatus = (ImportJobStatusMutator) getImportJobStatus(importJobProcessId);
		importJobStatus.setState(state);
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Changing state for import job: %s, started by user %s to: %s", 
					importJobStatus.getImportJob().getName(), importJobStatus.getStartedBy().getUsername(), state));
		}
		updateImportJobStatusTime(importJobStatus);
		
		importJobStatusDao.saveOrUpdate(importJobStatus);
	}

	/**
	 * Updates the start/end time of a status.
	 * 
	 * @param importJobStatus the status
	 */
	protected void updateImportJobStatusTime(final ImportJobStatusMutator importJobStatus) {
		if (ObjectUtils.equals(importJobStatus.getState(), ImportJobState.VALIDATING)) {
			importJobStatus.setStartTime(timeService.getCurrentTime());
		}
		if (importJobStatus.isFinished()) {
			importJobStatus.setEndTime(timeService.getCurrentTime());
		}
	}

	@Override
	public ImportJobStatus initialiseJobStatus(final String processId, final ImportJob importJob, final CmUser initiator) {
		ImportJobStatus statusInDb = importJobStatusDao.findByProcessId(processId);
		if (statusInDb != null) {
			LOG.debug("Found an existing status: " + statusInDb);
			return statusInDb;
		}
		ImportJobStatusMutator importJobStatus = getBean(ContextIdNames.IMPORT_JOB_STATUS);
		importJobStatus.setImportJob(importJob);
		importJobStatus.setStartedBy(initiator);
		importJobStatus.setState(ImportJobState.QUEUED_FOR_VALIDATION);
		importJobStatus.setProcessId(processId);
		
		return importJobStatusDao.saveOrUpdate(importJobStatus);
	}

	/**
	 *
	 * @return the timeService
	 */
	protected TimeService getTimeService() {
		return timeService;
	}
	
	/**
	 *
	 * @param timeService the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
	
	/**
	 * Determines whether an import job exists for the given process ID.
	 * 
	 * @param importJobProcessId the ID to check
	 * @return true if the import job has a status
	 */
	public boolean doesImportJobExist(final String importJobProcessId) {
		return importJobStatusDao.doesImportJobExist(importJobProcessId);
	}

}
