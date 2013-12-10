/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.dataimport.impl.ImportNotificationImpl;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.dataimport.dao.ImportNotificationDao;

/**
 * Test cases for {@link ImportNotificationDao}.
 */
public class ImportNotificationDaoImplTest extends BasicSpringContextTest {

	@Autowired
	private ImportNotificationDao importNotificationDao;

	@Autowired
	private ImportService service;

	@Autowired
	private CmUserService cmUserService;

	private ImportJob importJob;
	private CmUser cmUser;

	/**
	 *
	 */
	@Before
	public void setUp() {
		importJob = new ImportJobImpl();
		importJob.setName("Import Job 1");
		importJob.setGuid("importjob1");
		importJob.setCsvFileName("file.csv");
		importJob.setCsvFileColDelimeter('|');
		importJob.setCsvFileTextQualifier(':');
		importJob.setImportDataTypeName("dataType1");
		
		importJob = service.saveOrUpdateImportJob(importJob);

		cmUser = cmUserService.list().iterator().next();
	}
	
	/**
	 * Tests adding a notification to the data source.
	 */
	@DirtiesDatabase
	@Test
	public void testAddNotification() {
		ImportNotification notification = createNotification();
		importNotificationDao.add(notification);
	}

	/**
	 * Tests finding a notification by criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCriteria() {
		ImportNotification notification = createNotification();
		importNotificationDao.add(notification);
		
		List<ImportNotification> notificationList = importNotificationDao.
			findByCriteria(importJob.getGuid(), cmUser.getGuid(), ImportAction.LAUNCH_IMPORT);
		
		assertEquals("Only one result expected", 1, notificationList.size());
	
		ImportNotification loadedNotification = notificationList.iterator().next();
		assertEquals("The import job should be the same", notification.getImportJob(), loadedNotification.getImportJob());
		assertEquals("The user should be the same", notification.getInitiator(), loadedNotification.getInitiator());
		assertEquals("The action should be the same", notification.getAction(), loadedNotification.getAction());
		assertEquals("The locale should be the same", notification.getReportingLocale(), loadedNotification.getReportingLocale());
		assertNotNull("The date should be populated", loadedNotification.getDateCreated());
		
		
		notificationList = importNotificationDao.
		findByCriteria(importJob.getGuid(), cmUser.getGuid(), ImportAction.CANCEL_IMPORT);

		assertEquals("No cancel notifications have been logged", 0, notificationList.size());
	}

	/**
	 * Tests that notifications are ordered by creation time.
	 * @throws InterruptedException if the thread was interrupted
	 */
	@DirtiesDatabase
	@Test
	public void testNotificationsAreOrdered() throws InterruptedException {
		ImportNotification notification = createNotification();
		importNotificationDao.add(notification);

		final long oneSecond = 1000;
		Thread.sleep(oneSecond);
		
		ImportNotification notification2 = new ImportNotificationImpl();
		notification2.setAction(ImportAction.LAUNCH_IMPORT);
		notification2.setImportJob(importJob);
		notification2.setInitiator(cmUser);
		notification2.setReportingLocale(Locale.US);
		notification2.setProcessId("processId2");
		notification2.setDateCreated(new Date());
		
		importNotificationDao.add(notification2);

		List<ImportNotification> notificationList = importNotificationDao.
			findByCriteria(importJob.getGuid(), cmUser.getGuid(), ImportAction.LAUNCH_IMPORT);
		
		assertEquals("Two results expected", 2, notificationList.size());
	
		ImportNotification loadedNotification1 = notificationList.get(0);
		ImportNotification loadedNotification2 = notificationList.get(1);
		
		assertEquals("The date of notification1 is expected to be earlier in time compared to notification2", 
				-1, loadedNotification1.getDateCreated().compareTo(loadedNotification2.getDateCreated()));
	}


	private ImportNotification createNotification() {
		ImportNotification notification = new ImportNotificationImpl();
		notification.setAction(ImportAction.LAUNCH_IMPORT);
		notification.setImportJob(importJob);
		notification.setInitiator(cmUser);
		notification.setReportingLocale(Locale.US);
		notification.setProcessId("processId1");
		notification.setDateCreated(new Date());
		return notification;
	}
}
