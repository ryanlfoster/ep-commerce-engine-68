/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.target.result.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.impl.JobEntryImpl;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * Test cases for {@link SummaryImpl}.
 */
public class SummaryImplTest {

	private SummaryImpl summary;

	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		summary = new SummaryImpl();
	}

	/**
	 * Tests that adding a sync error is also the result of getSyncErrors().
	 */
	@Test
	public void testAddSyncError() {
		SyncErrorResultItem syncError = new SyncErrorResultItem();
		summary.addSyncError(syncError);
		
		assertEquals(1, summary.getSyncErrors().size());
		assertEquals(syncError, summary.getSyncErrors().iterator().next());
	}

	/**
	 * Tests has errors produces the expected results both when there are and there aren't errors.
	 */
	@Test
	public void testHasErrors() {
		assertFalse(summary.hasErrors());

		SyncErrorResultItem syncError = new SyncErrorResultItem();
		summary.addSyncError(syncError);

		assertTrue(summary.hasErrors());
	}

	/**
	 * Tests that the number of errors corresponds to the added error objects.
	 */
	@Test
	public void testGetNumberOfErrors() {
		assertEquals(0, summary.getNumberOfErrors());
		
		SyncErrorResultItem syncError = new SyncErrorResultItem();
		summary.addSyncError(syncError);
		
		assertEquals(1, summary.getNumberOfErrors());
	}

	/**
	 * Tests that adding a success job entry works properly.
	 */
	@Test
	public void testAddSuccessJobEntry() {
		JobEntry entry = new JobEntryImpl();
		summary.addSuccessJobEntry(entry);
		
		assertEquals(1, summary.getSuccessResults().size());
	}

	/**
	 * Tests that getting all results returns both error and success objects.
	 */
	@Test
	public void testGetAllResults() {
		assertEquals(0, summary.getAllResults().size());
		
		JobEntry entry = new JobEntryImpl();
		summary.addSuccessJobEntry(entry);
		
		SyncErrorResultItem syncError = new SyncErrorResultItem();
		summary.addSyncError(syncError);
		
		assertEquals(2, summary.getAllResults().size());
	}

	/**
	 * Tests that getting success results works as expected.
	 */
	@Test
	public void testGetSuccessResults() {
		JobEntry entry = new JobEntryImpl();
		summary.addSuccessJobEntry(entry);

		assertEquals(1, summary.getSuccessResults().size());
	}

}
