/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.exception.impl;

import com.elasticpath.tools.sync.client.controller.exception.ExceptionHandler;
import com.elasticpath.tools.sync.target.SyncServiceTransactionRollBackException;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * An exception handler for {@link SyncServiceTransactionRollBackException}.
 */
public class TranasactionRollbackExceptionHandler implements ExceptionHandler {

	@Override
	public boolean canHandle(final Exception exc) {
		return exc instanceof SyncServiceTransactionRollBackException;
	}

	@Override
	public void handleException(final Exception exc, final Summary summary) {
		SyncServiceTransactionRollBackException error = (SyncServiceTransactionRollBackException) exc;
		summary.addSyncError(error.getSyncError());
	}

}
