package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.transaction.TransactionIteratorFactory;

/**
 * Tests TransactionDemarcationProviderImpl class.
 */
public class TransactionDemarcationProviderImplTest {

	private TransactionIteratorFactory transactionIteratorFactory;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		transactionIteratorFactory = context.mock(TransactionIteratorFactory.class);
	}

	/**
	 * Tests getTransactionEntries() method.
	 */
	@Test
	public void testGetTransactionEntries() {
		final Iterator<List<TransactionJobDescriptorEntry>> iteratorResult =
				new ArrayList<List<TransactionJobDescriptorEntry>>().iterator();

		context.checking(new Expectations() {
			{
				oneOf(transactionIteratorFactory).createIterator(null, null);
				will(returnValue(iteratorResult));
			}
		});

		TransactionDemarcationProviderImpl demarcationProvider = new TransactionDemarcationProviderImpl();
		demarcationProvider.setTransactionIteratorFactory(transactionIteratorFactory);

		final Iterable<List<TransactionJobDescriptorEntry>> transactionEntries = demarcationProvider.getTransactionEntries();

		Assert.assertSame(iteratorResult, transactionEntries.iterator());
	}
}
