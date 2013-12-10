/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.exception.impl;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.tools.sync.client.controller.exception.ExceptionHandler;
import com.elasticpath.tools.sync.exception.ChangeSetNotFoundException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.target.result.Summary;
import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * A change set exception handler for {@link ChangeSetNotFoundException}.
 */
public class ChangeSetExceptionHandlerImpl implements ExceptionHandler {

	@Override
	public boolean canHandle(final Exception exc) {
		return exc instanceof ChangeSetNotFoundException;
	}

	@Override
	public void handleException(final Exception exc, final Summary summary) {
		ChangeSetNotFoundException changeSetNotFoundException = (ChangeSetNotFoundException) exc;
		// according to BB-234 error message should be output.
		SyncErrorResultItem syncError = new SyncErrorResultItem();
		syncError.setCause(changeSetNotFoundException);
		syncError.setTransactionJobUnitName(changeSetNotFoundException.getChangeSetName());
		syncError.setJobEntryType(ChangeSet.class);
		syncError.setJobEntryCommand(Command.UPDATE);
		summary.addSyncError(syncError);
	}

}
