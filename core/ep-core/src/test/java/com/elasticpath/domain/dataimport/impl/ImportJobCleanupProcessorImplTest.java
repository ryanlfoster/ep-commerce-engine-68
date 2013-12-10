package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.service.dataimport.StaleImportNotificationProcessor;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;
import com.elasticpath.service.dataimport.impl.ImportJobCleanupProcessorImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * ImportJobCleanupProcessorImpl unit test class.
 */
public class ImportJobCleanupProcessorImplTest {

	private ImportJobCleanupProcessorImpl importJobCleanupProcessor;

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private final ImportNotificationDao importNotificationDao = mockery.mock(ImportNotificationDao.class);
	private final ImportJobStatusDao importJobStatusDao = mockery.mock(ImportJobStatusDao.class);
	private final SettingsReader settingsReader = mockery.mock(SettingsReader.class);
	private final TimeService timeService = mockery.mock(TimeService.class);

	/**
	 * Prepare for tests.
	 */
	@Before
	public void setUp() {
		importJobCleanupProcessor = new ImportJobCleanupProcessorImpl();
		importJobCleanupProcessor.setImportJobStatusDao(importJobStatusDao);
		importJobCleanupProcessor.setImportNotificationDao(importNotificationDao);
		importJobCleanupProcessor.setSettingsReader(settingsReader);
		importJobCleanupProcessor.setTimeService(timeService);
	}
	
	/**
	 * 
	 */
	@Test(expected = EpSystemException.class)
	public void testCleanupImportJobWithNullMaxAgeSetting() {
		ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "1");
		final List<ImportNotification> importNotifications = new ArrayList<ImportNotification>();
		importNotifications.add(launchImportNotification);
		
		mockery.checking(new Expectations() { {
			oneOf(settingsReader).getSettingValue(with(ImportJobCleanupProcessorImpl.SETTING_IMPORT_JOB_MAX_AGE));
			will(returnValue(null));
		} });
		
		importJobCleanupProcessor.cleanupImportJobData();
		fail("An EpSystemException must be thrown if no no max age setting is found.");
	}
	
	/**
	 * Create a very young notification that is not expired and does not get it's data deleted.
	 */
	@Test
	public void testCleanupImportJobWithNonExpiredNotification() {
		final SettingValue importJobMaxAgeSettingValue = new SettingValueImpl();
		importJobMaxAgeSettingValue.setValue("100000000");
		
		ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "processId-1");
		final List<ImportNotification> importNotifications = new ArrayList<ImportNotification>();
		importNotifications.add(launchImportNotification);
		
		final List<ImportJobState> importJobStates = new ArrayList<ImportJobState>();
		
		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.PROCESSED));
			will(returnValue(importNotifications));
			oneOf(settingsReader).getSettingValue(with(ImportJobCleanupProcessorImpl.SETTING_IMPORT_JOB_MAX_AGE));
			will(returnValue(importJobMaxAgeSettingValue));
			oneOf(timeService).getCurrentTime();
			will(returnValue(new Date()));
			oneOf(importJobStatusDao).findByState(with(ImportJobState.QUEUED_FOR_VALIDATION));
			will(returnValue(importJobStates));
		} });
		
		int importJobsAffected = importJobCleanupProcessor.cleanupImportJobData();
		assertEquals("No import job should be affected with no notifications found.", 0, importJobsAffected);
	}
	
	/**
	 * Create a very old notification to force expiration, and thus a cleanup of it's data. 
	 */
	@Test
	public void testCleanupImportJobWithExpiredNotification() {
		final SettingValue importJobMaxAgeSettingValue = new SettingValueImpl();
		importJobMaxAgeSettingValue.setValue("1");
		
		final String processId = "processId-" + System.currentTimeMillis();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		
		final ImportNotification launchImportNotification = getLaunchImportNotification(cal.getTime(), processId);
		final List<ImportNotification> importNotifications = new ArrayList<ImportNotification>();
		importNotifications.add(launchImportNotification);
		
		final ImportJobStatus importJobStatus = new ImportJobStatusImpl();
		
		final ImportNotification cancelImportNotification = getCancelImportNotification();
		final List<ImportNotification> cancelImportNotifications = new ArrayList<ImportNotification>();
		cancelImportNotifications.add(cancelImportNotification);
		
		final List<ImportJobState> importJobStates = new ArrayList<ImportJobState>();
		
		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.PROCESSED));
			will(returnValue(importNotifications));
			oneOf(settingsReader).getSettingValue(with(ImportJobCleanupProcessorImpl.SETTING_IMPORT_JOB_MAX_AGE));
			will(returnValue(importJobMaxAgeSettingValue));
			oneOf(timeService).getCurrentTime();
			will(returnValue(new Date()));
			oneOf(importJobStatusDao).findByProcessId(with(processId));
			will(returnValue(importJobStatus));
			oneOf(importJobStatusDao).remove(importJobStatus);
			oneOf(importNotificationDao).findByProcessId(with(processId), with(ImportAction.LAUNCH_IMPORT));
			will(returnValue(importNotifications));
			oneOf(importNotificationDao).remove(launchImportNotification);
			oneOf(importNotificationDao).findByProcessId(with(processId), with(ImportAction.CANCEL_IMPORT));
			will(returnValue(cancelImportNotifications));
			oneOf(importNotificationDao).remove(cancelImportNotification);
			oneOf(importJobStatusDao).findByState(with(ImportJobState.QUEUED_FOR_VALIDATION));
			will(returnValue(importJobStates));
		} });
		
		int importJobsAffected = importJobCleanupProcessor.cleanupImportJobData();
		assertEquals("Exactly 1 import job should be affected.", 1, importJobsAffected);
	}
	
	/**
	 * Creates an launch import notification.
	 * 
	 * @param dateCreated the date to use as the creation
	 * @param processId the processId to assign to the new import notification
	 * @return an import notification
	 */
	protected ImportNotification getLaunchImportNotification(final Date dateCreated, final String processId) {
		ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileName("csv-filename.csv");
		
		ImportNotification notification = new ImportNotificationImpl() {
			private static final long serialVersionUID = -5086517190878593108L;

			@Override
			public Date getDateCreated() {
				return dateCreated;
			}
		};
		notification.setAction(ImportAction.LAUNCH_IMPORT);
		notification.setProcessId(processId);
		notification.setImportJob(importJob);
		
		return notification;
	}

	/**
	 * Creates an cancel import notification.
	 * 
	 * @return an import notification
	 */
	protected ImportNotification getCancelImportNotification() {
		ImportJob importJob = new ImportJobImpl();
		importJob.setCsvFileName("csv-filename.csv");
		
		ImportNotification notification = new ImportNotificationImpl();
		notification.setAction(ImportAction.CANCEL_IMPORT);
		notification.setImportJob(importJob);
		
		return notification;
	}

	/**
	 * Tests that having an import job status updated after the cut out time of 1 minute (defined by the setting)
	 * does not trigger a job for clean up.
	 */
	@Test
	public void testProcessStaleImportJob() {
		final SettingValue importJobStaleTimeoutValue = mockery.mock(SettingValue.class);

		final Date currentTime = new Date();
		
		final String processId = "processId1";
		final ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), processId);

		final ImportJobStatus importJobStatus = mockery.mock(ImportJobStatus.class);

		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.IN_PROCESS));
			will(returnValue(Arrays.asList(launchImportNotification)));
			oneOf(importJobStatusDao).findByProcessId(processId);
			will(returnValue(importJobStatus));
			oneOf(settingsReader).getSettingValue(with(ImportJobCleanupProcessorImpl.SETTING_IMPORT_JOB_STALE_TIMEOUT));
			will(returnValue(importJobStaleTimeoutValue));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));

			oneOf(importJobStatus).getLastModifiedDate();
			// the last modified date of a status is exactly the current time which is later
			// than one minute in the past (defined by the setting).
			will(returnValue(currentTime));

			oneOf(importJobStaleTimeoutValue).getValue();
			will(returnValue("1"));
		} });

		importJobCleanupProcessor.processStaleImportJobs();
	}
	
	/**
	 * Tests that having an import job status updated after the cut out time of 1 minute (defined by the setting)
	 * does not trigger a job for clean up.
	 */
	@Test
	public void testProcessStaleImportJobTimeoutElapsed() {
		final StaleImportNotificationProcessor staleImportNotificationProcessor = 
			mockery.mock(StaleImportNotificationProcessor.class);
		
		importJobCleanupProcessor.setStaleImportNotificationProcessor(staleImportNotificationProcessor);
		final SettingValue importJobStaleTimeoutValue = mockery.mock(SettingValue.class);

		// set the current time one minute in the past
		final Date currentTime = new Date();
		final ImportNotification launchImportNotification = getLaunchImportNotification(new Date(), "processId1");

		final ImportJobStatus importJobStatus = mockery.mock(ImportJobStatus.class);

		mockery.checking(new Expectations() { {
			oneOf(importNotificationDao).findByActionAndState(with(ImportAction.LAUNCH_IMPORT), with(ImportNotificationState.IN_PROCESS));
			will(returnValue(Arrays.asList(launchImportNotification)));
			oneOf(importJobStatusDao).findByProcessId("processId1");
			will(returnValue(importJobStatus));
			oneOf(settingsReader).getSettingValue(with(ImportJobCleanupProcessorImpl.SETTING_IMPORT_JOB_STALE_TIMEOUT));
			will(returnValue(importJobStaleTimeoutValue));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));

			oneOf(importJobStatus).getLastModifiedDate();
			// the last modified date of a status is 4 minutes before the calculated timeout (currentTime - 1)
			final int fiveMinutesBeforeNow = -5;
			will(returnValue(DateUtils.addMinutes(new Date(), fiveMinutesBeforeNow)));

			oneOf(importJobStaleTimeoutValue).getValue();
			will(returnValue("1"));

			oneOf(staleImportNotificationProcessor).process(launchImportNotification);
		} });

		importJobCleanupProcessor.processStaleImportJobs();
	}

}
