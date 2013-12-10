package com.elasticpath.tools.sync.target;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.JobEntry;

/**
 * Callback interface for hooking into Data Sync Tool JobEntry processing.
 */
public interface JobTransactionCallback {

	/**
	 * Method called before each update (add or change) job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process. 
	 *
	 * @param jobEntry the jobEntry being updated
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	void preUpdateJobEntryHook(JobEntry jobEntry, final Persistable targetPersistence);

	/**
	 * Method called after each update (add or change) job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process. 
	 *
	 * @param jobEntry the jobEntry being updated
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	void postUpdateJobEntryHook(JobEntry jobEntry, final Persistable targetPersistence);

	/**
	 * Method called before each remove of a job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process. 
	 *
	 * @param jobEntry the entry being removed
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	void preRemoveJobEntryHook(JobEntry jobEntry, Persistable targetPersistence);

	/**
	 * Method called after each remove of a job entry in a transaction. Calls
	 * are synchronous and in the same transaction as the main sync process.
	 *  
	 * @param jobEntry the entry being removed
	 * @param targetPersistence the persistable object on the target, for retrieving info not included in jobEntry
	 */
	void postRemoveJobEntryHook(JobEntry jobEntry, Persistable targetPersistence);
	
	/**
	 * Returns a callback id string which identifies what particular implementation is being used.
	 *
	 * @return callback id string which identifies implementation
	 */
	String getCallbackID();
}
