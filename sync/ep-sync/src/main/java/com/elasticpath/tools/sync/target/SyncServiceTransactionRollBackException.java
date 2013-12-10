package com.elasticpath.tools.sync.target;

import com.elasticpath.tools.sync.target.result.SyncErrorResultItem;

/**
 * <code>SyncServiceTransactionRollbackException</code> reports that the transaction which was executed by SyncService has been rolled back.
 */
public class SyncServiceTransactionRollBackException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SyncErrorResultItem syncError;

	/**
	 * @param message exception description
	 */
	public SyncServiceTransactionRollBackException(final String message) {
		super(message);		
	}

	/**
	 * @param message exception description
	 * @param cause previous exception causing this one
	 * @param syncError the SyncError which led to the exception.
	 */
	public SyncServiceTransactionRollBackException(final String message, final Throwable cause, final SyncErrorResultItem syncError) {
		super(message, cause);
		this.syncError = syncError;
		syncError.setCause(this);
	}

	/**
	 * @return the syncError
	 */
	public SyncErrorResultItem getSyncError() {
		return syncError;
	}
}
