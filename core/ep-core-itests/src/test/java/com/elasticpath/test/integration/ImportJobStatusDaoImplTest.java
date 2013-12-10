/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportJobStatusMutator;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobStatusImpl;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.service.dataimport.dao.ImportJobStatusDao;

/**
 * Test cases for {@link ImportJobStatusDao}.
 */
public class ImportJobStatusDaoImplTest extends BasicSpringContextTest {

	@Autowired
	private ImportJobStatusDao importJobStatusDao;

	@Autowired
	private ImportService service;

	@Autowired
	private CmUserService cmUserService;

	private ImportJob importJob;
	private CmUser cmUser;

	/**
	 * Sets up the test case.
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
	 * Tests that saving an {@link ImportJobStatus} is successfull.
	 */
	@DirtiesDatabase
	@Test
	public void testSave() {
		ImportJobStatusMutator status = new ImportJobStatusImpl();
		status.setImportJob(importJob);
		status.setState(ImportJobState.RUNNING);
		status.setStartedBy(cmUser);
		status.setProcessId("processId1");
		
		importJobStatusDao.saveOrUpdate(status);
	}	
	
	/**
	 * Tests that findByImportJobGuid() works properly.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByImportJobGuid() {
		ImportJobStatusMutator status = new ImportJobStatusImpl();
		status.setImportJob(importJob);
		status.setState(ImportJobState.RUNNING);
		status.setStartedBy(cmUser);
		status.setProcessId("processId1");

		importJobStatusDao.saveOrUpdate(status);
		
		List<ImportJobStatus> result = importJobStatusDao.findByImportJobGuid(importJob.getGuid());
		assertNotNull("Result must not be null", result);
		assertEquals("Exactly one result expected", 1, result.size());
		ImportJobStatus loadedStatus = result.iterator().next();
		assertEquals("Expected only one status which is the one created before", status, loadedStatus);
		assertFalse("The job is in running state so should not be cancelled", status.isCanceled());
		assertFalse("The job is in running state so should not be finished", status.isFinished());

		result = importJobStatusDao.findByImportJobGuid("unexistent_guid");
		assertNotNull("Result should not be undefined", result);
		assertTrue("No results should be returned", result.isEmpty());
	}
	
	/**
	 * Tests that findByState() works properly.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByState() {
		ImportJobStatusMutator status = new ImportJobStatusImpl();
		status.setImportJob(importJob);
		status.setState(ImportJobState.RUNNING);
		status.setStartedBy(cmUser);
		status.setProcessId("processId1");

		importJobStatusDao.saveOrUpdate(status);
		
		List<ImportJobStatus> result = importJobStatusDao.findByState(ImportJobState.RUNNING);
		assertNotNull("Result should not be null", result);
		assertEquals("Exactly one result expected", 1, result.size());
		assertEquals("Expected only one status which is the one created before", status, result.iterator().next());
		
		result = importJobStatusDao.findByState(ImportJobState.FINISHED);
		assertNotNull("Result should not be null", result);
		assertTrue("No results should be returned", result.isEmpty());
	}
	
	/**
	 * Test adding and retrieving bad row and import fault from database.
	 */
	@DirtiesDatabase
	@Test
	public void testBadRow() {
		ImportJobStatusMutator status = new ImportJobStatusImpl();
		status.setImportJob(importJob);
		status.setState(ImportJobState.RUNNING);
		status.setStartedBy(cmUser);
		status.setProcessId("processId2");

		ImportBadRow badRow = new ImportBadRowImpl();
		badRow.setRow("Sample ROW...");
		badRow.setRowNumber(1);
		ImportFault importFault = new ImportFaultImpl();
		String[] args = new String[]{"A", "1", "true"};
		importFault.setArgs(args);
		importFault.setCode("Sample Fault Code");
		importFault.setLevel(ImportFault.ERROR);
		importFault.setSource("Sample Exception Message");
		badRow.addImportFault(importFault);
		
		status.addBadRow(badRow);
		
		importJobStatusDao.saveOrUpdate(status);
		
		List<ImportJobStatus> result = importJobStatusDao.findByState(ImportJobState.RUNNING);
		assertNotNull("Result should not be null", result);
		assertEquals("Exactly one result expected", 1, result.size());
		ImportJobStatus resultStatus = result.iterator().next();
		assertEquals("Expected only one status which is the one created before", status, resultStatus);
		assertEquals("Expected only one bad row", 1, resultStatus.getBadRows().size());
		ImportBadRow resultBadRow = resultStatus.getBadRows().get(0);
		assertEquals("Expected row from bad row", "Sample ROW...", resultBadRow.getRow());
		assertEquals("Expected only one import fault", 1, resultBadRow.getImportFaults().size());
		ImportFault resultImportFault = resultBadRow.getImportFaults().get(0);
		assertEquals("Expected code of import fault", "Sample Fault Code", resultImportFault.getCode());
		assertTrue("Expected args", ArrayUtils.isEquals(args, resultImportFault.getArgs()));
	}
	
	/**
	 * Tests find by process ID.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByProcessId() {
		assertNull("No import status has been created. so no result should come back from DB", importJobStatusDao.findByProcessId(""));
	}

}
