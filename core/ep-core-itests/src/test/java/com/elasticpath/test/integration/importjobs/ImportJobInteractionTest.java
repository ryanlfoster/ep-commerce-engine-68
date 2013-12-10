package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test that the import job running correctly during concurrent modifications.
 */
public class ImportJobInteractionTest extends ImportJobTestCase {

	/**
	 * Tests that in case of import cancellation some data gets skipped.
	 * 
	 * @throws InterruptedException if interrupted
	 */
	@DirtiesDatabase
	@Test
	public void testCancelImportCategories() throws InterruptedException {
		CmUser initiator = scenario.getCmUser();
		ImportJob importJob = createInsertCategoriesImportJobManyRows();
		ImportJobRequest importJobProcessRequest = new ImportJobRequestImpl();
		importJobProcessRequest.setImportJob(importJob);
		importJobProcessRequest.setImportSource(importJob.getCsvFileName());
		importJobProcessRequest.setInitiator(initiator);
		importJobProcessRequest.setReportingLocale(Locale.getDefault());
		
		ImportJobStatus status = importService.scheduleImport(importJobProcessRequest);

		new ImportJobProcessorLauncher(getBeanFactory()).launch();
		
		/** wait until several row gets imported. */
		final long startTime = System.currentTimeMillis();
		while (status.getCurrentRow() == 0) {
			Thread.sleep(100);
			status = importService.getImportJobStatus(status.getProcessId());
			if (System.currentTimeMillis() > startTime + 10*1000) {
			    fail("Timed out waiting for import job status");
			}
		}
		assertEquals(0, status.getFailedRows());

		/** force import cancellation. */
		importService.cancelImportJob(status.getProcessId(), initiator);

		/** wait until working thread will be actually cancelled and all the statuses are to be set. */
		Thread.sleep(1500);

		status = importService.getImportJobStatus(status.getProcessId());
		
		assertTrue(status.isCanceled());
		assertTrue(status.isFinished());
		// at least the last category should not be imported, since import job was canceled.
		CategoryService categoryService = getBeanFactory().getBean(ContextIdNames.CATEGORY_SERVICE);
		assertNull(categoryService.findByGuid("10100", scenario.getCatalog()));
	}
}
