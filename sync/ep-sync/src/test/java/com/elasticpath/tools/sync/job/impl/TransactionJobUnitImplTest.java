/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.JobEntryCreator;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Test cases for {@link TransactionJobUnitImpl}.
 */
public class TransactionJobUnitImplTest {

	private JobEntryCreator transactionJobBuilder;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TransactionJobUnitImpl transactionJobUnit;
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		transactionJobBuilder = context.mock(JobEntryCreator.class);
		transactionJobUnit = new TransactionJobUnitImpl(transactionJobBuilder);
	}

	/**
	 * Tests that getting job entries creates then on the fly using a transaction job builder.
	 */
	@Test
	public void testGetJobEntries() {
		final TransactionJobDescriptorEntry jobUnit = context.mock(TransactionJobDescriptorEntry.class);
		transactionJobUnit.addJobEntry(jobUnit);
		final JobEntry jobEntry = new JobEntryImpl();
		
		context.checking(new Expectations() { {
			oneOf(transactionJobBuilder).createJobEntry(transactionJobUnit, jobUnit);
			will(returnValue(jobEntry));
		} });
		List<JobEntry> jobEntries = transactionJobUnit.createJobEntries();
		
		Iterator<JobEntry> iterator = jobEntries.iterator();
		
		assertEquals("Expected a job entry", jobEntry, iterator.next());
		assertFalse("No other entries expected", iterator.hasNext());
	}

}
