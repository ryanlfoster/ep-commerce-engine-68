/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.dao.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.controller.impl.FileSystemHelper;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * The default, file system based, DAO implementation.
 */
public class TransactionJobDaoImpl implements TransactionJobDao {

	private static final Logger LOG = Logger.getLogger(TransactionJobDaoImpl.class);

	private String jobUnitFileName;

	private FileSystemHelper fileSystemHelper;		


	/**
	 * Saves a transaction job.
	 * 
	 * @param transactionJob the transaction job to save
	 */
	public void save(final TransactionJob transactionJob) {
		fileSystemHelper.saveTransactionJobToFile(transactionJob, getJobUnitFileName());
		LOG.debug(getJobUnitFileName() + " has been saved");
	}

	/**
	 * Loads a transaction job by loading its elements.
	 * This depends on the order the job gets saved.
	 * 
	 * @param objectListener an object listener to be notified of the transaction job elements
	 */
	public void load(final SerializableObjectListener objectListener) {
		fileSystemHelper.readTransactionJobFromFile(jobUnitFileName, objectListener);
	}

	/**
	 *
	 * @return the fileSystemHelper
	 */
	protected FileSystemHelper getFileSystemHelper() {
		return fileSystemHelper;
	}

	/**
	 *
	 * @param fileSystemHelper the fileSystemHelper to set
	 */
	public void setFileSystemHelper(final FileSystemHelper fileSystemHelper) {
		this.fileSystemHelper = fileSystemHelper;
	}

	/**
	 *
	 * @return the jobUnitFileName
	 */
	protected String getJobUnitFileName() {
		return jobUnitFileName;
	}

	/**
	 *
	 * @param jobUnitFileName the jobUnitFileName to set
	 */
	public void setJobUnitFileName(final String jobUnitFileName) {
		this.jobUnitFileName = jobUnitFileName;
	}

}
