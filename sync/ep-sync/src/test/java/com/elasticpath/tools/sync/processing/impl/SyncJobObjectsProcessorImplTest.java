/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing.impl;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactory;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.target.JobUnitTransactionCallbackListener;
import com.elasticpath.tools.sync.target.SyncService;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Test cases for {@link com.elasticpath.tools.sync.processing.impl.SyncJobObjectsProcessorImpl}.
 */
public class SyncJobObjectsProcessorImplTest {

	private SyncJobObjectsProcessorImpl objectsProcessor;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SyncBeanFactory syncBeanFactory;

	private PersistenceEngine persistenceEngine;

	private PersistenceSession persistenceSession;

	private SyncService syncService;

    private List<JobUnitTransactionCallbackListener> callbackListeners;

	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		syncBeanFactory = context.mock(SyncBeanFactory.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		persistenceSession = context.mock(PersistenceSession.class);
		syncService = context.mock(SyncService.class);

		objectsProcessor = new SyncJobObjectsProcessorImpl();
		objectsProcessor.setSyncBeanFactory(syncBeanFactory);
		callbackListeners = new ArrayList<JobUnitTransactionCallbackListener>();

	}

	/**
	 * Tests that {@link SyncJobObjectsProcessorImpl#transactionJob(TransactionJob)}
	 * gets the shared persistence session.
	 */
	@Test
	public void testTransactionJob() {
		final TransactionJob job = context.mock(TransactionJob.class);

		context.checking(new Expectations() { {
			oneOf(syncBeanFactory).getTargetBean(ContextIdNames.PERSISTENCE_ENGINE);
			will(returnValue(persistenceEngine));

			oneOf(persistenceEngine).getSharedPersistenceSession();
			will(returnValue(persistenceSession));
		} });
		objectsProcessor.transactionJob(job);
	}

	/**
	 * Tests that the method {@link SyncJobObjectsProcessorImpl#transactionJobEntry(JobEntry, Summary)}
	 * processes a job entry.
	 */
	@Test
	public void testTransactionJobEntry() {
		final Summary summary = context.mock(Summary.class);
		final JobEntry entry = context.mock(JobEntry.class);

		context.checking(new Expectations() { {
			oneOf(syncBeanFactory).getTargetBean("syncService");
			will(returnValue(syncService));

			oneOf(syncService).processJobEntry(entry);
			oneOf(summary).addSuccessJobEntry(entry);
		} });


		objectsProcessor.transactionJobEntry(entry, summary);
	}

	/**
	 * Tests that a job unit start triggers a new transaction.
	 */
	@Test
	public void testTransactionJobUnitStart() {
		final TransactionJobUnit unit = context.mock(TransactionJobUnit.class);

		context.checking(new Expectations() { {
			oneOf(persistenceSession).beginTransaction();
		} });

		objectsProcessor.setPersistenceSession(persistenceSession);
		objectsProcessor.transactionJobUnitStart(unit);
	}

	/**
	 * Tests that processing a transaction job unit end will
	 * close the already started transaction.
	 */
	@Test
	public void testTransactionJobUnitEnd() {
		final Summary summary = context.mock(Summary.class);
		final TransactionJobUnit unit = context.mock(TransactionJobUnit.class);
		final Transaction transaction = context.mock(Transaction.class);

		context.checking(new Expectations() { {
			oneOf(syncBeanFactory).getTargetBean("callbackListeners"); will(returnValue(callbackListeners));
			allowing(transaction).isRollbackOnly(); will(returnValue(false));
			oneOf(transaction).commit();
		} });

		objectsProcessor.setTransaction(transaction);
		objectsProcessor.transactionJobUnitEnd(unit, summary);
	}
	
	/**
	 * Tests that processing a transaction job unit will rollback the transaction if it is marked as roll-back only.
	 */
	@Test
	public void testTransactionJobUnitEndRollback() {
		final Summary summary = context.mock(Summary.class);
		final TransactionJobUnit unit = context.mock(TransactionJobUnit.class);
		final Transaction transaction = context.mock(Transaction.class);
		
		context.checking(new Expectations() { {
			oneOf(syncBeanFactory).getTargetBean("callbackListeners"); will(returnValue(callbackListeners));
			allowing(transaction).isRollbackOnly(); will(returnValue(true));
			oneOf(transaction).rollback();
		} });
		
		objectsProcessor.setTransaction(transaction);
		objectsProcessor.transactionJobUnitEnd(unit, summary);
	}

	/**
	 * Tests that finished will close the session and clears the cache.
	 */
	@Test
	public void testFinished() {
		final Summary summary = context.mock(Summary.class);

		context.checking(new Expectations() { {

		    oneOf(syncBeanFactory).getTargetBean(ContextIdNames.PERSISTENCE_ENGINE); will(returnValue(persistenceEngine));
			oneOf(persistenceEngine).clearCache();
			oneOf(persistenceSession).close();
		} });

		objectsProcessor.setPersistenceSession(persistenceSession);
		objectsProcessor.finished(summary);


	}
}
