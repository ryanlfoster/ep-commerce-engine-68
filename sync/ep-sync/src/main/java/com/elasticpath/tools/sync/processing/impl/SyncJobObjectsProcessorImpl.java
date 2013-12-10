/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactory;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.JobUnitTransactionCallbackListener;
import com.elasticpath.tools.sync.target.SyncService;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * A processor of new object events that uses the {@link SyncService}
 * to process the objects.
 */
public class SyncJobObjectsProcessorImpl implements SyncJobObjectProcessor {

	private static final Logger LOG = Logger.getLogger(SyncJobObjectsProcessorImpl.class);

	private SyncBeanFactory syncBeanFactory;

	private Transaction transaction;

	private PersistenceSession persistenceSession;

	@Override
	public void transactionJob(final TransactionJob job) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start processing a transaction job: " + job);
		}
		openSession();
	}

	@Override
	public void transactionJobEntry(final JobEntry entry, final Summary summary) {
		final SyncService syncService = getSyncBeanFactory().getTargetBean("syncService");

		syncService.processJobEntry(entry);
		summary.addSuccessJobEntry(entry);
	}


	@Override
	public void transactionJobUnitStart(final TransactionJobUnit unit) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start of a new transaction job unit: " + unit);
		}

		openTransaction();
	}

	@Override
	public void transactionJobUnitEnd(final TransactionJobUnit unit, final Summary summary) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("End of the transaction job unit: " + unit);
		}
		firePreCommitListeners();

		commitTransaction();
	}

	@Override
	public void finished(final Summary summary) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing transaction job finished event.");
		}


		closeSession();

		// Clear any cache on the target
		final PersistenceEngine targetEngine = getPersistenceEngine();
		targetEngine.clearCache();
	}



	private void firePreCommitListeners() {
	    final List<JobUnitTransactionCallbackListener> callbackListeners = getSyncBeanFactory().getTargetBean("callbackListeners");
	    for (final JobUnitTransactionCallbackListener listener : callbackListeners) {
	        listener.preCommitHook();
	    }

	}

	/**
	 * Opens a persistence session.
	 */
	protected void openSession() {
		persistenceSession = getPersistenceEngine().getSharedPersistenceSession();
	}

	/**
	 * Closes the persistence session.
	 */
	protected void closeSession() {
		if (persistenceSession != null) {
			try {
				persistenceSession.close();
			} catch (final EpPersistenceException e) {
				LOG.error("Error closing the persistence session", e);
			}
		}
	}

	/**
	 * Opens a new transaction.
	 */
	protected void openTransaction() {
		setTransaction(persistenceSession.beginTransaction());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Opened a new transaction: " + getTransaction());
		}
	}

	/**
	 * Commits the already opened transaction.
	 */
	protected void commitTransaction() {
		if (getTransaction() == null) {
			LOG.debug("No transaction is created, so will not commit.");
		} else if (getTransaction().isRollbackOnly()) {
			LOG.debug("The transaction is marked as rollback only. Rolling back... ");
			getTransaction().rollback();
		} else {
			LOG.debug("Committing the transaction...");
			getTransaction().commit();
		}
	}

	/**
	 *
	 * @return the persistence engine
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return getSyncBeanFactory().getTargetBean(ContextIdNames.PERSISTENCE_ENGINE);
	}

	/**
	 *
	 * @return the syncBeanFactory
	 */
	protected SyncBeanFactory getSyncBeanFactory() {
		return syncBeanFactory;
	}

	/**
	 *
	 * @param syncBeanFactory the syncBeanFactory to set
	 */
	public void setSyncBeanFactory(final SyncBeanFactory syncBeanFactory) {
		this.syncBeanFactory = syncBeanFactory;
	}

	/**
	 *
	 * @return the sharedPersistenceSession
	 */
	protected PersistenceSession getPersistenceSession() {
		return persistenceSession;
	}

	/**
	 *
	 * @param persistenceSession the persistence session to set
	 */
	void setPersistenceSession(final PersistenceSession persistenceSession) {
		this.persistenceSession = persistenceSession;
	}

	/**
	 *
	 * @return the transaction
	 */
	protected Transaction getTransaction() {
		return transaction;
	}

	/**
	 *
	 * @param transaction the transaction to set
	 */
	void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}

}

