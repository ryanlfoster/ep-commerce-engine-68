/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport.impl;

import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportJobStatusMutator;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.misc.ImportEmailPropertyHelper;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.EmailService;

/**
 * A processor that sends an email with information about the stale job.
 */
public class EmailSenderStaleImportNotificationProcessorImpl extends AbstractEpServiceImpl implements StaleImportNotificationProcessor {

	private EmailService emailService;
	private ImportJobStatusDao importJobStatusDao;
	private ImportNotificationDao importNotificationDao;

	/**
	 * Processes a notification by setting its state to PROCESSED. Also sets the status of the associated import job to FAILED
	 * and sends an email to the user who ran the import job.
	 * 
	 * @param importNotification the import notification
	 */
	public void process(final ImportNotification importNotification) {
		setImportNotificationProcessedState(importNotification);
		
		ImportJobStatus status = findImportJobStatus(importNotification.getProcessId());
		setImportJobStatusStateFailed(status);
		
		sendEmail(status, importNotification.getInitiator(), importNotification.getReportingLocale());
	}

	/**
	 * Sets the state of the status to FAILED.
	 * 
	 * @param status the import job status
	 */
	protected void setImportJobStatusStateFailed(final ImportJobStatus status) {
		ImportJobStatusMutator statusMutator = (ImportJobStatusMutator) status;
		statusMutator.setState(ImportJobState.FAILED);
		getImportJobStatusDao().saveOrUpdate(status);
	}

	/**
	 * Sets the import notification state to processed.
	 * 
	 * @param importNotification the import notification
	 */
	protected void setImportNotificationProcessedState(final ImportNotification importNotification) {
		importNotification.setState(ImportNotificationState.PROCESSED);
		getImportNotificationDao().update(importNotification);
	}

	/**
	 * Finds an import job status by its processId.
	 * 
	 * @param processId the process ID
	 * @return the import job status
	 */
	protected ImportJobStatus findImportJobStatus(final String processId) {
		return getImportJobStatusDao().findByProcessId(processId);
	}

	/**
	 * Sends an email when the import job completes.
	 * 
	 * @param importJobStatus the import job status
	 * @param cmUser the CM user
	 * @param locale the locale
	 */
	protected void sendEmail(final ImportJobStatus importJobStatus, final CmUser cmUser, final Locale locale) {
		// Send email report
		if (cmUser != null) {
			ImportEmailPropertyHelper importEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_IMPORT);
			
			EmailProperties emailProperties = importEmailPropHelper.getEmailProperties(importJobStatus, cmUser, locale);
			getEmailService().sendMail(emailProperties);
		}
	}

	/**
	 *
	 * @return the emailService
	 */
	protected EmailService getEmailService() {
		return emailService;
	}

	/**
	 *
	 * @param emailService the emailService to set
	 */
	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
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

}
