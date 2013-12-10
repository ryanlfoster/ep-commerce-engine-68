/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.impl;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.processing.ObjectProcessingException;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Distributes object events to the job listener.
 */
public abstract class AbstractObjectEventDistributor implements SerializableObjectListener {

	private SyncJobObjectProcessor objectProcessor;

	private Summary summary;

	private TransactionJobUnit currentJobUnit;

	private boolean finishedInvoked;

	/**
	 * Processes an object by handling it by its type.
	 * 
	 * @param obj the object to process
	 * @throws ObjectProcessingException on error
	 */
	public void processObject(final SerializableObject obj) throws ObjectProcessingException {
		// Any failures detected in this code will be propagated as exceptions and handled by 
		// AbstractSyncController.synchronize()
		if (obj instanceof TransactionJob) {
			getObjectProcessor().transactionJob((TransactionJob) obj);
		} else if (obj instanceof TransactionJobUnit) {
			if (currentJobUnit != null) {
				getObjectProcessor().transactionJobUnitEnd(currentJobUnit, getSummary());
			}
			currentJobUnit = (TransactionJobUnit) obj;
			getObjectProcessor().transactionJobUnitStart(currentJobUnit);
		} else if (obj instanceof JobEntry) {
			getObjectProcessor().transactionJobEntry((JobEntry) obj, getSummary());
		}
	}

	/**
	 * A callback method to handler exceptions occurred during the processing of job units.
	 * 
	 * @param exc the exception that occurred
	 * @param summary the summary to use to update
	 * @param jobUnit the transaction job unit
	 */
	protected abstract void handleException(Exception exc, Summary summary, TransactionJobUnit jobUnit);

	/**
	 *
	 */
	public void finished() {
		if (this.finishedInvoked) {
			return;
		}
		try {
			getObjectProcessor().transactionJobUnitEnd(currentJobUnit, getSummary());
			currentJobUnit = null;
		} finally {
	            this.finishedInvoked = true;
        	    getObjectProcessor().finished(getSummary());
        	}
	}

	/**
	 *
	 * @return the summary
	 */
	protected Summary getSummary() {
		return summary;
	}

	/**
	 *
	 * @param summary the summary to set
	 */
	public void setSummary(final Summary summary) {
		this.summary = summary;
	}

	/**
	 *
	 * @return the objectProcessor
	 */
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

}