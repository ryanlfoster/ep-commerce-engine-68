/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.processing.SerializableObject;

/**
 * Test cases for {@link TransactionJobImpl}.
 */
public class TransactionJobImplTest {

	private TransactionJobImpl transactionJob;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		transactionJob = new TransactionJobImpl();
	}

	/**
	 * Tests that a transaction job returns proper list of providers when
	 * one job unit with one job entry is added to a transaction job.
	 */
	@Test
	public void testGetAllProviders() {
		final TransactionJobUnit transactionJobUnit = context.mock(TransactionJobUnit.class);
		transactionJob.addTransactionJobUnit(transactionJobUnit);
		List<Iterable<SerializableObject>> providers = transactionJob.getAllProviders();

		final int expectedProviders = 3;
		assertEquals("Expected providers: for the transaction job, for the job unit and for the entry in the job unit", 
				expectedProviders, providers.size());
		
		Iterable<SerializableObject> jobProvider = providers.get(0);
		Iterator<SerializableObject> jobProviderIterator = jobProvider.iterator();
		assertEquals("First provider should only contain the transaction job object", transactionJob, jobProviderIterator.next());
		assertFalse("No more objects expected by this provider", jobProviderIterator.hasNext());
		
		Iterable<SerializableObject> jobUnitProvider = providers.get(1);
		Iterator<SerializableObject> jobUnitProviderIterator = jobUnitProvider.iterator();
		assertEquals("The second provider should only return the transaction job unit", 
				transactionJobUnit, jobUnitProviderIterator.next());
		assertFalse("No more objects expected", jobUnitProviderIterator.hasNext());
		
		final JobEntry entry = context.mock(JobEntry.class);
		context.checking(new Expectations() { { 
			oneOf(transactionJobUnit).iterator();
			will(returnValue(Arrays.asList(entry).iterator()));
		} });
		
		Iterable<SerializableObject> jobEntryProvider = providers.get(2);
		Iterator<SerializableObject> jobEntryProviderIterator = jobEntryProvider.iterator();
		assertEquals("The third provider expected is the one that holds the job entries of the parent transaction job unit", 
				entry, jobEntryProviderIterator.next());
		assertFalse("No more elements expected by this provider because only one was set as an expectation", 
				jobEntryProviderIterator.hasNext());
		
	}

}
