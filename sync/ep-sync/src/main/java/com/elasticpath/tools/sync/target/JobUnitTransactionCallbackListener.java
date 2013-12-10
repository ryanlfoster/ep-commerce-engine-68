package com.elasticpath.tools.sync.target;

/**
 * The listener interface for receiving jobUnitTransactionCallback events.
 * The method preCommitHook() is called before the synced data is committed.
 */
public interface JobUnitTransactionCallbackListener {

    /**
     * Runs before the job unit commits.
     */
    void preCommitHook();

}
