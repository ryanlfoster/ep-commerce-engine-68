/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.exception.impl;

import com.elasticpath.tools.sync.client.controller.exception.ExceptionHandler;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * A default implementation of the {@link ExceptionHandler} 
 * that handles general exceptions of type {@link Exception}.
 */
public class DefaultExceptionHandler implements ExceptionHandler {

	@Override
	public boolean canHandle(final Exception exc) {
		return true;
	}

	@Override
	public void handleException(final Exception exc, final Summary summary) {
		SyncErrorResultItem syncError = new SyncErrorResultItem();
		syncError.setCause(exc);
		summary.addSyncError(syncError);
	}

}
