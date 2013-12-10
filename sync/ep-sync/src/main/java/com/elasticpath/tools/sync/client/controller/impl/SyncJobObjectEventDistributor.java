/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.controller.exception.ExceptionHandler;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * The default sync job objects event distributor.
 */
public class SyncJobObjectEventDistributor extends AbstractObjectEventDistributor {

	private static final Logger LOG = Logger.getLogger(SyncJobObjectEventDistributor.class);
	
	private List<ExceptionHandler> exceptionHandlers;
	
	@Override
	protected void handleException(final Exception exc, final Summary summary, final TransactionJobUnit jobUnit) {
		LOG.error("Failed to synchronize a transaction job unit: " + jobUnit, exc);
		for (ExceptionHandler exceptionHandler : getExceptionHandlers()) {
			if (exceptionHandler.canHandle(exc)) {
				exceptionHandler.handleException(exc, summary);
				return;
			}
		}
	}

	/**
	 *
	 * @return the exceptionHandlers
	 */
	protected List<ExceptionHandler> getExceptionHandlers() {
		return exceptionHandlers;
	}

	/**
	 *
	 * @param exceptionHandlers the exceptionHandlers to set
	 */
	public void setExceptionHandlers(final List<ExceptionHandler> exceptionHandlers) {
		this.exceptionHandlers = exceptionHandlers;
	}
	
	
}