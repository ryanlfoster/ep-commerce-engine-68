package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;

/**
 * The LoadController loads data from a TransactionJob. 
 * It does only use the target system. It synchronizes by deploying the data to the target system.
 */
public class LoadController extends AbstractSyncController {
	
	private static final Logger LOG = Logger.getLogger(LoadController.class);

	private TransactionJobDao transactionJobDao;

	private SyncJobObjectProcessor objectProcessor;

	/**
	 * Reads TransactionJob from default file.
	 * 
	 * @param listener the object listener
	 */
	@Override
	protected void loadTransactionJob(final SerializableObjectListener listener) {
		LOG.debug("Loading transaction job...");
		transactionJobDao.load(listener);
	}

	/**
	 *
	 * @return the object processor instance
	 */
	@Override
	protected SyncJobObjectProcessor getObjectProcessor() {
		return objectProcessor;
	}

	/**
	 *
	 * @param objectProcessor the objectProcessor to set
	 */
	public void setObjectProcessor(final SyncJobObjectProcessor objectProcessor) {
		this.objectProcessor = objectProcessor;
	}

	/**
	 *
	 * @return the transactionJobDao
	 */
	protected TransactionJobDao getTransactionJobDao() {
		return transactionJobDao;
	}

	/**
	 *
	 * @param transactionJobDao the transactionJobDao to set
	 */
	public void setTransactionJobDao(final TransactionJobDao transactionJobDao) {
		this.transactionJobDao = transactionJobDao;
	}

	/**
	 * Initializes only the target system.
	 * 
	 * @param sourceSystem the source system configuration
	 * @param targetSystem the target system configuration
	 */
	@Override
	protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		targetSystem.initSystem();
	}

	/**
	 * Call destroy cleanup on just the target system.
	 *
	 * @param sourceSystem the source system
	 * @param targetSystem the target system
	 */
	@Override
	protected void destroyConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		targetSystem.destroySystem();
	}
}
